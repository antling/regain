/*
 * LanguageParser.java February 2001
 *
 * Copyright (C) 2001, Niall Gallagher <niallg@users.sf.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * Public License along with this library; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
 
package simple.util.parse;

import simple.util.PriorityQueue;
import java.util.Locale;

/** 
 * LanguageParser is used to parse the HTTP <code>Accept-Language</code> 
 * header. This takes in an <code>Accept-Language</code> header and parses 
 * it according the RFC 2616 BNF for the <code>Accept-Language</code> header. 
 * This also has the ability to sequence the language tokens in terms of 
 * the most preferred and the least preferred. 
 * <p>
 * This uses the qvalues outlined by RFC 2616 to order the language tokens 
 * by preference. Typically the language tokens will not have qvalues with 
 * the language. However when a language tag has the qvalue parameter then 
 * this tag will be ordered based on the value of that parameter. A language 
 * tag without the qvalue parameter is considered to have a qvalue of 1 and 
 * is ordered accordingly.
 *
 * @author Niall Gallagher
 */
public class LanguageParser extends Parser {

   /** 
    * Used to order the language tags on their qvalues.
    */
   private PriorityQueue queue;

   /** 
    * A list of the language <code>Locale</code>s preferred.
    */
   private Locale[] list;
   
   /** 
    * Used to consume the characters for the primary language.
    */
   private ParseBuffer primary;
   
   /** 
    * Used to consume the characters for the language subtag.
    */
   private ParseBuffer subtag;

   /** 
    * Used to represent the value of a <code>Locale</code>.
    */ 
   private int qvalue; 

   /** 
    * This is used to determine when the parsing is finished.
    */
   private boolean finished;

   /** 
    * This is used to determine if a language has been parsed.
    */
   private boolean parsed;

   /** 
    * Used to create a language parser for the 
    * <code>Accept-Language</code> HTTP header value. This will 
    * parse a set of language tokens and there parameters. The 
    * languages will be ordered on preference.
    */
   public LanguageParser(){      
      primary = new ParseBuffer();
      subtag = new ParseBuffer();
      queue = new PriorityQueue();
   }

   /**   
    * This is used to create a language parser for the 
    * <code>Accept-Language</code> HTTP header value. This will 
    * parse a set of language tokens and there parameters. The 
    * languages will be ordered on preference. This constructor 
    * will parse the value given using <code>parse(String)</code>.
    *
    * @param value value of a <code>Accept-Language</code> header    
    */

   public LanguageParser(String value) {
      this();
      parse(value);
   } 
 
   /** 
    * This is used to remove all whitespace characters from the 
    * <code>String</code> excluding the whitespace within literals. 
    * The definition of a literal can be found in RFC 2616. The 
    * definition of a literal for RFC 2616 is any thin between 2 
    * quotes but excuding quotes that are prefixed with the backward 
    * slash character.
    */
   private void pack() {
      int len = count;
      int seek = 0;
      int pos = 0;
      char old = buf[0];

      while(seek < len){
         char ch = buf[seek++];
         if(ch == '"' && old != '\\'){  /* qd-text*/
            buf[pos++] = ch;
            while(seek < len){
               old = buf[seek-1];
               ch = buf[seek++];  
               buf[pos++] = ch;             
               if(ch =='"'&& old!='\\'){  /*qd-text*/
                  break;
               }
            }
         }else if(!space(ch)){            
            old = buf[seek - 1];  
            buf[pos++] = old;                   
         }         
      }
      count = pos;
   }

   /** 
    * This will return the preferred <code>Locale</code> for this 
    * header. The preferred <code>Locale</code> may be any of the 
    * <code>Locale</code>s given in the header with a qvalue 
    * parameter value of 1.
    *
    * @return this returns the users preferred <code>Locale</code>
    */
   public Locale getLocale() {
      if(list == null){
         build();
      }
      return list[0];   
   }

   /** 
    * This will order the tokens based on the preference of the 
    * language. The <code>Locale[]</code> objects extracted from the
    * Accept-Language header. The array returned is sorted using the
    * qvalue preferences within the Accept-Language header.
    *
    * @return a sorted array of <code>Locale</code> objects
    */
   public Locale[] getLocales() {
      if(list == null){   
         build();
      }
      return list;
   }

   /** 
    * This is used to remove the <code>Locale</code> tags from the 
    * priority queue and place those tags in a <code>Locale</code> 
    * array. The <code>Locale</code>s are placed into the array in 
    * an ordered manner so that the most preferred <code>Locale</code> 
    * is inserted into the start of the list.
    */
   private void build() {
      list = new Locale[queue.length()];
      for(int i = 0; i < list.length; i++){     
         list[i] = (Locale)queue.remove();
      }
   }    

   /** 
    * This will iteratively take language tokens from the HTTP 
    * header list. This places each language token parsed from
    * the buffer into the priority queue based on its value.
    */
   protected void parse(){
      while(hasMore()){
         Locale lang = next();
         queue.add(lang,qvalue);         
      }
   }

   /** 
    * This will initialize the parser when it is ready to parse a 
    * new <code>String</code>. This will reset the parser to a ready 
    * state. The <code>init</code> method is invoked by the parser 
    * when the <code>parse</code> method is invoked.
    */
   protected void init(){      
      pack();
      list = null;
      off = 0;
   }

   /** 
    * This will remove the tokens from the buffer and create a 
    * <code>Locale</code> object based on the primary language
    * tag and if it exists the language subtag. The format of 
    * these tags is primary-subtag, example en-US.
    *
    * @return returns the <code>Locale</code> that was parsed
    */
   private Locale next(){
      if(!hasMore()) {
         return null;      
      }
      parsed = false;
      String prime = primary.toString();
      String sub = null;

      if(subtag.length() >0){
         sub = subtag.toString();
         return new Locale(prime,sub);
      }
      return new Locale(prime,"");
   }

   /** 
    * This will determine if there are any more tokens in the buffer 
    * by attempting to parse a language token from the buffer. If the 
    * primary tag will filled with some data then this constitutes an 
    * new language token. Multiple calls to this will no result in 
    * multiple parses. Only after the next method is called will this 
    * start parsing.
    *
    * @return returns true if there is another token left
    */       
   private boolean hasMore(){     
      if(finished) {
         return false; 
      }
      if(parsed) {
         return true;
      }
      clear();
      language(); 
      parsed = true;

      if(primary.length() <=0){
         finished = true;         
         return false;
      }
      return true;
   }

   /** 
    * This will clear the tokens that may have been filled on 
    * a previous parse of the Parser.buf. This just emptys the 
    * primary language tag and the language subtag tokens.
    */
   private void clear() {
      primary.clear();
      subtag.clear();
   }

   /** 
    * This will extract the language token from the header.
    * This accounts for a special case where the language is 
    * represented as a '*' character.
    */
   private void language(){
      if(skip("*")){
         primary.append('*');         
      } else {
         primarytag();
         while(skip("-")){
            subtag();         
         }
      }
      qvalue();
      off++; /* skip comma */            
   }

   /** 
    * This will extract the primary language tag from the header. 
    * This token is then stored in a parse buffer so that its 
    * contents can be represented as in individual string. This 
    * will only allow a token of up to 8 characters long.
    */
   private void primarytag() {
      int end = off + 8;
      
      while(off < end && off < count){
         if(!alpha(buf[off])){
            break;
         }
         primary.append(buf[off]);
         off++;
      } 
   }

   /** 
    * This is used to determine wheather or not a character is 
    * an alpha numeric character or not. If the character is an 
    * ascii or unicode alpha-numeric character this is true.
    *
    * @param c this will be checked to see if is alpha-numeric
    *
    * @return this returns true if this is alpha-numeric
    */
   private boolean alpha(char c){   
      return (c <= 'z' && 'a' <= c) || 
       (c <= 'Z' && 'A' <= c);
   }

   /** 
    * This will extract the language subtag from the header. This 
    * token is then stored in a parse buffer so that its contents 
    * can be represented as in individual string. This will only 
    * allow a token of up to 8 characters long.
    */  
   private void subtag(){
      int end = off + 8;
      
      while(off < end && off < count){
         if(!alpha(buf[off])){
            break;
         }
         subtag.append(buf[off]);
         off++;
      } 
   }

   /** 
    * This is used to extract the qvalue parameter from the header. 
    * The qvalue parameter is identified by a parameter with the name 
    * "q" and a numeric floating point number. The number can be in 
    * the range of 0.0 to 1.0. The <code>qvalue</code> is parsed as 
    * if there is no decimal place so 1.0 becomes 10 and 0.7 becomes
    * 7 etc.
    */
   private void qvalue() {      
      qvalue = 0;
      if(skip(";q=")){         
         while(off < count){
            if(buf[off] == '.'){
               off++;
               continue;
            }
            if(!digit(buf[off])){
               break;
            }
            qvalue *= 10;
            qvalue += buf[off];
            qvalue -= '0';
            off++;
         }
      }else{
         qvalue = 10; /* i.e. 1.0 */
      }
   }  
}

/*
 * ListParser.java September 2003
 *
 * Copyright (C) 2003, Niall Gallagher <niallg@users.sf.net>
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

/**
 * The <code>ListParser</code> is used to extract a comma seperated 
 * list of HTTP header values. This will extract values without 
 * any leading or trailing spaces, which enables the values to be
 * used. Listing the values that appear in the header also requires
 * that the values are ordered. This orders the values using the 
 * values that appear with any quality parameter associated with it.
 * The quality value is a special parameter that often found in a
 * comma seperated value list to specify the client preference.
 * <pre>
 * 
 *    image/gif, image/jpeg, text/html
 *    image/gif;q=1.0, image/jpeg;q=0.8, image/png;  q=1.0,*;q=0.1
 *    gzip;q=1.0, identity; q=0.5, *;q=0
 *
 * </pre>
 * The above lists taken from RFC 2616 provides an example of the
 * common form comma seperated values take. The first illustrates
 * a simple comma delimited list, here the ordering of values is
 * determined from left to right. The second and third list have
 * quality values associated with them, these are used to specify
 * a preference and thus order. 
 * <p> 
 * Each value within a list has an implicit quality value of 1.0.
 * If the value is explicitly set with a the "q" parameter, then
 * the values can range from 1.0 to 0.001. This parser ensures
 * that the order of values returned from the <code>list</code>
 * method adheres to the optional quality parameters and ensures
 * that the quality parameters a removed from the resulting text.
 * 
 * @author Niall Gallagher
 */
public class ListParser extends Parser {

   /**
    * Provides a quick means of sorting the values extracted.
    */
   private PriorityQueue queue;

   /**
    * Contains all the values extracted from the header(s).
    */
   private String[] list;
   
   /**
    * This is used as a working space to parse the value. 
    */
   private char[] text;

   /**
    * The quality associated with an individual value.
    */ 
   private int qvalue;

   /**
    * Used to provide a starting value for the quality.
    */
   private int start;

   /**
    * Used to index into the write offset for the value. 
    */
   private int pos;

   /**
    * Constructor for the <code>ListParser</code>. This creates a
    * parser with no initial parse data, if there are headers to
    * be parsed then the <code>parse(String)</code> method or
    * <code>parse(String[])</code> method can be used. This will
    * parse a delimited list according so RFC 2616 section 4.2.
    */
   public ListParser(){
      queue = new PriorityQueue(); 
      text = new char[0];
   }

   /**
    * Constructor for the <code>ListParser</code>. This creates a
    * parser with the text supplied. This will parse the comma
    * seperated list according to RFC 2616 section 2.1 and 4.2.
    * The tokens can be extracted using the <code>list</code>
    * method, which will also sort and trim the tokens.
    * 
    * @param text this is the comma seperated list to be parsed
    */
   public ListParser(String text) {
      this();
      parse(text);
   }
   
   /**
    * Constructor for the <code>ListParser</code>. This creates a
    * parser with the text supplied. This will parse the comma
    * seperated list according to RFC 2616 section 2.1 and 4.2.
    * The tokens can be extracted using the <code>list</code>
    * method, which will also sort and trim the tokens.
    * 
    * @param list a list of comma seperated lists to be parsed
    */
   public ListParser(String[] list) {
      this();
      parse(list);
   }

   /**
    * This allows multiple header values to be represented as one
    * single comma seperated list. RFC 2616 states that multiple 
    * message header fields with the same field name may be present
    * in a message if and only if the entire field value for that
    * header field is defined as a comma separated list. This means
    * that if there are multiple header values with the same name
    * they can be combined into a single comma seperated list.
    *
    * @param list this is a list of header values to be combined
    */
   public void parse(String[] list) {
      for(int i = 0; i < list.length; i++) {
         parse(list[i]);
      }
   }

   /**
    * This will build an ordered list of values extracted from the
    * comma seperated header value. This enables the most preferred
    * token, to be taken from the first index of the array and the
    * least preferred token to be taken from the last index.
    * 
    * @return tokens parsed from the list ordered by preference
    */
   public String[] list() {
      if(list == null){
         build();
      }
      return list;
   }
   
   /** 
    * This is used to remove the <code>String</code> tokens from 
    * the priority queue and place those tokens in an array. The
    * The <code>String</code> tokens are placed into the array 
    * in an ordered manner so that the most preferred token is
    * inserted into the start of the list.
    */
   private void build() {
      list = new String[queue.length()];
      for(int i = 0; i < list.length; i++){     
         list[i] = (String)queue.remove();
      }
   }
   
   /**
    * This ensures that tokens are taken from the comma seperated
    * list as long as there bytes left to be examined within the
    * source text. This also makes sure that the implicit qvalue
    * is decreased each time a token is extracted from the list.
    */
   protected void parse() {
      while(off < count) {
         clear();
         value();
         save();
      }
   }

   /**
    * Initializes the parser so that tokens can be extracted from 
    * the list. This creates a write buffer so that a if there is 
    * only one token as long as the source text, then that token
    * can be accomodated, also this starts of the initial qvalue
    * implicit to tokens within the list as 1000.
    * <p>
    * One thing that should be noted is that this will not empty
    * the priority queue on each string parsed. This ensures that
    * if there are multiple strings they can be parsed quickly 
    * and also contribute to the final result.
    */
   protected void init(){
      if(text.length < count){
         text = new char[count];
      }
      pos = off = 0;
      list = null;
      start = 1000;
   }

   /**
    * This is used to return the parser to a semi-initialized state.
    * After extracting a token from the list the buffer will have
    * accumulated bytes, this ensures that bytes previously written
    * to the buffer do not interfere with the next token extracted.
    * <p>
    * This also ensures the implicit qvalue is less 0.001 than the
    * previous qvalue, so that the priority queue does not upset 
    * the order that tokens appear within the list. This ensures
    * that the ordering performed by this parser is more useful.
    */
   private void clear() {
      qvalue = start--;
      pos = 0;
   }

   /**
    * This method will extract a token from a comma seperated list
    * and write it to a buffer. This performs the extraction in such
    * a way that it can tolerate literals, parameters, and quality
    * value parameters. The only alterations made to the token by
    * this method is the removal of quality values, that is, qvalue
    * parameters which have the name "q". Below is an example of 
    * some of the lists that this can parse.
    * <pre>
    *
    *    token; quantity=1;q=0.001, token; text="a, b, c, d";q=0
    *    image/gif, , image/jpeg, image/png;q=0.8, *
    *    token="\"a, b, c, d\", a, b, c, d", token="a";q=0.9,,
    *    
    * </pre>
    * This will only interpret a comma delimiter outside quotes of
    * a literal. So if there are comma seperated tokens that have
    * quoted strings, then commas within those quoted strings will
    * not upset the extraction of the token. Also escaped strings
    * are tolerated according to RFC 2616 section 2.
    */
   private void value() {
      parse: while(off < count) {
         if(buf[off++] == '"'){ /* "[t]ext" */
            text[pos++] = buf[off-1]; /* ["]text"*/
            while(++off < count){   /* "text"[] */ 
               if(buf[off -1] =='"'){ /* "text["] */
                  if(buf[off -2] !='\\')
                     break;
               }
               text[pos++] = buf[off-1]; /* "tex[t]"*/
            }
         } else if(buf[off -1] == ';'){ /* [;] q=0.1 */
            for(int seek = off; seek+1 < count;){/* ;[ ]q=0.1 */
               if(!space(buf[seek])){  /* ;[ ]q=0.1*/
                  if(buf[seek] =='q'){ /* ; [q]=0.1*/
                     if(buf[seek+1] =='='){ /* ; q[=]0.1*/
                        off = seek;
                        qvalue();
                        continue parse;
                     }
                  }
                  break;
               }
               seek++;
            }
         } 
         if(buf[off-1] ==','){
            break;
         }
         text[pos++] = buf[off-1];
      }
   }
   
   /**
    * This method will trim whitespace from the extracted token and
    * store that token within the <code>PriorityQueue</code>. This
    * ensures that the tokens parsed from the comma seperated list
    * can be used. Trimming the whitspace is somthing that will be
    * done to the tokens so that they can be examined, so this 
    * ensures that the overhead of the <code>String.trim</code> 
    * method is not required to remove trailing or leading spaces.
    * This also ensures that empty tokens are not saved.
    */
   private void save() {
      int size = pos;
      int start = 0;

      while(size > 0){
         if(!space(text[size-1])){
            break;
         }
         size--;
      }
      while(start < pos){
         if(space(text[start])){
            start++;
            size--;
         }else {
            break;
         }
      }
      if(size > 0)
         save(start, size);
   }

   /**
    * This creates a string object using an offset and a length.
    * The string is created from the extracted token and the offset
    * and length ensure that no leading or trailing whitespace are
    * within the created string object. Once the string object has
    * been created it is stored in the <code>PriorityQueue</code>.
    * 
    * @param start the offset within the buffer to take characters
    * @param len this is the number of characters within the token
    */
   private void save(int start, int len){
      save(new String(text, start, len));
   }

   /**
    * This stores the string in the <code>PriorityQueue</code>. If
    * the qvalue extracted from the header value is less that 0.001
    * then this will not store the token. This ensures that client
    * applications can specify tokens that are unacceptable to it.
    *
    * @param text this is the token to be enqueued into the queue
    */
   private void save(String text) {
      if(qvalue > 0) {
         queue.add(text, qvalue);
      }
   }
   
   /** 
    * This is used to extract the qvalue parameter from the header. 
    * The qvalue parameter is identified by a parameter with the 
    * name "q" and a numeric floating point number. The number can 
    * be in the range of 0.000 to 1.000. The <code>qvalue</code> 
    * is parsed as if there is no decimal place so 1.000 becomes 
    * 1000 and 0.7 becomes 700 and so on.
    */
   private void qvalue() {
      qvalue = 0;
      if(skip("q=")){         
         int value = start;
         int digit = 0;

         while(off < count){
            if(buf[off] == '.'){
               off++;
               continue;
            }
            if(!digit(buf[off])){
               break;
            }
            digit = buf[off]; 
            digit -= '0';  
            digit *= value;  
            value /= 10;    
            qvalue += digit;  
            off++;   
         }
      }else {
         qvalue = start;
      }
   }
}

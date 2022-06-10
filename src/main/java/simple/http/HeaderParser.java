/*
 * HeaderParser.java February 2001
 *
 * Copyright (C) 2001, Niall Gallagher <niallg@email.com>
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
 
package simple.http;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import simple.util.ByteStore;

/**
 * This is a <code>HeaderParser</code> object that is used to parse a
 * <code>ByteStore</code> object containing a HTTP header ended by a
 * CRLF CRLF sequence. This produces <code>Header</code> objects from 
 * the parsing process. 
 * <p>
 * This provides an internal implementation to the <code>Header</code>
 * interface which allows the <code>HeaderParser</code> to produce
 * the implementations rather than using the <code>PlainHeader</code> 
 * implementation.
 *
 * @author Niall Gallagher
 */ 
final class HeaderParser {    

   /**
    * Information about the current name. 
    */
   private Token name = new Token();
   
   /**
    * Information about the current value. 
    */
   private Token value = new Token();
   
   /**
    * The bytes the represent the HTTP header. 
    */
   private byte[] buf;  
   
   /**
    * The number of bytes read from the header. 
    */
   private int count;
   
   /**
    * The current read offset within the header. 
    */
   private int off;  
   
   /**
    * The number if bytes within the header.
    */
   private int len;
   
   /**
    * If the current header has been parsed. 
    */
   private boolean parsed = false;
   
   /**
    * This creates a <code>HeaderParser</code> object that can be 
    * used to parse a <code>ByteStore</code> object containing a 
    * HTTP message header ended with a CRLF CRLF sequence. This 
    * cannot change the <code>ByteStore</code>.
    *
    * @param buf the store containing the HTTP message header
    */ 
   public HeaderParser(ByteStore buf) {
      this.buf = new byte[buf.length()];
      buf.getBytes(0, this.buf, 0, this.buf.length);
      len = this.buf.length;
      count = off = 0;        
      description(); 
   }   
    
   /**
    * This will retrive the next <code>Header</code> in the HTTP 
    * message header. If there are no more <code>Header</code>s 
    * then this will return <code>null</code>. If there are any 
    * more <code>Header</code>'s the <code>hasMore</code> method 
    * will return true.
    *
    * @return the next <code>Header</code> that was found in the 
    * message header
    */ 
   public Header next() {
      if(hasMore()) {
         parsed = false;
         return new Entry(name, value, buf);
      }
      return null;
   }

   /**
    * This is used to determine if there is any more headers 
    * in the HTTP message header. If there is more this will 
    * return true.
    *
    * @return true if there are more headers in the message
    */ 
   public boolean hasMore() {
      if(parsed) return true;
      if(count + 1 < len) {  /* check for CRLF ending */
         byte a = buf[off];
         byte b = buf[off + 1];            
         if(a == 13 && b == 10) {
            return false;
         }
      }        
      parse();
      if(value.len > 0 && name.len > 0){
         parsed = true;
         return true;
      }
      return false;
   }    
     
   /**
    * This removes the description line from the HTTP message
    * header. The description line is either the status line or
    * the request line and is not in a <code>GenericHeader</code> 
    * object. The description line cannot be represented as a 
    * <code>Header</code> object.
    */ 
   private void description() {  
      while(count < len) {
         if(buf[off] == 10){ /* cannot be folded see RFC 2616 pg35 */
            count++; /* skip past LF */
            off++;
            break;
         }
         count++;
         off++;            
      }    
   }
     
   /**
    * This is used to update the token objects for the name and
    * value of the <code>Header</code>. The <code>Header</code> 
    * will be parsed and read from the <code>ByteStore</code>. It 
    * is then wrapped in a <code>Header</code> object which can 
    * be used to access the <code>Header</code>s data.
    */ 
   private void parse() {        
      name();     /* Some-Header: */
      value();    /* some-value CRLF */
   }  

   /**
    * This updates the token for the <code>Header</code> name. The 
    * name is parsed according to the presence of a colon ':'. Once 
    * a colon character is encountered then this <code>Header</code> 
    * name is considered to be read from the buffer.
    */ 
   private void name() {
      whitespace();  
      name.off = off;
      name.len = 0;
        
      while(count < len){
         if(buf[off] == ':') {
            off++;   /* skip past the colon */
            count++; 
            break;
         }
         name.len++;
         count++;
         off++;
      }                 
   }
    
   /**
    * This is used to parse the HTTP header value. This will 
    * parse it in such a way that the line can be folded over 
    * several lines (i.e. it may contain a CRLF) see RFC 2616 
    * for the syntax of a folded line. The folded line is 
    * basically a way to wrap a single HTTP header into sevral 
    * lines using a tab at the start of the following line to 
    * indicate that the header flows onto the next line. For 
    * example <code>Some-Name: value, value CRLF TAB value, 
    * value</code>.
    */ 
   private void value() {
      whitespace();  
      value.off = off;
      value.len = 0;
        
      for(int mark= 0; count < len;){
         if(terminal(buf[off])) {  /* CR  or  LF */
            for(int i = 0; count < len; i++){
               if(buf[off] == 10) {
                  count++;  /* skip the LF */
                  off++;
                  if(space(buf[off])) {
                     value.len += i;  /* acount for bytes examined */
                     break; /* folding line */
                  } 
                  return; /* not a folding line */
               } 
               count++;
               off++;
            }       
         } else {
            if(!space(buf[off])){
               value.len= ++mark;
            } else {
               mark++;
            }
            count++;
            off++;   
         }
      }                
   }

   
   /**
    * This will update the offset variable so that the next read 
    * will be of a non whitespace character. According to RFC 2616 
    * a white space character is a tab or a space. This will remove 
    * multiple occurences of whitespace characters until an 
    * non-whitespace character is encountered.
    */ 
   private void whitespace() {
      while(count < len) {
         if(!space(buf[off])){
            break;
         }
         count++;
         off++;
      }
   }   
    
   /**
    * This itentifies a given ISO-8859-1 byte as a space
    * character. A space is either a space or a tab char.
    *
    * @param b the byte thats value is to be checked
    *
    * @return true if the byte is a space character
    */ 
   private boolean space(byte b) {
      return b == ' ' || b == '\t';
   }

   /**
    * This determines if an ISO-8859-1 byte is a terminal
    * character. A terminal character is a CR or an LF.
    *
    * @param b the byte thats value is to be checked
    *
    * @return true if the byte is a terminal character
    */ 
   private boolean terminal(byte b){
      return b == 13 || b == 10;
   }


   /**
    * This implements the <code>Header</code> interface. The
    * <code>HeaderParser</code> class produces <code>Header</code> 
    * objects this inner class hides the implementstion of the 
    * <code>Header</code> objects and also provides a quick 
    * implementation for the <code>Header</code> object.
    */    
   private class Entry implements Header{

      /**
       * This contains data about the name.
       */
      private Token name = new Token();
      
      /**
       * This contains data about the value. 
       */
      private Token value = new Token();
      
      /**
       * This is so only one creation is needed.
       */
      private Cache cache = new Cache();
      
      /**
       * Tthe "name: value" string for this header. 
       */
      private String str;
      
      /**
       * This is a handle on the message header.
       */
      private byte[] buf;
   
      /**
       * This creates an entry object that is used to represent a 
       * HTTP header. This allows a quick implementation for the 
       * <code>Header</code> as it only has to create the string 
       * objects if they are requested.
       *
       * @param name information on the bytes that from the name
       * @param value information on the bytes that form the value
       * @param buf the bytes that form the HTTP message header
       */ 
      public Entry(Token name, Token value, byte[] buf) {        
         this.name.off = name.off;
         this.name.len = name.len;        
         this.value.off = value.off;
         this.value.len = value.len;        
         this.buf = buf;
      }  
      
      /**
       * This is used to retrive the value of the <code>Header</code>
       * object. This method uses a cache so that the <code>String</code>
       * value of the name does not have to be created from the byte
       * buffer for each invocation of the <code>getName</code> method.
       *
       * @return the value that this <code>Header</code> contains
       */ 
      public String getName(){
         if(cache.name == null){   
            try{
               cache.name = createName();
            }catch(IOException e){
               cache.name = "";
            }
         }
         return cache.name;
      }  
 
      /**
       * Used to retrive the name of the <code>Header</code> object. 
       * This method uses a cache so that the <code>String</code>
       * value of the value does not have to be created from the 
       * byte buffer for each invocation of the <code>getValue</code> 
       * method.

       * @return the name that this <code>Header</code> contains
       */      
      public String getValue(){
         if(cache.val == null){
            try{
               cache.val = createValue();
            }catch(IOException e){
               cache.val = "";
            }
         }
         return cache.val;
      }

      /**
       * This returns the "name: value" <code>String</code> that 
       * corrosponds to the name and value of this <code>Header</code> 
       * exactly as it is. This does not trim whitespace nor does it 
       * normalize header values that are wrapped over several lines.
       *
       * @return the value of this <code>Header</code> as a HTTP header
       */ 
      public String toString(){
         if(str == null){
            str = getName()+ ": "+getValue();
         }  
         return str;
      }
      
      /**
       * This allows the name of the <code>Header</code> to be 
       * compared. The comparson of the strings is done in a case 
       * insensitive manner, as HTTP/1.1 standard suggests.
       *
       * @param name the <code>String</code> to compare with the 
       * name
       *
       * @return true if the name matches the <code>String</code> 
       * value given as the param
       */ 
      public boolean nameMatches(String name) {      
         if(name == null) return false;
         if(this.name.len != name.length()) {
            return false;
         }
         if(cache.name != null){
            return cache.name.equalsIgnoreCase(name);
         }
         for(int i = 0; i < this.name.len; i++) {
            byte a = toLower(buf[this.name.off + i]);            
            byte b = toLower(name.charAt(i));
            
            if(a != b) {            
               return false;
            }
         }
         return true;        
      } 

      /**
       * This will convert an ISO-8859-1 char to lowercase. 
       * This will return the byte value of the character 
       * that is converted to the lowercase ISO-8859-1 
       * character.
       *
       * @param c the character thats to be converted
       *
       * @return the lowercase version of the ISO-8859-1 char
       */ 
      private byte toLower(char c) {
         return toLower((byte)c);
      }

      /**
       * This will convert an ISO-8859-1 byte to lowercase. 
       * This will return the byte value of the octet that 
       * is converted to the lowercase ISO-8859-1 character.
       *
       * @param b the byte that is to be converted
       *
       * @return the lowercase version of the ISO-8859-1 char
       */ 
      private byte toLower(byte b) {
         if(b >= 'A' && b <= 'Z') {
            return (byte)((b - 'A') + 'a');
         }
         return b;                               
      }    
   
      /**
       * This will use the token object that represents the
       * name to create a string representation of the name.
       *
       * @throws UnsupportedEncodingException never happens
       *
       * @return header name of the <code>Header</code> object
       */ 
      private String createName() 
      throws UnsupportedEncodingException {
         return new String(buf, name.off, name.len, "iso-8859-1");
      }

      /**
       * This will use the token object that represents the
       * value to create a string representation of the value.
       *
       * @throws UnsupportedEncodingException will never happen
       *
       * @return header value of the<code>Header</code> object
       */ 
      private String createValue() 
      throws UnsupportedEncodingException {
         return new String(buf, value.off, value.len, "iso-8859-1") ;      
      }

      /**
       * This allows quick access to the name and 
       * value of this <code>Header</code> object,
       * that is, they are not consistantly created.
       */ 
      private class Cache{
         public String name;
         public String val;
      }
   }

   /**
    * This is used so that the parser can
    * remember where the tokens start and
    * where they finish within the buffer.
    */ 
   private class Token {
      public int off;
      public int len;
   }
}

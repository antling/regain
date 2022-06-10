/*
 * RequestParser.java February 2001
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
 
package simple.http;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import simple.util.ByteStore;

/**
 * This is a <code>RequestParser</code> class that can be used to 
 * parse a HTTP request line. It provides some convenience methods 
 * to extract key components of the request line. 
 * <p>
 * The <code>RequestParser</code> provides an easy way to extract
 * the various details from a HTTP request line. This provides
 * methods which enable the user to extract specific tokens such
 * as <code>getMethod</code>, which provides the request method.
 *
 * @author Niall Gallagher
 */ 
final class RequestParser {

   /**
    * This contains information on the uri. 
    */
   private Token uri = new Token();
   
   /**
    * Used to store info about the method. 
    */
   private Token method = new Token(); 
   
   /**
    * The major version number of the request. 
    */
   private int major;
   
   /**
    * The minor version number of the request.
    */
   private int minor;
   
   /**
    * A handle on the bytes of the message header. 
    */
   private byte[] buf;
   
   /**
    * The number of bytes examined count must not &gt; len. 
    */
   private int count;
   
   /**
    * The current offset within the header. 
    */
   private int off;  
   
   /**
    * The length of the region we are looking at. 
    */
   private int len;  
   
   /**
    * This creates a <code>RequestParser</code> object that can 
    * be used to parse a HTTP request line. To see the syntax of a 
    * HTTP request line consult RFC 2616. This will read only the
    * bytes up to the first LF character from the
    * <code>ByteStore</code>.
    *
    * @param buf the buffer containing the request line
    */ 
   public RequestParser(ByteStore buf) {
      this.buf = new byte[buf.length()];
      buf.getBytes(0, this.buf, 0, this.buf.length);
      len = this.buf.length;
      count = off = 0;      
      parse();
   }   

   /**
    * This can be used to get the method for this.
    *
    * @return the request method for this request
    */ 
   public String getMethod(){
      try{
         return createMethod();
      }catch(IOException e){
         return "";
      }
   }

   /**
    * This can be used to get the URI specified for
    * this request. This corrosponds to the /index
    * part of a http://www.domain.com/index URL.
    *
    * @return the uri for this request
    */ 
   public String getURI(){
      try{
         return createURI();
      }catch(IOException e){
         return "";
      }
   }  

   /**
    * This creates the string representation for the method.
    *
    * @throws UnsupportedEncodingException this never happens
    *
    * @return the string value for the HTTP request method
    */ 
   private String createMethod() 
    throws UnsupportedEncodingException {
      return new String(buf, method.off, method.len, "iso-8859-1");            
   }
   
   /**
    * This is create the string value for the request URI.
    *
    * @throws UnsupportedEncodingException this never happens
    *
    * @return the string representation of of the request uri
    */ 
   private String createURI() 
    throws UnsupportedEncodingException {
      return new String(buf, uri.off, uri.len, "iso-8859-1");  
   }

   /**
    * This can be used to get the major number from
    * a HTTP version. The major version corrosponds
    * to the major version, 1 from a HTTP/1.0 string.
    *
    * @return the major version number
    */ 
   public int getMajor() {
      return major;
   }

   /**
    * This can be used to get the minor number from
    * a HTTP version. The minor version corrosponds
    * to the minor type, 0 from a HTTP/1.0 string.
    *
    * @return the major version number
    */ 
   public int getMinor() {
      return minor;
   }      
   
   /**
    * This will parse the header from the current offset
    * and initialize the uri token with info about this uri.
    * If there is any whitespace before the uri it is removed.
    */ 
   private void uri() {
      whitespace();  
      uri.off = off;
      uri.len = 0;
      
      while(count < len){
         if(space(buf[off])){            
            break;
         }
         uri.len++;
         count++;
         off++;
      }             
   }
   
   /**
    * This will parse the header from the current offset an
    * will initialize the method token. This will remove any
    * white space that is found before the HTTP request method.
    */ 
   private void method() {
      whitespace();
      method.off = off;
      method.len = 0;
      
      while(count < len){
         if(space(buf[off])){            
            break;
         }
         method.len++;
         count++;
         off++;
      }       
   }
   
   /**
    * This will parse the header from the current offset to
    * extract the major and minor version numbers. This will
    * remove any whitespace that comes before the version.
    */ 
   private void version() {
      whitespace();
      off += 5;   /* "HTTP/" */
      count+= 5;
      major();  /* "1" */
      off++;    /* "." */
      count++;
      minor();   /* "1" */
   }
   
   /**
    * This will parse the header from the current offset and
    * convert the bytes found into an int as it parses the
    * digits it comes accross. This will cease to parse bytes
    * when it encounters a non digit byte.
    */ 
   private void major() {      
      while(count < len){
         if(!digit(buf[off])){            
            break;
         }
         major *= 10;
         major += buf[off++];
         major -= '0';
         count++;
      }        
   }

   /**
    * This will parse the header from the current offset and
    * convert the bytes found into an int as it parses the
    * digits it comes accross. This will cease to parse bytes
    * when it encounters a non digit byte.
    */ 
   private void minor() {
      while(count < len){
         if(!digit(buf[off])){            
            break;
         }
         minor *= 10;
         minor += buf[off++];                  
         minor -= '0';
         count++;
      }           
   } 

   /**
    * This will position the offset within the header to the
    * next non space character. A space character is considered
    * to be an ISO-8859-1 space or TAB character.
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
    * This is used to determine wheather or not a given
    * ISO-8859-1 character is a white space character or not.
    *
    * @param b this is to be checked to see if its a space
    *
    * @return true if the byte is a space character
    */ 
   private boolean space(byte b){
      return b == ' ' || b == '\t';
   }

   /**
    * This is used to determine if a given ISO-8859-1 byte
    * is a digit character, between an ISO-8859-1 0 and 9.
    *
    * @param b this is to be checked to see if it is a digit
    *
    * @return true if the byte is a digit character
    */ 
   private boolean digit(byte b) {
      return b >= '0' && b <= '9';
   }

   /**
    * This commences the parsing of the header. This uses the
    * functions provided to build a parse tree. This parses 
    * the method uri and version in that sequence.
    */ 
   private void parse() {            
      method();      
      uri();
      version();
   }  

   /**
    * This is used to store information
    * about a token that was taken from
    * the HTTP request line.
    */ 
   private class Token {
      public int off;
      public int len;         
   }     
}

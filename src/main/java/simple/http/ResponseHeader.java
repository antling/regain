/*
 * ResponseHeader.java February 2001
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

import simple.util.parse.DateParser;
import simple.util.parse.ListParser;

/**
 * This is a <code>ResponseHeader</code> object that is used to store
 * and retrive message header information about a given response. This
 * implements the <code>StatusLine</code> and <code>GenericHeader</code> 
 * interfaces which provide methods to access key components within a
 * HTTP message header.
 * <p>
 * The <code>StstusLine</code> interface provides specific methods for
 * dealing with the HTTP status line. The <code>StatusLine</code> also
 * contains methods for manipulating the version that the response has.
 *
 * @author Niall Gallagher
 */ 
abstract class ResponseHeader implements Response {

   /**
    * Provides quick search an retreval for HTTP headers.
    */
   private HeaderList list;

   /**
    * This is used to parse the header values of dates.
    */
   private DateParser date;
   
   /**
    * This represents the status lines description.
    */
   private String text;
   
   /**
    * This represents the ststus code of the header.
    */
   private int code;
   
   /**
    * This represents the major version number.
    */
   private int major;   
   
   /**
    * This represents the minor version number.
    */
   private int minor;     

   /**
    * This creates a <code>ResponseHeader</code> object that 
    * can be used manipulate a HTTP <code>ResponseHeader</code>.
    */ 
   protected ResponseHeader(){    
      list = new HeaderList();
      date = new DateParser();
      major = minor = 1;
      text = "OK";
      code = 200;
   }
   
   /**
    * This represents the status code of the HTTP response. 
    * The response code represents the type of message that is
    * being sent to the client. For a description of the codes
    * see RFC 2616 section 10, Status Code Definitions. 
    *
    * @return the status code that this HTTP response has
    */ 
   public int getCode(){      
      return code;
   }  
     
   /**
    * This method allows the status for the response to be 
    * changed. This MUST be reflected the the response content
    * given to the client. For a description of the codes see
    * RFC 2616 section 10, Status Code Definitions.
    *
    * @param code the new status code for the HTTP response
    */ 
   public void setCode(int code){
      this.code = code;
   }

   /**
    * This can be used to retrive the text of a HTTP status
    * line. This is the text description for the status code.
    * This should match the status code specified by the RFC.
    *
    * @return the message description of the response
    */ 
   public String getText(){
      return text;
   } 

   /**
    * This is used to set the text of the HTTP status line.
    * This should match the status code specified by the RFC.
    *
    * @param text the descriptive text message of the status
    */ 
   public void setText(String text){
      this.text = text;
   }

   /**
    * This can be used to get the major number from a HTTP
    * version. The major version corrosponds to the major 
    * type that is the 1 of a HTTP/1.0 version string.
    *
    * @return the major version number for the response
    */ 
   public int getMajor(){
      return major;
   }

   /**
    * This can be used to specify the major version. This
    * should be the major version of the HTTP request.
    *
    * @param major this is the major number desired
    */ 
   public void setMajor(int major){
      this.major = major;
   }

   /**
    * This can be used to get the minor number from a HTTP
    * version. The major version corrosponds to the minor
    * type that is the 0 of a HTTP/1.0 version string.
    *
    * @return the major version number for the response
    */ 
   public int getMinor(){
      return minor;
   }
   
   /**
    * This can be used to specify the minor version. This
    * should not be set to zero if the HTTP request was 
    * for HTTP/1.1. The response must be equal or higher.
    *
    * @param minor this is the minor number desired
    */ 
   public void setMinor(int minor){
      this.minor = minor;
   }   

   /**
    * This can be used to determine how many HTTP message headers
    * this object contains. The <code>headerCount</code> represents 
    * the number of individual HTTP message headers that this has.
    *
    * @return returns the number of HTTP message headers this has
    */  
   public int headerCount() {
      return list.headerCount();
   }
   
   /**
    * This can be used to get the value of the HTTP message header 
    * at the specified index. This is a convinence method that 
    * avoids having to deal with a HTTP message header object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the HTTP message header value to be
    * returned
    *
    * @return this returns the value that the HTTP message header
    */    
   public String getValue(int off) {
      return list.getValue(off);
   }

   /**
    * This is used to get the name value of the HTTP message header
    * at the specified index. This is used in conjunction with the
    * <code>getValue(int)</code> method so that the contents of the 
    * HTTP message header can be fully examined.
    *
    * @param off the offset of the HTTP message header name value 
    *
    * @return this returns the name of the header at that index 
    */
   public String getName(int off) {
      return list.getName(off);
   }

   /**
    * This can be used to find the first occurence of the specified 
    * HTTP message header. This will search through the list of 
    * HTTP message headers that this contains and when it encounters 
    * a HTTP message header with the name specified it returns the 
    * index of that HTTP message header. The index will change when 
    * a remove is used. So the index is valid only for the until the 
    * next remove method or possible the next add method.
    *
    * @param name name of the HTTP message header being searched for
    *
    * @return returns the position of the first HTTP message header
    */     
   public int indexOf(String name){
      return list.indexOf(name);
   }
   
   /**
    * This can be used to find the first occurence of the specified 
    * HTTP message header from a given index. This will search through 
    * the list of HTTP message headers that occur after the index. 
    * When it encounters a HTTP message header with the name specified 
    * it returns the index of that HTTP message header. The index will 
    * change when a remove is used. So the index is valid only until a 
    * remove or add method is used.
    *
    * @param name name of the HTTP message header being searched for
    * @param from the index from which the search will start
    *
    * @return this returns the position of the HTTP message header
    */    
   public int indexOf(String name, int from) {
      return list.indexOf(name, from);
   }
   
   /**
    * This can be used to add a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrived using
    * the <code>indexOf</code> in combination with the get methods.
    *
    * @param name the name of the HTTP message header to be added
    * @param val the value the HTTP message header will have
    */      
   public void add(String name, String val){
      list.add(new PlainHeader(name, val));
   }
   
   /**
    * This can be used to set a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrived using
    * the <code>indexOf</code> in combination with the get methods.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.    
    *
    * @param name the name of the HTTP message header to be added
    * @param val the value the HTTP message header will have
    */    
   public void set(String name, String val){
      removeAll(name);
      add(name, val);
   }
   
   /**
    * This can be used to add a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrived using
    * the <code>indexOf</code> in combination with the get methods.
    *
    * @param name the name of the HTTP message header to be added
    * @param val the value the HTTP message header will have
    */    
   public void add(String name, int val){
      add(name, String.valueOf(val));
   }
   
   /**
    * This can be used to set a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrived using
    * the <code>indexOf</code> in combination with the get methods.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.       
    *
    * @param name the name of the HTTP message header to be added
    * @param val the value the HTTP message header will have
    */ 
   public void set(String name, int val){
      removeAll(name);
      add(name, val);
   }
   
   
   /**
    * This is used as a convienence method for adding a header that
    * needs to be parsed into a HTTP-date string. This will convert
    * the date given into a date string defined in RFC 2616 sec 3.3.1.
    *
    * @param name the name of the HTTP message header to be added
    * @param time the value the HTTP message header will have when
    * parsed into RFC 1123 format
    */   
   public void addDate(String name, long time){
      date.parse(time);
      add(name, date.toString());
   }
   
   /**
    * This is used as a convienence method for setting a header that
    * needs to be parsed into a HTTP-date string. This will convert
    * the date given into a date string defined in RFC 2616 sec 3.3.1.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.
    *
    * @param name the name of the HTTP message header to be added
    * @param time the value the HTTP message header will have when
    * parsed into RFC 1123 format
    */
   public void setDate(String name, long time){
      removeAll(name);
      addDate(name, time);
   }

   /**
    * This can be used to remove the HTTP message header at the
    * specified index. This will invalidate any value recived by 
    * an <code>indexOf</code> method previous to this. If the 
    * index specified is not valid then an 
    * <code>IndexOutOfBoundsException</code> may be thrown.
    *
    * @param off index of the HTTP message header to be removed
    */  
   public void remove(int off){
      list.remove(off);
   }
   
   /**
    * This can be used to remove all HTTP message headers with 
    * the specified name. This will search through the list of 
    * HTTP message header an remove the HTTP message headers
    * from the list. This will invalidate any previous indexes
    * recived.
    *
    * @param name name of the HTTP message headers to be removed
    */    
   public void removeAll(String name){
      list.removeAll(name);
   }

   /**
    * This can be used to get the value of the first HTTP message header
    * that has the specified name. This is a convinence method that 
    * avoids having to deal with a HTTP message header object and
    * the <code>indexOf</code> methods. this returns null if theres
    * not HTTP message header.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns the value that the HTTP message header
    */ 
   public String getValue(String name) {
      return list.getValue(name);
   }
   
   /**
    * This can be used to get the values of HTTP message headers
    * that have the specified name. This is a convenience method that 
    * will present that values as tokens extracted from the header.
    * This has obvious performance benifits as it avoids having to 
    * deal with <code>substring</code> and <code>trim</code> calls.
    * <p>
    * The tokens returned by this method are ordered according to
    * there HTTP quality values, or "q" values, see RFC 2616 section
    * 3.9. This also strips out the quality parameter from tokens
    * returned. So "image/html; q=0.9" results in "image/html". If
    * there are no "q" values present then order is preserved. 
    *
    * @param name the name of the headers that are to be retrieved
    *
    * @return ordered array of tokens extracted from the header(s)
    */
   public String[] getValues(String name){
      return getValues(list.getValues(name));
   }

   /**
    * This method will parse the given list of HTTP header values
    * into usable tokens. Although the conversion from the complete
    * HTTP header values to descrete tokens is costly the overall
    * benifit to performance is obvious. 
    * <p>
    * Rather that calling the <code>getValue(String)</code> method
    * and parsing a token with the <code>String.substring</code> 
    * or <code>String.trim</code> methods the tokens can be used
    * immediately, this avoids errors and overheads.
    *
    * @param list this is the list of HTTP header values to parse
    *
    * @return this wll return an ordered list of extracted tokens
    */
   private String[] getValues(String[] list) {
      return new ListParser(list).list();
   }
  
   /**
    * This can be used to get the value of the HTTP message header 
    * at the specified index. This is a convinence method that 
    * avoids having to deal with a HTTP message header object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the HTTP message header value to be
    * returned
    *
    * @return this returns the date as a long from the parsed value 
    * of that HTTP message header
    */
   public long getDate(int off) {
      String value = getValue(off);
      if(value == null) {
         return -1;
      }
      date.parse(value);
      return date.toLong();
   }
   
   /**
    * This can be used to get the date of the first HTTP message header
    * that has the specified name. This is a convinence method that 
    * avoids having to deal with parsing the value of the requested
    * HTTP message header. This also avoids having to deal with the
    * <code>indexOf</code> methods. This returns -1 if theres not a
    * HTTP message header.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns the date as a long from the parsed value 
    * of that HTTP message header
    */ 
   public long getDate(String name){   
      String value = getValue(name);
      if(value == null) {
         return -1;
      }
      date.parse(value);
      return date.toLong();
   }

   /**
    * This is used to see if there is a HTTP message header with the
    * given name in this container. If there is a HTTP message header
    * with the specified name then this returns true otherwize false.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns true if the HTTP message header exists
    */ 
   public boolean contains(String name) {
      return !(indexOf(name) < 0);
   }

   /**
    * This is used to clear all HTTP message headers from the 
    * message header. This will leave no data remaining, i.e.
    * <code>headerCount</code> is zero after this method is 
    * invoked, this is a convienence method.
    */
   public void clear(){
      while(headerCount()>0){
         remove(0);
      }
   }

   /** 
    * This constructs the HTTP message header according to the 
    * format of RFC 2616. This returns a <code>String</code> that 
    * contains each header formatted according to the HTTP/1.1 
    * header format. The header will contain the status line 
    * followed by each header and ended with the CRLF.
    * <p>
    * It is important that this perform the header generation
    * quickly. This is used by the <code>MonitoredResponse</code>
    * to output the HTTP mesage header and thus this becomes a
    * criticial performance point.
    *
    * @return the HTTP response header as a string object
    */   
   public String toString() {
      int size = headerCount()*50;
      StringBuffer buf =new StringBuffer(size); 
      buf.append("HTTP/");
      buf.append(major).append(".");
      buf.append(minor).append(" ");
      buf.append(code).append(" ");
      buf.append(text).append("\r\n");
      for(int i = 0,len = headerCount(); i< len;i++){
         Header head = list.get(i);         
         buf.append(head.getName());
         buf.append(": ");
         buf.append(head.getValue());
         buf.append("\r\n");
      }
      buf.append("\r\n");
      return buf.toString();
   }   
}    

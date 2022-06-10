/*
 * RequestHeader.java February 2001
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
import simple.util.ByteStore;

/**
 * This is a <code>RequestHeader</code> object that can be used to
 * manipulate data about a HTTP request header. This provides some 
 * convinence methods that allow the HTTP request to be manipulated 
 * easily.
 * <p>
 * This implements the <code>RequestLine</code> interface which 
 * provides methods specific to the HTTP request line header.
 * The <code>RequestLine</code> also contains information on the
 * HTTP version used with this request.
 *
 * @author Niall Gallagher
 */ 
abstract class RequestHeader implements Request {

   /**
    * This is used to parse the message header.
    */
   private HeaderParser parser;
   
   /**
    * This is used to parse the request line.
    */
   private RequestParser req;  

   /**
    * This is used to parse any date headers.
    */
   private DateParser date;
   
   /**
    * Provides a quick search and retrevial.
    */
   private HeaderList list;
   
   /**
    * The HTTP method of this request header.
    */
   private String method;
   
   /**
    * The uri that this request refers to.
    */
   private String uri;
   
   /**
    * The major version number of this request.
    */
   private int major = -1;   
   
   /**
    * The minor version number of this request.
    */
   private int minor = -1;  

   /**
    * This creates a <code>RequestHeader</code> object that can be
    * used to manipulate data of a HTTP <code>RequestHeader</code>.
    * The buffer that is used contains the bytes that form the 
    * HTTP <code>RequestHeader</code>. 
    *
    * @param buf the <code>RequestHeader</code> bytes.
    */ 
   protected RequestHeader(ByteStore buf) {
      req = new RequestParser(buf);
      parser = new HeaderParser(buf);
      list = new HeaderList();
      date = new DateParser();
      initialize();
   }
   
   /**
    * This is used to initialize the object by parsing the HTTP
    * message header and building the <code>HeaderList</code>.
    * This ensures that the the headers can be accessed. 
    */ 
   private void initialize() {
      while(parser.hasMore()){
         list.add(parser.next());            
      }
   }
   /**
    * This can be used to get the URI specified for this HTTP
    * request. This corrosponds to the /index part of a 
    * http://www.domain.com/index URL but may contain the full
    * URL. This can be set using <code>setURI</code>.
    *
    * @return the URI that this HTTP request is targeting
    */ 
   public String getURI() {      
      if(uri != null) {
         return uri;      
      }
      uri = req.getURI();
      return uri;
   }  

   /**
    * This can be used to set the URI for this HTTP request.
    * The <code>getURI</code> will return the String entered
    * which can be a full HTTP URL or a relative path URL.
    *
    * @param uri the URI that this HTTP request is to use
    */  
   public void setURI(String uri){
      this.uri = uri;
   }

   /**
    * This can be used to get the HTTP method for this
    * request. The HTTP specification RFC 2616 specifies the
    * HTTP request methods in section 9, Method Definitions.
    *
    * @return the request method for this request
    */ 
   public String getMethod(){
      if(method != null) {
         return method;      
      }
      method = req.getMethod();
      return method;
   }    

   /**
    * This is used to set the method for this HTTP request
    * object. For a list of possible string values that
    * can be used see RFC 2616 section 9, Method Definitions.
    *
    * @param method the desired method for this
    */ 
   public void setMethod(String method){
      this.method = method;
   }

   /**
    * This can be used to get the major number from a HTTP 
    * version. The major version corrosponds to the major 
    * type that is the 1 of a HTTP/1.0 version string.
    *
    * @return the major version number for the request
    */ 
   public int getMajor(){
      if(major != -1){
         return major;
      }
      major = req.getMajor();
      return major;
   }       

   /**
    * This can be used to specify the major version for the
    * HTTP request. Specifying the major version has little
    * effect on the semantics of the request.
    *
    * @param major this is the major number desired
    */ 
   public void setMajor(int major){
      this.major = major;
   }

   /**
    * This can be used to get the major number from a HTTP
    * version. The major version corrosponds to the major 
    * type that is the 0 of a HTTP/1.0 version string.
    *
    * @return the major version number
    */ 
   public int getMinor(){
      if(minor != -1){
         return minor;
      }
      minor = req.getMinor();
      return minor;
   }

   /**
    * This can be used to specify the minor version for the
    * HTTP request. Specifying the minor version will effect
    * the manner in which the request is processed.
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
   public int headerCount(){
      return list.headerCount();
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
   public void add(String name, String val) {
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
   public int indexOf(String name, int from){
      return list.indexOf(name, from);
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
    * will present the values as tokens extracted from the header.
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
   public void clear() {
      while(headerCount()>0){
         remove(0);               
      }
   }

   /** 
    * This method is used so that the original HTTP header can 
    * be reconstructed This returns a <code>String</code> that 
    * contains each header formatted according to the HTTP/1.1 
    * header format. The header will contain the request line 
    * followed by each header and ended with the CRLF.
    *
    * @return the HTTP request header as a sring object
    */
   public String toString() {
      int size = headerCount()*50;
      StringBuffer buf =new StringBuffer(size);      
      buf.append(getMethod());
      buf.append(" ").append(getURI());
      buf.append(" HTTP/");
      buf.append(getMajor()).append(".");
      buf.append(getMinor()).append("\r\n");
      for(int i = 0,len =headerCount(); i< len; i++){
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

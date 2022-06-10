/*
 * FilterRequest.java February 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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

import simple.http.session.Session;
import simple.util.net.Parameters;
import simple.util.net.Principal;
import simple.util.net.MimeType;
import simple.util.net.Cookie;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;

/** 
 * The <code>FilterRequest</code> object is used so that the original
 * <code>Request</code> object can be wrapped in a synchronized proxy
 * object. This allows a <code>ProtocolHandler</code> that interacts 
 * with the <code>Request</code> object in a concurrent environment.
 * This is also useful if any special behavior is need when a header 
 * with special significance is added.
 *
 * @author Niall Gallagher
 */
public class FilterRequest implements Request { 

   /**
    * This is the object that is being wrapped.
    */
   protected Request req;          

   /** 
    * Constructor for <code>FilterRequest</code> allows the original
    * <code>Request</code> object to be wrapped so that adjustments
    * to the behavior of a <code>Request</code> object handed to a
    * specialized <code>ProtocolHandler</code> can be done simply.
    *
    * @param req the <code>Request</code> object being wrapped
    */
   public FilterRequest(Request req){
      this.req = req;
   }
   
   /**
    * This can be used to get the URI specified for this HTTP
    * request. This corresponds to the /index part of a 
    * http://www.domain.com/index URL but may contain the full
    * URL. This can be set using <code>setURI</code>.
    *
    * @return the URI that this HTTP request is targeting
    */ 
   public synchronized String getURI(){
      return req.getURI();
   }

   /**
    * This can be used to set the URI for this HTTP request.
    * The <code>getURI</code> will return the String entered
    * which can be a full HTTP URL or a relative path URL.
    *
    * @param uri the URI that this HTTP request is to use
    */  
   public synchronized void setURI(String uri){
      req.setURI(uri);
   }

   /**
    * This can be used to get the HTTP method for this
    * request. The HTTP specification RFC 2616 specifies the
    * HTTP request methods in section 9, Method Definitions.
    *
    * @return the request method for this request
    */ 
   public synchronized String getMethod(){
      return req.getMethod();
   }

   /**
    * This is used to set the method for this HTTP request object.
    * For a list of possible string values that can be used see 
    * RFC 2616 section 9, Method Definitions.
    *
    * @param method the desired method for this
    */ 
   public synchronized void setMethod(String method){
      req.setMethod(method);
   }

   /**
    * This can be used to get the major number from a HTTP version.
    * The major version corresponds to the major type that is the 
    * 1 of a HTTP/1.0 version string.
    *
    * @return the major version number for the request
    */ 
   public synchronized int getMajor(){
      return req.getMajor();
   }

   /**
    * This can be used to specify the major version for the HTTP 
    * request. Specifying the major version has little effect on 
    *the semantics of the request.
    *
    * @param major this is the major number desired
    */ 
   public synchronized void setMajor(int major){
      req.setMajor(major);
   }

   /**
    * This can be used to get the major number from a HTTP version.
    * The major version corresponds to the major type that is the 
    * 0 of a HTTP/1.0 version string.
    *
    * @return the major version number
    */ 
   public synchronized int getMinor(){
      return req.getMinor();
   }

   /**
    * This can be used to specify the minor version for the HTTP 
    * request. Specifying the minor version will effect the manner
    * in which the request is processed.
    *
    * @param minor this is the minor number desired
    */ 
   public synchronized void setMinor(int minor){
      req.setMinor(minor);
   }

   /**
    * This can be used to determine how many HTTP message headers
    * this object contains. The <code>headerCount</code> represents 
    * the number of individual HTTP message headers that this has.
    *
    * @return returns the number of HTTP message headers this has
    */
   public synchronized int headerCount(){
      return req.headerCount();
   }

   /**
    * This can be used to find the first occurrence of the specified 
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
   public synchronized int indexOf(String name){
      return req.indexOf(name);
   }

   /**
    * This can be used to find the first occurrence of the specified 
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
   public synchronized int indexOf(String name, int from){
      return req.indexOf(name,from);
   }

   /**
    * This can be used to add a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */ 
   public synchronized void add(String name, String value){   
      req.add(name, value);
   }

   /**
    * This can be used to set a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.    
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */  
   public synchronized void set(String name, String value){
      req.set(name, value);
   }

   /**
    * This can be used to add a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */     
   public synchronized void add(String name, int value){
      req.add(name, String.valueOf(value));
   }  

   /**
    * This can be used to set a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.       
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */ 
   public synchronized void set(String name, int value){
      req.set(name, String.valueOf(value));
   }

   /**
    * This is used as a convenience method for adding a header that
    * needs to be parsed into a HTTP-date string. This will convert
    * the date given into a date string defined in RFC 2616 sec 3.3.1.
    *
    * @param name the name of the HTTP message header to be added
    * @param date the value the HTTP message header will have when
    * parsed into RFC 1123 format
    */   
   public synchronized void addDate(String name, long date){
      req.addDate(name, date);
   }

   /**
    * This is used as a convenience method for setting a header that
    * needs to be parsed into a HTTP-date string. This will convert
    * the date given into a date string defined in RFC 2616 sec 3.3.1.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.
    *
    * @param name the name of the HTTP message header to be added
    * @param date the value the HTTP message header will have when
    * parsed into RFC 1123 format
    */
   public synchronized void setDate(String name, long date){
      req.setDate(name, date);
   }

   /**
    * This can be used to remove the HTTP message header at the
    * specified index. This will invalidate any value received by 
    * an <code>indexOf</code> method previous to this. If the 
    * index specified is not valid then an 
    * <code>IndexOutOfBoundsException</code> may be thrown.
    *
    * @param off index of the HTTP message header to be removed
    */    
   public synchronized void remove(int off){
      req.remove(off);
   }

   /**
    * This can be used to remove all HTTP message headers with the
    * specified name. This will search through the list of HTTP 
    * message header an remove the HTTP message headers from the 
    * list. This will invalidate any previous indexes received.
    *
    * @param name name of the message headers to be removed
    */    
   public synchronized void removeAll(String name){
      req.removeAll(name);
   }
   
   /**
    * This is used to get the date value of the HTTP message header
    * at the specified index. This is a convenience method that 
    * avoids having to deal with a HTTP message header object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the HTTP message header value 
    *
    * @return this returns the date value value that the header
    */    
   public synchronized String getValue(int off){
      return req.getValue(off);
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
   public synchronized String getName(int off) {
      return req.getName(off);
   }

   /**
    * This can be used to get the value of the HTTP message header 
    * at the specified index. This is a convenience method that 
    * avoids having to deal with a HTTP message header object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the date HTTP message header value 
    *
    * @return this returns the date as a long from the parsed value 
    * of that HTTP message header
    */
   public synchronized long getDate(int off){
      return req.getDate(off);
   }

   /**
    * This can be used to get the value of the first message header
    * that has the specified name. This is a convenience method that 
    * avoids having to deal with a HTTP message header object and
    * the <code>indexOf</code> methods. This returns null if theres
    * not a HTTP message header.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns the value that the HTTP message header
    */
   public synchronized String getValue(String name){
      return req.getValue(name);
   }

   /**
    * This can be used to get the values of HTTP message headers
    * that have the specified name. This is a convenience method that 
    * avoids having to deal with the <code>indexOf</code> methods.
    * This will return an empty array if there are no headers of
    * the specified name within this list.
    *
    * @param name the name of the headers that are to be retrieved
    *
    * @return this returns and array of values that correspond to
    * the headers in the list
    */  
   public String[] getValues(String name){
      return req.getValues(name);
   }

   /**
    * This can be used to get the date of the first message header
    * that has the specified name. This is a convenience method that 
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
   public synchronized long getDate(String name){
      return req.getDate(name);
   }

   /**
    * This is used to read the content body. The specifics of the data
    * that is read from this <code>InputStream</code> can be determined
    * by the <code>getContentLength</code> method. If the data sent by
    * the client is chunked then it is decoded, any footers sent
    * are ignored as they provide optional meta-data, see RFC 2616
    * section 3.6. The <code>InputStream</code> must be synchronized 
    * so that multiple threads can use the stream.
    *
    * @exception IOException thrown if there is an I/O error
    *
    * @return an <code>InputStream</code> containing the bytes
    */
   public synchronized InputStream getInputStream() throws IOException{
      return req.getInputStream();
   }   

   /**
    * This provides access to HTML form and query parameters. This is
    * used to provide access to parameters by reading the parameters
    * from the <code>InputStream</code>. If the data cannot be read
    * from the stream then an <code>IOException</code> is thrown.
    * If the data on the stream is not of the correct MIME type then
    * an empty parameters object is returned. The MIME type for the 
    * parameters is <code>application/x-www-form-urlenoded</code>.
    * 
    * @return this returns all HTML form and query parameter data 
    *
    * @exception IOException thrown if there is an I/O problem   
    */
   public Parameters getParameters() throws IOException{
      return req.getParameters();
   }

   /**
    * This is used to provide quick access to the parameters. This
    * avoids having to acquire the <code>Parameters</code> object.
    * This basically acquires the parameters object and invokes 
    * the <code>getParameters</code> method with the given name.
    * 
    * @param name this is the name of the parameter value
    *
    * @exception IOException thrown if there is an I/O problem       
    */
   public String getParameter(String name) throws IOException{
      return req.getParameter(name);
   }

   /**
    * This is used to acquire the Content-Type for any HTTP message
    * body that may be sent with the request. If the header is not
    * sent with the HTTP message header this returns null. This
    * should be used with a <code>contains</code> invocation to see
    * if the Content-Type header is in the HTTP header. The MIME
    * type is returned is a read-only <code>MimeType</code> object.
    *
    * @return this returns the Content-Type value if it exists
    */
   public MimeType getMimeType(){
      return req.getMimeType();
   }

   /** 
    * This provides <code>Locale</code> for the Accept-Language value.
    * This returns the first language preference from the header. If
    * the set of values are required the <code>LanguageParser</code>
    * from the <code>simple.util.parse</code> package can be used.
    * If the header does not exist this will return the default
    * <code>Locale</code> from <code>Local.getDefault</code>.
    * 
    * @return the <code>Locale</code> preference of the client
    */
   public Locale getLanguage(){
      return req.getLanguage();
   }

   /**
    * This method can be used to retrieve cookies from the header.
    * This is a convenience method that avoids having to parse the 
    * Cookie header values. This will basically call the
    * <code>getProfile</code> method and from the <code>Profile</code>
    * invoke <code>get</code> using the specified name.
    *
    * @param name this is the name of the cookie to be retrieved
    *
    * @return returns a <code>simple.util.net.Cookie</code> object 
    * to to represent the cookie of that name within the header
    */
   public synchronized Cookie getCookie(String name){
      return req.getCookie(name);
   }

   /**
    * The <code>Profile</code> represents the collection of cookies
    * sent with this HTTP request. This represents a profile 
    * established between the server and client using either domain 
    * of path values see RFC 2109, HTTP State Management Mechanism.
    * <p>
    * The profile is shared with the <code>Response</code> which 
    * will write each cookie set in the profile as a Set-Cookie value 
    * once the response has committed. This enables simple
    * management of state with the browser by using the profile. 
    * 
    * @return this returns a <code>Profile</code> which represents
    * the state maintained with the client application
    */
   public synchronized Profile getProfile(){
      return req.getProfile();
   }

   /**
    * This is a convenience method that is used to retrieve the
    * client authorized to this server. This is used to that the HTTP
    * Basic authorization scheme could be used. This will check to 
    * see if the Authorization header is present, if it is then this    
    * will return a <code>simple.util.net.Principal</code> that
    * identifies the client without having to decode the header.
    *
    * @return this returns the authorized client as retrieved from
    * the HTTP Authorization header
    */
   public synchronized Principal getPrincipal(){
      return req.getPrincipal();
   }
   
   /**
    * This can be used to retrieve certain attributes about this 
    * <code>Request</code>. The <code>Attributes</code> contains 
    * certain properties about the <code>Request</code>. For 
    * example if this <code>Request</code> came over a secure 
    * line then there may be security attributes.
    *
    * @return returns attributes of this <code>Request</code>
    * object
    */ 
   public synchronized Attributes getAttributes() {
      return req.getAttributes();
   }

   /**
    * This method is used to acquire a <code>Session</code> for the
    * request. The object retrieved provides a container for data
    * associated to the connected client. This allows the request
    * to perform more complex operations based on knowledge that is
    * built up through a series of requests. The session is known
    * to the system using a <code>Cookie</code>, which contains
    * the session reference. This cookie value should not be 
    * modified as it used to reference the active session object.
    *
    * @return returns an active <code>Session</code> object
    */
   public synchronized Session getSession(){
      return req.getSession();
   }

   /**
    * Used to get the address from which this <code>Request</code> came 
    * from. This information can be retrieved from <code>Attributes</code>
    * object using the <code>getAttributes</code> method. This can be
    * used to log HTTP requests for the server. This can also provide 
    * security, where requests from specific locations are denied.
    *
    * @return the I.P. address this <code>Request</code> came from
    */ 
   public synchronized InetAddress getInetAddress(){
      return req.getInetAddress();
   }
   
   /**
    * This is a convenience method that is used to determine whether 
    * or not this message has the <code>Connection: close</code> 
    * header. If the close token is present then this stream is not
    * a keep-alive connection. If this has no <code>Connection</code> 
    * header then the keep-alive status is determined by the HTTP
    * version, that is, HTTP/1.1 is keep-alive by default, HTTP/1.0
    * is not keep-alive by default.
    *
    * @return returns true if this has a keep-alive stream
    */ 
   public boolean isKeepAlive(){
      return req.isKeepAlive();
   }
   
   /**
    * This is a convenience method that can be used to determine
    * the length of the message body. This will determine if there
    * is a <code>Content-Length</code> header, if it does then the
    * length can be determined. This will return a -1 if there is 
    * no length.
    *
    * @return returns -1 if the length cannot be determined
    */ 
   public synchronized int getContentLength(){
      return req.getContentLength();
   }

   /**
    * This is used to see if there is a HTTP message header with the
    * given name in this container. If there is a HTTP message header
    * with the specified name then this returns true otherwise false.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns true if the HTTP message header exists
    */    
   public synchronized boolean contains(String name){
      return req.contains(name);
   }

   /**
    * This is used to clear all HTTP message headers from the 
    * message header. This will leave no data remaining, i.e.
    * <code>headerCount</code> is zero after this method is 
    * invoked, this is a convenience method.
    */
   public synchronized void clear(){
      req.clear();      
   }

   /** 
    * This method is used so that the original HTTP header can 
    * be reconstructed This returns a <code>String</code> that 
    * contains each header formatted according to the HTTP/1.1 
    * header format. The header will contain the request line 
    * followed by each header and ended with the CRLF.
    *
    * @return the HTTP request header as a string object
    */
   public synchronized String toString(){
      return req.toString();
   }
}

/*
 * FilterResponse.java February 2002
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

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetAddress;

/** 
 * The <code>FilterResponse</code> object is used so that the original
 * <code>Response</code> object can be wrapped in a synchronized proxy
 * object. This allows a <code>ProtocolHandler</code> to interact with
 * the <code>Response</code> object in a concurrent environment. This 
 * is also useful if any special behavior is need when a HTTP header 
 * with special significance is added.
 * <code><pre>
 *
 *    public void handle(Request req, Response resp) {
 *       handler.handle(req, new ZipResponse(resp));
 *    }
 *
 * </pre></code>
 * The above is an example of how the <code>FilterResponse</code> can 
 * be used to provide extra functionality to a <code>Response</code>
 * in a transparent manner. Such an implementation could apply a
 * Content-Encoding header and compress the response for performance
 * over a slow network. Filtering can be applied with the use of
 * layered <code>ProtocolHandler</code> objects, an example of such
 * layering is the <code>simple.http.serve.DomainHandler</code>.
 *
 * @author Niall Gallagher
 */
public class FilterResponse implements Response { 

   /**
    * This is the object that is being wrapped.
    */
   protected Response resp;          

   /** 
    * Constructor for <code>FilterResponse</code> allows the original
    * <code>Response</code> object to be wrapped so that adjustments
    * to the behavior of a <code>Response</code> object handed to a
    * specialized <code>ProtocolHandler</code> can be done simply.
    *
    * @param resp the <code>Response</code> object being wrapped
    */
   public FilterResponse(Response resp){
      this.resp = resp;
   }

   /**
    * This represents the status code of the HTTP response. The HTTP
    * response code represents the type of message that is being sent
    * to the client. For a description of the codes see RFC 2616 
    * section 10, Status Code Definitions. 
    *
    * @return the status code that this HTTP response has
    */ 
   public synchronized int getCode(){
      return resp.getCode();
   }
     
   /**
    * This method allows the status for the response to be changed. 
    * This must be reflected the the response content given to the 
    * client. For a description of the codes see RFC 2616 section 
    * 10, Status Code Definitions.
    *
    * @param code the new status code for the HTTP response
    */ 
   public synchronized void setCode(int code){
      resp.setCode(code);
   }

   /**
    * This can be used to retrieve the text of a HTTP status line. 
    * This is the text description for the status code. This should
    * match the status code specified by the RFC.
    *
    * @return the message description of the response
    */ 
   public synchronized String getText(){
      return resp.getText();
   }

   /**
    * This is used to set the text of the HTTP status line. This 
    * should match the status code specified by the RFC.
    *
    * @param text the descriptive text message of the status
    */ 
   public synchronized void setText(String text){
      resp.setText(text);
   }

   /**
    * This can be used to get the major number from a HTTP version.
    * The major version corresponds to the major type that is the 
    * 1 of a HTTP/1.0 version string.
    *
    * @return the major version number for the response
    */ 
   public synchronized int getMajor(){
      return resp.getMajor();
   }

   /**
    * This can be used to specify the major version. This should 
    * be the major version of the HTTP request.
    *
    * @param major this is the major number desired
    */ 
   public synchronized void setMajor(int major){
      resp.setMajor(major);
   }

   /**
    * This can be used to get the minor number from a HTTP version. 
    * The major version corresponds to the minor type that is the 
    * 0 of a HTTP/1.0 version string.
    *
    * @return the major version number for the response
    */ 
   public synchronized int getMinor(){
      return resp.getMinor();
   }
   
   /**
    * This can be used to specify the minor version. This should 
    * not be set to zero if the HTTP request was for HTTP/1.1. The
    * response must be equal or higher.
    *
    * @param minor this is the minor number desired
    */ 
   public synchronized void setMinor(int minor){
      resp.setMinor(minor);
   }

   /**
    * This can be used to determine how many HTTP message headers
    * this object contains. The <code>headerCount</code> represents 
    * the number of individual HTTP message headers that this has.
    *
    * @return returns the number of HTTP message headers this has
    */
   public synchronized int headerCount(){
      return resp.headerCount();
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
      return resp.indexOf(name);
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
      return resp.indexOf(name,from);
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
      resp.add(name, value);
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
      resp.set(name, value);
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
      resp.add(name, String.valueOf(value));
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
      resp.set(name, value);
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
      resp.addDate(name, date);
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
      resp.setDate(name, date);
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
      resp.remove(off);
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
      resp.removeAll(name);
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
      return resp.getValue(off);
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
      return resp.getName(off);
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
      return resp.getValues(name);
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
      return resp.getDate(off);
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
      return resp.getValue(name);
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
      return resp.getDate(name);
   }

   /**
    * This should be used when the size of the message body is known. For 
    * performance reasons this should be used is the length of the output
    * is known. This ensures that Persistent HTTP (PHTTP) connections 
    * can be maintained for both HTTP/1.0 and HTTP/1.1 clients. If the
    * length of the output is not known HTTP/1.0 clients will require a
    * connection close, which reduces performance (see RFC 2616).
    * <p>
    * This removes any previous Content-Length headers from the message 
    * header. This will then set the appropriate Content-Length header with
    * the correct length. If a the Connection method is set with the close
    * token then the semantics of the connection are such that the server
    * will close it once the <code>OutputStream.close</code> is used.
    *
    * @param length this is the length of the HTTP message body
    */
   public synchronized void setContentLength(int length){
      resp.setContentLength(length);
   }
   
   /**
    * This can be used to retrieve certain attributes about
    * this <code>Response</code>. The attributes contains certain
    * properties about the <code>Response</code>. For example if
    * this response goes over a secure line then there may be any
    * arbitrary attributes.
    *
    * @return the attributes of this <code>Response</code> object
    */ 
   public synchronized Attributes getAttributes(){
      return resp.getAttributes();
   }

   /**
    * This can be used to get the I.P address for the browser that
    * the <code>Response</code> goes to. The <code>Attributes</code> 
    * shares this information. This method is used to that objects
    * can determine, based on the retrieved I.P address, what type
    * of output is suitable. Statistics such as location can be 
    * determined based on the DNS address obtained.
    *
    * @return returns the source I.P. address of the client
    */ 
   public synchronized InetAddress getInetAddress(){
      return resp.getInetAddress();
   }

   /**
    * Used to write a message body with the <code>Response</code>. The 
    * semantics of this <code>OutputStream</code> will be determined 
    * by the HTTP version of the client, and whether or not the content
    * length has been set, through the <code>setContentLength</code>
    * method. If the length of the output is not known then the output
    * is chunked for HTTP/1.1 clients and closed for HTTP/1.0 clients.
    * The <code>OutputStream</code> issued must be thread safe so that 
    * it can be used in a concurrent environment.
    *
    * @exception IOException this is thrown if there was an I/O error
    *
    * @return an <code>OutputStream</code> with the specified semantics
    */ 
   public synchronized OutputStream getOutputStream() throws IOException{   
      return resp.getOutputStream();
   }

   /**
    * Used to write a message body with the <code>Response</code>. The 
    * semantics of this <code>OutputStream</code> will be determined 
    * by the HTTP version of the client, and whether or not the content
    * length has been set, through the <code>setContentLength</code>
    * method. If the length of the output is not known then the output
    * is chunked for HTTP/1.1 clients and closed for HTTP/1.0 clients.
    * The <code>OutputStream</code> issued must be thread safe so that 
    * it can be used in a concurrent environment.   
    * <p>
    * This will ensure that there is buffering done so that the output
    * can be reset using the <code>reset</code> method. This will 
    * enable the specified number of bytes to be written without
    * committing the response. This specified size is the minimum size
    * that the response buffer must be. 
    *
    * @param size the minimum size that the response buffer must be
    *
    * @exception IOException this is thrown if there was an I/O error
    *
    * @return an <code>OutputStream</code> with the specified semantics
    */ 
   public OutputStream getOutputStream(int size) throws IOException{
      return resp.getOutputStream(size);
   }
   
   /**
    * This method is provided for convenience so that the HTTP content
    * can be written using the <code>print</code> methods provided by
    * the <code>PrintStream</code>. This will basically wrap the 
    * <code>getOutputStream</code> with a buffer size of zero.
    * <p>
    * The retrieved <code>PrintStream</code> uses the charset used to
    * describe the content, with the Content-Type header. This will
    * check the charset parameter of the contents MIME type. So if 
    * the Content-Type was <code>text/plain; charset=UTF-8</code> the
    * resulting <code>PrintStream</code> would encode the written data
    * using the UTF-8 encoding scheme. Care must be taken to ensure
    * that bytes written to the stream are correctly encoded.
    * <p> 
    * Implementations of the <code>Response</code> must guarantee
    * that this can be invoked repeatedly without effecting any issued 
    * <code>OutputStream</code> or <code>PrintStream</code> object.
    *
    * @return a <code>PrintStream</code> that provides convenience
    * methods to the <code>Response</code> for writing content
    *
    * @exception IOException this is thrown if there was an I/O error
    */   
   public PrintStream getPrintStream() throws IOException{
      return resp.getPrintStream();
   }

   /**
    * This method is provided for convenience so that the HTTP content
    * can be written using the <code>print</code> methods provided by
    * the <code>PrintStream</code>. This will basically wrap the 
    * <code>getOutputStream</code> with a specified buffer size.
    * <p>
    * The retrieved <code>PrintStream</code> uses the charset used to
    * describe the content, with the Content-Type header. This will
    * check the charset parameter of the contents MIME type. So if 
    * the Content-Type was <code>text/plain; charset=UTF-8</code> the
    * resulting <code>PrintStream</code> would encode the written data
    * using the UTF-8 encoding scheme. Care must be taken to ensure
    * that bytes written to the stream are correctly encoded.
    * <p> 
    * Implementations of the <code>Response</code> must guarantee
    * that this can be invoked repeatedly without effecting any issued 
    * <code>OutputStream</code> or <code>PrintStream</code> object.
    *
    * @param size the minimum size that the response buffer must be
    *
    * @return a <code>PrintStream</code> that provides convenience
    * methods to the <code>Response</code> for writing content
    *
    * @exception IOException this is thrown if there was an I/O error
    */
   public PrintStream getPrintStream(int size) throws IOException{
      return resp.getPrintStream(size);
   }

   /**
    * This is used to write the headers that where given to the
    * <code>Response</code>. Any further attempts to give headers 
    * to the <code>Response</code> will be futile as only the headers
    * that were given at the time of the first commit will be used 
    * in the message header.
    * <p>
    * This also performs some final checks on the headers submitted.
    * This is done to determine the optimal performance of the 
    * output. If no specific Connection header has been specified
    * this will set the connection so that HTTP/1.0 closes by default.
    *
    * @exception IOException thrown if there was a problem writing
    */ 
   public synchronized void commit() throws IOException{
      resp.commit();
   }

   /**
    * This can be used to determine whether the <code>Response</code>
    * has been committed. This is true if the <code>Response</code> 
    * was committed, either due to an explicit invocation of the
    * <code>commit</code> method or due to the writing of content. If
    * the <code>Response</code> has committed the <code>reset</code> 
    * method will not work in resetting content already written.
    *
    * @return true if the <code>Response</code> has been committed
    */ 
   public synchronized boolean isCommitted(){
      return resp.isCommitted();
   }

   /**
    * The <code>reset</code> method is used to reset the output 
    * from an issued <code>OutputStream</code>. This will not work
    * is the <code>isCommitted</code> returns true. If the streams
    * byte buffer overflows the response will commit and the
    * <code>reset</code> will fail.
    */
   public void reset(){
      resp.reset();
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
      return resp.contains(name);
   }

   /**
    * This is used to clear all HTTP message headers from the 
    * message header. This will leave no data remaining, i.e.
    * <code>headerCount</code> is zero after this method is 
    * invoked, this is a convenience method.
    */
   public synchronized void clear(){
      resp.clear();      
   }   

   /** 
    * This constructs the HTTP message header according to the 
    * format of RFC 2616. This returns a <code>String</code> that 
    * contains each header formatted according to the HTTP/1.1 
    * header format. The header will contain the status line 
    * followed by each header and ended with the CRLF.
    *
    * @return the HTTP response header as a string object
    */
   public synchronized String toString(){
      return resp.toString();
   }
}

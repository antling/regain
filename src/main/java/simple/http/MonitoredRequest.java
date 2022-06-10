/*
 * MonitoredRequest.java February 2001
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

import simple.util.parse.ParameterParser;
import simple.util.parse.LanguageParser;
import simple.util.parse.MimeParser;
import simple.http.session.Manager;
import simple.http.session.Session;
import simple.util.net.Parameters;
import simple.util.net.MimeType;
import simple.util.net.Principal;
import simple.util.net.Cookie;
import simple.util.ByteStore;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;

/**
 * This is a <code>MonitoredRequest</code> object that is used by a
 * <code>ProtocolHandler</code> to manage HTTP requests. This is a
 * <code>Request</code> object. This is used to encapsulate a HTTP
 * request providing some convenience methods to enable easier
 * management of HTTP requests. 
 * <p>
 * The intended purpose of this object is that it is to be given 
 * to a <code>ProtocolHandler</code> implementation so that it can 
 * be processed and a <code>Response</code> can be created. This 
 * <code>Request</code> object will send notification to a monitor
 * in the event that any problems arise in processing the request.
 *
 * @author Niall Gallagher
 */  
class MonitoredRequest extends RequestHeader {   

   /**
    * The monitor that will be given notification of events.
    */
   private InputMonitor mon;

   /**
    * The stream that contains the message body. 
    */
   private InputStream in;  
   
   /**
    * The parameters that were sent with the request.
    */
   private Parameters data;

   /**
    * Contains attributes for this HTTP request.
    */
   private Pipeline pipe;

   /**
    * Contains the cookies recovered from the request.
    */
   private Profile profile;

   /**
    * This is the session object used with the request.
    */
   private Session session;

   /**
    * Constructor for the <code>MonitoredRequest</code> object. This
    * <code>Request</code> is created by parsing the HTTP message 
    * header from the issued <code>ByteStore</code> object. This is
    * used to provide notification to an <code>InputMonitor</code> 
    * which allows it to communicate any I/O problems encountered.
    * <p>
    * This will try to determine a length for the HTTP message body 
    * initially, if this fails then the <code>InputMonitor</code> is
    * notified straight away, and the <code>InputStream</code> is 
    * set to a <code>NullInputStream</code>.
    *
    * @param buf this is the <code>ByteStore</code> for the header
    * @param  pipe the input that contains the HTTP message body
    * @param mon this is the monitor thats notified of events
    *
    * @exception IOException thrown of there is any I/O problems
    */ 
   public MonitoredRequest(ByteStore buf, Pipeline pipe, InputMonitor mon) 
   throws IOException {
      super(buf);
      this.in = pipe.getInputStream();
      this.mon = mon;
      this.pipe = pipe;   
      doConfigure();
   }

   /**
    * This can be used to retrieve certain attributes about this request. 
    * The attributes contains certain properties about the request. For 
    * example if the request came over a secure line then there may be
    * some attribute stating the scheme used. 
    * <p>
    * This <code>Attributes</code> object is shared by the associated
    * <code>Response</code> and comes from the <code>Pipeline</code>.
    *
    * @return returns <code>Attributes</code> for the HTTP pipeline
    */
   public Attributes getAttributes() {
      return pipe;
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
   public InetAddress getInetAddress() {
      return pipe.getInetAddress();
   }

   /**
    * This method can be used to retrieve cookies from the header.
    * This is a convenience method that avoids having to parse the 
    * Cookie header values. This will basically call the
    * <code>getProfile</code> method and from the <code>Profile</code>
    * invoke <code>getCookie</code> using the specified name.
    *
    * @param name this is the name of the cookie to be retrieved
    *
    * @return returns a <code>simple.util.net.Cookie</code> object 
    */   
   public Cookie getCookie(String name){
      return getProfile().getCookie(name);
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
    * @return this returns a <code>Profile</code> for the client
    */
   public Profile getProfile(){
      if(profile == null){
         String[] list = getValues("Cookie");
         profile = new PlainProfile(list);         
      }
      return profile;
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
   public Session getSession() {
      if(session == null){
         Profile profile = getProfile();
         session = Manager.getSession(profile);
      }
      return session;
   }

   /**
    * This is a convenience method that is used to retrieve the
    * client authorized to this server. This is used so that a HTTP
    * Basic authorization scheme could be used. This will check to 
    * see if the Authorization header is present, if it is then this    
    * will return a <code>simple.util.net.Principal</code> that
    * identifies the client without having to decode the header.
    *
    * @return this returns a HTTP authorized client if one exists
    */   
   public Principal getPrincipal(){
      String text = getValue("Authorization");
      if(text != null){
         return Authenticator.getPrincipal(text);
      }
      return null;
   }
   
   /**
    * This is a convenience method that is used to determine whether 
    * or not this message has the Connection header with the close 
    * token. If the close token is present then this stream is not a 
    * keep-alive connection. However if this has no Connection header 
    * then the keep alive status is determined by the HTTP version, 
    * that is HTTP/1.1 is keep alive by default, HTTP/1.0 has the 
    * connection close by default.
    *
    * @return returns true if this is keep alive connection
    */ 
   public boolean isKeepAlive(){
      int index = indexOf("Connection");
      if(index >= 0){
         String text = getValue(index);
         text = text.toLowerCase();         
         return text.indexOf("close") < 0;
      } else if(getMajor() > 1) {
         return true;         
      } else if(getMajor() == 1) {
         return getMinor() > 0;
      }
      return false;
   }
   
   /**
    * This is an attempt to parse the Content-Length header. If the
    * Content-Length header does not exist then the length returned 
    * is -1. If the Content-Length header cannot be parsed then 
    * a <code>NumberFormatException</code> is thrown by this method.
    *
    * @return this returns the value of the Content-Length header
    *
    * @exception NumberFormatException if Content-Length is invalid
    */    
   private int parseLength() throws NumberFormatException {
      int index = indexOf("Content-Length");
      if(index >= 0){
         String text = getValue(index).trim();
         return Integer.parseInt(text);         
      }
      return -1;      
   }

   /**
    * This is a convenience method that can be used to determine
    * the length of the message body. This will determine if there
    * is a <code>Content-Length</code> header, if it does then the
    * length can be determined, if not then this returns -1.
    *
    * @return the content length, or -1 if it cannot be determined
    */ 
   public int getContentLength(){
      try {
         return parseLength();
      }catch(NumberFormatException e){
         return -1;
      }
   }
   
   /**
    * This can be used to determine whether the message is chunked.
    * If the message body is chunked then the stream that this 
    * <code>Request</code> object will produce will have to decode
    * the chunked encoded message. If <code>getContentLength</code>
    * produces a valid length, that is, a length other than -1, and
    * this is true then the content length should be ignored. This
    * corresponds to a Transfer-Encoding: chunked.
    *
    * @return true if the HTTP message body is chunked encoded
    */ 
   private boolean isChunked(){
      int index = indexOf("Transfer-Encoding");
      if(index >= 0){      
         String text = getValue(index).trim();
         text = text.toLowerCase();
         return text.startsWith("chunked");
      }
      return false;
   }

   /**
    * This is used to read the content body. The specifics of the data
    * that is read from this <code>InputStream</code> can be determined
    * by the <code>getContentLength</code> method. If the data sent by
    * the client is chunked then it is decoded, see RFC 2616 section
    * 3.6. The <code>InputStream</code> must be synchronized so that 
    * multiple threads can use the stream in a concurrent environment.
    *
    * @exception IOException signifies that there is an I/O problem
    *
    * @return <code>InputStream</code> containing the message body
    */ 
   public InputStream getInputStream() throws IOException {
      return new DataInputStream(in);
   }
   
   /**
    * This is used to read the content body. The specifics of the 
    * data that is read from this <code>InputStream</code> can be 
    * determined by the <code>getContentLength</code> method or the
    * <code>isChunked</code> and <code>isKeepAlive</code> methods.
    * <p>
    * This returns a <code>DataInputStream</code> for convenience
    * so that the methods of <code>DataInput</code> can be used.
    * This is useful with a HTTP POST to read the data using the
    * <code>DataInput.readFully</code> method.
    *
    * @return returns a <code>DataInputStream</code> for the body
    *
    * @exception IOException signifies that there is an I/O error
    */ 
   private DataInputStream getDataInputStream() throws IOException {
      return new DataInputStream(in);
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
      int index = indexOf("Content-Type");
      if(index >= 0) {
         String text = getValue(index);
         return new MimeParser(text);   
      }
      return null;      
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
      int index = indexOf("Accept-Language");
      if(index >= 0) {
         String text = getValue(index);
         return new LanguageParser(text).getLocale();   
      }
      return Locale.getDefault();
   }
   
   /**
    * This returns the query string of the request URI if it has one.
    * If there is no query string with the request URI this returns
    * null. The <code>readParameters</code> method uses this to 
    * aggregate HTML form and query parameters.
    *
    * @return this returns the query string for the request URI
    */
   private String getQueryString() {
      String path = getURI();
      int index = path.indexOf("?");      
      if(index >= 0)  {
        return path.substring(index+1);
      }
      return null;
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
   public Parameters getParameters() throws IOException {   
      if(data == null){
         data = readParameters();
      }
      return data;   
   }   
   
   /**
    * This is used to read the parameters from the HTTP message body.
    * This will return an empty <code>Parameters</code> object if
    * the type is not <code>application/x-www-form-urlencoded</code>.    
    * This throws an <code>IOException</code> if the HTML form data
    * cannot be read from the <code>InputStream</code>.
    *
    * @return this returns all HTML form and query parameter data
    *
    * @exception IOException thrown if there is an I/O problem       
    */
   private Parameters readParameters() throws IOException {
      MimeType type = getMimeType();
      String query =  getQueryString();            
      String text = null;
      
      if(type != null && isPosting(type)){
         text = readContent(type);        
      } 
      return new ParameterList(text,query);      
   }

   /**
    * This will read all the contents of the HTTP message body with
    * the given charset type. This will read the number of bytes
    * specified in the Content-Length header. If there is a problem 
    * with reading the data from the <code>InputStream</code> then 
    * this will throw an <code>IOException</code>.   
    *
    * @param type the Content-Type header sent with the request
    *
    * @return returns any HTML form data sent with the request
    *
    * @exception IOException thrown if there is an I/O problem       
    */   
   private String readContent(MimeType type) throws IOException {      
      String charset = type.getCharset();         
      int size = getContentLength();

      if(charset == null){
         charset = "iso-8859-1";
      }
      return readContent(charset, size);
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
   public String getParameter(String name) throws IOException {
      return getParameters().getParameter(name);
   }
   
   /**
    * This checks to see if the MIME type given represents HTML form
    * data. The type <code>application/x-www-form-urlenoded</code>.
    * is the MIME used for HTML form data. If this represents any
    * other content type this returns false.
    *
    * @param type the <code>MimeType</code> that is to be verified
    *
    * @return true if the MIME type represents HTML form data
    */   
   private boolean isPosting(MimeType type) {   
      return type.getType().equals("application") &&
         type.getSubtype().equals("x-www-form-urlencoded");
   }
   
   /**
    * This will read all the contents of the HTTP message body with
    * the given charset type. This will read the number of bytes
    * specified in the length parameter. If there is a problem with
    * reading the data from the <code>InputStream</code> then this
    * will throw an <code>IOException</code>.
    *
    * @param charset the charset that came with the Content-Type
    * @param size this is the length of the HTML from data sent
    *
    * @return returns any HTML form data sent with the request
    *
    * @exception IOException thrown if there is an I/O problem       
    */
   private String readContent(String charset, int size) throws IOException {   
      byte[] buf = new byte[Math.max(size, 0)];      
      getDataInputStream().readFully(buf);
      return new String(buf, charset);      
   }  
   
   /**
    * This is used to determine the message delimiter and create the 
    * <code>InputStream</code> to manage the message body. This can
    * determine a message delimiter using the Content-Length header
    * of the Transfer-Encoding header. If the message is chunked
    * this this will provide a stream to decode it, if however the
    * message is delimited by length, then the length will delimit.
    *
    * @exception IOException this is thrown for an invalid length
    */ 
   private void doConfigure() throws IOException {
      boolean trailer = contains("Trailer");
      int length = parseLength();
      
      if(isChunked()){
         in = new ChunkedInputStream(in,mon,trailer);
      }else if(length >= 0){
         in = new FixedInputStream(in,mon,length);
      }else {
         in = new NullInputStream(in,mon);
      }
   }
   
   /**
    * This will ensure that any <code>MonitoredRequest</code> objects
    * will notify the <code>InputMonitor</code> of the status of the 
    * input. This will be a <code>notifyError</code> if the HTTP body
    * is not complete, i.e. if the <code>InputStream</code> has not 
    * been read from and there is a message body with the request.
    * <p>
    * This is needed so that the <code>InputMonitor</code> can be used
    * to provide a mechanism whereby concurrent threads can interact 
    * with the <code>MonitoredInputStream</code> in such a way that 
    * the <code>Dispatcher.run</code> can finish, and the stream can 
    * still control the connection using the <code>InputMonitor</code>.
    * The connection will be severed if there are no more threads
    * interacting with this <code>Request</code> object. 
    *
    * @exception Throwable may throw an <code>IOException</code>
    */ 
   protected void finalize() throws Throwable {                  
      in.close(); 
   }
}

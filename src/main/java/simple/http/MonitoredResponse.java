/*
 * MonitoredResponse.java February 2001
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

import simple.util.parse.MimeParser;
import simple.util.net.MimeType;
import simple.util.net.Cookie;
import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * This is a <code>MonitoredResponse</code> object that is used to
 * encapsulate a HTTP response. This is a <code>Response</code>
 * object. The intended use of this object is that it be passed to 
 * a <code>ProtocolHandler</code> object and used to process a HTTP
 * transaction. 
 * <p>
 * This <code>Response</code> will send any critical events to an 
 * <code>OutputMonitor</code> for processing, this may have the 
 * effect of closing the <code>OutputStream</code> used by the 
 * <code>Response</code>.
 * <p>
 * This provides features which allow the message body to be blocked
 * if it should not be sent with the headers as of RFC 2616 rules for 
 * the presence of a message body. A message body must not be 
 * included with a HEAD request or with a 304 or a 204 response.
 *
 * @author Niall Gallagher
 */ 
class MonitoredResponse extends ResponseHeader implements ResponseChannel {    

   /**
    * This enables buffering to be done for the response.
    */
   private ResponseStream buf;
   
   /**
    * The monitor that will be given notification of events. 
    */
   private OutputMonitor mon;
   
   /**
    * This is the <code>OutputStream</code> for the response.
    */
   private OutputStream out;
   
   /**
    * This is the pipeline that the request is from.
    */
   private Pipeline pipe; 
   
   /**
    * This is the request instance for this transaction.
    */   
   private Request req;
   
   /**
    * Indicates the status of this HTTP response.
    */
   private boolean committed;
   
   /**
    * Contains the value of the Content-Length header.
    */
   private int length = -1;

   /**
    * This creates an implementation of the <code>Response</code>
    * interface. This implementation uses a <code>Pipeline</code>
    * for its output. Each <code>Response</code> has a matching
    * <code>Request</code> object which specifies what
    * <code>Response</code> it wants.
    *
    * @param req the <code>Request</code> associated with this
    * @param pipe this is the where the output of this is written
    * @param mon the monitor that is to be used to monitor output
    *
    * @exception IOException thrown if there is any I/O errors
    */ 
   public MonitoredResponse(Request req, Pipeline pipe, OutputMonitor mon) 
   throws IOException {
      this.out = pipe.getOutputStream();
      this.buf = new ResponseStream(this);
      this.pipe = pipe;
      this.req = req;
      this.mon = mon;      
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
   public boolean isCommitted() {
      return committed;
   }

   /**
    * The <code>reset</code> method is used to reset the output 
    * from an issued <code>OutputStream</code>. This will not work
    * is the <code>isCommitted</code> returns true. If the streams
    * byte buffer overflows the response will commit and the
    * <code>reset</code> will fail.
    */
   public void reset(){
      if(!committed){
         buf.reset();
      }
   }
   
   /**
    * This is used to write the header that where given to the
    * <code>Response</code>. Any further attempts to give headers 
    * to the <code>Response</code> will be futile as only the HTTP
    * headers that were given at the time of the first commit will 
    * be used in the message header.
    *
    * @exception IOException if there is an I/O problem committing
    */ 
   private void ensureCommit() throws IOException {
      if(!committed) {
         commit(); 
      }
   }
   
   /**
    * This is used to determine if the <code>Response</code> has a
    * message body. If this does not have a message body then false 
    * is returned. This is determined as of RFC 2616 rules for the 
    * presence of a message body. A message body must not be 
    * included with a HEAD request or with a 304 or a 204 response. 
    * If when this is called there is no message length delimiter 
    * as specified by section RFC 2616 4.4, then there is no body.
    *
    * @return if the message has a body this returns true
    */ 
   private boolean hasBody() {
      if(req.getMethod().equals("HEAD")){
         return false;
      } else if(getCode() == 204){ /* sec 4.4 */
         return false;
      } else if(getCode() == 304){ /* sec 4.4 */
         return false;
      }      
      return true;   
   }
   
   /**
    * The <code>isKeepAlive</code> method is used to determine if
    * the connection semantics are set to maintain the connection.
    * If this is true then the <code>setClose</code> method should
    * be false unless the <code>getContentLength</code> and the
    * <code>isChunked</code> reveal that there is no specified
    * connection semantics set for the response channel.
    *
    * @return true if the response connection is to be maintained
    */   
   public boolean isKeepAlive() {
      if(contains("Connection")) {
         String text = getValue("Connection");
         text = text.toLowerCase();
         return text.indexOf("close") < 0; 
      } 
      return req.isKeepAlive();
   }
   
   /**
    * The <code>isChunked</code> is used to determine whether the
    * chunked encoding scheme is desired. This is enables data to
    * be encoded in such a way that a connection can be maintained
    * without a Content-Length header. If the output is chunked 
    * then the <code>setClose</code> should be false.
    *
    * @return true if the response output is chunked encoded
    */
   public boolean isChunked(){
      int index = indexOf("Transfer-Encoding");
      if(index >= 0){
         String text = getValue(index).trim();
         text = text.toLowerCase();
         return text.startsWith("chunked");
      }
      return false;
   } 
   
   /**
    * The <code>isChunkable</code> is used to determine whether the
    * chunked transfer encoding is acceptable to the client. This
    * will determine, according to RFC 2616, whether the client can
    * decode data written using the chunked encoding scheme.
    * <p>
    * RFC 2616, section 3.6 states that, A server must not send any
    * transfer-codings to an HTTP/1.0 client. RFC 2616 section 3.6.1
    * also states that an origin server should not use trailers in 
    * chunked encoded unless the trailers consist of optional meta 
    * data or the client explicitly states that it will accept them.
    *
    * @return this returns true if the client supports HTTP/1.1
    */
   public boolean isChunkable() {
      if(req.getMajor() > 1) {
         return true;         
      } else if(req.getMajor() == 1) {
         return req.getMinor() > 0;
      }
      return false;      
   }

   /**
    * This is used when the output is encoded in the chunked encoding.
    * This should only be used if <code>isChunkable</code> is true.
    * If this is used the <code>OutputStream</code> will encode data
    * using the transfer coding, specified in RFC 2616 section 3.6.1.
    * If this is set to true then <code>setClose</code> should be
    * set to false, this will ensure that connections are persistent.
    * 
    * @param chunked used to determine whether output is chunked
    */    
   public void setChunked(boolean chunked){
      if(!chunked && contains("Transfer-Encoding")){
         removeAll("Transfer-Encoding");
      }else if(chunked){
         set("Transfer-Encoding", "chunked");
      }
   } 

   /**
    * The <code>getContentLength</code> method is used to determine
    * the length that has been set with the Content-Length header
    * field. For the connection to be maintained as keep-alive the
    * number of bytes written to the <code>ResponseStream</code>
    * should be equal to the Content-Length value. If this is 
    * greater than -1 the <code>setClose</code> should be false.
    *
    * @return this returns the value in the Content-Length header
    */   
   public int getContentLength() {
      if(length >= 0) return length;
      try {
         length = parseLength();
      }catch(NumberFormatException e){                  
      }
      return length;
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
         String text = getValue(index);
         return Integer.parseInt(text);
      }
      return -1;     
   }

   /**
    * The allows the <code>ResponseChannel</code> to be configured
    * to have keep-alive semantics by setting the delimiter to the
    * number of bytes written in the response. This should be 
    * greater than or equal to zero. If this is set to a valid
    * length value then the <code>setClose</code> is false.
    *
    * @param length the length used in the Content-Length header
    */   
   public void setContentLength(int length) {
      set("Content-Length", String.valueOf(length));
   }

   /**
    * The <code>setClose</code> is used to set the Connection
    * header semantics. If <code>setClose</code> is false then
    * the connection semantics are keep-alive if true then the
    * semantics should be close. This is used as a fallback
    * delimiter when there has been no specific delimiter set.
    *
    * @param close specifies if the connection is keep alive
    */   
   public void setClose(boolean close) {
      set("Connection", close? "close":"keep-alive");
   }
   
   /**
    * This is used to ensure that the <code>Cookie</code> objects
    * that were set using the <code>Profile</code> are sent with 
    * the response. This uses the <code>Profile</code> issued by
    * the <code>Request.getProfile</code> and iteratively writes
    * the <code>getSetCookies</code> issued as Set-Cookie headers.
    *
    * @param profile <code>Profile</code> issued by the request
    */
   private void setCookies(Profile profile){
      Cookie[] list = profile.getSetCookies();      

      for(int i = 0; i < list.length; i++){
         add("Set-Cookie", list[i].toString());
      }
   }
   
   /**
    * This is used to get a <code>MimeType</code> object for the MIME
    * type value of the Content-Type header. This is used to that the
    * MIME type of the <code>Response</code> can be examined and so
    * that the charset value of the MIME type can be determined. This
    * is useful in determining what encoding that is to be used by
    * the <code>PrintStream</code> objects created.
    *
    * @return returns the Content-Type as a <code>MimeType</code>
    */
   private MimeType getMimeType(){
      int index = indexOf("Content-Type");
      if(index >= 0) {
         String text = getValue(index);
         return new MimeParser(text);   
      }
      return null;      
   }

   /**
    * This determines the charset for <code>PrintStream</code> objects
    * returned from the <code>getPrintStream</code> method. This will
    * return a valid charset regardless of whether the Content-Type
    * header has been set, set without a charset, or not set at all.
    * If unspecified, the charset returned is <code>ISO-8859-1</code>,
    * as suggested by RFC 2616, section 3.7.1.
    *
    * @return returns the charset used by this <code>Response</code>
    */
   private String getCharset() {
      MimeType type = getMimeType();      
      if(type == null) {         
         return "iso-8859-1";
      }
      if(type.getCharset()==null){
         return "iso-8859-1";
      }
      return type.getCharset();
   }
   
   /**
    * This can be used to retrieve certain attributes about
    * this <code>Response</code>. The attributes contains certain
    * properties about the <code>Response</code>. For example if
    * this Response goes over a secure line then there may be any
    * arbitrary attributes.
    *
    * @return the attributes of this <code>Response</code> object
    */ 
   public Attributes getAttributes() {
      return pipe;
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
   public InetAddress getInetAddress() {
      return pipe.getInetAddress();
   }
   
   /**
    * Used to write a message body with the <code>Response</code>. The 
    * semantics of this <code>OutputStream</code> will be determined 
    * by the HTTP version of the client, and whether or not the content
    * length has been set, through the <code>setContentLength</code>
    * method. If the length of the output is not known then the output
    * is chunked for HTTP/1.1 clients and closed for HTTP/1.0 clients.
    *
    * @exception IOException this is thrown if there was an I/O error
    *
    * @return an <code>OutputStream</code> with the specified semantics
    */ 
   public OutputStream getOutputStream(){
      return buf;
   }
   
   /**
    * Used to write a message body with the <code>Response</code>. The 
    * semantics of this <code>OutputStream</code> will be determined 
    * by the HTTP version of the client, and whether or not the content
    * length has been set, through the <code>setContentLength</code>
    * method. If the length of the output is not known then the output
    * is chunked for HTTP/1.1 clients and closed for HTTP/1.0 clients.
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
   public OutputStream getOutputStream(int size){
      buf.ensureCapacity(size);
      return buf;
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
    *
    * @return a <code>PrintStream</code> that provides convenience
    * methods to the <code>Response</code> for writing content
    *
    * @exception IOException this is thrown if there was an I/O error
    */
   public PrintStream getPrintStream() throws IOException {
      return getPrintStream(0, getCharset());
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
    *
    * @param size the minimum size that the response buffer must be
    *
    * @return a <code>PrintStream</code> that provides convenience
    * methods to the <code>Response</code> for writing content
    *
    * @exception IOException this is thrown if there was an I/O error
    */
   public PrintStream getPrintStream(int size) throws IOException {
      return getPrintStream(size, getCharset());
   }
   
   /**  
    * This is used to wrap the <code>getOutputStream</code> object in
    * a <code>PrintStream</code>, which will write content using a 
    * specified charset. The <code>PrintStream</code> created will not
    * buffer the content, it will write directly to the underlying
    * <code>OutputStream</code> where it is buffered (if there is a
    * buffer size greater than zero specified). In future the buffer
    * of the <code>PrintStream</code> may be usable.
    *
    * @param size the minimum size that the response buffer must be
    * @param charset this is the charset used by the resulting stream
    *
    * @return a <code>PrintStream</code> that provides convenience
    * methods to the <code>Response</code> for writing content
    *
    * @exception IOException the <code>IOException</code> is thrown
    * if the Content-Type charset is not supported     
    */
   private PrintStream getPrintStream(int size, String charset) throws IOException {
      return new PrintStream(getOutputStream(size), false, charset);
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
   public void commit() throws IOException {
      if(!committed) {
         setCookies(req.getProfile());    
         String head = toString();
         try {
            out.write(head.getBytes("iso-8859-1"));
         }catch(IOException cause){
            mon.notifyError(out);   
            throw cause;
         }                  
         doConfigure();         
         committed = true;
      }
   }

   /**
    * This method is used to configure the output to the specified
    * semantics. This is used by the <code>commit</code> method to
    * ensure that the <code>getOutputChannel</code> method issues a
    * channel which can be used to communicate with the client.
    * <p>
    * It is important that the <code>OutputStream</code> perform 
    * monitoring so that persistent connections can be maintained
    * by the server. If there is no output then this will use the
    * <code>NullOutputStream</code>.
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void doConfigure() throws IOException {
      int size = getContentLength();
      boolean hasBody = hasBody();
      
      if(hasBody && !isKeepAlive()) {
         out = new CloseOutputStream(out,mon);
      }else if(hasBody && isChunked()){
         out = new ChunkedOutputStream(out,mon);
      }else if(hasBody && size > 0){
         out = new FixedOutputStream(out,mon,size);
      } else {
         out = new NullOutputStream(out,mon);
      }
   }
   
   /** 
    * The retrieves an <code>OutputStream</code> that represents the
    * response output. The stream issued will have the connection 
    * semantics specified. Once this method is used the connection
    * semantics are set permanently. If there is an error committing
    * the response header this throws an <code>IOException</code>.
    * 
    * @return this returns the output channel for the response body
    *
    * @exception IOException thrown if there is an I/O problem
    */ 
   public OutputStream getOutputChannel() throws IOException {
      ensureCommit();
      return out;      
   }
   
   /**
    * This will ensure that the <code>MonitoredResponse</code> object
    * will notify the <code>OutputMonitor</code> of its status. If the
    * output is not configured then the monitor will be notified of a
    * <code>notifyError</code> event, when the GC invokes this.
    * <p>
    * This is needed so that the <code>OutputMonitor</code> is used 
    * to provide a mechanism whereby separate threads can interact 
    * with the <code>MonitoredOutputStream</code> in such a way that 
    * the <code>Dispatcher.run</code> can finish and the stream can 
    * still control the connection using the <code>OutputMonitor</code>.
    * The connection will be severed if there are no more threads with
    * a reference to the <code>Response</code> object.
    *
    * @exception Throwable an <code>IOException</code> on close
    */ 
   protected void finalize() throws Throwable {
      if(!committed){
         mon.notifyError(out); 
      }else {
         buf.close();
      }
   }
   
}

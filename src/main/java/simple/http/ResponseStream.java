/*
 * ResponseStream.java September 2002
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

import java.io.OutputStream;
import java.io.IOException;

/**
 * The <code>ResponseStream</code> is used to enable bytes written to
 * the response's <code>OutputStream</code> to be buffered. This also
 * enables the <code>Response.reset</code> method to be used.
 * <p>
 * Bytes written are buffered until the <code>flush</code> method is
 * invoked or until the buffer overflows. Once either of these events
 * occur the response is committed and the buffered bytes are written
 * to the response output stream. If a <code>flush</code> method is
 * explicitly invoked the response output stream is flushed, if the
 * buffer overflows the the buffer is written to the stream but not
 * flushed. This ensures that pipeline buffering is not affected.
 * <p>
 * This enables the connection to be configured so that Persistent
 * HTTP (PHTTP) connections can be maintained. RFC 2616 dictates that
 * all clients implementing the HTTP/1.1 protocol must support one of
 * three message delimiters, connection close, content length, and
 * chunked transfer encoding. The <code>ResponseStream</code> ensures
 * that if the client supports the chunked transfer encoding that the
 * chunked delimiter is used before a connection close.
 *
 * @author Niall Gallagher
 */
final class ResponseStream extends OutputStream {

   /**
    * Contains the bytes that have been written to the client.
    */
   private byte[] buf = new byte[0];

   /**
    * Used to enable a single byte to be written as an array.
    */
   private byte[] swap = new byte[1];

   /**
    * This is the <code>ResponseChannel</code> that this is for.
    */
   private ResponseChannel resp;

   /**
    * The <code>OutputStream</code> that is created for output.
    */
   private OutputStream out;

   /**
    * This is the number of bytes that have been buffered.
    */
   private int count;

   /**
    * This indicates whether or not the stream is open.
    */
   private boolean open;

   /**
    * Constructor for <code>ResponseStream</code>. This is used for
    * buffering data before writing to an output stream. This will
    * ensure that the <code>ResponseChannel</code> is used to set
    * the connection semantics such that Persistent HTTP (PHTTP)
    * connections are maintained. The number of bytes buffered using
    * this instance can be set using <code>ensureCapacity</code>.
    *
    * @param resp this is the what controls the HTTP connections
    */
   public ResponseStream(ResponseChannel resp){
      this.resp = resp;
      this.open = true;
   }

   /**
    * The <code>write</code> method is used to buffer data written.
    * If the number of bytes written is greater that the size of the
    * buffer then is is written directly to the underlying stream
    * after a flush of any buffered bytes. This ensures the buffer
    * does not affect the performance of the response stream.
    * <p>
    * When the buffer overflows, data is written to the underlying
    * stream, the underlying stream is not flushed to ensure that
    * the <code>ResponseStream</code> does not affect any secondary
    * buffers, such as those used to buffer socket based output.
    *
    * @param octet the byte that is to be written to the stream
    *
    * @exception IOException signifies that there is an I/O problem
    */
   public synchronized void write(int octet) throws IOException{
      swap[0] = (byte)octet;
      write(swap, 0, 1);
   }

   /**
    * The <code>write</code> method is used to buffer data written.
    * If the number of bytes written is greater than the size of the
    * buffer then it is written directly to the underlying stream
    * after a flush of any buffered bytes. This ensures the buffer
    * does not affect the performance of the response stream.
    * <p>
    * When the buffer overflows, data is written to the underlying
    * stream, the underlying stream is not flushed to ensure that
    * the <code>ResponseStream</code> does not affect any secondary
    * buffers, such as those used to buffer socket based output.
    *
    * @param b the buffer that contains the bytes to be written
    * @param off the offset within the buffer to begin writing data
    * @param len this is the number of bytes that is to be written
    *
    * @exception IOException signifies that there is an I/O problem
    */
   public synchronized void write(byte[] b, int off, int len) throws IOException{
      ensureOpen();
      if(count + len > buf.length){
         flushBuffer();
      }
      if(len > buf.length){
         out.write(b, off,len);
      }else{
         System.arraycopy(b,off,buf,count,len);
         count += len;
      }
   }

   /**
    * The <code>ensureCapacity</code> method is used to expand the
    * size of the internal buffer to the specified size. This will
    * enable the <code>ResponseStream</code> to accumulate more
    * data before it has to be written to the underlying stream.
    *
    * @param min the minimum number of bytes that can be buffered
    */
   public synchronized void ensureCapacity(int min) {
      if(buf.length < min) {
         int size = buf.length * 2;
         int max = Math.max(min, size);
         byte[] temp = new byte[max];
         System.arraycopy(buf, 0, temp, 0, count);
         buf = temp;
      }
   }

   /**
    * The <code>reset</code> method will clear any bytes buffered
    * by this stream. This enables the <code>Response.reset</code>
    * method to be used to reset any content that has been written.
    * If ant data has already been written, that is, if there has
    * been a buffer overflow for a <code>write</code> invocation
    * of if there has been an explicit <code>flush</code> used
    * the <code>reset</code> method will not clear the buffer.
    */
   public synchronized void reset(){
      if(!resp.isCommitted()){
         count = 0;
      }
   }

   /**
    * This will write the buffered data to the underlying stream
    * and flush it. Once this has been invoked the HTTP connection
    * is given the connection semantics of close or chunked. For
    * maximum performance invoking this method should be avoided.
    *
    * @exception IOException signifies that there is an I/O error
    */
   public synchronized void flush() throws IOException{
     flushBuffer();
     out.flush();
   }

   /**
    * The <code>flushBuffer</code> method is used to write buffered
    * data to the underlying stream, without flushing it. This is
    * used by the write methods when the buffer overflows. When the
    * buffer overflows it is important that the underlying stream
    * is not flushed so that secondary buffering is not affected.
    *
    * @exception IOException signifies that there is an I/O error
    */
   private void flushBuffer() throws IOException{
      ensureOpen();
      if(!isConfigured()){
         out = getOutputStream();
      }
      if(count > 0){
         out.write(buf,0,count);
         count = 0;
      }
   }

   /**
    * Determines whether the <code>OutputStream</code> has been
    * retrieved and whether the appropriate connection headers
    * have been set. This is an important step ensuring there is
    * a valid stream for the content to be written to.
    *
    * @return true if the connection is valid, false otherwise
    */
   public boolean isConfigured() {
      if(!resp.isCommitted()){
         return false;
      }
      return out != null;
   }

   /**
    * This sets the connection semantics for the HTTP pipeline. This
    * ensures that Persistent HTTP connections are maintained if
    * possible. This checks the current status of the HTTP response
    * message to determine the connection semantics desired. The
    * precedence of the connection delimiter is content length,
    * followed by chunked encoding, and, if chunked encoding is not
    * supported by the client, connection close as a last resort.
    */
   private void setConnection() {
      if(resp.isChunked()){
         resp.setClose(false);
      }else if(resp.getContentLength()<0){
         if(resp.isChunkable()) {
            resp.setChunked(true);
         }else {
            resp.setClose(true);
         }
      }
   }

   /**
    * This is used to get the <code>OutputStream</code> object for the
    * <code>ResponseChannel</code>. Before the stream is created the
    * connection semantics are set. The connection semantics for the
    * output channel are such that PHTTP, that is, Persistent HTTP, is
    * attempted. So if there is no Content-Length header set for the
    * output channel then chunked transfer encoding is attempted.
    * <p>
    * The HTTP/1.1 protocol enables the Transfer-Encoding header to
    * be used with HTTP/1.1 clients if the content length cannot be
    * determined (for dynamic content). Use of the Transfer-Encoding
    * allows persistent HTTP connections to be maintained by using
    * a self delimiting encoding, chunked encoding (see RFC 2616).
    *
    * @return the <code>OutputStream</code> for the response channel
    *
    * @exception IOException this is thrown if there us an I/O error
    */
   private OutputStream getOutputStream() throws IOException {
      if(resp.isKeepAlive()){
         setConnection();
      }
      return resp.getOutputChannel();
   }

   /**
    * The <code>ensureOpen</code> method is used to determine the
    * status of the stream. If the stream is closed this method
    * will throw an <code>IOException</code> when the writing to
    * the stream, using the <code>write</code> methods, or when
    * attempting to flush by using the <code>flush</code> method.
    *
    * @exception IOException thrown to signify this is closed
    */
   private void ensureOpen() throws IOException {
      if(!open) {
         throw new IOException("Stream closed");
      }
   }

   /**
    * The <code>ensureCommit</code> method is used to specify the
    * length of the output. This is used by <code>close</code> so
    * that the data buffered can be used to specify the length.
    * Since this is used on a close of the stream, the buffered
    * data is the only data that will be written to the channel.
    */
   private void ensureCommit() {
      if(!resp.isCommitted()){
         resp.setContentLength(count);
      }
   }

   /**
    * This will close the <code>OutputStream</code> and flush the
    * buffered bytes. Further use of the instance will result in
    * <code>IOException</code>'s being thrown.
    * <p>
    * If the <code>ResponseChannel</code> has not committed the
    * HTTP message headers to the client, then this will set the
    * content length to the number of bytes currently buffered.
    * This enables Persistent HTTP connections to be maintained.
    *
    * @exception IOException if there is an I/O problem closing
    */
   public synchronized void close() throws IOException{
      if(open) {
         ensureCommit();
         flushBuffer();
         out.close();
         open = false;
      }
   }
}

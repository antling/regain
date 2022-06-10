/*
 * ResponseChannel.java November 2002
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
 * The <code>ResponseChannel</code> is used to provide an interface 
 * for the configuration of the HTTP connection semantics. This enables
 * an object to configure the type of output channel is to be used and
 * can be used to retrieve the <code>OutputStream</code> for the
 * configured channel.
 * <p>
 * This is used in conjunction with the <code>ResponseStream</code> 
 * to facilitate output buffering. The <code>ResponseOutput</code> can
 * be used to buffer output for the HTTP response. This interface is
 * used to enable the output buffer to configure the underlying output
 * for maximum efficiency (prioritizing keep-alive connections).
 *
 * @author Niall Gallagher
 */
interface ResponseChannel {  

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
   public boolean isKeepAlive();

   /**
    * The <code>isChunked</code> is used to determine whether the
    * chunked encoding scheme is desired. This is enables data to
    * be encoded in such a way that a connection can be maintained
    * without a Content-Length header. If the output is chunked 
    * then the <code>setClose</code> should be false.
    *
    * @return true if the response output is chunked encoded
    */
   public boolean isChunked();
   
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
   public boolean isChunkable();
   
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
   public void setChunked(boolean chunked);

   /** 
    * This is used to determine whether the output from the HTTP
    * response has written to the underlying channel. The response
    * is committed when the <code>getOutputChannel</code> method
    * is invoked. If the channel <code>isCommitted</code> then the
    * connection semantics cannot be configured further.
    *
    * @return true if the <code>getOutputChannel</code> was used
    */    
   public boolean isCommitted();

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
   public int getContentLength();
   
   /**
    * The allows the <code>ResponseChannel</code> to be configured
    * to have keep-alive semantics by setting the delimiter to the
    * number of bytes written in the response. This should be 
    * greater than or equal to zero. If this is set to a valid
    * length value then the <code>setClose</code> is false.
    *
    * @param length the length used in the Content-Length header
    */
   public void setContentLength(int length);

   /**
    * The <code>setClose</code> is used to set the Connection
    * header semantics. If <code>setClose</code> is false then
    * the connection semantics are keep-alive if true then the
    * semantics should be close. This is used as a fallback
    * delimiter when there has been no specific delimiter set.
    *
    * @param close specifies if the connection is keep alive
    */
   public void setClose(boolean close);

   /** 
    * The retrieves an <code>OutputStream</code> that represents
    * the response output. The stream issued will have the 
    * connection semantics specified. Once this method is used
    * the connection semantics are set permanently.
    * 
    * @return this returns the output channel for the response
    *
    * @exception IOException thrown if there is an I/O problem
    */  
   public OutputStream getOutputChannel()
      throws IOException;
}

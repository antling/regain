/*
 * PollerInputStream.java February 2001
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

import java.io.PushbackInputStream;
import java.io.InterruptedIOException;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This is used to provide a means to detemine when the connected 
 * peer has closed its end of the <code>Socket</code>. When this 
 * <code>available</code> method is invoked when the client peer
 * has closed its end of the stream this will throw an exception.
 * This enables the synchronous socket stream to be used in a
 * non-blocking capacity, which enables polling to be much less
 * of a burden on CPU cycles. This is so beacuse once a stream is
 * closed polling no longer needs to be performed for the stream.
 *
 * @author Niall Gallagher
 */
class PollerInputStream extends PushbackInputStream{

   /**
    * This is the <code>Socket</code> that is being polled.
    */
   private Socket sock;

   /**
    * Represents the number of bytes that can be put back.
    */
   private int size;

   /**
    * This creates a <code>PollerInputStream</code> object that
    * wraps the <code>SocketInputStream</code>. This throws an 
    * IOException when the <code>available</code> method is used 
    * and the peer has closed the stream.
    *
    * @param sock the origin of the stream, that is the peer
    *
    * @exception IOException if the object could not be created
    */ 
   public PollerInputStream(Socket sock) throws IOException{
      this(sock, 512);
   }

   /**
    * This creates a <code>PollerInputStream</code> object that
    * wraps the <code>SocketInputStream</code>. This throws an 
    * IOException when the <code>available</code> method is used 
    * and the peer has closed the stream.
    *
    * @param sock the origin of the stream, that is the peer
    * @param size this specifies the number of bytes that can
    * be unread from this stream
    *
    * @exception IOException if the object could not be created
    */ 
   public PollerInputStream(Socket sock, int size) throws IOException{
      super(sock.getInputStream(), size);
      this.sock = sock;
      this.size = size;
   }

   /**
    * This is a wrapper for the <code>SocketInputStream</code>'s
    * <code>available</code> method. This is used to detect when 
    * this <code>InputStream</code> has closed and when the
    * peer closes the stream. This is important as it enables 
    * the <code>Poller</code> to read in a non-blocking capacity.
    * <p>
    * This has been written is such a way that it can support 
    * the very dumb implementation of TLS and SSL provided by 
    * Sun. In this implementation using <code>available</code>
    * on an <code>SSLSocket</code> always produces zero.
    *
    * @exception IOException if there was an I/O error with the
    * <code>available</code>, or the peer closed the stream
    *
    * @return this returns the number of bytes available in the
    * <code>Socket</code>
    */ 
   public int available() throws IOException {
      if(buf.length - pos > 0){ 
         return super.available();
      }
      int timeout = sock.getSoTimeout(); 
      sock.setSoTimeout(1); 
      try {
         int octet = read(); 
         if(octet == -1) {
            close();
         }
         unread(octet); 
      } catch(InterruptedIOException e){
      } 
      sock.setSoTimeout(timeout);  
      return Math.min(super.available(),size);
   }
}


/*
 * CloseOutputStream.java February 2001
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

import java.io.OutputStream;
import java.io.IOException;

/**
 * The <code>CloseOutputStream</code> provides an implementation of
 * the <code>MonitoredOutputStream</code> that writes directly to
 * the underlying stream. This does not perform any encoding of the
 * bytes written to the stream, it is used only to provide a close
 * of the underlying stream when <code>close</code> is used.
 * <p>
 * This requirement for a stream that performs a simple close can
 * be found in RFC 2616 section 4.4, Message Length. This allows a
 * client to recognize the length of the content when the stream 
 * the content is delivered on is explicitly closed. 
 * 
 * @author Niall Gallagher
 */  
class CloseOutputStream extends MonitoredOutputStream { 

   /**
    * Constructor for the <code>CloseOutputStream</code>. This uses
    * the issued <code>OutputStream</code> to write data directly.
    * All notifications of I/O events are issued to the monitor by 
    * the <code>MonitoredOutputStream</code> methods.
    *
    * @param out the stream that that data is to be written to
    * @param mon the monitor that will be notified of I/O events
    */     
   public CloseOutputStream(OutputStream out, OutputMonitor mon) {
      super(out, mon);
   }

   /**
    * The <code>writeBytes</code> method is used to write data to
    * the underlying <code>OutputStream</code>. This writes bytes
    * directly to the stream. If an <code>IOException</code> is
    * thrown the <code>write</code> methods notifies the monitor.
    *
    * @param buf the buffer that contains the bytes to be written
    * @param off the offset within the buffer to begin writing 
    * @param len the number of bytes that are to be written
    * 
    * @exception IOException signifies that there is an I/O error    
    */
   protected void writeBytes(byte[] buf, int off, int len) throws IOException {
      out.write(buf, off, len);
   }

   /**
    * The <code>flushBytes</code> method is used to flush any data
    * that is buffered within the underlying output stream. This
    * performs an invocation of the <code>flush</code> method.
    * 
    * @exception IOException signifies that there is an I/O error     
    */ 
   protected void flushBytes() throws IOException {
      out.flush();
   }

   /**
    * The <code>doClose</code> method is used to perform a close 
    * of the <code>OutputStream</code>. This is used to delimit
    * any data written to the stream. The connected client can
    * determine the end of the message once the stream is closed.
    *
    * @exception IOException signifies that there is an I/O error     
    */
   protected void doClose() throws IOException {
      out.close();
   }
}

/*
 * MonitoredOutputStream.java February 2001
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
 * The <code>MonitoredOutputStream</code> class is used to perform
 * monitoring of data written to an underlying stream. This must be
 * subclassed so that behavior can be added to the writing of data
 * and so that the stream can perform a dialog with the monitor.
 * <p>
 * The <code>OutputMonitor</code> is used by a monitored stream to
 * handle any I/O events that occur while writing data. Events 
 * such as <code>IOException</code>'s that are thrown are handled
 * by invoking the monitor's <code>notifyError</code> method. This
 * is synchronized so that it can be accessed by multiple threads.
 *
 * @author Niall Gallagher
 */
abstract class MonitoredOutputStream extends OutputStream {   

   /**
    * This is used to receive all I/O event notifications.
    */
   protected OutputMonitor mon;

   /**
    * This is the underlying stream used to write the data.
    */
   protected OutputStream out;
   
   /**
    * This is used for writing a single byte as an array. 
    */
   protected byte[] temp = new byte[1];
   
   /**
    * This determines whether the stream is open or closed. 
    */
   protected boolean open = true;
   
   /**
    * Constructor for the <code>MonitoredOutputStream</code>. This is
    * used initialize the stream. Implementations must provide an
    * <code>OutputStream</code> and an <code>OutputMonitor</code> so
    * that notifications can be issued regarding the data written.
    *
    * @param out the stream that that data is to be written to
    * @param mon the monitor that will be notified of I/O events
    */ 
   protected MonitoredOutputStream(OutputStream out, OutputMonitor mon) {
      this.mon = mon;
      this.out = out;
   }   

   /**
    * This is used to delegate to the <code>writeBytes</code> method.
    * This will invoke the <code>writeBytes</code> method with the
    * specified parameters. If an exception is thrown, then this is
    * used to perform a <code>notifyError</code> notification to the
    * <code>OutputMonitor</code>, also the stream is closed.
    *
    * @param octet the byte that is to be written to the stream
    * 
    * @exception IOException signifies that there is an I/O problem      
    */ 
   public synchronized void write(int octet) throws IOException {
      temp[0] = (byte)octet;
      write(temp, 0, 1);
   }

   /**
    * This is used to delegate to the <code>writeBytes</code> method.
    * This will invoke the <code>writeBytes</code> method with the
    * specified parameters. If an exception is thrown, then this is
    * used to perform a <code>notifyError</code> notification to the
    * <code>OutputMonitor</code>, also the stream is closed.
    *
    * @param buf the buffer that contains the bytes to be written
    * @param off the offset within the buffer to begin writing data
    * @param len this is the number of bytes that is to be written     
    * 
    * @exception IOException signifies that there is an I/O problem     
    */ 
   public synchronized void write(byte[] buf, int off, int len) throws IOException {
      ensureOpen();
      try {
         writeBytes(buf, off, len);
      } catch(IOException e) {
         mon.notifyError(out);
         destroy();
      }
   }

   /**
    * This is used to delegate to the <code>flushBytes</code> method.
    * This will invoke the <code>flushBytes</code> method to flush
    * any buffered bytes. If an exception is thrown, then this is
    * used to perform a <code>notifyError</code> notification to the
    * <code>OutputMonitor</code>, also the stream is closed.
    *
    * @exception IOException signifies that there is an I/O problem     
    */ 
   public synchronized void flush() throws IOException {
      ensureOpen();
      try {
         flushBytes();
      } catch(IOException e) {
         mon.notifyError(out);
         destroy();
      }
   }
   
   /**
    * The <code>ensureOpen</code> method is used to determine if the
    * stream has been closed. If the stream has been closed, either 
    * by an explicit invocation of the <code>close</code> method or
    * by an invocation of the <code>destroy</code> method then this
    * will throw an <code>IOException</code>.
    *
    * @exception IOException signifies that the stream is closed     
    */   
   protected void ensureOpen() throws IOException {
      if(!open) {
         throw new IOException("Stream closed");
      }
   }

   /**
    * This is used clean up the <code>OutputStream</code> after an 
    * <code>IOException</code> attempting to access it. This closes
    * the <code>MonitoredOutputStream</code> in such a way that the
    * <code>ensureOpen</code> method always throws an exception.
    *
    * @exception IOException signifies that there is an I/O problem     
    */ 
   protected void destroy() throws IOException {
      try{
         throw new IOException("Broken pipe");
      }finally{
         open = false;
      }
   }

   /**
    * The <code>close</code> method is used to perform notification
    * of error, and as a driver for the <code>doClose</code> method.
    * This will invoke the <code>doClose</code> method so that the
    * subclass can perform any final measures required to close.
    * <p>
    * This method provides notification of errors to the monitor if
    * an <code>IOException</code> is thrown from the delegate method.
    * Once an exception is thrown the <code>notifyError</code> event
    * is issued to the monitor and <code>destroy</code> is invoked.
    *
    * @exception IOException signifies that there is an I/O problem      
    */ 
   public synchronized void close() throws IOException {
      try {
         if(open) doClose();
      } catch(IOException e) {
         mon.notifyError(out);
      } finally {
         open = false;
      }
   }

   /**
    * The <code>flushBytes</code> method provides a means for the
    * subclass implementation to flush the <code>OutputStream</code>.
    * This should flush any bytes it has buffered and flush the
    * <code>OutputStream</code> using the <code>flush</code> method.
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>OutputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception
    * to signify that the <code>notifyError</code> should be used.
    *
    * @exception IOException signifies that there is an I/O problem 
    */    
   protected abstract void flushBytes() throws IOException;

   /**
    * The <code>writeBytes</code> method provides a means for bytes
    * to be written to the <code>OutputStream</code>. This can be
    * used to apply any encoding or monitoring to data before it is    
    * written to the <code>OutputStream</code>.
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>OutputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.
    *
    * @param buf the buffer that contains the bytes to be written
    * @param off the offset within the buffer to begin writing data
    * @param len this is the number of bytes that is to be written
    * 
    * @exception IOException signifies that there is an I/O problem
    */
   protected abstract void writeBytes(byte[] buf, int off, int len) 
      throws IOException;


   /**
    * The <code>doClose</code> method provides a means for closing
    * the <code>OutputStream</code>. This can be used to close the
    * stream by perhaps applying a final message delimiter or by
    * performing an actual <code>close</code> invocation.
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>OutputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.
    *
    * @exception IOException signifies that there is an I/O problem    
    */
   protected abstract void doClose() throws IOException;
}   

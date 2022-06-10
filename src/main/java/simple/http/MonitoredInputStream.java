/*
 * MonitoredInputStream.java February 2001
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

import java.io.InputStream;
import java.io.IOException;

/**
 * The <code>MonitoredInputStream</code> class is used to perform
 * monitoring of data read from an underlying stream. This must be
 * subclassed so that behavior can be added to the reading of data
 * and so that the stream can perform a dialog with the monitor.
 * <p>
 * The <code>InputMonitor</code> is used by a monitored stream to
 * handle any I/O events that occur while read data. Events such 
 * as <code>IOException</code>'s that are thrown are handled by 
 * invoking the monitor's <code>notifyError</code> method. This 
 * is synchronized so that it can be accessed by multiple threads.
 *
 * @author Niall Gallagher
 */ 
abstract class MonitoredInputStream extends InputStream { 

   /**
    * This is used to receive all I/O event notifications.
    */
   protected InputMonitor mon;

   /**
    * This is the underlying input stream used to read data.
    */
   protected InputStream in;   

   /**
    * This determines whether the stream is open or closed.     
    */
   protected boolean open = true;
   
   /**
    * Constructor for the <code>MonitoredInputStream</code>. This is
    * used initialize the stream. Implementations must provide an
    * <code>InputStream</code> and an <code>InputMonitor</code> so
    * that notifications can be issued regarding the data read.
    *
    * @param in this is the stream that that data is read from
    * @param mon the monitor that will be notified of I/O events
    */    
   protected MonitoredInputStream(InputStream in, InputMonitor mon) {
      this.mon = mon;
      this.in = in;
   }  

   /**
    * This is used to delegate to the <code>readByte</code> method.
    * This will invoke the <code>readByte</code> method. If the
    * <code>readByte</code> throws an exception, then this is used 
    * to perform a <code>notifyError</code> notification to the 
    * <code>InputMonitor</code>, also this will close the stream.
    *
    * @return the byte read, or -1 if the input stream is empty
    *
    * @exception IOException signifies that there is an I/O error
    */
   public synchronized int read() throws IOException {   
      ensureOpen();    
      try { 
         int octet = readByte();         
         if(octet < 0) {
            mon.notifyFinished(in);
         }
         return octet;
      }catch(IOException e){
         mon.notifyError(in);
         destroy();
      }
      return -1;
   }

   /**
    * This is used to delegate to the <code>readBytes</code> method.
    * This will invoke the <code>readBytes</code> method. If the
    * <code>readBytes</code> throws an exception, then this is used 
    * to perform a <code>notifyError</code> notification to the 
    * <code>InputMonitor</code>, this will also close the stream.
    *
    * @param buf the buffer that is to be filled with the read data
    * @param off this is the start of the region to write the data
    * @param len this is the length of the region to write the data
    *
    * @return this returns the number of bytes read from the stream  
    *
    * @exception IOException signifies that there is an I/O error    
    */
   public synchronized int read(byte[] buf, int off, int len) throws IOException {
      ensureOpen(); 
      try { 
         int num = readBytes(buf, off, len);         
         if(num < 0) {
            mon.notifyFinished(in);
         }
         return num;
      }catch(IOException e){
         mon.notifyError(in);
         destroy();
      }
      return -1; 
   }

   /**
    * The is used as a driver for the <code>skipBytes</code> method.
    * This will invoke the <code>skipBytes</code> method. If the
    * <code>skipBytes</code> throws an exception, then this is used 
    * to perform a <code>notifyError</code> notification to the 
    * <code>InputMonitor</code>, also this will close the stream.
    *
    * @param amount the amount of byte to skip from the stream
    *
    * @return this returns that number bytes that were skipped
    *
    * @exception IOException signifies that there is an I/O error    
    */ 
   public synchronized long skip(long amount) throws IOException {
      ensureOpen();      
      try { 
         return skipBytes(amount);
      }catch(IOException e){
         mon.notifyError(in);
         destroy();
      }
      return 0L;         
   }

   /**
    * The is used as a driver for the <code>readyBytes</code> method.
    * This will invoke the <code>skipBytes</code> method. If there
    * is en exception thrown from the <code>readyBytes</code> method
    * then this is used to issue a <code>notifyError</code> event to
    * the <code>InputMonitor</code>, also this will close the stream.
    *
    * @return this returns that number bytes that were skipped
    *
    * @exception IOException signifies that there is an I/O error    
    */ 
   public synchronized int available() throws IOException {
      ensureOpen();
      try {
         return readyBytes();         
      }catch(IOException e) {
         mon.notifyError(in);
         destroy();
      }
      return 0;
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
    * This is used clean up the <code>InputStream</code> after an 
    * <code>IOException</code> attempting to access it. This closes
    * the <code>MonitoredInputStream</code> in such a way that the
    * <code>ensureOpen</code> method always throws an exception.
    *
    * @exception IOException signifies that there is an I/O error
    */
   protected void destroy() throws IOException {
      try {
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
    * @exception IOException signifies that there is an I/O error
    */ 
   public synchronized void close() throws IOException {
      try {
         if(open) doClose();
      } catch(IOException e) {
         mon.notifyError(in);
      } finally {
         open = false;
      }
   }
   
   /**
    * The <code>readByte</code> method is used to read bytes from 
    * the underlying <code>InputStream</code>. This method is used
    * perform monitoring and perhaps decoding of bytes read from 
    * the stream. This must contain <code>read</code> semantics.
    * <p>    
    * This is provided for convenience, it enables notification 
    * to the <code>InputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.
    *
    * @return the byte read, or -1 if the input stream is empty
    *
    * @exception IOException signifies that there is an I/O error
    */   
   protected abstract int readByte() throws IOException;

   /**
    * The <code>readBytes</code> method is used to read bytes from 
    * the underlying <code>InputStream</code>. This method is used
    * perform monitoring and perhaps decoding of bytes read from 
    * the stream. This must contain <code>read</code> semantics.
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>InputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.
    *
    * @param buf the buffer that is to be filled with the read data
    * @param off this is the start of the region to write the data
    * @param len this is the length of the region to write the data
    *
    * @return this returns the number of bytes read from the stream  
    *
    * @exception IOException signifies that there is an I/O error
    */
   protected abstract int readBytes(byte buf[], int off, int len) 
      throws IOException;

   /**
    * The <code>readyBytes</code> method is used to determine the
    * number of bytes that can be read from the stream without
    * blocking. This basically performs an <code>available</code>.
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>InputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.
    *
    * @exception IOException signifies that there is an I/O error
    */
   protected abstract int readyBytes() throws IOException;
  
   /**
    * The <code>skipBytes</code> method is used to skip a specified
    * number of bytes from the <code>InputStream</code>. This is 
    * what is used to perform the <code>skip</code> method. 
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>InputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.    
    *
    * @param amount the amount of byte to skip from the stream
    *
    * @return this returns that number bytes that were skipped
    *
    * @exception IOException signifies that there is an I/O error
    */  
   protected abstract long skipBytes(long amount) throws IOException;

   /**
    * This is used to close the underlying stream by perhaps issuing
    * a final notification to the <code>InputMonitor</code>. 
    * <p>
    * This is provided for convenience, it enables notification 
    * to the <code>InputMonitor</code> to be handled by the caller
    * of the method. Implementations of this can throw an exception    
    * to signify that the <code>notifyError</code> should be used.
    *
    * @exception IOException signifies that there is an I/O error
    */
   protected abstract void doClose() throws IOException;
}

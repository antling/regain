/*
 * FixedOutputStream.java February 2001
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
 * The <code>FixedOutputStream</code> is used to write a specified 
 * number of bytes to a given <code>OutputStream</code>. This is a 
 * <code>MonitoredOutputStream</code> class. This can be used to
 * monitor output to a given <code>OutputStream</code>, and sends
 * the monitor notification of significant I/O events. 
 * <p>
 * Events such as the writing of the number of bytes specified is 
 * given a <code>notifyFinished</code> event. When this encounters 
 * an <code>IOException</code> the exception is propagated and the
 * monitor is notified, by the <code>MonitoredOutputStream</code>.
 * If the specified number of bytes are not written when this is
 * closed, then a <code>notifyError</code> event is issued.
 *
 * @author Niall Gallagher
 */  
class FixedOutputStream extends MonitoredOutputStream {

   /**
    * Determines whether any more data can be written.
    */
   private boolean finished;
   
   /**
    * Used to determine the number of bytes written. 
    */
   private int count;

   /**
    * The number of bytes that can be written. 
    */
   private int limit;

   /**
    * Constructor for the <code>FixedOutputStream</code>. This uses 
    * an <code>OutputStream</code> which is to be written to, and
    * an <code>OutputMonitor</code> that is used for notification
    * of any significant events that occur while writing the data.
    *
    * @param out the stream that that data is to be written to
    * @param limit the maximum number of bytes that can be written
    * @param mon the monitor that will be notified of I/O events
    */ 
   public FixedOutputStream(OutputStream out, OutputMonitor mon, int limit) {
      super(out, mon);
      this.limit = limit;
      if(limit <= 0) {
         mon.notifyFinished(out);
         finished = true;
      }
   }

   /**
    * The <code>writeBytes</code> method is used to write data to 
    * the underlying <code>OutputStream</code>. This will count the
    * bytes written to the stream so that a check on the number of
    * bytes written can be made. The <code>OutputMonitor</code> is
    * given notification of the <code>notifyFinished</code> event
    * once a sufficient number of bytes are written.
    *
    * @param buf the buffer that contains the bytes to be written
    * @param off the offset within the buffer to begin writing data
    * @param len this is the number of bytes that is to be written
    * 
    * @exception IOException signifies that there is an I/O problem    
    */    
   protected void writeBytes(byte[] buf, int off, int len) throws IOException {
      int remaining = limit - count;
      int min = Math.min(len, remaining);          
      
      if(!finished) {      
         out.write(buf, off, min);                                    
         if(count + min == limit) {
            mon.notifyFinished(out);
            finished = true;
         }
         count += min;
      } 
   }
   
   /**
    * The <code>flushBytes</code> method flushes any bytes that are
    * be buffered by the underlying <code>OutputStream</code>. This
    * basically uses the output streams <code>flush</code> method.
    *
    * @exception IOException if there is an I/O problem flushing
    */
   protected void flushBytes() throws IOException {
      out.flush();
   }
      
   /**
    * The <code>doClose</code> method performs the final functions 
    * of the stream in order to close it. This will check to see 
    * that the correct number of bytes were written to the stream 
    * if there has not been sufficient bytes written this throws 
    * an <code>IOException</code>.
    *
    * @exception IOException if insufficient bytes were written 
    */ 
   protected void doClose() throws IOException {
      if(!finished) 
         throw new IOException("Bad close");
   }
}


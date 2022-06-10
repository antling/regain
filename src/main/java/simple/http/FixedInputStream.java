/*
 * FixedInputStream.java February 2001
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
 * The <code>FixedInputStream</code> class is used to read a specified 
 * number of bytes from a given <code>InputStream</code>. This is a 
 * <code>MonitoredInputStream</code> class. 
 * <p>
 * This is used to monitor and read input from a given stream. This
 * sends event notification to a monitor. Events such as the the reading
 * of the number of bytes specified is a <code>notifyFinished</code>
 * event. When there is an error the error is propagated and the monitor
 * is also notified of the event. 
 * <p>
 * If a close is invoked before the number of bytes specified has been 
 * read then this attempts to read and discard the unread bytes without 
 * blocking. However this cannot be done then a <code>notifyError</code> 
 * event is preferred to a <code>notifyFinished</code> event, this will 
 * cause the monitor to close the stream.
 *
 * @author Niall Gallagher
 */  
class FixedInputStream extends MonitoredInputStream {

   /**
    * Indicates when there is no more bytes to be read.
    */
   private boolean finished = false;

   /**
    * Counts the number of bytes already read by this. 
    */
   private int count = 0;

   /**
    * The limit to the number of bytes that can be read.
    */
   private int limit = 0;

   /**
    * This indicates whether or not the stream is open. 
    */
   private boolean open = true;     

   /**
    * Constructor for the <code>FixedInputStream</code>. This is used
    * to read a specified amount of data from a given stream. This will 
    * immediately notify the <code>InputMonitor</code> if the length 
    * specified is less than or equal to zero. If the limit is greater
    * than zero then the <code>FixedInputStream</code> will read bytes 
    * from the underlying stream until limit bytes is reached or -1 is 
    * read.
    *
    * @param in the <code>InputStream</code> that the data is
    * extracted from
    * @param mon the monitor that will be notified of input events
    * @param limit the number of bytes to be read from the stream
    */ 
   public FixedInputStream(InputStream in, InputMonitor mon, int limit) {
      super(in, mon);
      this.limit = limit;        
      if(limit <= 0) {
         mon.notifyFinished(in);                     
         finished = true;
      }
   }  
   
   /**
    * Used to read from the given <code>InputStream</code>. If a -1 
    * is read from the given stream then the monitor is notified that 
    * the stream is finished and it then returns the -1. If there are 
    * any exceptions thrown while reading from the stream then the
    * monitor is notified and the stream is closed.
    *
    * @exception IOException if the stream given has an I/O problem
    *
    * @return the byte that was read or -1 if the stream is empty
    */ 
   protected int readByte() throws IOException {
      if(finished) return -1;
      int octet = in.read();
      if(octet < 0 || (++count == limit)) {
         mon.notifyFinished(in);
         finished = true;
      }
      return octet;
   }

   /**
    * Used to read from the given <code>InputStream</code>. If a -1 
    * is read from the given stream then the monitor is notified that 
    * the stream is finished and it then returns the -1. If there are 
    * any exceptions thrown while reading from the stream then the
    * monitor is notified and the stream is closed.   
    *
    * @exception IOException if the stream given has an I/O problem
    *
    * @param buf this is the buffer that is to be filled with the data
    * @param off this is the start of the region to write the bytes
    * @param len this is the length of the region to write the bytes
    *
    * @return the byte that was read or -1 if the stream is empty
    */ 
   protected int readBytes(byte[] buf, int off, int len) throws IOException {
      if(off < 0 || off > buf.length || len < 0) {
         throw new IndexOutOfBoundsException();
      } else if(off + len > buf.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      } else if(finished) {
         return -1;
      }   
      return parseRead(buf, off, len);
   }

   /**
    * Used to read from the given <code>InputStream</code>. If a -1 
    * is read from the given stream then the monitor is notified that 
    * the stream is finished and it then returns the -1. If there are 
    * any exceptions thrown while reading from the stream then the
    * monitor is notified and the stream is closed.   
    *
    * @exception IOException if the stream given has an I/O problem
    *
    * @param buf this is the buffer that is to be filled with the data
    * @param off this is the start of the region to write the bytes
    * @param len this is the length of the region to write the bytes
    *
    * @return the byte that was read or -1 if the stream is empty
    */    
   private int parseRead(byte[] buf, int off, int len) throws IOException {
      int remaining = limit - count;
      int min = Math.min(len, remaining);   
      int amount = in.read(buf, off, min);            
            
      if(amount < 0) {
         if(count == limit) {
            finished = true;
            return -1;
         } else {
            throw new IOException("Bad stream");
         }
      } else if(count + amount == limit) {   
         mon.notifyFinished(in);               
         finished = true;
      }
      if(amount > 0) {
         count += amount;   
      }
      return amount;
   }     
   
   /**
    * This is used to determine the number of bytes available on
    * a read buffer, this will never be more than the fixed length.
    * This will return the number of bytes available using this
    * stream not the underlying stream.
    *
    * @exception IOException if there is an I/O error with the stream
    *
    * @return minimum of the number of bytes available and the limit
    */ 
   protected int readyBytes() throws IOException {
      return Math.min(limit - count, in.available());
   }
   
   /**
    * Used to skip a given number of bytes from the stream. This will 
    * notify the <code>InputMonitor</code> of any errors trying to 
    * skip the bytes from the underlying <code>InputStream</code>. 
    * This will not skip more that the specified number of bytes this 
    * <code>FixedInputStream</code> is supposed to read.
    *
    * @param amount number of bytes to skip from the input stream
    *
    * @return returns the number of bytes skipped from the stream
    *
    * @exception IOException signifies that there is an I/O error
    */ 
   protected long skipBytes(long amount) throws IOException {
      long min = Math.min(amount, limit - count);
      long skipped = in.skip(min);            
      
      count += skipped;      
      if(count == limit) {
         mon.notifyFinished(in);
         finished = true;
      }
      return skipped;        
   }

   /**
    * Skip's the remaining data so that a <code>notifyFinished</code> 
    * can be issued instead of a <code>notifyError</code>. This will 
    * determine the number of bytes available on the underlying 
    * <code>InputStream</code> by using the <code>available</code> 
    * method. This then attempts to skip the bytes. If it skips the 
    * rest of the bytes from the stream then the <code>skip</code> 
    * method will issue the <code>notifyFinished</code> event, however 
    * it there is still bytes remaining on the stream then the 
    * <code>notifyError</code> event is issued.
    *
    * @exception IOException if there is an I/O problem skipping the
    * bytes
    */ 
   protected void doClose() throws IOException {
      if(!finished) {
         skip(available()); 
      }
      if(!finished) {
         throw new IOException("Bad close");
      }
   }
}

/*
 * BufferPoller.java March 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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

import java.io.IOException;

/**
 * The <code>BufferPoller</code> provides convenience methods to
 * the <code>Poller</code> for buffering the bytes read. This is
 * used if the desired method of storing the bytes read is by
 * using a byte array. This uses an initial buffer size of one
 * kilobyte, which is typically large enough to contain headers
 * from browsers such as Internet Explorer and Mozilla. However,
 * this buffer can be expanded if the initial size is too small.
 *
 * @author Niall Gallagher
 */ 
abstract class BufferPoller extends Poller {   

   /**
    * The byte buffer that contains the HTTP header consumed. 
    */
   protected byte[] buf = new byte[1024];
   
   /**
    * This is the number of bytes the HTTP header contains. 
    */
   protected int count;
   
   /**
    * Constructor for the <code>BufferPoller</code> object. The
    * <code>BufferPoller</code> provides convenience methods so
    * that the bytes read from the pipeline can be stored and
    * retrieved from a byte array. 
    *
    * @param pipe the pipeline that contains the HTTP requests
    *
    * @throws IOException if the pipeline can not be polled
    */ 
   protected BufferPoller(Pipeline pipe) throws IOException{      
      super(pipe);
   }
 
   /**
    * This is basically a simple read method for the bytes in the 
    * <code>ByteStore</code>. This will allow a user to read from 
    * the <code>ByteStore</code> at the specified position. This
    * is read only and synchronized so concurrent threads can 
    * access the <code>ByteStore</code> without any problems.
    *
    * @param pos the offset to read from the buffer to read data
    *
    * @return the byte at the position specified in this object
    */ 
   public synchronized byte getByte(int pos) {
      if(pos > count) {
         throw new IndexOutOfBoundsException(); 
      } 
      return buf[pos];   
   }
   
   /**
    * This is basically a simple read method for the bytes in the 
    * <code>ByteStore</code>. This will allow a user to read from 
    * the <code>ByteStore</code> at the specified position. This
    * is read only and synchronized so concurrent threads can 
    * access the <code>ByteStore</code> without any problems.
    *
    * @param pos the offset to read from the buffer to read data
    * @param b the byte array that is to be filled with bytes
    */    
   public synchronized void getBytes(int pos, byte[] b) {
      getBytes(pos, b, 0, b.length);
   }

   /**
    * This is basically a simple read method for the bytes in the 
    * <code>ByteStore</code>. This will allow a user to read from 
    * the <code>ByteStore</code> at the specified position. This
    * is read only and synchronized so concurrent threads can 
    * access the <code>ByteStore</code> without any problems.
    *
    * @param pos the offset to read from the buffer to read data
    * @param b the byte array that is to be filled with bytes
    * @param len the number of bytes to be read from this
    * @param off position in the buffer to write the bytes 
    */    
   public synchronized void getBytes(int pos, byte[] b, int off, int len) {
      if(pos > count || pos + len > count) {            
         throw new IndexOutOfBoundsException(); 
      } 
      System.arraycopy(buf, pos, b, off, len);
   }
   
   /**
    * This ensures that there is enough space in the byte buffer 
    * to allow for an arbitrary size HTTP header. This method is
    * used to facilitate the <code>process</code> method. When
    * the number of bytes read from the <code>Pipeline</code> is
    * greater than the buffers capacity this allows the capacity
    * to increase. If the buffer is already larger than min then
    * the buffer will not be expanded at all.
    *
    * @param min the minimum size the buffer must be expanded to
    */     
   protected synchronized void ensureCapacity(int min) {
      if(buf.length < min) {
         int size = buf.length * 2;
         int max = Math.max(min, size);
         byte[] temp = new byte[max];         
         System.arraycopy(buf, 0, temp, 0, count); 
         buf = temp;
      }
   }

   /**
    * This returns the number of bytes that the HTTP header
    * consumed. The length is the number of bytes that can 
    * be accessed using the <code>ByteStore</code> methods.
    * The <code>ByteStore</code> will contain the bytes from
    * the start to the CRLF CRLF end token inclusive.
    *
    * @return the number of bytes the HTTP header contains
    */ 
   public synchronized int length(){
      return count;
   }
}

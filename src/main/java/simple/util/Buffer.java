/*
 * Buffer.java February 2001
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
 
package simple.util;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * The <code>Buffer</code> is intended to be a general purpose byte 
 * <code>Buffer</code>. The intended use of the <code>Buffer</code> 
 * is to allow the constructiuon of buffering for streams to be 
 * seamless and easy.
 * <p>
 * This provides several convinence methods which make the use of the 
 * <code>Buffer</code> easy and useful. The <code>Buffer</code> object 
 * allows an initial capacity to be specified however if there is a 
 * need for extra space to be added to the <code>Buffer</code> then the 
 * append methods will expand the capacity of the <code>Buffer</code> 
 * as needed. 
 * <p>
 * This is also capable of appending other <code>Buffer</code> objects,
 * which is quicker that extracting the bytes and then appending those 
 * bytes to the second <code>Buffer</code>. The append methods have 
 * much the same semantics as the <code>java.lang.StringBuffer</code>.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.ByteStore
 */ 
public final class Buffer implements ByteStore { 

   /**
    * Used to store the bytes passed to the <code>Buffer</code>.
    */
   private byte[] buf = null;

   /**
    * This is the count of the number of bytes buffered.
    */
   private int count = 0;

   /**
    * This creates a default <code>Buffer</code> object. The initial
    * capacity of the default <code>Buffer</code> object is set to 16,
    * the capacity will be expanded when the append methods are used 
    * and  there is not enough space in the <code>Buffer</code> to
    * accomodate the extra bytes.
    */     
   public Buffer() {
      this(16);
   }

   /**
    * This creates a <code>Buffer</code>. The initial capacity of
    * this <code>Buffer</code> can be specified by the size parameter, 
    * the capacity will be expanded when the append methods are used 
    * and there is not enough space in the <code>Buffer</code> to 
    * accomodate the extra bytes.
    *
    * @param size the initial capacity of the <code>Buffer</code>
    */    
   public Buffer(int size) {
      buf = new byte[size];
   }
   
   /**
    * This creates a <code>Buffer</code> with the data given. The
    * capacity will be expanded when the append methods are used 
    * and there is not enough space in the <code>Buffer</code> to
    * accomodate the extra bytes.
    *
    * @param b this is the initial data this will have
    */  
   public Buffer(byte[] b) {
      this(b, 0, b.length);
   }

   /**
    * This creates a <code>Buffer</code> with the data given. The
    * capacity will be expanded when the append methods are used 
    * and there is not enough space in the <code>Buffer</code> to
    * accomodate the extra bytes.
    *
    * @param buf this is the initial data this will have
    */    
   public Buffer(Buffer buf) {
      this(buf.buf, 0, buf.count);
   }

   /**
    * This creates a <code>Buffer</code> with the data given. The
    * capacity will be expanded when the append methods are used 
    * and there is not enough space in the <code>Buffer</code> to
    * accomodate the extra bytes.
    *
    * @param b this is the initial data this will have
    * @param len this is the number of bytes to be taken
    * @param off this is the offset to take the bytes from
    */    
   public Buffer(byte[] b, int off, int len) {
      buf = new byte[len];
      count = 0;
      append(b, off, len);  /* checks bounds */
   }

   /**
    * This creates a <code>Buffer</code> with the data given. The
    * capacity will be expanded when the append methods are used 
    * and there is not enough space in the <code>Buffer</code> to
    * accomodate the extra bytes.
    *
    * @param buf this is the initial data this will have
    * @param len this is the number of bytes to be taken
    * @param off this is the offset to take the bytes from
    */       
   public Buffer(Buffer buf, int off, int len) {
      this.buf = new byte[len];
      count = 0;
      append(buf, off, len);  /* checks bounds */
   }
   
   
   /**
    * This method is used to append bytes to the end of the
    * <code>Buffer</code>. This will expand the capacity of 
    * the <code>Buffer</code> if there is not enough space 
    * to accomodate the extra bytes. 
    *
    * @param b this is the data that is to be append to this
    * <code>Buffer</code>
    */  
   public void append(byte b) {
      if(count >= buf.length) {
         ensureCapacity(buf.length + 1);
      }
      buf[count++] = b;   
   }

   /**
    * This method is used to append bytes to the end of the
    * <code>Buffer</code>. This will expand the capacity of 
    * the <code>Buffer</code> if there is not enough space 
    * to accomodate the extra bytes. 
    *
    * @param b this is the data that is to be append to this
    * <code>Buffer</code>
    */  
   public void append(byte[] b) {
      if(b.length + count > buf.length) {
         ensureCapacity(b.length + count);
      }
      System.arraycopy(b, 0, buf, count, b.length);
      count += b.length;
   }

   /**
    * This method is used to append bytes to the end of the
    * <code>Buffer</code>. This will expand the capacity of 
    * the <code>Buffer</code> if there is not enough space 
    * to accomodate the extra bytes. 
    *
    * @param b this is the data that is to be append to this
    * <code>Buffer</code>
    * @param len the number of bytes that are to be taken
    * @param off the offset to take the bytes from the array
    */  
   public void append(byte[] b, int off, int len) {
      if(off < 0 || len < 0 || off + len > b.length) {
         throw new IndexOutOfBoundsException();
      } else if(off > b.length) {            
         throw new IndexOutOfBoundsException(); 
      }
      if(len + count > buf.length) {
         ensureCapacity(count + len);
      }
      System.arraycopy(b, off, buf, count, len);
      count += len;
   }

   /**
    * This method is used to append bytes to the end of the
    * <code>Buffer</code>. This will expand the capacity of 
    * the <code>Buffer</code> if there is not enough space 
    * to accomodate the extra bytes. 
    *
    * @param buf the data that is to be append to this
    * <code>Buffer</code>
    */  
   public void append(Buffer buf) {
      append(buf.buf, 0, buf.count);
   }

   /**
    * This method is used to append bytes to the end of the 
    * <code>Buffer</code>. This will expand the capacity of 
    * the <code>Buffer</code> if there is not enough space 
    * to accomodate the extra bytes. 
    *
    * @param buf this is the data that is to be append to this
    * <code>Buffer</code>
    * @param len the number of bytes that are to be taken
    * @param off the offset to take bytes from the array
    */ 
   public void append(Buffer buf, int off, int len) {
      if(off + len > buf.count) {  /* append(byte[]) does the rest */
         throw new IndexOutOfBoundsException();
      }       
      append(buf.buf, off, len);
   }

   /**
    * This method is used to insert bytes into the <code>Buffer</code>
    * at the specified position. This will insert the bytes into the
    * <code>Buffer</code> without over writing any bytes in the
    * <code>Buffer</code>. This will simply move the bytes that come
    * before the specified position up the ways and insert the data.
    * This will throw an IndexOutOfBoundsException if the insert is
    * done at a position where there is no bytes, i.e. must be in the
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to insert data
    * @param b this is the data that is to be inserted into the
    * <code>Buffer</code>
    */ 
   public void insert(int pos, byte b) {
      if(pos < 0 || pos >= count) {      
         throw new IndexOutOfBoundsException();
      }
      if(count + 1 > buf.length) {
         ensureCapacity(count + 1);
      }      
      System.arraycopy(buf, pos, buf, pos + 1, count - pos);
      buf[pos] = b;      
   }

   /**
    * This method is used to insert bytes into the <code>Buffer</code>
    * at the specified position. This will insert the bytes into the
    * <code>Buffer</code> without over writing any bytes in the
    * <code>Buffer</code>. This will simply move the bytes that come
    * before the specified position up the ways and insert the data.
    * This will throw an IndexOutOfBoundsException if the insert is
    * done at a position where there is no bytes, i.e. must be in the
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to insert data
    * @param b this is the data that is to be inserted into the
    * <code>Buffer</code>
    */   
   public void insert(int pos, byte[] b) {
      if (pos < 0 || pos >= count) {
         throw new IndexOutOfBoundsException();
      }
      if(count + b.length > buf.length) {
         ensureCapacity(count + b.length);
      }   
      System.arraycopy(buf, pos, buf, pos + b.length, b.length);
      System.arraycopy(buf, pos, b, 0, b.length);
   }   

   /**
    * This method is used to insert bytes into the <code>Buffer</code>
    * at the specified position. This will insert the bytes into the
    * <code>Buffer</code> without over writing any bytes in the
    * <code>Buffer</code>. This will simply move the bytes that come
    * before the specified position up the ways and insert the data.
    * This will throw an IndexOutOfBoundsException if the insert is
    * done at a position where there is no bytes, i.e. must be in the
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to insert data
    * @param b this is the data that is to be inserted into the
    * <code>Buffer</code>
    * @param len this is the number of bytes that are to be taken
    * @param off this is the offset to take the bytes from the array
    */   
   public void insert(int pos, byte[] b, int off, int len) {
      if(off < 0 || len < 0 || off + len > b.length) {
         throw new IndexOutOfBoundsException();
      } else if(pos >= count || pos < 0) {            
         throw new IndexOutOfBoundsException(); 
      }   
      if(count + len > buf.length) {
         ensureCapacity(count + len);
      }
      System.arraycopy(buf, pos, buf, pos + len, len);
      System.arraycopy(buf, pos, b, off, len);
   }   

   /**
    * This method is used to insert bytes into the <code>Buffer</code>
    * at the specified position. This will insert the bytes into the
    * <code>Buffer</code> without over writing any bytes in the
    * <code>Buffer</code>. This will simply move the bytes that come
    * before the specified position up the ways and insert the data.
    * This will throw an IndexOutOfBoundsException if the insert is
    * done at a position where there is no bytes, i.e. must be in the
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to insert data
    * @param buf this is the data that is to be inserted into the
    * <code>Buffer</code>
    */      
   public void insert(int pos, Buffer buf) {
      insert(pos, buf.buf, 0, buf.count);
   }

   /**
    * This method is used to insert bytes into the <code>Buffer</code>
    * at the specified position. This will insert the bytes into the
    * <code>Buffer</code> without over writing any bytes in the
    * <code>Buffer</code>. This will simply move the bytes that come
    * before the specified position up the ways and insert the data.
    * This will throw an IndexOutOfBoundsException if the insert is
    * done at a position where there is no bytes, i.e. must be in the
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to insert data
    * @param buf this is the data that is to be inserted into the
    * <code>Buffer</code>
    * @param len this is the number of bytes that are to be taken
    * @param off this is the offset to take the bytes from the array
    */    
   public void insert(int pos, Buffer buf, int off, int len) {
      if(off + len > buf.count) { /* insert(byte[] b) does the rest */
         throw new IndexOutOfBoundsException();
      }    
      insert(pos, buf.buf, off, len);
   }

   /**
    * This is used to delete a region of data from the <code>Buffer</code>. 
    * This will ensure that all the bytes that follow the position of the 
    * delete will be copied down the so that the information is removed 
    * from the <code>Buffer</code>.
    *
    * @param pos this is the position that the delete is to be performed
    * on
    */     
   public void delete(int pos) {
      if(pos >= count || pos < 0) {
         throw new IndexOutOfBoundsException();
      } 
      System.arraycopy(buf, pos + 1, buf, pos, count - pos + 1);       
      count--;
   }
   
   /**
    * This is used to delete a region of data from the <code>Buffer</code>. 
    * This will ensure that all the bytes that follow the position of the 
    * delete will be copied down the so that the information is removed from 
    * the <code>Buffer</code>.
    *
    * @param pos this is the position that the delete is to be performed
    * on
    * @param len this is the number of bytes after pos that are to be
    * removed
    */    
   public void delete(int pos, int len) {
      if(pos > count || pos + len > count) { 
         throw new IndexOutOfBoundsException();
      } else if(pos < 0 || len < 0) {
         throw new IndexOutOfBoundsException();  
      }      
      System.arraycopy(buf, pos + len, buf, pos, count - pos + len);
      count -= len;
   }
   
   /**
    * This is used to examine the bytes that are stored in the
    * <code>Buffer</code>. This does a byte comparison of the data
    * that is stored in the <code>Buffer</code>. The comparison is
    * done from an arbitrary offset within the <code>Buffer</code>
    * data.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to compare data
    * @param b this is the data that is to be compared to the
    * <code>Buffer</code>
    * @param len this is the number of bytes that are to be compares
    * @param off this is the offset to compare the bytes from the data
    *
    * @return this returns true if the region given matches the
    * <code>Buffer</code>s
    */      
   public boolean regionMatches(int pos, byte[] b, int off, int len) {
      if(off < 0 || len < 0 || off + len > b.length) {
         throw new IndexOutOfBoundsException();
      } else if(pos + len > count || pos < 0) {            
         throw new IndexOutOfBoundsException(); 
      }  
      for(int i = len + off; off < i; off++) {
         if(buf[pos++] != b[off]) 
            return false;         
      }
      return true;
   }

   /**
    * This is used to examine the bytes that are stored in the
    * <code>Buffer</code>. This does a byte comparison of the data
    * that is stored in the <code>Buffer</code>. The comparison is
    * done from an arbitrary offset within the <code>Buffer</code>
    * data.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to compare data
    * @param buf this is the data that is to be compared to the
    * <code>Buffer</code>
    * @param len this is the number of bytes that are to be compares
    * @param off this is the offset to compare the bytes from the data
    *
    * @return this returns true if the region given matches the
    * <code>Buffer</code>s.
    */     
   public boolean regionMatches(int pos, Buffer buf, int off, int len) {
      if(off + len > buf.count) {
         throw new IndexOutOfBoundsException();
      }      
      return regionMatches(pos, buf.buf, off, len);
   }

   /**
    * This is used to compare the contents of one <code>Buffer</code>
    * with the contents of another. If the bytes are that same this is
    * true.
    *
    * @param buf this is the data that is to be compared with this
    *
    * @return this returns true if the <code>Buffer</code>s are
    * equal
    */ 
   public boolean equals(Buffer buf) {
      if(buf.count != count) {
         return false;
      }
      return regionMatches(0, buf.buf, 0, count);
   }

  
   /**
    * This is used to write bytes into the <code>Buffer</code> at 
    * an arbitrary position. This unlike the insert methods will
    * overwrite the data found at that position. This will throw an
    * IndexOutOfBoundsException if the position that the data is
    * attempting to write to is greater than the <code>Buffer</code>
    * length.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to write the data
    * @param b this is the data that is to be written into the
    * <code>Buffer</code>
    */ 
   public void setByte(int pos, byte b) {
      if(pos >= count || pos < 0) {
         throw new IndexOutOfBoundsException();
      }     
      buf[pos] = b;
   }

   /**
    * This is used to write bytes into the <code>Buffer</code> at 
    * an arbitrary position. This unlike the insert methods will
    * overwrite the data found at that position. This will throw an
    * IndexOutOfBoundsException if the position that the data is
    * attempting to write to is greater than the <code>Buffer</code>
    * length.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to write the data
    * @param b this is the data that is to be written into the
    * <code>Buffer</code>
    * @param len this is the number of bytes that are to be taken
    * @param off this is the offset to take the bytes from the array
    */    
   public void setBytes(int pos, byte[] b, int off, int len) {
      if(off < 0 || len < 0 || off + len > b.length) {
         throw new IndexOutOfBoundsException();
      } else if(pos >= count || pos < 0) {            
         throw new IndexOutOfBoundsException(); 
      }    
      System.arraycopy(b, off, buf, pos, len); 
   }

   /**
    * This is used to write bytes into the <code>Buffer</code> at 
    * an arbitrary position. This unlike the insert methods will
    * overwrite the data found at that position. This will throw an
    * IndexOutOfBoundsException if the position that the data is
    * attempting to write to is greater than the <code>Buffer</code>
    * length.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to write the data
    * @param buf this is the data that is to be written into the
    * <code>Buffer</code>
    */ 
   public void setBytes(int pos, Buffer buf) {
      setBytes(pos, buf.buf, 0, buf.count);
   }

   /**
    * This is used to write bytes into the <code>Buffer</code> at 
    * an arbitrary position. This unlike the insert methods will
    * overwrite the data found at that position. This will throw an
    * IndexOutOfBoundsException if the position that the data is
    * attempting to write to is greater than the <code>Buffer</code>
    * length.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to write the data
    * @param buf this is the data that is to be written into the
    * <code>Buffer</code>
    * @param len this is the number of bytes that are to be taken
    * @param off this is the offset to take the bytes from the array
    */  
   public void setBytes(int pos, Buffer buf, int off, int len) {
      if(pos + off > buf.count) {
         throw new IndexOutOfBoundsException();
      }       
      setBytes(pos, buf.buf, off, len);
   }
 
   /**
    * This is used to read data from the <code>Buffer</code>. This 
    * will read data from the <code>Buffer</code> at an arbitrary
    * position. An IndexOutOfBoundsException will be thrown if there
    * is an attempt to read data outside the <code>Buffer</code>s
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to read the data
    * @return this will return the byte that can be found at position
    * pos
    */ 

   public byte getByte(int pos) {
      if(pos >= count || pos < 0) { 
         throw new IndexOutOfBoundsException();
      }
      return buf[pos];
   }

   /**
    * This is used to read data from the <code>Buffer</code>. This 
    * will read data from the <code>Buffer</code> at an arbitrary
    * position. An IndexOutOfBoundsException will be thrown if there
    * is an attempt to read data outside the <code>Buffer</code>'s
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to read the data
    * @param b this is the <code>Buffer</code> that the bytes are 
    * to be read into
    */ 
   public void getBytes(int pos, byte[] b){
      getBytes(pos,b,0,b.length);
   }
   
   /**
    * This is used to read data from the <code>Buffer</code>. This 
    * will read data from the <code>Buffer</code> at an arbitrary
    * position. An IndexOutOfBoundsException will be thrown if there
    * is an attempt to read data outside the <code>Buffer</code>'s
    * bounds.
    *
    * @param pos this is the position within the <code>Buffer</code>
    * to read the data
    * @param b this is the <code>Buffer</code> that the bytes are 
    * to be read into
    * @param len this is the number of bytes to be read from the
    * <code>Buffer</code>
    * @param off this is the offset to take the bytes from the
    * <code>Buffer</code>
    */    
   public void getBytes(int pos, byte[] b, int off, int len) {
      if(off < 0 || len < 0 || off + len > b.length) {
         throw new IndexOutOfBoundsException();
      } else if(pos + len > count || pos < 0) {            
         throw new IndexOutOfBoundsException(); 
      }     
      System.arraycopy(buf, pos, b, off, len);
   }
   
   /**
    * This is used to emit data from the <code>Buffer</code> out to 
    * an <code>OutputStream</code>. This will invoke a direct call 
    * to write in the <code>OutputStream</code>. This method will
    * not flush the stream and exceptions will propagate.
    *
    * @param out this is the <code>OutputStream</code> that the byte
    * are to write to
    *
    * @exception IOException this will be thrown if there is an I/O
    * error
    */ 
   public void writeTo(OutputStream out) throws IOException {
      out.write(buf, 0, count);      
   }
   
   /**
    * This is used to emit data from the <code>Buffer</code> out to 
    * an <code>OutputStream</code>. This will invoke a direct call 
    * to write in the <code>OutputStream</code>. This method will
    * not flush the stream and exceptions will propagate.
    *
    * @param out this is the <code>OutputStream</code> that the byte
    * are to write to
    * @param len this is the number of bytes to be write from the
    * <code>Buffer</code>
    * @param off this is the offset to take the bytes from the
    * <code>Buffer</code>
    *
    * @exception IOException this will be thrown if there is an I/O
    * error
    */    
   public void writeTo(OutputStream out, int off, int len) throws IOException {      
      if(off < 0 || len < 0 || off + len > count) {
         throw new IndexOutOfBoundsException();
      }
      out.write(buf, off, len);
   }

   /**
    * This ensure that there is enough space in the <code>Buffer</code> 
    * to allow for more chars to be added. If the <code>Buffer</code> 
    * is already larger than min then the <code>Buffer</code> will not 
    * be expanded at all.
    *
    * @param min the minimum size needed for the <code>Buffer</code>
    */   
   private void ensureCapacity(int min) { /* just get it from vector */
      if(buf.length < min) {
         int size = buf.length * 2;
         int max = min > size ? min : size;
         byte[] temp = new byte[max];         
         System.arraycopy(buf, 0, temp, 0, count); 
         buf = temp;
      }
   }   

   /**
    * This method is used so that the <code>Buffer</code> can be
    * represented as an input stream. Note however that this does not
    * make a copy of the data but rather provides direct access to the
    * data that is being used. This means that this should not be used
    * concurrently with <code>Buffer</code>s methods.
    *
    * @return this returns an <code>InputStream</code> of the data
    * within this <code>Buffer</code>
    */    
   public InputStream getInputStream() {
      return new ByteArrayInputStream(buf, 0, count);
   }
   
   /**
    * This is used to create a copy of the bytes that are stored in the 
    * <code>Buffer</code> object. This does not return the internal
    * bytes of the <code>Buffer</code> but rather creates a byte array
    * <code>Buffer</code>.length in length and fills the byte array
    * with the data in the <code>Buffer</code> before its returned.
    *
    * @return this returns a byte array that contains the
    * <code>Buffer</code>s contents
    */ 
   public byte[] toArray() {
      byte[] copy = new byte[count];
      System.arraycopy(buf, 0, copy, 0, count);
      return copy;
   }

   /**
    * This will clear all data from the <code>Buffer</code>.
    * This simply sets the count to be zero.
    */ 
   public void clear() {
      count = 0;
   }  
   
   /**
    * This returns the number of bytes that have been 
    * appended or written to this <code>Buffer</code>.
    *
    * @return the number bytes in the <code>Buffer</code>
    */ 
   public int length() {
      return count;
   }
}






/*
 * ByteStore.java February 2001
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

/**
 * The <code>ByteStore</code> class is used to provide access 
 * to a <code>byte</code> buffer. This allows objects to share 
 * a <code>byte</code> buffer without the problems of readers 
 * and writers changing the data. This data is read only and 
 * it provides simple methods to access the bytes stored.
 *
 * @author Niall Gallagher
 */ 
public interface ByteStore {

   /**
    * This is basically a simple read method for the bytes in 
    * the <code>ByteStore</code>. This will allow a user to 
    * read from the <code>ByteStore</code> at the specified 
    * positions. Concurrent threads can access the 
    * <code>ByteStore</code> because it is a read-only object.
    *
    * @param pos the position to read from the 
    * <code>ByteStore</code>
    *
    * @return the byte at the position specified in this 
    * object
    */ 
   public byte getByte(int pos);
   
   /**
    * This is basically a simple read method for the bytes in 
    * the <code>ByteStore</code>. This will allow a user to 
    * read from the <code>ByteStore</code> at the specified 
    * positions. Concurrent threads can access the 
    * <code>ByteStore</code> because it is a read-only object.
    *
    * @param pos the position to read from the 
    * <code>ByteStore</code>
    * @param b the byte buffer to fill with the bytes 
    */    
   public void getBytes(int pos, byte[] b);

   /**
    * This is basically a simple read method for the bytes in 
    * the <code>ByteStore</code>. This will allow a user to 
    * read from the <code>ByteStore</code> at the specified 
    * positions. Concurrent threads can access the 
    * <code>ByteStore</code> because it is a read-only object.
    *
    * @param pos the position to read from the 
    * <code>ByteStore</code>
    * @param b the byte buffer to fill with the bytes 
    * @param off position in the buffer to write the bytes 
    * @param len the number of bytes to be read from 
    */    
   public void getBytes(int pos, byte[] b, int off, int len);
   
   /**
    * This returns the number of bytes that this 
    * <code>ByteStore</code> object contains.
    *
    * @return the number of bytes stored
    */ 
   public int length();
}

/*
 * Enumerator.java February 2001
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

import java.util.Enumeration;

/**
 * This object is used to enumerate an array of objects. This
 * will take an array and traverse each offset in the array and
 * return the contents of that offset. If the data in the array
 * offset is null then this is what is returned, also when all
 * the elements of the array have been traversed then this will
 * return null.
 *
 * @author Niall Gallagher
 */ 
public class Enumerator implements Enumeration {

   /**
    * This is the array of elements to be traversed.
    */
   private Object[] arr;

   /**
    * This is the current offset within the array.
    */
   private int off;
   
   /**
    * This is number of objects to enumerate. 
    */
   private int len;

   /**
    * This creates an enumerator object for an array of
    * objects. Every object in this array will be returned
    * from the array. This uses the array within this object
    * so any changes to the array will change the enumerator.
    *
    * @param arr this is the array that is to be enumerated
    */ 
   public Enumerator(Object[] arr){
      this(arr, 0, arr.length);
   }

   /**
    * This creates an enumerator object for an array of
    * objects. Every object in this array will be returned
    * from the array. This uses the array within tis object
    * so any changes to the array will change the enumerator.
    *
    * @param arr this is the array that is to be enumerated    
    * @param off this is the offset to begin enumerating
    * @param len this is the number of objects to enumerate
    */ 
   public Enumerator(Object[] arr, int off, int len) {
      this.arr = arr;
      this.off = off;
      this.len = len;
   }

   /**
    * This will return the <code>nextElement</code> in the array that
    * is to be returned. Once all elements are enumerated then this
    * returns null. This may throw an IndexOutOfBoundsException
    * if the len parameter in the constructor was out of bounds.
    *
    * @return this returns the contents of the array given
    */ 
   public Object nextElement() {
      if(len-- <= 0){
         return null;
      }
      return arr[off++];
   }

   /**
    * This returns true if there is more elements in the
    * array to be enumerated using the <code>nextElement</code>
    * method.
    *
    * @return this returns true if there is more elements
    */ 
   public boolean hasMoreElements() {
      return len > 0;
   }
}

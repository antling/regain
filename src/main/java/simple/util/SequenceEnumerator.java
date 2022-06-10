/*
 * SequenceEnumerator.java February 2001
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
 * This is an <code>Enumerator</code> for <code>Enumeration</code> 
 * objects to be enumerated in sequence. This will traverse a list 
 * of <code>Enumeration</code> objects and enumerate the elements 
 * of the non-null objects in the list. This <code>Enumeration</code> 
 * is done in sequence so that the first <code>Enumeration</code> 
 * object in the list is the first <code>Enumeration</code> object 
 * to be emptyed.
 *
 * @author Niall Gallagher
 */ 
public class SequenceEnumerator implements Enumeration  {

   /**
    * This is the list of <code>Enumeration</code> objects. 
    */
   private Enumeration[] e;

   /**
    * This is the current <code>Enumeration</code> object used.
    */
   private int off;

   /**
    * This is a constructor that takes two <code>Enumeration</code>'s. 
    * This will enumerate the <code>Enumeration</code> objects as they 
    * appear from the first index 0, to the final index e.length.
    *
    * @param e this is the list of <code>Enumeration</code> objects
    * used
    */ 
   public SequenceEnumerator(Enumeration e[]) {
      this.e = e; 
   }

   /**
    * This is a constructor that takes two <code>Enumeration</code>'s. 
    * This will enumerate the first parameter, when the first one is 
    * finished then this will enumerate the second <code>Enumeration</code>.
    *
    * @param a this is the first <code>Enumeration</code> to be
    * enumerated
    * @param b this is the second <code>Enumeration</code> to be
    * enumerated
    */ 
   public SequenceEnumerator(Enumeration a, Enumeration b){
      e = new Enumeration[2];
      e[0] = a;
      e[1] = b;
   }

   /**
    * This is used to retrive the <code>nextElement</code> from the
    * list of <code>Enumeration</code> objects. This will empty the
    * <code>Enumeration</code> objects until the last
    * <code>Enumeration</code> object is used or the list ends.
    *
    * @return this will enumerate the elements in sequence
    */ 
   public Object nextElement() {
      if(!hasMoreElements()){
         return null;
      }
      return e[off].nextElement();     
   } 

   /**
    * This will check to see if there are any more elements in the
    * list of <code>Enumeration</code> objects. This will traverse
    * the list of <code>Enumeration</code>s, find the first non-null
    * <code>Enumeration</code> and check to see if there is anymore
    * elements left if not it will continue.
    *
    * @return this returns true if there is any more elements left
    */ 
   public boolean hasMoreElements(){
      if(off >= e.length){
         return false;
      }
      if(e[off].hasMoreElements()){
         return true;         
      }
      for(;off < e.length; off++){
         if(e != null && e[off].hasMoreElements()){
            return true;            
         }
      }
      return false;
   }
}

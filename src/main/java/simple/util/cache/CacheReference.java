/*
 * CacheReference.java February 2001
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
 
package simple.util.cache;

import java.lang.ref.SoftReference;

/**
 * This is used so that objects in the <code>Cache</code> can be removed
 * when all references to it are gone. The <code>CacheReference</code> 
 * is a special type of reference. Once an object is enqueued into the 
 * <code>Cache</code> then that object is wrapped in a 
 * <code>CacheReference</code> and scheduled for removal. 
 * <p>
 * Once the objects time in the queue has finished then the reference
 * is obtained. The <code>CacheReference</code> is then searched to see 
 * if it still holds the cached item. If it does not hold the cached 
 * item then that item must have lost all its references because the 
 * semantics of the <code>java.lang.ref.SoftReference</code> says that 
 * it will release an object for garbage collection only when all 
 * references to it are gone. This means that it has already been 
 * removed from the cache. If however the <code>CacheReference</code> 
 * still contains the cached item then the reference can behave as a 
 * key into the <code>Cache</code> for the cached item. The reference 
 * can then be used to access the cached item for removal, or whatever.
 *
 * @author Niall Gallagher
 */ 
final class CacheReference extends SoftReference {

   /**
    * Remember the key for the cached object.
    */
   private Object key;

   /**
    * This will create a reference object that acts like a key.
    *
    * @param key this is the key that references the object
    * @param obj this is the object that is to be stored
    */ 
   public CacheReference(Object key, Object obj){
      super(obj);
      this.key = key;
   }

   /**
    * This allows this reference to act like a key when it is used
    * by any data arbatrary structures such as <code>Hashtable</code>, 
    * <code>Vector</code> etc.
    *
    * @return the referents <code>hashCode</code>, i.e. the original 
    * keys <code>hashCode</code>
    */ 
   public int hashCode(){
      return key.hashCode();
   }

   /**
    * This allows this reference to act like a key.
    *
    * @param obj the objects used in comparison
    *
    * @return true if obj this is equal to the key
    */    
   public boolean equals(Object obj){
      return key.equals(obj);      
   }   
}

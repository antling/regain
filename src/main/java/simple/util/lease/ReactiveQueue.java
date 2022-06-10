/*
 * ReactiveQueue.java May 2004
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

package simple.util.lease; 

import simple.util.PriorityQueue;

/**
 * This class implements a <code>ReactiveQueue</code>. This class 
 * is implemented in such a way that objects are added using an  
 * <code>add</code> function. The <code>add</code> function takes 
 * two paramaters an object and a long. 
 * <p>
 * The object represents an item in the queue, the long indicates 
 * its priority in the queue. The remove function in this class 
 * returns the object first in the queue and that object is removed 
 * from the queue permenantly. This differs from a conventional
 * priority queue in that it allows the priority of elements to
 * be changed by adding an existing element with a new priority.
 *
 * @author Niall Gallagher
 */  
final class ReactiveQueue extends PriorityQueue {   

   /**
    * Creates a new <code>ReactiveQueue</code> object. The
    * <code>ReactiveQueue</code> object allows objects to be
    * entered into the queue and to leave in the order of 
    * priority, the highest priority get's to leave first.
    */     
   public ReactiveQueue() {
      super(20);
   }
   
   /**
    * Creates a new <code>ReactiveQueue</code> object. The
    * <code>ReactiveQueue</code> object allows objects to 
    * be entered into the queue an to leave in the order of 
    * priority, the highest priority get's to leave first.
    *
    * @param capacity the initial capacity of the queue before 
    * a resize
    */    
   public ReactiveQueue(int capacity) {
      super(capacity);
   }
   
   /**
    * Creates a new <code>ReactiveQueue</code> object. The
    * <code>ReactiveQueue</code> object allows objects to 
    * be entered into the queue an to leave in the order of 
    * priority, the highest priority get's to leave first.
    *
    * @param capacity the initial capacity of the queue before 
    * a resize
    * @param maxPriority is the maximum possible priority for 
    * an object
    */ 
   public ReactiveQueue(int capacity, long maxPriority) {
      super(capacity, maxPriority);
   }

   /**
    * This function adds the element into the <code>ReactiveQueue</code>, 
    * its priority is the long priority. The way in which priority can be 
    * associated with the elements of the queue is by keeping the priority 
    * and the elements array entrys parallel.
    *
    * @param element is the object that is to be entered into this
    * <code>ReactiveQueue</code>
    * @param priority this is the priority that the object holds in the
    * <code>ReactiveQueue</code>
    */ 
   public void add(Object element, long priority) {   
      if(count >= capacity){
         expandCapacity();
      }            
      int pos = search(element);      

      if(pos < 0) {
         pos = ++count;
      }
      value[pos] = priority;
      data[pos] = element;

      if(value[pos/2] < priority){
         bubbleUp(pos);      
      }else {
         bubbleDown(pos);
      }
   }  

   /**
    * Search for the element within the queue. If the element exists
    * then return its position within the element array. If the given
    * element does not exist then return -1 to indicate a failure. The
    * equality of the element is determined by the objects reference
    * rather than using the <code>Object.equals</code> method.
    *
    * @param element this is the element that is to be searched for
    *
    * @return this returns the index of the element if it is found
    */
   private int search(Object element){
      for(int i = 1; i <= count; i++) {
         if(element == data[i]) {
            return i;
         }
         if(element.equals(data[i])){
            return i;
         }
      }
      return -1;
   }
}

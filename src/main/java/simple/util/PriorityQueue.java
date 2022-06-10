/*
 * PriorityQueue.java February 2001
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

import java.io.Serializable;

/**
 * This class implements a <code>PriorityQueue</code>. This class 
 * is implemented in such a way that objects are added using an  
 * <code>add</code> function. The <code>add</code> function takes 
 * two paramaters an object and a long. 
 * <p>
 * The object represents an item in the queue, the long indicates 
 * its priority in the queue. The remove function in this class 
 * returns the object first in the queue and that object is removed 
 * from the queue permentaly. 
 *
 * @author Niall Gallagher
 */  
public class PriorityQueue implements Serializable {   

   /**
    * The maximum priority possible in this priority queue.
    */      
   protected long maxPriority = Long.MAX_VALUE; 

   /** 
    * This contains the list of objects in the queue.
    */   
   protected Object[] data;

   /** 
    * This contains the list of prioritys in the queue. 
    */   
   protected long[] value;
   
   /** 
    * Holds the number of elements currently in the queue.
    */   
   protected int count;   

   /**
    * This holds the number elements this queue can have.
    */
   protected int capacity;

   /**
    * Creates a new <code>PriorityQueue</code> object. The
    * <code>PriorityQueue</code> object allows objects to be
    * entered into the queue and to leave in the order of 
    * priority, the highest priority get's to leave first.
    */     
   public PriorityQueue() {
      init(20);
   }
   
   /**
    * Creates a new <code>PriorityQueue</code> object. The
    * <code>PriorityQueue</code> object allows objects to 
    * be entered into the queue an to leave in the order of 
    * priority, the highest priority get's to leave first.
    *
    * @param capacity the initial capacity of the queue before 
    * a resize
    */    
   public PriorityQueue(int capacity) {
      init(capacity);
   }
   
   /**
    * Creates a new <code>PriorityQueue</code> object. The
    * <code>PriorityQueue</code> object allows objects to 
    * be entered into the queue an to leave in the order of 
    * priority, the highest priority get's to leave first.
    *
    * @param capacity the initial capacity of the queue before 
    * a resize
    * @param maxPriority is the maximum possible priority for 
    * an object
    */ 
   public PriorityQueue(int capacity, long maxPriority) {
      this.maxPriority = maxPriority;
      init(capacity);
   }


   /**
    * This is an initializer for the object. It basically initializes 
    * an array of long called value to represent the priorities of 
    * the objects, it also creates an array of objects to be used 
    * in parallel with the array of longs, to represent the objects 
    * entered, these can be used to sequence the data.
    *
    * @param size initial capacity of the queue, it can be resized
    */ 
   private void init(int size) {
      capacity = size;
      data = new Object[capacity+1];   
      value = new long[capacity+1];
      value[0] = maxPriority;
      data[0] = null;
   } 

   /**
    * This function adds the given object into the <code>PriorityQueue</code>, 
    * its priority is the long priority. The way in which priority can be 
    * associated with the elements of the queue is by keeping the priority 
    * and the elements array entrys parallel.
    *
    * @param element is the object that is to be entered into this
    * <code>PriorityQueue</code>
    * @param priority this is the priority that the object holds in the
    * <code>PriorityQueue</code>
    */ 
   public void add(Object element, long priority) {   
      if(count++ >= capacity){
         expandCapacity();
      }            
      value[count] = priority;
      data[count] = element;       
      bubbleUp(count);
   }    

   /**
    * Remove is a function to remove the element in the queue with the
    * maximum priority. Once the element is removed then it can never be
    * recovered from the queue with further calls. The lowest priority
    * object will leave last.
    *
    * @return the object with the highest priority or, if empty, null
    */     
   public Object remove() {         
      if(count == 0) return null;       
      Object element = data[1];      
      
      data[1] = data[count];
      value[1] = value[count];      
      data[count] = null;
      value[count--] = 0L;      
      bubbleDown(1);
      return element;
   }

   /**
    * Bubble down is used to put the element at subscript 'pos' into 
    * it's rightfull place in the heap (a heap is another name used
    * for <code>PriorityQueue</code>). If the priority of an element
    * at subscript 'pos' is less than it's children then it must
    * be put under one of these children, that is, the ones with the 
    * maximum priority must come first. 
    *
    * @param pos the position the bubble down procedure starts from
    */     
   protected void bubbleDown(int pos) {   
      Object element = data[pos];
      long priority = value[pos];      
      int child;      
      
      for(; pos * 2 <= count; pos = child) {
         child = pos * 2;   
         
         if(child != count) {
            if(value[child] < value[child + 1])
               child++; 
         }
         if(priority < value[child]) {                   
            value[pos] = value[child];
            data[pos] = data[child];
         } else {
            break;
         }             
      }
      value[pos] = priority;
      data[pos] = element;
   }

   /**
    * Bubble up is used to place an element relatively low in the 
    * queue to it's rightful place higher in the queue, but only 
    * if it's priority allows it to do so, similar to bubbleDown 
    * only in the other directon this swaps out its parents.
    *
    * @param pos the position the bubble up procedure starts from
    */     
   protected void bubbleUp(int pos) {         
      Object element = data[pos];
      long priority = value[pos];      
      
      while(value[pos/2] < priority) {            
         value[pos] = value[pos/2];         
         data[pos] = data[pos/2];
         pos /= 2;
      }
      value[pos] = priority;
      data[pos] = element;      
   }

   /**
    * This ensures that there is enough space to keep adding elements 
    * to the priority queue. It is however advised to make the capacity 
    * of the queue large enough so that this will not be used as it is
    * an expensive method. This will copy across from 0 as 'off' equals 
    * 0 is contains some important data.
    */ 
   protected void expandCapacity() {
      capacity = count * 2;
      Object[] elements = new Object[capacity+1];
      long[] prioritys = new long[capacity+1];
      System.arraycopy(data,0,elements, 0, data.length);
      System.arraycopy(value, 0,prioritys, 0, data.length);
      data = elements;
      value = prioritys;
   }

   /**
    * This method will empty the queue. This also helps garbage
    * collection by releasing any reference it has to the elements 
    * in the queue. This starts from offset 1 as off equals 0
    * for the elements array.
    */ 
   public void clear() {            
      for(int i = 1; i < count; i++) {
         data[i] = null; 
      }
      count = 0;
   }

   /**
    * The number of elements in the queue. The length 
    * indicates the number of elements that are currently 
    * in the queue.   
    *
    * @return the number of elements in the queue
    */     
   public int length() {
      return count;
   }
}


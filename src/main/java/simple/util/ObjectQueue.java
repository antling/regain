/*
 * ObjectQueue.java February 2001
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
 * This provides a quick implementation for a thread safe queue. This
 * ensures that if there are several threads enqueuing or dequeuing
 * from this queue that no thread will dequeue a null.
 * <p>
 * This will block a thread from dequeuing when there is not items 
 * in the queue. It will also block an enqueuer if there is no space 
 * left in the queue. 
 * <p>
 * When a thread is waiting and gets notified by another thread the 
 * thread that has been notified reasserts the check for the full 
 * or empty case, this means that there will be no problems if there 
 * are several enqueuing threads and several dequeuing threads.
 *
 * @author Niall Gallagher
 */ 
public class ObjectQueue implements Serializable{

   /**
    * The number of objects in the queue.
    */
   private volatile int count;

   /**
    * This is where the objects are stored.
    */
   private Object[] queue;

   /**
    * Offset where objects are dequeued from. 
    */
   private int front;

   /**
    * Offset where objects are enqueued to.
    */
   private int rear;

   /**
    * The number of objects that can be enqueued.
    */
   private int capacity;

   /**
    * The number of threads waiting to dequeue.
    */
   private int enqueuing;

   /**
    * The number of threads waiting to dequeue.
    */
   private int dequeuing;    
    
   /**
    * When the size of the <code>ObjectQueue</code> is not specified
    * then the default size of 30 is set, this <code>ObjectQueue</code> 
    * cannot resize so care is need when picking a size.
    */     
   public ObjectQueue() {
      this(30);
   }

   /**
    * If the capacity given is less than 1 then the size of
    * the queue is increased so that it becomes 1 this means
    * that the queue can tolerate at least one add by a thread.
    *
    * @param capacity the initial size that this queue can handle
    */     
   public ObjectQueue(int capacity) {
      this.capacity = capacity < 1 ? 1 : capacity;   
      this.rear = this.capacity - 1;
      this.queue = new Object[capacity];       
   }
   
   /**
    * If this enqueues to an empty queue it notifys any dequeuers
    * that were trying to take from the empty queue. This ensures
    * that a dequeuer can wake-up an will not block indefinitely.
    *
    * @param object the object to enqueue into this
    * <code>ObjectQueue</code>.
    * @exception InterruptedException if an interrupt is issued.
    */       
   public void enqueue(Object object) throws InterruptedException {       
      synchronized(queue) {  
         enqueuing(); 
         count++;
         rear = (rear + 1) % capacity;
         queue[rear] = object;
      }
   }

   /**
    * If this dequeues from a full queue it notifys any enqueuers who
    * were waiting to add to the full queue, so that they do not block.
    *
    * @return an object from the queue, this will never be a null object
    *
    * @exception InterruptedException is thrown if an interrupt is
    * issued
    */       
   public Object dequeue() throws InterruptedException {
      synchronized(queue) {
         dequeuing();
         Object object = queue[front];
         queue[front] = null;
         count--;
         front = (front + 1) % capacity;
         return object;
      }
   }

   /**
    * In 'enqueuing' there is no waste in notification because if there
    * is dequeuer's then that means that there cannot be also enqueuer's
    * because the wait condition would fail for the enqueuer. For example
    * if a dequeuer was waiting on the lock and an enqueuer came along, 
    * it would release that dequeuer, however if a number of dequeuer's 
    * came along they would all be waiting for the 'queue' lock, no 
    * enqueuer can ever be waiting with it also because when an enqueuer 
    * comes along it would automatically pass the while condition and 
    * release the waiting dequeuer, vice-versa.
    *
    * @exception InterruptedException if an interrupt is issued to
    * the enqueuing thread
    */ 
   private void enqueuing() throws InterruptedException {
      while(count == capacity) {
         enqueuing++;
         try {
            queue.wait();
         } finally {
            enqueuing--;
         }
      }
      if(dequeuing > 0) {
         queue.notify();            
      }
   }
   
   /**
    * As above for 'enqueuing' there will never be enqueuers and
    * dequeres waiting at the same time, it will either be all
    * enqueuer's or all dequeuer's waiting to be notified and only an
    * enqueuer can notify a dequeuer as well as only a dequeuer can
    * notify a enqueuer. This does an iterative check because of the
    * semantics of the wait/notify paradigm. When a dequeuer is
    * waiting to be notified it will add 1 to the dequeuers count. When
    * there are several dequeuers this means that there is nothing in
    * the object queue, the enqueue method will then detect that there
    * are dequeuers and notify one of them. This however does not mean
    * that the thread that has been notified will be the next to grab the
    * lock. The notified thread must wait its turn before it can grab the
    * lock, this means that there could have been some thread that took
    * the only enqueued item. Once the notified thread regains the lock
    * it will have to detect wheather another thread dequeued the only
    * object, if this is the case the thread must wait again to be
    * notified.
    *
    * @exception InterruptedException if interrupt is issued to the
    * thread to the dequuer
    */       
   private void dequeuing() throws InterruptedException {
      while(count == 0) { 
         dequeuing++;
         try {
            queue.wait();
         }finally{
            dequeuing--;
         }
      }       
      if(enqueuing > 0) {
         queue.notify();
      }
   }  

   /**
    * This returns the number of objects that are actually enqueued
    * into the <code>ObjectQueue</code>. If the length is zero then 
    * a dequeue method will block.
    *
    * @return the number of objects that is currently in the queue
    */ 
   public int length() {
      return count;
   }
}

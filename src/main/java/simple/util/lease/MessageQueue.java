/*
 * MessageQueue.java May 2004
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

import java.util.LinkedList;

/**
 * This provides a means to pass messages between threads. This
 * can be given an arbitrary number of messages from a notifier
 * thread without blocking so that message passing is not a
 * time consuming task. The recieving thread can then acquire
 * the messages using the <code>dequeue</code> method which
 * will block if there are no messages in the queue.
 *
 * @author Niall Gallagher
 *
 * @see java.util.LinkedList
 */
final class MessageQueue {

   /**
    * This will schedule the objects on priority.
    */
   private LinkedList queue;

   /**
    * This is a lock so there can be concurrency.
    */
   private Object lock;

   /**
    * This counts the no of threads waiting.
    */
   private int dequeuers;

   /**
    * This creates a new <code>MessageQueue</code> object for
    * passing messages between threads. This synchronizes itself
    * using a private object so that if this instance is used as
    * a monitor it will not interfere with the operation of the
    * message notifications between the communicating threads.
    */
   public MessageQueue() {
      this.queue = new LinkedList();
      this.lock = new Object();
   }

   /**
    * If this enqueues to an empty queue it notifys any dequeuers
    * that were trying to take from the empty queue. This ensures
    * that a dequeuer can wake-up an will not block indefinitely.
    *
    * @param value the object to be scheduled in the queue
    */
   public void enqueue(Object value) {
      synchronized(lock){
         enqueuing();
         queue.addFirst(value);
      }
   }

   /**
    * This is guaranteed to return an object. If the queue is
    * empty then the thread that is dequeuing blocks until there
    * is an object to dequeue, it will be notified by the enqueuer.
    *
    * @throws InterruptedException thrown if theres an interrupt
    *
    * @return the object with the highest priority in the queue
    */
   public Object dequeue() throws InterruptedException {
      synchronized(lock){
         dequeuing();
         return queue.removeLast();
      }
   }

   /**
    * This will determine weather the current thread must block and
    * wait for an object. The semantics of the wait notify paradigm is
    * such that this check must be iterative. When there are no objects
    * in the queue then a dequeuer will wait on the lock. However once
    * an object is enqueued then the thread that was waiting is
    * notified. This does not mean that the thread that was notified
    * will be the thread that grabs the lock next. In fact the notified
    * thread must compete with all other threads to grab the lock. This
    * means that while the notified thread is attempting to grab the
    * lock another thread may scoop the object that was enqueued. The
    * check must be made iteratively so that this occurrence does not
    * cause the queue to return null, the dequeuer count is changed
    * within a lock.
    *
    * @throws InterruptedException thrown if the thread is
    * interrupted
    */
   private void dequeuing() throws InterruptedException {
      while(queue.isEmpty()) {
         dequeuers++;
         try{
            lock.wait();
         }finally{
            dequeuers--;
         }
      }
   }

   /**
    * This notifies any waiting thread that there is an object to
    * dequeue. This may be called when all the dequeuers have been
    * notified. This is because the dequeuer must grab the lock
    * before it can decrement the dequeuers variable, this is not
    * really that wasteful.
    */
   private void enqueuing() {
      if(dequeuers > 0) {
         lock.notify();
      }
   }
}

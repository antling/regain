/*
 * SchedulerQueue.java February 2001
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

package simple.util.schedule;

import simple.util.PriorityQueue;

/**
 * This is a <code>SchedulerQueue</code> used to prioritize objects
 * enqueued. This queue is a <code>PriorityQueue</code>. It ensures
 * the object with the highest priority value will leave the queue
 * first, this also ensures that a null item is never dequeued.
 * <p>
 * This prioritizes the entry into a <code>PriorityQueue</code> so
 * that objects that are removed can be synchronized if there are
 * multiple threads removing from the queue. It is important to
 * note that this should not cap the prioritys be cause the queue
 * that is being used will only remove objects that are highest
 * in priority at the moment of <code>PriorityQueue.dequeue</code>
 * so if the prioritys are all capped then it becomes impossible
 * to remove some of the items from the queue.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.PriorityQueue
 */
final class SchedulerQueue {

   /**
    * This will schedule the objects on priority.
    */
   private PriorityQueue queue;

   /**
    * This is a lock so there can be concurrency.
    */
   private Object lock;

   /**
    * This counts the no of threads waiting.
    */
   private int dequeuers;

   /**
    * This creates a new <code>SchedulerQueue</code> object for
    * scheduling objects that are queued. The objects that are
    * queued have a maximum and a minimum priority that they can
    * be set to. All prioritys are capped to be inside this range.
    */
   public SchedulerQueue() {
      this.queue = new PriorityQueue();
      this.lock = new Object();
   }

   /**
    * This method is for enqueuing objects. If the priority
    * specified is larger than the largest priority allowed
    * then the priority is capped to the maximum priority,
    * the priority is also capped to the minimum of the queue.
    *
    * @param obj the object to be scheduled in the queue
    * @param priority the priority of the object to be queued
    */
   public void enqueue(Object obj, long priority) {
      synchronized(lock){
         enqueuing();
         queue.add(obj, priority);
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
         return queue.remove();
      }
   }

   /**
    * This will determine wheather the current thread must block and
    * wait for an object. The semantics of the wait notify paradigm is
    * such that this check must be iterative. When there are no objects
    * in the queue then a dequeuer will wait on the lock. However once
    * an object is enqueued then the thread that was waiting is
    * notified. This does not mean that the thread that was notified
    * will be the thread that grabs the lock next. In fact the notified
    * thread must compete with all other threads to grab the lock. This
    * means that while the notified thread is attempting to grab the
    * lock another thread may scoop the object that was enqueued. The
    * check must be made iteratively so that this occurance does not
    * cause the queue to return null, the dequeuer count is changed
    * within a lock.
    *
    * @throws InterruptedException thrown if the thread is
    * interrupted
    */
   private void dequeuing() throws InterruptedException {
      while(queue.length() == 0) {
         dequeuers++;
         try{
            lock.wait();
         }finally{
            dequeuers--;
         }
      }
   }

   /**
    * This notifys any waiting thread that there is an object to
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

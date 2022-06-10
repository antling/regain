/*
 * Scheduler.java February 2001
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

/**
 * The <code>Scheduler</code> is used for scheduling arbitrary objects.
 * When arbitrary objects are enqueued into a <code>Scheduler</code>
 * the <code>Scheduler</code> object will guarantee that that object
 * will not be dequeued until its timeout has expired.
 * <p>
 * This guarantees that an object is returned when the dequeue method
 * is invoked. If there are no objects within the <code>Scheduler</code>
 * then the thread that invoked this dequeue method will wait until there
 * is an object to dequeue. If however there is an object within the
 * <code>Scheduler</code> the dequeue method will block the thread
 * until such time as the objects timeout has expired or if another
 * object has been enqueued concurrently that will allow the dequeue
 * method to return earlier then it returns the object which has the
 * least time to wait.
 * <p>
 * The waiting of an object is based on the time when it is allowed to
 * be released. For instance if an object with a given timeout of 1000
 * ms is enqueued then the time when that object is permitted to be
 * released is the sum of the <code>System.currentTimeMillis</code> and
 * the timeout which is 1000, relative to the <code>currentTime</code>,
 * that is from the time that it is scheduled.
 * <p>
 * This ensures regardless of the timeout peroid an object will always
 * be released at some time because its priority is based on the time
 * it entered the <code>SchedulerQueue</code>.
 *
 * @author Niall Gallagher
 */
public class Scheduler {

   /**
    * The maximum length of time an object can be enqueued.
    */
   protected static final long DEFAULT_MAX = 120000L;

   /**
    * This is the <code>PriorityQueue</code> for the objects.
    */
   protected SchedulerQueue queue;

   /**
    * List of current dequeuers for this <code>Scheduler</code>.
    */
   protected Registry registry;

   /**
    * The default maximum time a object can wait.
    */
   protected long max;

   /**
    * The time this object was created.
    */
   private long start;

   /**
    * This will create a default <code>Scheduler</code>. The default
    * <code>Scheduler</code> will allow timeouts of up to 60000
    * miliseconds. This means that an object that has been scheduled
    * for release at a specified time will be guaranteed to be released
    * at least 1 minute after it was scheduled and if the timeout is
    * less than 1 minute then it will be released after that timeout
    * has expired.
    */
   public Scheduler() {
      this(DEFAULT_MAX);
   }


   /**
    * This creates a <code>Scheduler</code> object with the maximum
    * timeout specified. This will ensure that no object will be
    * blocked for longer than the specified timeout. If an object is
    * enqueued with a timeout greater than the specified timeout then
    * the timeout is capped to the max.
    *
    * @param max the maximum timeout for this <code>Scheduler</code>
    */
   public Scheduler(long max) {
      this.queue = new SchedulerQueue();
      this.registry = new Registry();
      this.start = currentTime();
      this.max = max;
   }


   /**
    * This schedules objects so that they will not be released
    * until the specified timeout has expired. If this timeout
    * is greater than the maximum timeout then it is capped and
    * released once the maximum timeout has expired instead.
    *
    * @param object the object to be scheduled for a timeout
    * @param wait time it is to wait within the <code>Scheduler</code>
    */
   public void enqueue(Object object, long wait) {
      long timeout = Math.min(wait, max) + currentTime();
      Entry entry = new Entry();
      entry.object = object;
      entry.timeout = timeout;
      enqueue(entry);
      interrupt(entry); /* N.B. */
   }


   /**
    * This method is important for enqueuing items as it tells the
    * <code>Registry</code> what the timeout of an incomming item is.
    * If the item has a timeout that is less than a dequeuer then the
    * thread that is dequeuing the item with the larger timeout is
    * interrupted.
    *
    * @param entry this is the new entry that has been enqueued
    */
   protected void interrupt(Entry entry) {
      registry.interrupt(entry.timeout);
   }


   /**
    * This adds an entry object into the <code>SchedulerQueue</code>
    * this will set a priority based on the time this object is to be
    * released at, that is the sooner that the entry is to be released
    * the higher its priority. The actual priority is the time of
    * release times -1.
    *
    * @param entry the entry object that is being queued
    */
   protected void enqueue(Entry entry) {
      long wait = entry.timeout;
      long minus = -1L * wait;
      queue.enqueue(entry, minus);
   }


   /**
    * This is used to dequeue the objects from the
    * <code>Scheduler</code>. This will return an object only when it
    * has one whose timeout has expired. If there are no objects in the
    * <code>Scheduler</code> then it will block the thread until one
    * becomes available. This is guaranteed to return an object once an
    * objects timeout has expired. If a thread is blocked trying to
    * retrive an object whose timeout has not yet expired and another
    * object is entered concurrently which does not have to wait such a
    * long time then the thread will become unblocked and return the
    * object with the least timeout. This method cannot be synchronized
    * because the thread will wait when the <code>SchedulerQueue</code>
    * <code>isEmpty</code> and thus that thread will cause a deadlock
    * situation, because it will have the lock.
    *
    * @throws InterruptedException thrown if the thread is interrupted
    *
    * @return the returns the next object which timeout has expired
    */
   public Object dequeue()throws InterruptedException{
      while(true){
         Object top = queue.dequeue();
         Entry entry = (Entry)top;
         try{
            sleep(entry.timeout);
            return entry.object;
         }catch(InterruptedException e){
            enqueue(entry);
         }
      }
   }


   /**
    * This will unregister the current thread from the
    * <code>Registry</code> object. This means that this thread will
    * no longer recive interrupts from the <code>Registry</code>.
    */
   protected void unregister(){
      registry.remove();
   }


   /**
    * This will register with the <code>Registry</code> object. This
    * basically means that this thread wishes to recieve interrupts
    * if there is a thread that calls the interrupt method of the
    * <code>Registry</code> with an wake timeout less that this
    * threads wake timeout.
    *
    * @param awaken the time this thread will stop sleeping
    */
   protected void register(long awaken){
      registry.sleeping(awaken);
   }


   /**
    * This will put the curent thread to sleep. This thread
    * may be woken by the <code>Registry</code> if there is a
    * <code>Registry.interrupt</code> method invoked that has
    * a sleep time less than the registered thread.
    *
    * @exception InterruptedException Thread.sleep throws this
    *
    * @param awaken the time that this thread will stop sleeping
    */
   protected void sleep(long awaken) throws InterruptedException {
      try{
         long time = currentTime();
         long sleep = awaken - time;
         if(sleep <= 0) return;
         register(awaken);
         Thread.sleep(sleep);
      }finally {
         unregister();
      }
   }


   /**
    * This returns the <code>currentTime</code> in milliseconds from
    * the creation of this. The time is made smaller by subtracting
    * the time this object was created at. This time will return an
    * increasing time from 0 to the Long.MAX_VALUE.
    *
    * @return the <code>currentTime</code> minus the time that this
    * was created.
    */
   protected long currentTime() {
      return System.currentTimeMillis() -start;
   }


   /**
    * This is used to keep objects with there
    * time of release. The time when this object
    * can be dequeued is retrived by timeout.
    */
   public class Entry {
      public Object object;
      public long timeout;
   }

}



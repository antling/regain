/*
 * ProcessQueue.java February 2001
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
 
package simple.util.process;

import simple.util.ObjectQueue;
import java.util.Observable;

/**
 * The <code>ProcessQueue</code> object provides a means for quick
 * independent executions. This is a thread cache, the aim  of this
 * is to make execution of <code>Runnable</code> objects quick. The
 * <code>Runnable</code> objects given to this should be transient
 * so that the thread can be reused. This should not be used as an
 * easy way to access a thread.
 * <p>
 * When an object needs to be run in its own thread then usually 
 * a separate thread object needs to be created and initialized 
 * for that object to execute. However this is an expensive 
 * undertaking for the system. A cheaper way to execute the objects 
 * is to create a number of the threads and allow these threads to
 * process <code>Runnable</code> objects when they are free, i.e. 
 * not busy with another <code>Runnable</code>. This removes the 
 * need for the expensive initialization of the threads for every 
 * execution of an object. 
 * <p>
 * The <code>ProcessQueue</code> extends the <code>Observable</code>
 * object. This enables any object that may be using this to 
 * receive notification when the <code>ProcessQueue.resize</code>
 * is used. The need for notifications is evident when there is
 * a small number of threads. If <code>Runnable</code> objects are
 * executed, and remain in execution for a long time, the number of
 * threads active can be equal to the number of threads pooled. This
 * means that any object executing the <code>execute</code> method 
 * will be frozen waiting for a <code>Runnable</code> to finish. 
 *
 * @author Niall Gallagher
 */ 
public final class ProcessQueue extends Observable {

   /**
    * This is the singleton object used by the entire JVM.
    */
   private static ProcessQueue engine;

   static {
      engine = new ProcessQueue(10);
   }

   /**
    * Used by each daemon object to notify readiness.
    */
   private ObjectQueue ready;

   /**
    * Represents the number of threads used by the pool.
    */
   private int capacity;
  
   /**
    * This allows the <code>ProcessQueue</code> to become a system
    * static object. This provides the system with a thread cache 
    * that can be shared by objects across the JVM. Caution should
    * be taken when using the <code>ProcessQueue</code>, as it has
    * a limited amount of threads. This means that if number of 
    * objects processing <code>Runnable</code> objects exceeded 
    * the number of threads then the objects would block waiting 
    * for the next thread to be available.
    * <p>
    * Because this is a singleton object, that is, an instance
    * that is static for the entire JVM, access control must be
    * required. The caller of this method must have the permission
    * <code>ProcessPermission</code> with the "execute" action.
    *
    * @return returns the system static <code>ProcessQueue</code>
    *
    * @exception SecurityException the caller needs permission
    */ 
   public static synchronized ProcessQueue getInstance() {      
      SecurityManager manager = System.getSecurityManager();
      if(manager != null) {
         manager.checkPermission(new ProcessPermission("execute"));
      }   
      return engine;
   }

   /**
    * This is a private constructor that can be used only by 
    * the thread pool itself. This allows the thread pool to 
    * behave as a singleton object. There is only ever one 
    * instance of the <code>ProcessQueue</code> object this 
    * allows the sharing of cached threads across the system 
    * which saves time.
    */   
   private ProcessQueue(int size) {
      this.ready = new ObjectQueue(size);
      this.increase(size);
   }

   /**
    * This is used to execute the <code>Runnable</code> objects 
    * with one of the threads from the pool. This method is not 
    * synchronized so as many threads can invoke this method as 
    * needs be. If all threads are occupied then this will block 
    * waiting for the next available thread to be made ready.
    *
    * @param target the <code>Runnable</code> to be executed
    *
    * @exception InterruptedException if there is  an interrupt
    */ 
   public void execute(Runnable target) throws InterruptedException {   
      Daemon daemon = (Daemon)ready.dequeue();
      daemon.process(target);         
   }

   /**
    * This is used to either increase or decrease the number of 
    * threads that are active in the <code>ProcessQueue</code>. 
    * This is an internally synchronized method so if there are 
    * several threads using the <code>ProcessQueue</code> each 
    * will wait its turn before the resize is invoked. This is 
    * tricky when there a several threads trying to resize 
    * concurrently because the capacity may change.
    * <p>
    * This will notify all <code>Observer</code> of the change
    * in the number of threads pooled. This passes all of the
    * <code>Observer</code> objects the old capacity of the 
    * <code>ProcessQueue</code> as an <code>Integer</code>.
    *
    * @param size the number of threads that this is to use
    *
    * @exception InterruptedException if there is an interrupt
    */   
   public synchronized void resize(int size) {
      if(capacity < size){
         increase(size);
      } else if(size < capacity){
         reduce(size);
      }
      notifyObservers();
   }

   /**
    * This will return the number of threads that this pool 
    * currently has cached. This can be used before the resize 
    * method to determine if there is a need for new threads 
    * to be activated or to be deactivated.
    *
    * @return the number of threads that this pool contains
    */  
   public synchronized int capacity() {
      return capacity;
   }

   /**
    * Used to increase the number of threads that this pool 
    * contains. This will create the difference in the number 
    * of threads the pool currently has and the number that 
    * the pool is to have. Each daemon holds a reference to 
    * the ready queue and when that daemon is activated and 
    * not busy it enqueues itself into the ready queue.
    *
    * @param size the number of threads to be created by this
    */ 
   private synchronized void increase(int size){      
      while(capacity < size) {
         new Daemon(ready);
         capacity++;
      }
   }

   /**
    * Used to reduce the number of threads that this pool has. 
    * This will continue to dequeue from the object queue until 
    * such time as the number of active threads is the same as 
    * the number specified.
    *
    * @param size the number of threads that this is to reduce to
    */ 
   private synchronized void reduce(int size) {      
      while(size < capacity){         
         Daemon daemon = null;
         try {
            daemon = (Daemon)ready.dequeue();
         }catch(InterruptedException e){
            continue;
         }
         daemon.kill();         
         capacity--;
      }
   }

   /**
    * This is a convenience method that is used to deactivate 
    * all the threads in the <code>ProcessQueue</code> this 
    * is similar a resize with zero. This will continue to 
    * dequeue threads from the <code>ObjectQueue</code>
    * until all <code>Daemon</code> threads have ended.
    */ 
   public synchronized void stop() {            
      resize(0);
   }
}

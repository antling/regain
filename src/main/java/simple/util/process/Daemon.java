/*
 * Daemon.java February 2001
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

/**
 * This is used with the <code>ProcessQueue</code> object for running
 * <code>Runnable</code> objects. The daemon works by inter thread 
 * communication via a pair of <code>ObjectQueue</code>'s. One queue 
 * is used by the <code>ProcessQueue</code> to notify the daemon that 
 * there is a process to be run the other is used by the daemon to 
 * communicate to the <code>ProcessQueue</code> that it is ready to 
 * execute another <code>Runnable</code> object. The daemon lets the 
 * <code>ProcessQueue</code> know that it is ready by enqueuing itself 
 * into the the <code>ProcessQueue</code>'s ready queue.
 *
 * @author Niall Gallagher
 */ 
final class Daemon extends Thread {   

   /**
    * Used to pass an object to the daemon for execution. 
    */
   private ObjectQueue slot = new ObjectQueue(1);

   /**
    * This is used to signify that it should deactivate. 
    */
   private volatile boolean alive = true;

   /**
    * The <code>ProcessQueue</code> ready queue of daemons.
    */
   private ObjectQueue ready;

   /**
    * This creates a daemon object for a <code>ProcessQueue</code> 
    * the queue given to the constructor is the ready queue of the 
    * pool. The constructor will automatically start the thread.
    *
    * @param ready the ready queue of the <code>ProcessQueue</code>
    */  
   public Daemon(ObjectQueue ready){
      this.ready = ready;
      this.start();
   }
  
   /**
    * This is used by the <code>ProcessQueue</code> to process a
    * <code>Runnable</code> object. This method will pass the 
    * <code>Runnable</code> into an <code>ObjectQueue</code> 
    * where the <code>ObjectQueue</code> will be later gathered 
    * by this.
    *
    * @param target the <code>Runnable</code> to be executed
    *
    * @exception InterruptedException if theres an interrupt
    */      
   public void process(Runnable target) throws InterruptedException {
      slot.enqueue(target);
   }
   
   /**
    * This is used to dequeue a <code>Runnable</code> object from 
    * the input queue. This will block until the pool inserts a
    * <code>Runnable</code> into the queue, this is done when the
    * <code>ProcessQueue</code> invokes process.
    *
    * @return this returns the <code>Runnable</code> object that 
    * is to be executed
    *
    * @exception InterruptedException if theres an interrupt
    */ 
   private Runnable dequeue() throws InterruptedException {
      return (Runnable)slot.dequeue();
   }
  
   /**
    * This is the main method of the daemon. This will indefinitely
    * invoke the execute method, until such time as the kill method
    * is invoked by some other object. The execute method will catch
    * exceptions that may propagate from the <code>Runnable</code> 
    * <code>run</code> method.
    */ 
   public void run() {
      while(alive) {
         execute();
      }
   } 

   /**
    * This is used to dequeue <code>Runnable</code> objects from 
    * the slot. When the <code>Runnable</code> is dequeued than 
    * the run method is executed. After the <code>run</code>. 
    * method has finished the daemon will enqueue itself into the
    * <code>ProcessQueue</code> ready queue, this will also call
    * <code>interrupted</code>.
    */ 
   private void execute() {
      try {         
         ready.enqueue(this);
         dequeue().run();
      } catch(Exception e){
         e.printStackTrace();
      } finally {
         interrupted();         
      }
   }
   
   /**
    * This is used to stop the daemon from processing. This will    
    * not stop the thread in its tracks but rather stop the any
    * further executions, that is, this will stop the run method
    * from executing, which ensures not more processing.
    */ 
   public void kill() {
      alive = false;
   }

}

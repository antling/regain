/*
 * Processor.java February 2001
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
 
package simple.http;

import simple.util.schedule.Scheduler;

/**
 * The <code>Processor</code> is used to execute <code>Poller</code> 
 * objects on the <code>Scheduler</code> queue. These are used to 
 * execute the <code>process</code> method of the <code>Poller</code>, 
 * the <code>Poller</code> will then do some form of polling before 
 * being re-enqueued into the <code>Scheduler</code>.
 *
 * @author Niall Gallagher
 */ 
final class Processor implements Runnable {

   /**
    * The processor for each of the <code>Poller</code>'s.
    */
   private PollerHandler proc;

   /**
    * The queue of <code>Poller</code> objects to be executed.
    */
   private Scheduler queue;

   /**
    * Determines wheather this processor is to continue.
    */
   private volatile boolean alive;

   /**
    * This creates a new <code>Processor</code> object that can 
    * be used to execute the <code>Poller</code> objects queued 
    * on the <code>Scheduler</code>.
    *
    * @param proc needed so that <code>Poller</code>'s can be 
    * re-processed.
    * @param queue the <code>Scheduler</code> that contains 
    * the <code>Poller</code>'s
    */ 
   public Processor(PollerHandler proc, Scheduler queue){
      this.proc = proc;
      this.queue = queue;       
      this.alive = true;
   }      
   
   /**
    * This is used by the thread to execute this object. This
    * method will never finish it will allways be polling. This
    * will not allow exceptions to propagate past the run method.
    * The execute method will dequeue a <code>Poller</code> from 
    * the queue and execute it.
    */ 
   public void run() {
      while(alive) {
         try {
            execute();
         }catch(Exception e){
            e.printStackTrace();
         }         
      }
   }  

   /**
    * The <code>kill</code> method is used so that this object
    * can stop dequeuing <code>Poller</code>s. This is convinent
    * for the users of the <code>Processor</code> objects in so
    * much as the thread that is executing this can be stopped
    * without having to use <code>Thread.stop</code>.
    */
   public void kill() {
      alive = false;
   }

   /**
    * This is used to dequeue <code>Poller</code>'s from the queue 
    * and give them time on the CPU. The <code>Poller</code>'s can 
    * then read from the recive buffers to see if there is enough 
    * data to form a HTTP request. The <code>Poller</code>'s can 
    * then use the <code>notifyReady</code> method to notify this 
    * <code>Processor</code> that it has enough data to form a 
    * HTTP request and wishes to be processed as a HTTP transaction. 
    * If there is still not enough data to form a HTTP request then 
    * it re-enqueues itself using the <code>notifyWait</code> method.
    *
    * @throws InterruptedException thrown if the thread was
    * interrupted
    */ 
   private void execute() throws InterruptedException {
      Object top = queue.dequeue();             
      Poller poller = (Poller)top;
      try{ 
         poller.process(proc);      
      }catch(Exception e){
         poller.close();
      }      
   }   
}



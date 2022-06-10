/*
 * PipelineProcessor.java February 2001
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

import simple.util.schedule.PulseScheduler;
import simple.util.schedule.Scheduler;
import simple.util.process.ProcessQueue;
import java.io.IOException;
import java.util.Vector;

/**
 * This is a <code>PipelineProcessor</code> object that is used 
 * to process <code>Pipeline</code> objects and make asynchronous 
 * callbacks to the given <code>ProtocolHandler</code> object. 
 * This is responsible for generating <code>Request</code> and
 * <code>Response</code> objects which are to be handled by the 
 * specified <code>ProtocolHandler</code>.
 * <p> 
 * This is what ties everything together. This uses a cache of
 * <code>Thread</code>'s to process the HTTP transactions. So
 * the <code>ProtocolHandler</code> will receive asynchronous
 * callbacks from multiple threads. 
 * <p>
 * This implementation of the <code>PipelineHandler</code> can 
 * be tuned to meet specific requirements of the implementation
 * is serves. The characteristics that can be modified include
 * the number of threads used for polling, the maximum wait time
 * for a <code>Pipeline</code>.
 * 
 * @author Niall Gallagher
 */ 
class PipelineProcessor implements PipelineHandler, PollerHandler {

   /**
    * This enables the threads to be recycled quickly.
    */
   private static ProcessQueue engine;
   
   static {
      engine = ProcessQueue.getInstance();  
   }

   /**
    * This handles HTTP the transactions requested.
    */
   protected ProtocolHandler handler;
   
   /**
    * Used to schedule the <code>Poller</code> objects.
    */
   protected Scheduler queue;

   /**
    * Used to store the activated <code>Processors</code>. 
    */    
   protected Vector active;

   /**
    * The constructor for the <code>PipelineProcessor</code>. 
    * This takes a <code>ProtocolHandler</code> which is used 
    * to handle the requests that are read from the pipelines.
    * <p>
    * The <code>PipelineProcessor</code> is an implementation 
    * of the <code>PipelineHandler</code> interface. Arguments 
    * to this constructor allows the performance to be tuned to 
    * any specific requirements of the <code>Pipeline<code>'s 
    * being polled or the <code>ProtocolHandler</code> used.
    *
    * @param handler this will receive the requests from the
    * <code>Pipeline</code>
    *
    * @param threads the number of threads used for polling 
    * the <code>Pipeline</code>'s, this will not effect the 
    * number of <code>Pipeline</code>'s that can be polled
    *
    * @param wait the maximum waiting time for the polling
    * of the <code>Pipeline</code>, there is an exponential 
    * backoff when polling the <code>Pipeline</code> this 
    * limits the wait 
    */ 
   public PipelineProcessor(ProtocolHandler handler, int threads, int wait) {
      this.queue = new PulseScheduler(wait);  
      this.active = new Vector(threads);
      this.handler = handler;
      this.init(threads);     
   }

   /**
    * When a <code>PipelineProcessor</code> is constructed in needs
    * to initialize several <code>Processor</code> objects which
    * will execute the <code>Poller.process</code> method to scan
    * the <code>Pipeline</code> objects for the end of a HTTP
    * header. The <code>Processors</code> are stored so they can
    * be deactivated by perhaps a subclass the implements the
    * <code>Observer.update</code> method.
    *
    * @param size this is the number of threads that this 
    * manager is to create for dequeuing <code>Poller</code>s
    */
   private void init(int size) {
      while(active.size() < size){         
         Runnable run = new Processor(this,queue);
         active.add(run);         
         new Thread(run).start();
      }
   }

   /**
    * The <code>process</code> method for the interface. This is 
    * used to process the given <code>Pipeline</code>. When the 
    * pipeline produces a HTTP message header a <code>Request</code> 
    * and <code>Response</code> object will then be passed to the 
    * <code>ProtocolHandler</code> where the <code>Request</code> 
    * and <code>Response</code> objects will be used to process 
    * the HTTP transaction that was extracted from the pipeline
    * using the <code>Poller</code>.
    *
    * @param pipe the <code>Pipeline</code> that is being to be 
    * polled and processed
    *
    * @exception IOException thrown if the <code>Pipeline</code> 
    * could not be polled for some reason
    * @exception InterruptedException thrown it the thread is
    * interrupted
    */ 
   public void process(Pipeline pipe) 
      throws IOException, InterruptedException{
      queue.enqueue(PollerFactory.getInstance(pipe), 0);
   }

   /**
    * This is used to by <code>Poller</code> objects to notify 
    * the <code>PipelineProcessor</code> that there is sufficient 
    * data to create a HTTP request. A <code>Dispatcher</code> 
    * is then created that is used to execute the transaction. 
    * The <code>Dispatcher</code> object is given to a thread 
    * where it is executed. The <code>ProtocolHandler</code> is 
    * given a <code>Request</code> and <code>Response</code> 
    * at to handle the transaction. When the transaction is 
    * finished it is re-polled by the <code>Monitor</code> object.
    *
    * @param poller the <code>Poller</code> that has read a full
    * header from the <code>Pipeline</code>
    *
    * @exception IOException if there was a problem creating a
    * <code>Dispatcher</code> object
    * @exception InterruptedException thrown if the thread was
    * interrupted
    */ 
   public void notifyReady(Poller poller) 
      throws IOException, InterruptedException{
      Monitor mon = new Monitor(this, poller);
      engine.execute(new Dispatcher(handler, poller, mon));
   }
   
   /**
    * This is used to notify that the <code>Poller</code> has not 
    * yet got enough data to form a HTTP request header. This is 
    * the <code>Poller</code> object giving up time on the CPU and 
    * waiting until there is enough data read from the pipeline 
    * to create a HTTP request, this will schedule the next poll.
    *
    * @param poller this is the <code>Poller</code> object that 
    * wishes to wait a while
    *
    * @exception InterruptedException thrown if the thread was
    * interrupted
    */ 
   public void notifyWait(Poller poller) 
      throws InterruptedException{
      queue.enqueue(poller, 0);
   }
   /**
    * This is used to notify that the <code>Poller</code> has not 
    * yet got enough data to form a HTTP request header. This is 
    * effectively the <code>Poller</code> object giving up time on 
    * the CPU and waiting until there is enough data read from the 
    * <code>Pipeline</code> to create a HTTP request, this will 
    * schedule the next poll.
    *
    * @param poller this is the <code>Poller</code> object that 
    * wishes to wait a while
    * @param sleep this is amount of time to wait for the next
    * <code>Poller.process</code>
    *
    * @exception InterruptedException thrown if the thread was
    * interrupted.
    */ 
   public void notifyWait(Poller poller, int sleep) 
      throws InterruptedException{
      queue.enqueue(poller, sleep);
   }
}   

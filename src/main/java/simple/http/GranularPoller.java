/*
 * GranularPoller.java April 2004
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
 
package simple.http;

import java.io.IOException;

/**
 * The <code>GranularPoller</code> object is implemented to wait
 * between failed polls using a gradually increasing wait time.
 * This polling strategy ensures that the server can respond to
 * pipelines quickly when they become active after a peroid of
 * inactivity. This also enables the pipeline to say open longer.
 * <p>
 * The wait phase is calculated by the <code>BasicPoller</code>
 * and indicates the number of polls that have resulted in zero
 * bytes being read from the pipeline. This simply uses the phase
 * as an index into a array of pre-calculated wait times.
 * <p>
 * This type of poller is recommended for browsers that cannot
 * handle cuircumstances where an asynchronous close may occur.
 * For example when the browser requires some user input before 
 * it sends the new request, like accepting an SSL certificate.
 * For further information of asynchronous closure see RFC 2616
 * section 8.1.4 on practical considerations.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.BasicPoller
 */ 
final class GranularPoller extends BasicPoller {  

   /**
    * These are pre-calculated wait times used for scheduling.
    */
   private static final int[] sleep = {
      0,    0,    0,  200,  400,  600, 8000, 1000,
   1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600,
   2800, 3000, 3200, 3400, 3600, 3800, 4000, 4200,
   4400, 4600, 4800, 5000, 5200, 5400, 5600, 5800};

   /**
    * Constructor for the <code>GranularPoller</code> object. The
    * instance created by this performs scheduling using a set 
    * of pre-calculated wait durations, which are indexed by the
    * wait phase. Durations increase by two hundred milliseconds.
    *
    * @param pipe the pipeline that contains the HTTP requests
    *
    * @throws IOException if the pipeline can not be polled
    */ 
   public GranularPoller(Pipeline pipe) throws IOException{
      super(pipe);
   }

   /**
    * This is invoked when the HTTP header has been consumed from
    * the pipeline in full. This simply notifies the handler that
    * the pipeline contains a full HTTP request header using the
    * <code>notifyReady</code> method.
    *
    * @param handler this is handles all events that can occur
    *
    * @exception IOException if an I/O error occurs in reading 
    * @exception InterruptedException if an interrupt is issued
    */
   protected void ready(PollerHandler handler)
      throws InterruptedException, IOException {
      handler.notifyReady(this);
   }

   /**
    * This is invoked when no more bytes can be read from the
    * pipeline without blocking. This notifies the handler that
    * the pipeline contains a full HTTP request header using the
    * <code>notifyWait</code> method. If the phase has gone past
    * the maximum phase the inactive pipeline is closed.
    *
    * @param handler this is handles all events that can occur
    * @param phase the number of unsuccessful polls attempted
    *
    * @exception InterruptedException if an interrupt is issued
    */ 
   protected void wait(PollerHandler handler, int phase) 
      throws InterruptedException {
      if(phase < sleep.length){
         handler.notifyWait(this, sleep[phase]);
      }else{
         pipe.close();
      }      
   }
}

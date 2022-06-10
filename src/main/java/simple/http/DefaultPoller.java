/*
 * DefaultPoller.java February 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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
 * The <code>DefaultPoller</code> object is implemented to wait
 * between failed polls using an exponentially increasing wait
 * duration. This ensures that the more inactive the pipeline the 
 * less time the system spends polling that pipeline. Determining
 * the length of time to wait is done using the wait given phase.
 * <p>
 * The wait phase is calculated by the <code>BasicPoller</code>
 * and indicates the number of polls that have resulted in zero
 * bytes being read from the pipeline. This simply uses the phase
 * as an index into a array of pre-calculated wait times.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.BasicPoller
 */ 
final class DefaultPoller extends BasicPoller {  

   /**
    * These are pre-calculated wait times used for scheduling.
    */
   private static final int[] sleep = {
   0, 0, 0, 0, 0, 256, 1024, 4096, 16384, 65536};

   /**
    * Constructor for the <code>DefaultPoller</code> object. The
    * instance created by this performs scheduling using a set 
    * of pre-calculated wait durations, which are indexed by the
    * wait phase. The wait durations are exponentials of four.
    *
    * @param pipe the pipeline that contains the HTTP requests
    *
    * @throws IOException if the pipeline can not be polled
    */ 
   public DefaultPoller(Pipeline pipe) throws IOException{
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

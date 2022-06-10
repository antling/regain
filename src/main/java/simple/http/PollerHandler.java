/*
 * PollerHandler.java February 2001
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

import java.io.IOException;

/**
 * This is a <code>PollerHandler</code> interface that is used 
 * to process <code>Poller</code> objects. This is responsible for 
 * scheduling the <code>Poller</code>'s for processing. This will
 * either put the <code>Poller</code> to sleep for a peroid of
 * time or process the <code>Poller</code> once it has read the
 * HTTP header.
 * <p> 
 * This is needed to abstract the scheduling and processing of 
 * the <code>Poller</code> objects from the implementation of
 * the polling mechanism. The <code>PollerHandler</code>
 * implementation will schedule the <code>Poller</code> for its
 * next <code>Poller.process</code> invocation based on the 
 * required waiting time.
 * 
 * @author Niall Gallagher
 */ 
interface PollerHandler {

   /**
    * This method is used by the <code>Poller</code> object to
    * convey that it has read the HTTP header and is ready for
    * processing. The <code>Poller</code> object will then be
    * used as a HTTP message where the <code>ByteStore</code>
    * represents the HTTP header and the <code>Pipeline</code>
    * contains the HTTP content body.
    *
    * @exception IOException if there was a problem creating a
    * <code>Dispatcher</code> object
    * @exception InterruptedException thrown if the thread was
    * interrupted
    */
   public void notifyReady(Poller poller)
      throws IOException, InterruptedException;
   
   /**
    * This method is used so that the <code>Poller</code> is
    * scheduled for re-processing. The <code>Poller</code> will
    * wait a peroid of time before the next <code>process</code>
    * is invoked on it.
    *
    * @param poller this is the <code>Poller</code> object that 
    * wishes to wait a while
    *
    * @exception InterruptedException thrown if the thread was
    * interrupted
    */
   public void notifyWait(Poller poller)
      throws InterruptedException;
   
   /**
    * This method is used so that the <code>Poller</code> is
    * scheduled for re-processing. The <code>Poller</code> will
    * wait a peroid of time before the next <code>process</code>
    * is invoked on it. The peroid of time that it can wait can
    * be specified also.
    *
    * @param poller this is the <code>Poller</code> object that 
    * wishes to wait a while
    * @param sleep this is the length of time to wait before 
    * the next <code>Poller.process</code> is tried
    *
    * @exception InterruptedException thrown if the thread was
    * interrupted.
    */
   public void notifyWait(Poller poller, int sleep)
      throws InterruptedException;
}










/*
 * PipelineHandlerFactory.java February 2001
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

/**
 * The <code>PipelineHandlerFactory</code> is used to hide a specific 
 * implementation of the <code>PipelineHandler</code> used. This allows 
 * a user of this to get access to a <code>PipelineHandler</code> using 
 * an implementation of a <code>ProtocolHandler</code>. 
 * <p>
 * This will create an instance of a <code>PipelineHandler</code> to 
 * process any incomming <code>Pipeline</code>'s from a user. This will
 * will multiplex <code>Pipeline</code>'s and read requests from the 
 * <code>Pipeline</code>'s generating <code>Request</code> objects and
 * <code>Response</code> objects which will then be passed to the 
 * <code>ProtocolHandler</code> implementation provided.
 * 
 * @author Niall Gallagher
 */ 
public final class PipelineHandlerFactory {

   /**
    * The default number of polling threads for a handler object. 
    */
   private static final int DEFAULT_POLLERS = 10;

   /**
    * The default maximum waiting time between polls of the input.
    */
   private static final int DEFAULT_WAIT = 1000;

   /**
    * This will create an instance of a <code>PipelineHandler</code>
    * object for processing <code>Pipeline</code> objects using an
    * instance of the <code>ProtocolHandler</code>. This uses defaults
    * for the <code>PipelineHandler</code> created. Specifics can be 
    * specified by using the overloaded getinstance method. The job
    * of the <code>PipelineHandler</code> once it has been created is
    * to read HTTP requests from <code>Pipeline</code>'s passed to it.
    * These transactions will be used to generate <code>Request</code>
    * and <code>Resposne</code> objects.
    * <p>
    * This will create a <code>PipelineHandler</code> to handle any 
    * connected <code>Pipeline</code>'s. This handler will poll the 
    * <code>Pipeline</code>'s <code>InputStream</code> so that there
    * is no blocking, this will use an exponential backoff so that if
    * the socket is not producing data it is polled less frequently.
    *
    * @param handler this will recive the requests from the
    * <code>Pipeline</code>
    */ 
   public static PipelineHandler getInstance(ProtocolHandler handler) {
      return getInstance(handler, DEFAULT_POLLERS, DEFAULT_WAIT);
   }
   
   /**
    * This will create an instance of a <code>PipelineHandler</code>
    * object for processing <code>Pipeline</code> objects using an
    * instance of the <code>ProtocolHandler</code>. This uses defaults
    * for the <code>PipelineHandler</code> created. Specifics can be 
    * specified by using the overloaded getinstance method. The job
    * of the <code>PipelineHandler</code> once it has been created is
    * to read HTTP requests from <code>Pipeline</code>'s passed to it.
    * These transactions will be used to generate <code>Request</code>
    * and <code>Resposne</code> objects.   
    * <p>
    * This will create a <code>PipelineHandler</code> to handle any 
    * connected <code>Pipeline</code>'s. This handler will poll the 
    * <code>Pipeline</code>'s <code>InputStream</code> so that there
    * is no blocking, this will use an exponential backoff so that if
    * the socket is not producing data it is polled less frequently.
    * The <code>wait</code> parameter identifies a maximum that the 
    * <code>Pipeline</code>'s <code>InputStream</code> will be queued
    * before the next poll.
    *
    * @param handler this will recive the requests from the
    * <code>Pipeline</code>
    *
    * @param pollers this is the number of threads used for polling 
    * the <code>Pipeline</code>'s, this will not effect the number
    * of <code>Pipeline</code>'s that can be polled
    *
    * @param wait this is the maximum waiting time for the polling
    * of the <code>Pipeline</code>, there is an exponential backoff
    * when polling the <code>Pipeline</code> this limits the wait 
    */   
   public static PipelineHandler getInstance(ProtocolHandler handler, int pollers, int wait) {
      return new PipelineProcessor(handler, pollers, wait);
   }
 
}

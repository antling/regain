/*
 * Poller.java February 2001
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

import simple.util.ByteStore;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This is used to poll a pipeline for a HTTP request header. Once 
 * a request header is fully read from the <code>Pipeline</code>
 * then an HTTP transaction can take place. The objective of this
 * object is to read the HTTP request header in a non-blocking
 * manner so that multiple pipelines can be polled using a fixed
 * number of threads. This also serves to prioritize active pipes.
 * <p>
 * This is used in conjunction with the <code>PollerHandler</code> 
 * implementation which puts <code>Poller</code> objects on a wait 
 * queue and repeatedly dequeues the next poller object to execute
 * it. The <code>process</code> method performs a non-blocking 
 * read and either re-enqueues the poller if the HTTP header has 
 * not been fully read or dispatches the request for processing. 
 * <p>
 * The <code>Poller</code> implements the <code>ByteStore</code> 
 * interface which allows the bytes that were read from the header 
 * to be retrieved. The <code>ByteStore</code> is a read only so 
 * coupling is not a problem when the bytes are taken from it.
 * The performance of the <code>Poller</code> implementation is
 * fundamental to the performance of the entire system.
 *
 * @author Niall Gallagher
 */ 
abstract class Poller extends FilterPipeline implements ByteStore {

   /**
    * Allows the HTTP header data to be unread from the stream.
    */
   protected PollerInputStream data;

   /**
    * This creates a new <code>Poller</code> object for polling a
    * <code>Pipeline</code>. This creates a stream that can be
    * used to push back any bytes that have been read. This means
    * that the HTTP header can be read quickly with large chunks.
    *
    * @param pipe the pipeline that contains the HTTP requests
    *
    * @throws IOException thrown if the pipeline can not be polled
    */ 
   protected Poller(Pipeline pipe) throws IOException{      
      this(pipe, pipe.sock); 
   }

   /**
    * This creates a new <code>Poller</code> object for polling a
    * <code>Pipeline</code>. This creates a stream that can be
    * used to push back any bytes that have been read. This means
    * that the HTTP header can be read quickly with large chunks.
    *
    * @param pipe the pipeline that contains the HTTP requests
    * @param sock this is the socket that the pipeline is using
    *
    * @throws IOException if the pipeline can not be polled
    */ 
   private Poller(Pipeline pipe, Socket sock) throws IOException{
      this.data = new PollerInputStream(sock);
      this.table = pipe.table;
      this.sock = pipe.sock;      
      this.pipe = pipe;
   }

   /**
    * This will return the <code>InputStream</code> that can be
    * used to read the message body. The next byte read from this
    * stream will be either the next HTTP request in the pipeline
    * or the message body of the request. It is important that 
    * the contents of the stream are not interfered with unless
    * the contents represent the HTTP request body.
    *
    * @return the pipeline stream containing the message content
    */ 
   public synchronized InputStream getInputStream(){
      return new DataInputStream(data); 
   }
 
   /**
    * This is used to poll the <code>Pipeline</code>. When there 
    * are enough bytes to form a HTTP request then this will send
    * notification that it is ready for processing by invoking 
    * the <code>PollerHandler.notifyReady</code> method. This can
    * then create the HTTP request using the bytes consumed.
    * <p>
    * If however there is not sufficient data that can be read
    * from the pipeline to form the HTTP request then this sends
    * notification that the poller should be put on a wait queue 
    * for a specific period of time before being polled again. 
    * The length of time the poller should wait can be specified
    * by the <code>Poller</code> implementation.
    *
    * @param handler this is handles all events that can occur
    *
    * @exception IOException if an I/O error occurs in reading 
    * @exception InterruptedException if an interrupt is issued
    */ 
   public abstract void process(PollerHandler handler)
      throws IOException, InterruptedException;
}

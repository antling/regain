/*
 * SocketHandler.java October 2002
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
 
package simple.http.connect;

import simple.http.PipelineHandler;
import simple.http.PipelineFactory;
import simple.http.Pipeline;
import java.io.IOException;
import java.net.Socket;

/**
 * The <code>SocketHandler</code> object is used in conjunction 
 * with the <code>Connection</code> to dispatch <code>Pipeline</code>
 * objects to a <code>PipelineHandler</code> for incoming TCP 
 * connections. The <code>PipelineFactory</code> object can be used
 * to dispatch specialized <code>Pipeline</code> objects to the 
 * pipeline handler. This is useful when buffering is required for 
 * performance or when security measures are required.
 *
 * @author Niall Gallagher
 */
public class SocketHandler {

   /**
    * This will produce objects with desired functionality.
    */
   private PipelineFactory factory;
   
   /**
    * The handler for the TCP connections established.
    */
   private PipelineHandler handler;
   
   /**
    * This constructor creates a <code>SocketHandler</code> using 
    * a <code>PipelineHandler</code> object. The handler will 
    * dispatch <code>Pipeline</code> objects to the given
    * <code>PipelineHandler</code> once a socket is issued.
    *
    * @param handler the <code>PipelineHandler</code> used
    * to process the connections
    */   
   public SocketHandler(PipelineHandler handler){
      this.handler = handler;
   }
   
   /**
    * This constructor creates a <code>SocketHandler</code> using 
    * a <code>PipelineHandler</code> object. The processor will 
    * dispatch <code>Pipeline</code> objects to the given
    * <code>PipelineHandler</code> once a socket is issued.
    * <p>
    * The <code>Pipeline</code>'s are created once a socket is
    * given to the <code>PipelineFactory</code>. This enables
    * the handler to attach special functionality to the 
    * pipelines dispatched to the <code>PipelineHandler</code>.
    *
    * @param handler the <code>PipelineHandler</code> used    
    * to process the connections
    * @param factory this is the factory for implementations 
    * of the <code>Pipeline</code> object
    */
   public SocketHandler(PipelineHandler handler, PipelineFactory factory){
      this.handler = handler;
      this.factory = factory;
   }
   
   /**
    * Once the <code>Socket</code> has been configured it can be
    * used to create a <code>Pipeline</code> object. This object
    * is then given to the <code>PipelineHandler</code> so that
    * the connection can be processed and given to the 
    * <code>ProtocolHandler</code> so the HTTP request can be
    * processed.
    *
    * @param sock this is the connected socket that will be used
    * to create the <code>Pipeline</code>
    *
    * @exception IOException thrown if the <code>Socket</code>
    * experiences an error
    * @exception InterruptedException thrown if the thread
    * has been interrupted before being used to hand over the 
    * <code>Pipeline</code> to the <code>PipelineHandler</code>
    */
   public void process(Socket sock)
   throws IOException, InterruptedException {
      Pipeline pipe = null;      
      
      if(factory == null){
         pipe = new Pipeline(sock);
      }else{
         pipe = factory.getInstance(sock);
      }
      handler.process(pipe);   
   }
}

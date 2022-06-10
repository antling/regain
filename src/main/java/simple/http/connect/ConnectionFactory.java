/*
 * ConnectionFactory.java October 2002
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

import simple.http.serve.ResourceEngine;
import simple.http.serve.HandlerFactory;
import simple.http.PipelineHandlerFactory;
import simple.http.PipelineHandler;
import simple.http.PipelineFactory;
import simple.http.ProtocolHandler;

/**
 * This is used to create a <code>Connection</code> that will 
 * dispatch either HTTP requests or <code>Pipeline</code> objects 
 * to the handler specified. If a <code>ProtocolHandler</code> is 
 * used then HTTP requests will be dispatched using a default 
 * instances of the <code>PipelineHandler</code> using the
 * <code>PipelineHandlerFactory.getInstance</code> method.
 * <p>
 * This also allows a <code>Connection</code> to be produced
 * that can use a <code>PipelineFactory</code> which enables the 
 * <code>Connection</code> to attach desired functionality. This
 * can be used to add some form of security or network buffering 
 * features to the pipeline instances.
 *
 * @author Niall Gallagher
 */
public final class ConnectionFactory {

   /** 
    * Creates the default <code>PipelineHandler</code> object 
    * using the <code>PipelineHandlerFactory.getInstance</code>
    * method. This <code>PipelineHandler</code> will then send
    * HTTP requests to a <code>ProtocolHandler</code> issued
    * from the <code>HandlerFactory.getInstance</code>.
    *
    * @param engine this is the <code>ResourceEngine</code>
    * that is used to acquire resources for each request 
    *
    * @return this returns a <code>Connection</code> that
    * can be used to establish listening sockets a port
    */
   public static Connection getConnection(ResourceEngine engine){
      return getConnection(HandlerFactory.getInstance(engine));      
   }

   /** 
    * Creates the default <code>PipelineHandler</code> object 
    * using the <code>PipelineHandlerFactory.getInstance</code>
    * method. This <code>PipelineHandler</code> will then send
    * HTTP requests to a <code>ProtocolHandler</code> issued
    * from the <code>HandlerFactory.getInstance</code>.
    *
    * @param engine this is the <code>ResourceEngine</code>
    * that is used to acquire resources for each request   
    * @param factory this is the factory object that 
    * will produce <code>Pipeline</code> implementations    
    *
    * @return this returns a <code>Connection</code> that
    * can be used to establish listening sockets a port
    */
   public static Connection getConnection(ResourceEngine engine, PipelineFactory factory){
      return getConnection(HandlerFactory.getInstance(engine), factory);
   }

   /** 
    * Creates the default <code>PipelineHandler</code> object 
    * using the <code>PipelineHandlerFactory.getInstance</code>
    * method. This <code>PipelineHandler</code> will then send
    * HTTP requests to the <code>ProtocolHandler</code>.
    *
    * @param handler this is the <code>ProtocolHandler</code>
    * that will receive HTTP requests from ports specified by
    * the <code>Connection</code>
    *
    * @return this returns a <code>Connection</code> that
    * can be used to establish listening sockets a port
    */
   public static Connection getConnection(ProtocolHandler handler){
      return getConnection(PipelineHandlerFactory.getInstance(handler));
   }
  
   /** 
    * Creates the default <code>PipelineHandler</code> object 
    * using the <code>PipelineHandlerFactory.getInstance</code>
    * method. This <code>PipelineHandler</code> will then send
    * HTTP requests to the <code>ProtocolHandler</code>.
    *
    * @param handler this is the <code>ProtocolHandler</code>
    * that will receive HTTP requests from ports specified by
    * the <code>Connection</code>
    * @param factory this is the factory object that 
    * will produce <code>Pipeline</code> implementations
    *
    * @return this returns a <code>Connection</code> that
    * can be used to establish listening sockets a port
    */
   public static Connection getConnection(ProtocolHandler handler, PipelineFactory factory){
      return getConnection(PipelineHandlerFactory.getInstance(handler), factory);
   }

   /**
    * Creates <code>Connection</code> object using the 
    * <code>PipelineHandler</code> given. This enables the 
    * <code>Connection</code> to manage connections to
    * requested port numbers an dispatch <code>Pipeline</code>
    * objects to the <code>PipelineHandler.handle</code> method.
    *
    * @param handler this is the <code>PipelineHandler</code> 
    * that will manage <code>Pipeline</code> objects created
    * by the <code>Connection</code>
    *
    * @return this returns a <code>Connection</code> that
    * can be used to establish listening sockets a port
    */     
   public static Connection getConnection(PipelineHandler handler){
      return new Connection(new SocketHandler(handler));
   } 
   
   /**
    * Creates <code>Connection</code> object using the 
    * <code>PipelineHandler</code> given. This enables the 
    * <code>Connection</code> to manage connections to
    * requested port numbers an dispatch <code>Pipeline</code>
    * objects to the <code>PipelineHandler.handle</code> method.
    *
    * @param handler this is the <code>PipelineHandler</code> 
    * that will manage <code>Pipeline</code> objects created
    * by the <code>Connection</code>
    * @param factory this is the factory object that 
    * will produce <code>Pipeline</code> implementations
    *
    * @return this returns a <code>Connection</code> that
    * can be used to establish listening sockets a port
    */   
   public static Connection getConnection(PipelineHandler handler, PipelineFactory factory){
      return new Connection(new SocketHandler(handler,factory));
   }
}

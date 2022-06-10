/*
 * Connection.java October 2002
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

import java.net.ServerSocket;
import java.io.IOException;

/**
 * The <code>Connection</code> manages connections from  
 * <code>ServerSocket</code>'s. This class is used to dispatch 
 * HTTP requests from <code>Sockets</code> connected to a specific 
 * port. The requests are processed by a handler which hands a 
 * <code>Pipeline</code> to a <code>PipelineHandler</code>.
 * <p>
 * This handles connections from <code>ServerSocket</code>
 * objects so that features such as SSL can be used by a server
 * that uses this package. This will terminate the connection 
 * once the <code>ServerSocket</code> is closed, or once there
 * is an I/O problem accepting new connections.
 *
 * @author Niall Gallagher
 */
public class Connection {

   /** 
    * The handler that manages the incoming HTTP requests.
    */
   private SocketHandler handler;

   /** 
    * Constructor for the <code>Connection</code> object 
    * is used to create an instance that delegates all TCP
    * connections to the issued <code>SocketHandler</code>.
    *
    * @param handler this is the handler that is used to
    * process HTTP requests from accepted sockets
    */
   public Connection(SocketHandler handler){
      this.handler = handler;
   }
   
   /**
    * This will establish a thread that will listen for
    * connections using the issued <code>ServerSocket</code>.
    * Once this method has been invoked HTTP requests can
    * be made to the server socket port. If requests from
    * this port are no longer desired the <code>close</code>
    * method of the <code>ServerSocket</code> can be used.
    *
    * @param sock this is the <code>ServerSocket</code>
    * that will listen for incoming TCP connections
    */
   public void connect(ServerSocket sock){
      new Processor(handler,sock);   
   }
}

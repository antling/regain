/*
 * Processor.java October 2002
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
import java.net.Socket;
import java.io.IOException;

/**
 * The <code>Processor</code> object is used in conjunction with 
 * the <code>Connection</code> to accept incoming TCP connections.
 * This uses the <code>ServerSocket</code> given to create 
 * <code>Socket</code> objects which are then given to the 
 * <code>SocketHandler</code> for further processing.
 * <p>
 * This is started in a thread by the <code>Connection</code>
 * so that it can listen for incoming connections asynchronously.
 * The <code>run</code> method accepts incoming TCP connections 
 * and dispatches them to the <code>SocketHandler</code>, once 
 * they have been configured for performance with HTTP.
 *
 * @author Niall Gallagher
 */  
final class Processor extends Thread {

   /** 
    * The handler that manages the incoming HTTP requests.
    */ 
   private SocketHandler handler;
   
   /**
    * This is the socket that will listen for connections.
    */    
   private ServerSocket server;

   /**
    * Creates a <code>Processor</code> that is used to listen 
    * to a port. The <code>Processor</code> will accept TCP 
    * connections and configure the <code>Socket</code>
    * objects accepted for performance with HTTP.
    *
    * @param handler this is the <code>SocketHandler</code> 
    * that will handle the connected <code>Socket</code>'s
    * @param server this is the <code>ServerSocket</code> that 
    * listens for and accepts connections from clients 
    */
   public Processor(SocketHandler handler, ServerSocket server){
      this.handler = handler;
      this.server = server;
      this.start();
   }

   /**
    * The main processing done by this object is done using a 
    * thread calling the <code>run</code> method. Here the TCP 
    * connections are accepted by the <code>ServerSocket</code> 
    * which creates the <code>Socket</code> objects. Each 
    * <code>Socket</code> is then dispatched to the 
    * <code>SocketHandler</code> instance.
    */
   public void run(){   
      while(true){
         try {  
            execute();
         }catch(IOException e){
            if(server.isClosed())
               break;
         }
      }
   } 
   
   /**
    * This method is used to distinguish <code>Exception</code>
    * occurrences related to the <code>ServerSocket</code> from
    * those related to the accepted <code>Socket</code> objects.
    * <p>
    * This enables critical actions to be taken when the server
    * socket fails. This will ignore failures with individual
    * sockets, as these are specific to the connections.
    *
    * @exception IOException this is thrown if the is a problem
    * accepting connections from the <code>ServerSocket</code>
    */
   private void execute() throws IOException {
      while(true) {
         Socket sock = server.accept();
         try {
            configure(sock);
            handler.process(sock);
         } catch(Exception e){
            continue;
         }
      }
   }
   
   /**
    * This configures each TCP connection for use with HTTP 
    * applications. This has a wait time of 1 minute for a read 
    * before an there is an <code>InterruptedIOException</code> 
    * is thrown from the <code>SocketInputStream</code>.
    * <p>
    * This also configures the <code>Socket</code> with 
    * <code>Socket.setTcpNoDelay</code> set to true and the 
    * <code>Socket.setSoLinger</code> to false. The reasons for 
    * this is so that Nagles algorithm is not used and so that 
    * the connection will not linger.  
    *
    * @param sock this is the <code>Socket</code> that is to
    * be configured
    * 
    * @exception IOException this is thrown if there is an I/O 
    * problem configuring the <code>Socket</code>
    */
   private void configure(Socket sock) throws IOException{
      if(sock.getSoTimeout() <= 0){
         sock.setSoTimeout(60000);
      }
      sock.setTcpNoDelay(true);
      sock.setSoLinger(false,0);
   }
}

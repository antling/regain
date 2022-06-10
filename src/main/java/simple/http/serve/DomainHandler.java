/*
 * DomainHandler.java February 2001
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
 
package simple.http.serve;

import simple.http.ProtocolHandler;
import simple.http.Request;
import simple.http.Response;
import java.util.Hashtable;

/**
 * This class is used to route requests to the proper host. This 
 * allows the virtual host facility to be achieved with a set of 
 * <code>ProtocolHandler</code>'s. This will ensure that if requests 
 * are issued, the host header is checked to see what domain it has
 * and dispatches it to the correct <code>ProtocolHandler</code>.
 * <p>
 * Also if there is a Host header with an unknown domain name then 
 * the default <code>ProtocolHandler</code> is given the request.
 * In this case the default protocol handler chosen may be an error
 * handler. However to avoid this the domain multiplexer may be 
 * wrapped in another <code>ProtocolHandler</code> implementation
 * to handle these requests before the <code>DomainHandler</code>
 * sees them. 
 *
 * @author Niall Gallagher
 */ 
public class DomainHandler implements ProtocolHandler {

   /**
    * This is the primary <code>ProtocolHandler</code>.
    */
   private ProtocolHandler primary;

   /**
    * The <code>Hashtable</code> for the virtual hosts.
    */
   private Hashtable handlers; 

   /**
    * Constructor for the <code>DomainHandler</code>. This takes
    * a default <code>ProtocolHandler</code>. Which enables this to
    * accommodate HTTP/1.0 clients to interact with the server without
    * using the Host header. This default <code>ProtocolHandler</code> 
    * can also be an error generator that will respond with a 400, Bad 
    * Request, status code as suggested by RFC 2616 section 14.23 for
    * HTTP/1.1 clients without the Host header.
    * <p>
    * This ensures that there will be very little performance loss when 
    * there are several <code>ProtocolHandler</code>'s being multiplexed 
    * using this <code>ProtocolHandler</code> implementation. 
    * 
    * @param primary the default <code>ProtocolHandler</code>
    */ 
   public DomainHandler(ProtocolHandler primary){
      this.handlers = new Hashtable();
      this.primary = primary;
   }

   /**
    * This is used to route the request to the correct handler. This 
    * will use the Host header to choose a handler for the request.
    * The host header is a mandatory client  request header for 
    * HTTP/1.1 clients. The header contains the domain of the intended 
    * host and an optional port.
    * <p>
    * For compatibility with HTTP/1.0 clients that do not support the
    * Host header this delegates the processing of requests without 
    * the Host header to a default <code>ProtocolHandler</code>.
    *
    * @param req this is the <code>Request</code> that was made from
    * the http client
    * @param resp this is the <code>Response</code> that will be given
    * to the client
    */ 
   public void handle(Request req, Response resp){          
      ProtocolHandler handler = primary;
      if(req.getMajor()>0 && req.getMinor()>0){
         if(req.contains("Host")){
            String domain = req.getValue("Host");
            handler = lookup(domain);         
         }            
      }
      handler.handle(req, resp);                        
   }
   
   /**
    * This will insert a new <code>ProtocolHandler</code> for the
    * specified domain. The new <code>ProtocolHandler</code> will
    * manage the requests that have the host header with the domain 
    * name part equal to the domain given.
    *
    * @param domain the name if the virtual host to be added
    * @param handler the <code>ProtocolHandler</code> that will
    * process requests
    */ 
   public void insert(ProtocolHandler handler, String domain){
      handlers.put(domain, handler);
   }

   /**
    * This will remove any <code>ProtocolHandler</code> for the
    * specified domain name. Further <code>Request</code>'s to 
    * this domain will not be processed.
    *
    * @param domain this is the name of the handler to remove
    */ 
   public void remove(String domain){      
      handlers.remove(domain);
   }

   /**
    * This is used to lookup a certain domain name to see if the 
    * <code>DomainHandler</code> has it. If this returns true 
    * then the domain exists.
    *
    * @param domain the host name being checked for in this
    *
    * @return true if there is a handler with this domain 
    */ 
   public boolean contains(String domain){
      return handlers.containsKey(domain);
   }

   /**
    * Used to lookup a specific <code>ProtocolHandler</code>
    * for a specific domain. This allows the user to take the
    * <code>ProtocolHandler</code> objects from this for some
    * modification, or to use it.
    *
    * @param domain the host domain name for this handler
    *
    * @return the <code>ProtocolHandler</code> for the domain
    */ 
   public ProtocolHandler lookup(String domain){       
      domain = domain.trim();
      if(domain.endsWith(":80")){ 
         domain = domain.substring(0,domain.length()-3);          
      }
      Object handler = null;
      handler = handlers.get(domain);      
      
      if(handler == null){
         return primary;
      }
      return (ProtocolHandler)handler;
   }
}

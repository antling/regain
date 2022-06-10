/*
 * ResourceProcessor.java February 2001
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

/**
 * The <code>ResourceProcessor</code> is an implementation of the
 * <code>ProtocolHandler</code> interface for handling an arbitrary
 * <code>ResourceEngine</code>. 
 * <p>
 * The <code>HandlerFactory</code> produces this type of
 * <code>ProtocolHandler</code> which wraps the engine instance.
 * This uses the <code>Request.getURI</code> to resolve the object
 * to be used for the HTTP transaction.
 *
 * @author Niall Gallagher
 */
class ResourceProcessor implements ProtocolHandler {   

   /**
    * The engine that produces the <code>Resources</code>.
    */
   private ResourceEngine engine;

   /**
    * The creates a <code>ResourceProcessor</code> object using an
    * implementation of the <code>ResourceEngine</code> interface.
    * The <code>ResourceEngine</code> will be used to produce the
    * implementations of the <code>Resource</code> object.
    *
    * @param engine this is the <code>ResourceEngine</code> that 
    * produces the <code>Resource</code> objects
    */
   public ResourceProcessor(ResourceEngine engine){
      this.engine = engine;
   }

   /**
    * This method is where most of the work is done to retrieve 
    * the <code>Resource</code> and process the HTTP request. This
    * will basically use the <code>lookup</code> method of the
    * issued <code>ResourceEngine</code> to acquire resources.
    *
    * @param req the <code>Request</code> to be processed
    * @param resp the <code>Response</code> to be processed
    */
   public void handle(Request req, Response resp){
      engine.lookup(req.getURI()).handle(req,resp);
   }
}

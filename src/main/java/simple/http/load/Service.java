/*
 * Service.java March 2002
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
  
package simple.http.load;

import simple.http.serve.Resource;

/**
 * The <code>Service</code> is used for implementations of the
 * <code>Resource</code> that require/desire dynamic loading
 * using the <code>LoaderEngine</code> object. Any implementation
 * of the <code>Service</code> MUST implement a public single 
 * argument constructor that takes a <code>Context</code> so that
 * it can be instantiated by the <code>LoaderEngine</code>. 
 * <p>
 * The instance is created by attempting to invoke the public 
 * single argument constructor. If this succeeds the instance 
 * is initialized using the <code>prepare</code> method before it 
 * handles its first request. If the service instance recieves no
 * requests then the <code>prepare</code> method may not be used
 * by the <code>LoaderEngine</code>.
 * 
 * @author Niall Gallagher
 */
public interface Service extends Resource {

   /**
    * Allows the <code>Service</code> to be initialized without
    * the need for introspection. The <code>LoaderEngine</code>
    * enables implementations of this to delegate to other
    * loaded services. The <code>LoaderEngine</code> is used to 
    * acquire loaded <code>Resource</code> objects that share 
    * the same <code>Context</code>.
    * <p> 
    * This is useful if an implementation provides some initial
    * functionality and then wishes to delegate the remaining
    * processing to some other resource. The engine provided
    * will initialize the instance and so each resource object
    * retrieved from the engine will share the same context.
    *
    * @param engine this is the engine that this was loaded by
    * @param data an object that can be used to that can be used
    * to configure itself the service
    *
    * @exception LoadingException this is thrown if for some 
    * reason the <code>Service</code> cannot be initialized
    */               
   public void prepare(LoaderEngine engine, Object data)
      throws LoadingException; 
} 

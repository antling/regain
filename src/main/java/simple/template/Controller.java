/*
 * Controller.java December 2003
 *
 * Copyright (C) 2003, Niall Gallagher <niallg@users.sf.net>
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

package simple.template;

import simple.http.load.LoadingException;
import simple.http.load.Service;

/**
 * The <code>Controller</code> interface is used to describe all
 * service implementations that act as controllers within an MVC
 * architecture. Service implementations will use this interface 
 * so that the service type is clear and in order to provide the
 * the correct <code>prepare</code> method signature.
 * <p>
 * Implementations of this type will typically be loaded by the 
 * <code>TemplateEngine</code> object, which is used to map the
 * service implementations using a specific mapping policy. Once
 * a mapping resolves to a specific implementation, the service
 * is loaded, instantiated, and initialized with an environment.
 * 
 * @author Niall Gallagher
 *
 * @see simple.template.TemplateEngine
 */
public interface Controller extends Service {

   /**
    * This <code>prepare</code> method is used to initialize the
    * instance once it has been loaded. This is given the
    * environment used by the template engine, which will be 
    * shared by all controllers loaded by that template engine.
    * <p>  
    * The <code>Environment</code> implementation can be used to
    * acquire <code>Document</code> objects and properties that
    * are available from the templating system. This
    * provides a simple but effective means of passing all 
    * initialization data to the controller once it is loaded.
    *
    * @param system the environment used by the template engine
    * 
    * @throws LoadingException thrown if the preparation fails
    */
   public void prepare(Environment system) throws LoadingException;
}

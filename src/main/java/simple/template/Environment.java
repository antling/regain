/*
 * Environment.java December 2003
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

/**
 * The <code>Environment</code> object represents an environment for
 * service objects. It unifies the methods required for accessing 
 * templates from the templating system with those that are used
 * to interact with the outside world. This allows services required
 * to act as <code>Controller</code> objects to access both model
 * and view for processing requests, thus completing a Model View
 * Controller (MVC) architecture. This encapsulates the mapping
 * mechanism used by the template engine.
 *
 * @author Niall Gallagher
 */
public interface Environment extends Container {

   /**
    * Looks for the named template and wraps the template within
    * a new <code>Document</code> instance. Resolving the location
    * of the template is done using the internal mapping scheme
    * employed by the template engine. This will extract the path 
    * from the HTTP URI and delegate to the <code>lookup</code>
    * method, which will use the templating system to locate the
    * template. This is used to hide all mapping details.
    * <p>
    * The document created by this method is transient, that is,
    * it is a unique instance. This means that changes to the 
    * properties of any created document object affect only that 
    * instance. By default this assumes the UTF-8 encoding. 
    * 
    * @param target the unmodified URI to resolve to a template
    *
    * @return the specified template wrapped within a document
    *
    * @throws Exception this is thrown if ther is a problem with
    * locating or rendering the resolved template
    */
   public Document resolve(String target) throws Exception;
}

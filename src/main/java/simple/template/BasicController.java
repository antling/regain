/*
 * BasicController.java December 2003
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

import simple.http.load.ReflectionService;
import simple.http.load.LoadingException;
import simple.http.serve.Context;
import simple.http.Response;
import simple.http.Request;

/**
 * The <code>BasicController</code> object provides a base class,
 * which can be used by services requiring templating functionality. 
 * Implementations can override either of the <code>process</code>
 * methods to make use of templating. The templating functionality
 * is provided by an <code>Environment</code> implementation.
 * <pre>
 *
 *    process(Request,&nbsp;Response)
 *    process(Request,&nbsp;Response,&nbsp;Document)
 *
 * </pre>
 * Typically subclasses of the <code>BasicController</code> will
 * implement either of the above methods. The first of the two is
 * used when the URI mapping scheme does not directly reference a
 * template document. The second method is overridden when the URI
 * can be mapped to a valid template path. For example if the URI
 * issued was <code>/demo/DemoController.class/template</code>,
 * and the default mapping scheme was left in place, then the path
 * <code>/template</code>, which is a valid document path, is used
 * to acquire the <code>Document</code>.
 *
 * @author Niall Gallagher 
 *
 * @see simple.http.load.BasicService
 */
public class BasicController extends ReflectionService implements Controller{

   /**
    * Represents the environment the controller exists within.
    */
   protected Environment system;

   /**
    * Constructor for the <code>BasicController</code> is used so
    * that the <code>Context</code> can be acquired. This enables 
    * the implementation to retrieve resources from the underlying
    * file system. The <code>Context</code> is required for format
    * of errors and directory listing. If the context is null the
    * implementation will not generate HTTP error messages.
    *
    * @param context this is the <code>Context</code> that is used
    * to generate error messages and acquire files with
    */
   public BasicController(Context context){
      super(context);
   }

   /**
    * The <code>prepare</code> method is used to initialize the
    * instance once it has been loaded. This is given the
    * environment used by the template engine, which will be 
    * shared by all controllers loaded by that template engine.
    *
    * @param system the environment used by the template engine
    * to acquire templates and data sources
    *
    * @throws LoadingException thrown if the preparation fails
    */
   public void prepare(Environment system) throws LoadingException{
      this.system = system;
   }

   /**
    * This <code>process</code> method is used if there are several 
    * different templates to be used or if the URI mapping scheme 
    * does not directly map on to the required template. Overriding 
    * this method is generally done by more complex controller 
    * objects, such as those used to perform special layout 
    * configurations for skinnable templates.  
    *
    * @param req the HTTP request object representing the client
    * @param resp the HTTP response object to send a reply with
    *
    * @throws Exception thrown if there is a problem processing
    */
   protected void process(Request req, Response resp) throws Exception{
      process(req, resp, system.resolve(req.getURI()));
   }    

   /**
    * This <code>process</code> method is used if the HTTP 
    * URI mapping is used to identify the primary template. This 
    * relies on the system wide mapping scheme in use to provide  
    * the <code>Document</code> that  will be used to process 
    * the request. This is a convenience method that avoids 
    * having to locate the template to use.
    * <p>
    * Controller implementations that perform simple operations
    * on a specific template should override this method in a
    * subclass. Once the issued document has been populated with
    * data to be used by the template its contents can then be 
    * written using the <code>OutputStream</code>. 
    *
    * @param req the HTTP request object representing the client
    * @param resp the HTTP response object to send a reply with
    * @param doc the document resolved from the HTTP URI mapping
    *  
    * @throws Exception thrown if there is a problem processing
    */
   protected void process(Request req, Response resp, Document doc) 
      throws Exception{}
}


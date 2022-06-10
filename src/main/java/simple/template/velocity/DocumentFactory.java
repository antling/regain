/*
 * DocumentFactory.java December 2003
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

package simple.template.velocity;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.Template;
import java.io.FileNotFoundException;
import simple.template.Document;

/**
 * The <code>DocumentFactory</code> serves to resolve a document
 * instance from a specified path. The location of the document
 * is relative to the user specified <cite>Velocity</cite> 
 * template root. The documents resolved by this factory object
 * wrap the template located at the specified path. In order to
 * supply an initial set of data to a created document object 
 * a <code>Context</code> instance must be supplied.
 *
 * @author Niall Gallagher 
 */
class DocumentFactory {

   /**
    * The engine used to acquire templates from a root path.
    */
   private VelocityEngine engine;

   /**
    * Constructor for the <code>DocumentFactory</code>. This will
    * create a factory for creating document objects. The engine
    * is used to create and configure all referenced templates
    * which are then wrapped in a <code>Document</code> object.
    *
    * @param engine the engine used to acquire the templates 
    */
   public DocumentFactory(VelocityEngine engine) {
      this.engine = engine;
   }

   /**      
    * Retrieves the referenced <code>Template</code> object from
    * the <cite>Velocity</cite> runtime. This method is provided
    * for convenience, to avoid using <code>getTemplate</code> 
    * with the <code>VelocityEngine</code> object. By default
    * this uses UTF-8 character encoding.
    *
    * @param path this is the path used to locate the template
    *
    * @throws FileNotFoundException if the template is not found
    */
   private Template getTemplate(String path) throws Exception {
      try {
         return engine.getTemplate(path, "utf-8");
      }catch(ResourceNotFoundException e){
         throw new FileNotFoundException(path);
      }
   }

   /**
    * Creates a new <code>Document</code> object, which wraps the
    * referenced template. Resolving the location of the template 
    * to load is left up the <cite>Velocity</cite> runtime, which
    * acquires all resources from a user specified template root.
    * <p>
    * The document created by this method is transient, that is,
    * it exists locally only. This means that changes to the 
    * properties of any created document object affect only that 
    * instance. By default this assumes the UTF-8 encoding. 
    * <p>
    * To provide an initial set of properties to the document a
    * <code>Context</code> is used to populate the document. All
    * that is required to supply supplemental properties to the
    * document is to provide a populated context instance.
    * 
    * @param path this is the path used to locate the template
    * @param context the context object to use for properties
    *
    * @throws FileNotFoundException if the template is not found
    */
   public Document getInstance(String path, Context context) throws Exception {
      return getInstance(getTemplate(path), context);
   }

   /**
    * This method will create a <code>Document</code> using the
    * issued <code>Template</code> object. The resulting document
    * is populated with data from the issued context object. 
    * Changes to the properties of the document will be directly
    * inserted into the issued <code>Context</code> instance.
    * 
    * @param template the template object wrapped by the document
    * @param context this contains the initial set of properties 
    * 
    * @return an empty document that wraps the given template
    */
   private Document getInstance(Template template, Context context) {
      return new DefaultDocument(template, context);
   }
}

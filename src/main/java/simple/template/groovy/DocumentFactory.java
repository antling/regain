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

package simple.template.groovy;

import simple.http.serve.FileContext;
import simple.http.serve.Context;
import simple.template.Document;
import groovy.text.TemplateEngine;
import groovy.text.Template;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.io.File;

/**
 * The <code>DocumentFactory</code> serves to resolve a document
 * instance from a specified path. The location of the document
 * is determined using the provided <code>Context</code> object.
 * The documents resolved by this factory object wrap the 
 * template located at the specified path. To supply an initial
 * set of data to a created document object a <code>Map</code>
 * instance must be supplied, properties within this object are
 * available to the template when it is rendered.
 *
 * @author Niall Gallagher 
 */
class DocumentFactory {

   /**
    * The engine used to acquire templates from the file.
    */
   private TemplateEngine engine;
   
   /**
    * Determines where the referenced template is located.
    */
   private Context context;

   /**
    * Constructor for the <code>DocumentFactory</code>. This will
    * create a factory for creating document objects. The engine
    * is used to create and configure all referenced templates
    * which are then wrapped in a <code>Document</code> object.
    *
    * @param engine the engine used to acquire the templates 
    */
   public DocumentFactory(TemplateEngine engine) {
      this(engine, new FileContext());
   }

   /**
    * Constructor for the <code>DocumentFactory</code>. This will
    * create a factory for creating document objects. The engine
    * is used to create and configure all referenced templates
    * which are then wrapped in a <code>Document</code> object.
    *
    * @param engine the engine used to acquire the templates 
    * @param base the directory that contains all templates
    */ 
   public DocumentFactory(TemplateEngine engine, File base) {
      this(engine, new FileContext(base));
   }

   /**
    * Constructor for the <code>DocumentFactory</code>. This will
    * create a factory for creating document objects. The engine
    * is used to create and configure all referenced templates
    * which are then wrapped in a <code>Document</code> object.
    *
    * @param engine the engine used to acquire the templates 
    * @param context the context used to locate the templates
    */   
   public DocumentFactory(TemplateEngine engine, Context context) {
      this.context = context;
      this.engine = engine;
   }

   /**      
    * Retrieves the referenced <code>Template</code> object from
    * the <cite>Groovy</cite> runtime. This method is provided
    * for convenience, to avoid using <code>createTemplate</code> 
    * with the <code>TemplateEngine</code> object. By default
    * this uses UTF-8 character encoding.
    *
    * @param path this is the path used to locate the template
    *
    * @throws FileNotFoundException if the template is not found
    */
   private Template getTemplate(String path) throws Exception {
      return getTemplate(context.getFile(path));
   }

   /**      
    * Retrieves the referenced <code>Template</code> object from
    * the <cite>Groovy</cite> runtime. This method is provided
    * for convenience, to avoid using <code>createTemplate</code> 
    * with the <code>TemplateEngine</code> object. By default
    * this uses UTF-8 character encoding.
    *
    * @param file this is the file that references the template
    *
    * @throws FileNotFoundException if the template is not found
    */
   private Template getTemplate(File file) throws Exception {
      return getTemplate(file, "utf-8");
   }

   /**      
    * Retrieves the referenced <code>Template</code> object from
    * the <cite>Groovy</cite> runtime. This method is provided
    * for convenience, to avoid using <code>createTemplate</code> 
    * with the <code>TemplateEngine</code> object. This will use  
    * the specified character encoding to read the file contents.
    *
    * @param file this is the file that references the template
    * @param charset this is the charset to read the contents as
    *
    * @throws FileNotFoundException if the template is not found
    */
   private Template getTemplate(File file, String charset) throws Exception {
      return getTemplate(new FileInputStream(file), charset);
   }

   /**      
    * Retrieves a <code>Template</code> using the provided stream.
    * This method is provided for convenience, to avoid using 
    * <code>createTemplate</code> for each template created. This 
    * will use  the specified charset to read the stream data.
    *
    * @param data this is stream that contains the template data
    * @param charset this is the charset to read the contents as
    */
   private Template getTemplate(InputStream data, String charset) throws Exception {
      return getTemplate(new InputStreamReader(data, charset));
   }
 
   /**      
    * Retrieves a <code>Template</code> using the provided data.
    * This method is provided for convenience, to avoid using 
    * <code>createTemplate</code> for each template created.
    *
    * @param data this is source that contains the template data
    */
   private Template getTemplate(Reader data) throws Exception {
      return engine.createTemplate(data);
   } 

   /**
    * Creates a new <code>Document</code> object, which wraps the
    * referenced template. Resolving the location of the template 
    * to load is left up to the <code>Context</code> object, which
    * acquires all resources from its base directory.
    * <p>
    * The document created by this method is transient, that is,
    * it exists locally only. This means that changes to the 
    * properties of any created document object affect only that 
    * instance. By default this assumes the UTF-8 encoding. 
    * <p>
    * To provide an initial set of properties to the document a
    * <code>Map</code> is used to populate the document. All
    * that is required to supply supplemental properties to the
    * document is to provide a populated <code>Map</code> object.
    * 
    * @param path this is the path used to locate the template
    * @param binding the bindings to set within the template
    *
    * @throws FileNotFoundException if the template is not found
    */
   public Document getInstance(String path, Map binding) throws Exception {
      return getInstance(getTemplate(path), binding);
   }

   /**
    * This method will create a <code>Document</code> using the
    * issued <code>Template</code> object. The resulting document
    * is populated with data from the issued <code>Map</code>.
    * Changes to the properties of the document will be directly
    * inserted into the issued <code>Map</code> instance.
    * 
    * @param template the template object wrapped by the document
    * @param binding this contains the initial set of properties 
    * 
    * @return an empty document that wraps the given template
    */
   private Document getInstance(Template template, Map binding) {
      return new DefaultDocument(template, binding);
   }
}

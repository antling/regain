/*
 * GroovyContainer.java January 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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

import groovy.text.TemplateEngine;
import groovy.text.SimpleTemplateEngine;
import simple.http.serve.FileContext;
import simple.http.serve.Context;
import simple.template.Container;
import simple.template.Document;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * The <code>GroovyContainer</code> provides an implementation
 * of the <code>Container</code> object for <cite>Groovy</cite>.
 * This is used with <code>simple.template.TemplateEngine</code> 
 * to provide template documents, written in the Groovy Script
 * Language, to service objects. 
 * <p>
 * A subclassed implementation of this <code>Container</code>
 * typically implements the <code>lookup(String)</code> method
 * in order to create a set of bindings for the template. This
 * can done by creating a <code>Map</code> object and delegating
 * to the <code>lookup(String,&nbsp;Map)</code> method once the
 * object has been populated.
 *
 * @author Niall Gallagher
 */
public class GroovyContainer extends PlainDatabase implements Container {

   /**
    * Creates the document objects using the specified engine.
    */
   private DocumentFactory factory;

   /**
    * Provides the engine used to create template objects.
    */
   private TemplateEngine engine;

   /**
    * Determines where the referenced template is located.
    */
   private Context context;

   /**
    * Constructor for the <code>GroovyContainer</code> object. The
    * instance created will contain an empty set of properties and
    * ensures that all referenced templates are loaded from the 
    * current working directory of the executing application.
    */
   public GroovyContainer(){
      this(new FileContext());
   }

   /**
    * Constructor for the <code>GroovyContainer</code> object. The
    * instance created will contain an empty set of properties and
    * ensures that all referenced templates are loaded from the 
    * specified base directory. If the provided <code>File</code>
    * is not a directory then referenced templates will not load.
    *
    * @param base the root directory that contains the templates
    */
   public GroovyContainer(File base){
      this(new FileContext(base));
   }

   /**
    * Constructor for the <code>GroovyContainer</code> object. The
    * instance created will contain an empty set of properties and
    * ensures that all referenced templates are located using the
    * specified <code>Context</code> object.
    *
    * @param context determines where the templates are located
    */
   public GroovyContainer(Context context){
      this(new SimpleTemplateEngine(), context);
   }

   /**
    * Constructor for the <code>GroovyContainer</code> object. The
    * instance created will contain an empty set of properties and
    * ensures that all referenced templates are loaded from the 
    * current working directory of the executing application.
    * <p>
    * If a specialized <code>TemplateEngine</code> instance is
    * required one can be provided this enables features such as
    * the syntax of the template commands to be independant.
    *
    * @param engine this is the engine used by this instance
    */
   public GroovyContainer(TemplateEngine engine){
      this(engine, new FileContext());
   }

   /**
    * Constructor for the <code>GroovyContainer</code> object. The
    * instance created will contain an empty set of properties and
    * ensures that all referenced templates are loaded from the 
    * specified base directory. If the provided <code>File</code>
    * is not a directory then referenced templates will not load.
    * <p>
    * If a specialized <code>TemplateEngine</code> instance is
    * required one can be provided this enables features such as
    * the syntax of the template commands to be independant.
    *
    * @param engine this is the engine used by this instance
    * @param base the root directory that contains the templates
    */
   public GroovyContainer(TemplateEngine engine, File base){
      this(engine, new FileContext(base));
   }

   /**
    * Constructor for the <code>GroovyContainer</code> object. The
    * instance created will contain an empty set of properties and
    * ensures that all referenced templates are located using the
    * specified <code>Context</code> object.
    * <p>
    * If a specialized <code>TemplateEngine</code> instance is
    * required one can be provided this enables features such as
    * the syntax of the template commands to be independant.
    *
    * @param engine this is the engine used by this instance
    * @param context determines where the templates are located
    */
   public GroovyContainer(TemplateEngine engine, Context context){
      this.factory = new DocumentFactory(engine, context);
      this.binding = new Hashtable();
      this.context = context;
      this.engine = engine;
   }

   /**
    * Determines whether the named template exists. This is used
    * to determine if the <code>lookup</code> method will locate
    * a template given the specified path. If the template is
    * accessible this returns true, otherwise false is returned.
    *
    * @param path this is the path used to locate the template
    *
    * @return true if the template exists, false if it does not
    */
   public boolean exists(String path) {
      return context.getFile(path).exists();
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
    * 
    * @param path this is the path used to locate the template
    *
    * @throws Exception this is thrown if the is a problem with
    * locating or rendering the specified template
    */
   public Document lookup(String path) throws Exception {
      return lookup(path, new Delegate());
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
    * @throws Exception this is thrown if the is a problem with
    * locating or rendering the specified template
    */
   public Document lookup(String path, Map binding) throws Exception{
      return factory.getInstance(path, binding);
   }

   /**
    * The <code>Delegate</code> object is used provide documents
    * with a populated <code>Map</code>. This enables properties
    * that have been set within this <code>Container</code> to
    * be inherited by each document instance. If the properties
    * need to be overridden then they only need to be reset.
    */
   private class Delegate extends HashMap {

      /**
       * Constructor for the <code>Delegate</code> object. The
       * constructor delegates to the super class constructor
       * using the containers bindings to provide initial data.
       * This prevents documents from changing global bindings.
       */
      public Delegate() {
         super(binding);
      }
   }
}

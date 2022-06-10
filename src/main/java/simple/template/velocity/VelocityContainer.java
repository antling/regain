/*
 * VelocityContainer.java January 2004
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

package simple.template.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import simple.http.serve.FileLocator;
import simple.http.serve.Locator;
import simple.template.Container;
import simple.template.Document;
import java.io.File;

/**
 * The <code>VelocityContainer</code> provides an implementation
 * of the <code>Container</code> object for <cite>Velocity</cite>.
 * This can be used with a <code>TemplateEngine</code> to provide
 * template documents written in the Velocity Templating Language
 * (VTL) to service objects. This container will use a Java
 * properties file named either <code>velocity.properties</code>
 * or <code>Velocity.properties</code> to configure itself.
 * <p>
 * The Java properties file used for configuration is searched
 * for within the current working directory. However, if there
 * is a <code>Locator</code> object supplied then it is used to
 * search for the properties file. The means of searching is
 * determined by the <code>Locator</code> object.
 * <p>
 * A subclassed implementation of this <code>Container</code>
 * typically implements the <code>lookup(String)</code> method
 * inorder to populate a context with various tools or objects.
 * This can done by creating a <code>Context</code> instance and
 * delegating to the <code>lookup(String,&nbsp;Context)</code>
 * method once the context object has been populated.
 *
 * @author Niall Gallagher
 *
 * @see org.apache.velocity.app.VelocityEngine
 */
public class VelocityContainer extends PlainDatabase implements Container {

   /**
    * Creates the document objects using the specified engine.
    */
   private DocumentFactory factory;

   /**
    * Provides the engine used to create template objects.
    */
   private VelocityEngine engine;

   /**
    * Constructor for the <code>VelocityContainer</code> object.
    * The instance created will contain an empty set of properties
    * and initializes the engine by using a Java properties file
    * located within the current working directory.
    *
    * @throws Exception if there is an initialization problem
    */
   public VelocityContainer() throws Exception{
      this(new FileLocator());
   }

   /**
    * Constructor for the <code>VelocityContainer</code> object.
    * The instance created will contain an empty set of properties
    * and initializes the engine by using a Java properties file
    * located using the supplied <code>Locator</code> object.
    *
    * @param lookup the locator used to locate the properties
    *
    * @throws Exception if there is an initialization problem
    */
   public VelocityContainer(Locator lookup) throws Exception{
      this(new VelocityEngine(), lookup);
   }

   /**
    * Constructor for the <code>VelocityContainer</code> object.
    * The instance created will contain an empty set of properties
    * and initializes the engine by using a Java properties file
    * located within the current working directory.
    * <p>
    * If a specialized <code>VelocityEngine</code> instance is
    * required one can be provided, this enables properties, such
    * as the template root and application attributes to be set.
    *
    * @param engine this is the engine used by this instance
    *
    * @throws Exception if there is an initialization problem
    */
   public VelocityContainer(VelocityEngine engine) throws Exception{
      this(engine, new FileLocator());
   }

   /**
    * Constructor for the <code>VelocityContainer</code> object.
    * The instance created will contain an empty set of properties
    * and initializes the engine by using a Java properties file
    * located using the supplied <code>Locator</code> object.
    * <p>
    * If a specialized <code>VelocityEngine</code> instance is
    * required one can be provided, this enables properties, such
    * as the template root and application attributes to be set.
    *
    * @param engine this is the engine used by this instance
    * @param lookup the locator used to locate the properties
    *
    * @throws Exception if there is an initialization problem
    */
   public VelocityContainer(VelocityEngine engine, Locator lookup) throws Exception{
      this.factory = new DocumentFactory(engine);
      this.context = new VelocityContext();
      this.engine = engine;
      this.init(lookup);
   }

   /**
    * In the event that the <code>VelocityEngine</code> instance is
    * uninitialized this attempts to load a Java properties file to
    * provide configuration information. By default the properties
    * are located in a file named <code>velocity.properties</code>.
    * If the properties file cannot be loaded from the supplied
    * <code>Locator</code> instance then this will attempt to
    * initialize the engine without any specified properties.
    *
    * @param lookup the locator used to locate the properties
    *
    * @throws Exception if there is a problem on initialization
    */
   protected void init(Locator lookup) throws Exception {
      try {
         load(lookup);
      }catch(Exception e){
         engine.init();
      }
   }

   /**
    * This <code>load</code> method attempts to load a properties
    * file <code>Velocity.properties</code> using the given locator.
    * If the properties file exists then it is used to configure
    * the velocity engine used by this container instance.
    * <p>
    * This will attempt to load the file in a case insensitive
    * manner so the file can be named either <code>Velocity</code>
    * or <code>velocity</code>. If the file cannot be loaded this
    * will throw an <code>Exception</code>.
    *
    * @param lookup the locator used to locate the properties
    *
    * @exception Exception thrown if there is an I/O problem
    */
   private void load(Locator lookup) throws Exception {
      try {
         load(lookup,"velocity.properties");
      }catch(Exception e) {
         load(lookup,"Velocity.properties");
      }
   }

   /**
    * This will load the named file using the given locator. This
    * is used so that a properties file can be located using an
    * arbitrary search method. If the Java properties file cannot
    * be loaded this will throw an <code>Exception</code>.
    *
    * @param lookup the locator used to locate the properties
    * @param name this is the name of the properties file loaded
    *
    * @exception Exception thrown if there is an I/O problem
    */
   private void load(Locator lookup, String name) throws Exception {
      engine.init(lookup.getProperties(name));
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
      return engine.templateExists(path);
   }

   /**
    * Looks for the named template and wraps the template within
    * a new <code>Document</code> instance. Resolving the location
    * of the template is left up the templating system, typically
    * this requires a file path reference to locate the template.
    * <p>
    * The document created by this method is transient, that is,
    * it is a unique instance. This means that changes to the
    * properties of any created document object affect only that
    * instance. By default this assumes the UTF-8 encoding.
    *
    * @param path this is the path used to locate the template
    *
    * @return the specified template wrapped within a document
    *
    * @throws Exception this is thrown if the is a problem with
    * locating or rendering the specified template
    */
   public Document lookup(String path) throws Exception {
      return lookup(path, new Delegate());
   }

   /**
    * Looks for the named template and wraps the template within
    * a new <code>Document</code> instance. Resolving the location
    * of the template is left up the templating system, typically
    * this requires a file path reference to locate the template.
    * <p>
    * The document created by this method is transient, that is,
    * it is a unique instance. This means that changes to the
    * properties of any created document object affect only that
    * instance. By default this assumes the UTF-8 encoding.
    * <p>
    * This method allows the <code>Context</code> used by the
    * document object to be specified. This is useful if various
    * tools need to be added to the context before it is used by
    * the document, such as rendering tools and various others.
    *
    * @param path this is the path used to locate the template
    * @param context this provides a set of default properties
    *
    * @return the specified template wrapped within a document
    *
    * @throws Exception this is thrown if the is a problem with
    * locating or rendering the specified template
    */
   public Document lookup(String path, Context context) throws Exception{
      return factory.getInstance(path, context);
   }

   /**
    * The <code>Delegate</code> object is used provide documents
    * with their own <code>Context</code>. This enables changes
    * made to the documents properties to be specific to that
    * instance. This will wrap this <code>Container</code>
    * context in such a way that it is used in a read only way.
    *
    * @see org.apache.velocity.VelocityContext
    */
   private class Delegate extends VelocityContext {

      /**
       * Constructor for the <code>Delegate</code> object. The
       * constructor delegates to the super class constructor
       * using the containers context as the inner context.
       * This prevent documents from changing that context.
       */
      public Delegate() {
         super(context);
      }
   }
}

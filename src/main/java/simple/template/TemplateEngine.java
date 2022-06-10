/*
 * TemplateEngine.java December 2003
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

import simple.http.serve.ResourceEngine;
import simple.http.serve.FileContext;
import simple.http.serve.Resource;
import simple.http.serve.Context;
import java.io.File;

/**
 * The <code>TemplateEngine</code> provides the core functionality 
 * required to process templates. This provides an implementation of
 * the <code>ResourceEngine</code> interface, and as such is used to
 * acquire resources using a HTTP URI. The <code>Resource</code>
 * objects served by this are loaded and mapped using the mapping
 * functionality found in the <code>MapperEngine</code>. This means
 * that resource implementations, known as services or controllers,
 * can be implemented and placed in a specific directory, and once
 * the objects are referenced they will be loaded and served.
 * <p>
 * Typically the <code>Resource</code> objects served by this will
 * be implementations of the <code>Controller</code>, which enables
 * them to interact with a shared environment. The controllers are
 * similar in concept to a Java Servlet and allow event based HTTP
 * request and response objects to be processed. 
 * <p>  
 * This requires a <code>Container</code> implementation in order
 * to configure itself. The container provided is used to look for
 * templates and set properties that are accessable to all service
 * objects loaded by this engine, also, properties set with the
 * container will be accessable to all templates processed. 
 *
 * @author Niall Gallagher
 */
public class TemplateEngine implements ResourceEngine {

   /**
    * The internal mapping engine that loads the controllers.
    */   
   private ReloadEngine engine;

   /**
    * Used to monitor and maintain loaded controller objects.
    */
   private Maintainer monitor;

   /**
    * Constructor for the <code>TemplateEngine</code> object. The
    * instance created by this provides a mapping resource engine
    * that loads and instantiates referenced services using the
    * <code>simple.http.load.MapperEngine</code> to resolve the
    * class names for services. Each loaded controller service is
    * instantiated using an <code>Environment</code> object.
    * 
    * @param context this is the context used by this engine
    * @param system this provides the environment for controllers
    *
    * @throws Exception if there is a problem on initialization
    */
   public TemplateEngine(Context context, Container system) throws Exception {
      this(context, system, new File("."));
   }

   /**
    * Constructor for the <code>TemplateEngine</code> object. The
    * instance created by this provides a mapping resource engine
    * that loads and instantiates referenced services using the
    * <code>simple.http.load.MapperEngine</code> to resolve the
    * class names for services. Each loaded controller service is
    * instantiated using an <code>Environment</code> object. The
    * file specified acts as a local file system classpath.
    * 
    * @param context this is the context used by this engine
    * @param system the interface to the templating system used
    * @param base this is the local file system classpath used
    *
    * @throws Exception if there is a problem on initialization
    */
   public TemplateEngine(Context context, Container system, File base) throws Exception{
      this.engine = new ReloadEngine(context, system, base);
      this.monitor = new Maintainer(engine, base); 
   }

   /**
    * This will look for and retrieve the requested resource. The 
    * target given must be in the form of a request URI. This will
    * locate the resource, using the system wide mapping scheme
    * and return the <code>Resource</code> implementation that is
    * acquired. Typically the implementation returned by this is
    * a <code>Controller</code>, however it can be any object that
    * implements the <code>Service</code> interface.
    *
    * @param target the URI style path that represents the target 
    *
    * @return returns the <code>Resource</code> object refereed to
    */
   public Resource lookup(String target) {
      return engine.lookup(target);
   }
}

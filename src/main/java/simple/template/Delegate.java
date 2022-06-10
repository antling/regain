/*
 * Delegate.java January 2004
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

import simple.util.parse.PathParser;
import simple.util.parse.URIParser;
import simple.http.serve.Context;
import simple.http.load.Mapper;
import java.util.Set;

/**
 * The <code>Delegate</code> provides a full implementation of an
 * environment. This enables the template engine to provide its
 * mapping scheme to the object transparently and decouples both
 * the controller objects and the templating system from the 
 * mapping scheme used. The <code>resolve</code> method provided
 * by this will use the mapping scheme to locate the required
 * templates, all completly transparent to the controllers.
 *
 * @author Niall Gallagher
 */
final class Delegate implements Environment {

   /**
    * Provides the mapping scheme used by the tempate engine.
    */
   private TemplateMapper mapper;

   /**
    * Represents the templating system that provides documents.
    */
   private Container system;

   /**
    * Constructor for the <code>Delegate</code> object. In order
    * to incorporate the template engines mapping scheme into
    * this environment, this requires that the engines context
    * is provided, the templating system is also required to 
    * produce documents that can be configured for rendering.
    *
    * @param system this represents the templating system used
    * @param context this is the context of the template engine
    */
   public Delegate(Container system, Context context) {
      this.mapper = new TemplateMapper(context);
      this.system = system;
   }

   /**
    * The <code>put</code> method is used to insert a mapping in
    * the environment that pairs the issued name with the issued
    * value. The value can be referenced in future by its name.
    * Properties set with this method will be set within the
    * container, and will thus be available to all documents.
    *
    * @param name this is the name of the value being inserted
    * @param value this is the named value that is inserted
    */
   public synchronized void put(String name, Object value) {
      system.put(name, value);
   }

   /**
    * The <code>get</code> method is used to retrieve the value
    * mapped to the specified name. If a value does not exist
    * matching the given name, then this returns null. All the 
    * property values retrieved by this method are obtained 
    * from the container, and are retrievable from documents.  
    * 
    * @param name this is the name of the value to be retrieved
    *  
    * @return returns the value if it exists or null otherwise
    */
   public synchronized Object get(String name) {
      return system.get(name);
   }

   /**
    * The <code>remove</code> method is used to remove the 
    * mapping from the environment. This method either removes
    * the value or returns silently if the name does not exits.
    * Once removed, all documents with access to the property
    * can no longer reference it, nor can the container.
    *
    * @param name this is the name of the value to be removed
    */
   public synchronized void remove(String name) {
      system.remove(name);
   }

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into thhis database can be retrieved with 
    * this method. This will return a <code>Set</code> that 
    * contains the names of all the mappings added to this.
    *
    * @return this returns all the keys for existing mappings
    */
   public synchronized Set keySet() {
      return system.keySet();
   }

   /**
    * The <code>contains</code> method is used to determine if
    * a mapping exists for the given name. This returns true if
    * the mapping exists or false otherwise. This delegates to 
    * the internal container object that contains all mappings.
    *
    * @param name this is the name of the mapping to determine
    *
    * @return returns true if a mapping exists, false otherwise
    */
   public synchronized boolean contains(String name) {
      return system.contains(name);
   }   

   /**
    * Determines whether the named template exists. This is used
    * to determine if the <code>lookup</code> method will locate
    * a template given the specified path. If the template is
    * accessable this returns true, otherwise false is returned. 
    *
    * @param path this is the path used to locate the template
    *
    * @return true if the template exists, false if it does not
    */
   public synchronized boolean exists(String path) {
      return system.exists(path);
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
   public synchronized Document lookup(String path) throws Exception {      
      return system.lookup(path);
   }

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
   public synchronized Document resolve(String target) throws Exception {
      return lookup(mapper.getPath(target));
   }
}

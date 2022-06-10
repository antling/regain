/*
 * ContentLoader.java December 2002
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
  
package simple.page;

import simple.http.serve.Locator;
import java.net.URLClassLoader;
import java.io.IOException;
import java.io.File;
import java.net.URL;

/**
 * The <code>ContentLoader</code> loads the <code>Class</code> objects
 * for specific <code>Content</code> implementations. The types are 
 * resolved in terms of their content type and path. The MIME type
 * <code>text/x-ssi-html</code> type will always resolve to the 
 * <code>PageContent</code> implementation. Other MIME types are not 
 * used to resolve a <code>Content</code> implementation.
 * <p>
 * Implementations other than the <code>PageContent</code> will be
 * resolved using the given path. This will first check to see if the
 * properties file "Content.properties" exists, if it does then the
 * class name is extracted from this, if not then the path is assumed
 * to be a class name after the first slash, "/".
 *
 * @author Niall Gallagher
 */
class ContentLoader {

   /**
    * This represents the loader with an extended classpath.
    */
   private ClassLoader loader;

   /**
    * This is used to resolver content types from given paths. 
    */
   private Resolver resolver;

   /**
    * This is used to load the classes from a specific path.
    */
   private URL[] path;

   /**
    * Constructor for the <code>ContentLoader</code> object. This will
    * create an instance of the <code>ContentLoader</code> that will
    * load the <code>Class</code> object for specific implementations 
    * of the <code>Content</code> interface. 
    *
    * @param path this forms the base classpath for the content types       
    * @param lookup this is used to search for the properties file
    *
    * @exception IOException this is thrown by <code>File.toURL</code>
    */
   public ContentLoader(File path, Locator lookup) throws IOException {
      this(path.getCanonicalFile().toURL(), lookup);
   }

   /**
    * Constructor for the <code>ContentLoader</code> object. This will
    * create an instance of the <code>ContentLoader</code> that will
    * load the <code>Class</code> object for specific implementations 
    * of the <code>Content</code> interface. 
    *
    * @param path this is the URL classpath for the content objects
    * @param lookup this is used to search for the properties file
    */
   public ContentLoader(URL path, Locator lookup) {
      this(new URL[]{path}, lookup);
   }

   /**
    * Constructor for the <code>ContentLoader</code> object. This will
    * create an instance of the <code>ContentLoader</code> that will
    * load the <code>Class</code> object for specific implementations 
    * of the <code>Content</code> interface.
    *
    * @param path this is the URL classpath for the content objects
    * @param lookup this is used to search for the properties file
    */
   public ContentLoader(URL[] path, Locator lookup) {
     this.loader = ContentLoader.class.getClassLoader();
     this.resolver = new Resolver(lookup);
     this.path = path;
   }

   /**
    * This will attempt to load a suitable <code>Content</code> type for
    * the given path and type. This will explicitly return the type    
    * <code>text/x-ssi-html</code> as a <code>PageContent</code> class.
    * Any other specific types are returned as a class of the given path
    * name. So for instance if the path given was "/demo.DemoContent"
    * then the returned type would be <code>demo.DemoContent</code>.
    * <p>
    * If the content type cannot be found then this will explicitly 
    * throw an exception. This provides an opportunity for some other 
    * mechanism for acquiring the content type to be used.
    *
    * @param path this is the path name for the content object 
    * @param type this is the type that the path was given
    * 
    * @exception Exception thrown if a content type match is not found
    *
    * @return this returns a <code>Class</code> for the content type
    */
   public Class loadClass(String path, String type) throws Exception{ 
      if(type.equals("text/x-ssi-html")){   
         return PageContent.class;
      }
      String name = resolver.resolve(path);
      return getClassLoader().loadClass(name);      
   }

   /**
    * This creates a new <code>ClassLoader</code> that loads from only
    * the specified path. If <code>Content</code> objects are loaded
    * from this instances class loader then they are loaded once. This 
    * is not convenient if the <code>Content</code> byte codes change.
    * <p>
    * This returns a new <code>ClassLoader</code> that will load only
    * from the specified base path. This ensures that every time the
    * <code>loadClass</code> method is invoked it will return the most
    * up to date version of the <code>Content</code> implementation.
    *
    * @return a new <code>ClassLoader</code> for the specified path
    * 
    * @exception Exception if the class loader could not be created
    */
   private ClassLoader getClassLoader() throws Exception {
      return getClassLoader(loader);
   }

   /**
    * This creates a new <code>ClassLoader</code> that loads from only
    * the specified path. This will use the <code>ClassLoader</code> 
    * that was used to load this class as a parent. This ensures that
    * if there is a dynamically loaded object using this, that it 
    * will have no problem loading <code>Content</code> objects.
    *
    * @param parent the parent loader which contains extra classpaths
    *
    * @return a new <code>ClassLoader</code> for the specified path
    * 
    * @exception Exception if the class loader could not be created
    */
   private ClassLoader getClassLoader(ClassLoader parent) throws Exception {
      return new URLClassLoader(path, parent);
   }
}

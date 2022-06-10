/*
 * PageFactory.java December 2002
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

import java.lang.reflect.Constructor;
import simple.http.serve.ContentException;
import simple.http.serve.ContentFactory;
import simple.http.serve.FileLocator;
import simple.http.serve.Locator;
import simple.http.serve.Context;
import simple.http.serve.Content;
import java.io.IOException;
import java.io.File;

/**
 * The <code>PageFactory</code> object is used to produce instances
 * of <code>Content</code> objects. This uses the request URI that is
 * given by the <code>Context</code> object to create the instance. 
 * <p>
 * To create the instance of the <code>Content</code> object the  
 * <code>Context.getMimeType</code> is used. If the type of the URI
 * is <code>x-ssi-html</code> then the <code>PageFactory</code> 
 * will produce a <code>PageContent</code>. Other types are loaded
 * as Java classes using either a Java properties file to match
 * the request URI or the name after the first forward slash.
 * <p>
 * All <code>Content</code> implementations returned from this must
 * provide a two argument constructor. The signature must contain
 * a <code>Context</code> parameter followed by a <code>String</code>
 * parameter. If the implementation does not provide this signature
 * then the <code>getContent</code> method throws an exception.
 * <p>
 * To resolve the names of <code>Content</code> implementations the
 * page factory uses two techniques. In order of resolution this will
 * use a Java properties file named "Content.properties" and the
 * given request URI as a class name. So basically if the properties
 * file does not exist or does not have a match for the issued URI
 * then the class name is extracted from the URI. The class names
 * is extracted as every thing after the first forward slash, "/".
 *
 * @author Niall Gallagher
 */
public class PageFactory implements ContentFactory {

   /**
    * This will load the <code>Content</code> from the path.
    */
   private ContentLoader loader;

   /**
    * Constructor for the <code>PageFactory</code>. The the base path
    * provided is used as a classpath for loaded content objects.
    * The loaded <code>Content</code> implementations are resolved
    * by attempting to match them with a Java properties file.
    * The properties file is loaded from the issued base path, and
    * if that fails the current working directory is used.
    *
    * @param path this provides a base classpath for loaded objects
    *
    * @exception IOException thrown by <code>File.toURL</code>     
    */
   public PageFactory(File path) throws IOException {
      this(path, new FileLocator(path));
   }

   /**
    * Constructor for the <code>PageFactory</code>. The the base path
    * provided is used as a classpath for loaded content objects.
    * The loaded <code>Content</code> implementations are resolved
    * by attempting to match them with a Java properties file.
    * The properties file is loaded using the <code>Locator</code>
    * supplied and the class files are loaded from the given path.
    *
    * @param path this provides a base classpath for loaded objects
    * @param lookup this is used to search for the properties file
    *
    * @exception IOException thrown by <code>File.toURL</code>     
    */
   public PageFactory(File path, Locator lookup) throws IOException {
      this.loader = new ContentLoader(path, lookup);
   }

   /**
    * This attempts to load and instantiate arbitrary content objects.
    * The <code>Content</code> objects returned by this method are
    * loaded from either the class loader used to load this, or a 
    * loader that loads from the path given to this on construction.
    * <p>
    * The <code>Class</code> for a specific content implementation
    * is resolved by checking a Java properties file in the issued
    * base path called "Content.properties". This is referenced to 
    * see if the issued request URI contains a class name. If the
    * Java properties file does not exist then the class is resolved
    * by extracting the first forward slash, "/", and using the
    * remaining text as a class name.
    * <p>
    * If a match is found then this will attempt to instantiate the
    * object using a two argument constructor consisting of a
    * <code>Context</code> object and a <code>String</code>. If the
    * implementation does not provide such a constructor then this
    * will throw a <code>ContentException</code>.    
    *
    * @param target this is a request URI that the content is for
    * @param context the context that is used to create the object
    *
    * @return an instance of the <code>Content</code> interface
    *
    * @exception ContentException if a match cannot be resolved
    */
   public Content getContent(Context context, String target) throws ContentException {     
      try {
         return (Content)getInstance(context, target);
      }catch(Exception e){
         throw new ContentException("No content");
      }
   }

   /**
    * This resolves and creates the <code>Content</code> instance.
    * The instance is created using a two argument constructor that 
    * consists of a <code>Context</code> and <code>String</code>
    * signature, if the constructor does not exist or a class file
    * for the issued request URI does not exist this throws an
    * <code>Exception</code>.
    *
    * @param target the request URI used to resolve the class name   
    * @param context the context that is used to create the object    
    *
    * @return an instance of the <code>Content</code> interface
    *
    * @exception Exception if a content instance cannot be created
    */
   private Object getInstance(Context context, String target) throws Exception {
      Constructor method = getConstructor(context, target);        
      return method.newInstance(new Object[]{context, target});   
   }

   /**
    * This loads the class for the <code>Context</code> and returns
    * the standard constructor for the implementation. This will
    * load the <code>Class</code> for the context based on its MIME
    * type and the URI target given. Once the class of the content
    * is loaded the standard constructor of a <code>Context</code>
    * object followed by a <code>String</code> is returned.
    * 
    * @param target the request URI used to resolve the class name   
    * @param context the context that is used to create the object        
    *
    * @return the standardized object <code>Constructor</code> 
    *
    * @exception Exception if the constructor could not be created
    */
   private Constructor getConstructor(Context context, String target) throws Exception {
      Class[] types = new Class[]{Context.class,String.class};
      String type = context.getMimeType(target);
      Class loaded = loader.loadClass(target, type);       
      return loaded.getDeclaredConstructor(types);
   }
}

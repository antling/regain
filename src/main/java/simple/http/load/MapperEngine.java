/*
 * MapperEngine.java May 2003
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
  
package simple.http.load;

import simple.http.serve.FileContext;
import simple.http.serve.Context;
import simple.util.Resolver;
import java.io.IOException;
import java.net.URL;
import java.io.File;

/**
 * The <code>MapperEngine</code> provides an implementation of the
 * <code>LoaderEngine</code> that does not require administration.
 * It enables <code>Service</code> objects to be loaded and then
 * executed without the need to use manual <code>load</code>
 * invocations. This ability means that byte codes are taken from
 * the class path once they have been referenced.
 * <p>
 * The <code>MapperEngine</code> makes use of a system wide 
 * <code>Mapper</code> to determine what references, that is, what
 * URI references, map to a <code>Service</code> implementation.
 * If a URI maps to a class name then this will check to determine
 * if it has been previously loaded, if so then it will simply
 * return that instance. However if it has not been loaded then
 * it will load the object and return a reference to it.
 * <p>
 * The loaded <code>Service</code> objects have an instance name
 * which is the same as the class name. This means that when it is
 * loaded using the <code>load</code> method that the class name 
 * is issued as both the instance name and the class name. A side
 * effect to this is that only one instance per type is loaded by
 * this <code>LoaderEngine</code>. 
 * 
 * @author Niall Gallagher
 *
 * @see simple.http.load.MapperFactory
 * @see simple.http.load.Mapper
 */
public class MapperEngine extends LoaderEngine {

   /**
    * This is the mapper used by this engine to resolve URIs.
    */
   protected Mapper mapper;

   /**
    * Contains the object that is passed to each service object.
    */
   protected Object data;
   
   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes. This constructor uses a context for
    * the current working directory as a <code>FileContext</code>.
    */
   public MapperEngine() throws IOException{
      this(new FileContext());
   }   
   
   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This will load the class byte codes from the base path of
    * the supplied <code>Context</code> object. If the byte codes
    * cannot be found using the inherided or system class loader
    * the base path is checked, failing that this will return a
    * service object that can supply an HTTP 404 Not Found.
    *
    * @param context the <code>Context</code> for this instance
    */
   public MapperEngine(Context context) throws IOException {
      this(context, context.getBasePath());
   }
   
   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This will load the class byte codes from the base path of
    * the supplied <code>Context</code> object. If the byte codes
    * cannot be found using the inherided or system class loader
    * the base path is checked, failing that this will return a
    * service object that can supply an HTTP 404 Not Found.
    * <p>
    * This enables an arbitrary object to be issused, which is
    * what is used by this <code>LoaderEngine</code> to load the
    * <code>Service</code> objects. Each instance that is loaded
    * is given this object via its <code>prepare</code> method.
    *
    * @param context the <code>Context</code> for this instance
    * @param data this is the data that is given to each service
    */   
   public MapperEngine(Context context, Object data) throws IOException {
      this(context, context.getBasePath(), data);      
   }

   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This will load the class byte codes from the supplied path,
    * if they cannot be found within the inherited or system class
    * path. Providing a path other than the <code>Context</code>
    * base path can be useful in seperating services from content.
    * If a service cannot be found an HTTP 404 Not Found is used.
    *
    * @param context the <code>Context</code> for this instance
    * @param path this is added to the classpath of the engine
    */ 
   public MapperEngine(Context context, String path) throws IOException{
      this(context, path, null);
   }

   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This will load the class byte codes from the supplied path,
    * if they cannot be found within the inherited or system class
    * path. Providing a path other than the <code>Context</code>
    * base path can be useful in seperating services from content.
    * If a service cannot be found an HTTP 404 Not Found is used.
    * <p>
    * This enables an arbitrary object to be issused, which is
    * what is used by this <code>LoaderEngine</code> to load the
    * <code>Service</code> objects. Each instance that is loaded
    * is given this object via its <code>prepare</code> method.
    *
    * @param context the <code>Context</code> for this instance
    * @param path this is added to the classpath of the engine
    * @param data this is the data that is given to each service
    */ 
   public MapperEngine(Context context, String path, Object data) throws IOException{
      this(context, new File(path), data);
   }

   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This will load the class byte codes from the supplied path,
    * if they cannot be found within the inherited or system class
    * path. Providing a path other than the <code>Context</code>
    * base path can be useful in seperating services from content.
    * If a service cannot be found an HTTP 404 Not Found is used.
    *
    * @param context the <code>Context</code> for this instance
    * @param path this is added to the classpath of the engine
    */ 
   public MapperEngine(Context context, File path) throws IOException{
      this(context, path, null);
   }

   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This will load the class byte codes from the supplied path,
    * if they cannot be found within the inherited or system class
    * path. Providing a path other than the <code>Context</code>
    * base path can be useful in seperating services from content.
    * If a service cannot be found an HTTP 404 Not Found is used.
    * <p>
    * This enables an arbitrary object to be issused, which is
    * what is used by this <code>LoaderEngine</code> to load the
    * <code>Service</code> objects. Each instance that is loaded
    * is given this object via its <code>prepare</code> method.
    *
    * @param context the <code>Context</code> for this instance
    * @param path this is added to the classpath of the engine
    * @param data this is the data that is given to each service
    */ 
   public MapperEngine(Context context, File path, Object data) throws IOException{
      this(context, path.getCanonicalFile().toURL(), data);
   }
   
   /**
    * Constructor for the <code>MapperEngine</code>. This creates 
    * an implementation of the <code>LoaderEngine</code> that does
    * not require manual loading of service instances. This allows
    * URI references that contain a service implementation name
    * to load the byte codes.
    * <p>
    * This enables an arbitrary object to be issused, which is
    * what is used by this <code>LoaderEngine</code> to load the
    * <code>Service</code> objects. Each instance that is loaded
    * is given this object via its <code>prepare</code> method.
    *
    * @param context the <code>Context</code> for this instance
    * @param codebase this is added to the classpath of the engine
    * @param data this is the data that is given to each service
    */ 
   private MapperEngine(Context context, URL codebase, Object data) throws IOException{
      super(context, new URL[]{codebase});
      this.mapper = MapperFactory.getInstance(context);
      this.data = data;
   }
   
   /**
    * This extracts the path part from a request URI. This ensures
    * that the <code>MapperEngine.lookup</code> method conforms to
    * the <code>ResourceEngine.lookup</code> requirement that the 
    * method should be able to accept a request URI whether it be
    * absolute or relative. So request URI formats like
    *
    * <pre>    
    * http://some.host/pub;param=value/bin/index.html?name=value
    * http://some.host:8080/index.en_US.html
    * some.host:8080/index.html
    * /usr/bin;param=value/README.txt
    * /usr/bin/compress.tar.gz
    * </pre>
    *
    * must be accepted by the <code>MapperEngine</code>. This is
    * because RFC 2616 states that a HTTP request line can use a
    * URI syntax that contains the scheme and domain. This will
    * check the issued URI to determine whether it contains any
    * reference to a class name, if it does that class will then
    * be loaded (if it has not been previously loaded) and that
    * class name will be returned from this method.
    *
    * @param target this is the request URI to be resolved
    *
    * @return returns the name of the service instance resolved
    */
   protected synchronized String resolve(String target) {   
      String path = context.getRequestPath(target);
      return path != null ? doResolve(target, path) : null;
   }  

   /**
    * This will detetmine whether the URI string issued contains 
    * a reference to a service object. This uses the system wide
    * implementation of the <code>Mapper</code> object to check
    * whether a mapping exists. If a class name can be retrieved
    * from the URI then it is used to load the class. If there is 
    * failure loading the class then it is resolved as a path.
    * <p>
    * Service objects are loaded the first time they are mapped. 
    * The instance name of a loaded object is the implementation
    * class name, for example <code>demo.DemoService</code>. If
    * a class has already been loaded then the name of the class
    * is returned and no resolution is done. 
    *
    * @param target this is the request URI to be resolved
    * @param path this is the request path from the context
    *
    * @return returns the name of the service instance resolved    
    */
   private synchronized String doResolve(String target, String path){
      String className = mapper.getClass(target);

      if(className != null) {
         if(!registry.contains(className)){
            try {
               load(className, className, data);
            }catch(LoadingException e) {
               return resolver.resolve(path);
            }catch(NoClassDefFoundError e){
               return resolver.resolve(path);
            }
         }
         return className;         
      }
      return resolver.resolve(path);
   }
}

/*
 * PrefixMapper.java July 2003
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

import simple.util.parse.URIParser;
import simple.http.serve.Context;
import simple.http.serve.Locator;
import simple.util.cache.Cache;
import simple.util.net.Path;
import simple.util.net.URI;
import java.io.File;

/**
 * The <code>PrefixMapper</code> provides a mapper that is used to
 * perform mapping using prefix paths. This provides a scheme like
 * the Servlet context mapping scheme. This <code>Mapper</code> 
 * allows arbitrary path configurations to be mapped directly to
 * a service class, which can be autoloaded to serve content.
 * <p>
 * This can break the URI path into three components, the prefix,
 * the class name, and the relative path. The prefix is the path
 * part that is used to acquire the service name. A prefix is
 * a URI directory path, such as <code>/path/</code>, which is
 * unique. The relative path is the remaining path, after the
 * prefix is removed. So a path of <code>/path/bin/README</code> 
 * has the relative path of <code>/bin/README</code>.
 * <p>
 * The specification of prefix paths to service class names is
 * done using the properties file <code>Mapper.properties</code>.
 * This must is used by a <code>MapperEngine</code> to resolve
 * services to be loaded, and can also be used by the services to
 * acquire the location of resources using the relative paths.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.load.MapperEngine
 */
public class PrefixMapper implements Mapper {

   /**
    * This determines the prefix for arbitrary URI paths.
    */
   private PrefixResolver resolver;

   /**
    * Constructor for the <code>PrefixMapper</code>. This is used
    * to create a <code>Mapper</code> that can be used to resolve
    * service class names given an arbitrary URI path.  This uses
    * a Java properties file located using the <code>Locator</code> 
    * object supplied with the context. These properties are used
    * to acquire the mappings for URI path to service class names.
    *
    * @param context used to locate the mapping properties file
    */
   public PrefixMapper(Context context) {
      this(context.getLocator());
   }

   /**
    * Constructor for the <code>PrefixMapper</code>. This is used
    * to create a <code>Mapper</code> that can be used to resolve
    * service class names given an arbitrary URI path. This uses
    * a Java properties file located using the <code>Locator</code> 
    * object supplied with the context. These properties are used
    * to acquire the mappings for URI path to service class names.
    *
    * @param lookup used to locate the mapping properties file
    */
   private PrefixMapper(Locator lookup){
      this.resolver = new PrefixResolver(lookup);
   }
   
   /**
    * This method is used to acquire a path relative to the prefix
    * path. This will accept all HTTP URI formats, including an
    * absolute URI. However, the relative path is determined with
    * only the path part of the URI. If there is no mapping found
    * for the issued path the normalized path part is returned.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return the URI path relative to the resolved path prefix
    */
   public String getPath(String target){
      return getPath(new URIParser(target));
   }

   /**
    * This method is used to acquire a path relative to the prefix
    * path. The relative path is determined with only the path 
    * part of the <code>URI</code>. If there is no mapping found 
    * for the issued path the normalized path part is returned.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return the URI path relative to the resolved path prefix    
    */
   private String getPath(URI target){
      return getPath(target.getPath());
   }

   /**
    * This method is used to acquire a path relative to the prefix
    * path. The relative path is determined with the normalized
    * path using <code>getPath</code>. If no mapping is resolved
    * the path is returned relative to the root, <code>/</code>. 
    *
    * @param path the URI path to extract a relative path with
    *
    * @return the URI path relative to the resolved path prefix    
    */
   private String getPath(Path path){
      return resolver.getPath(path.getPath());
   }

   /**
    * This will resolve the service class name given a HTTP URI.
    * This will determine the class name by matching the URI with
    * the loaded prefix paths. If no match is found the this will
    * return null, otherwise the service class name is returned.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return this returns the service class name thats matched
    */
   public String getClass(String target){
      return resolver.getClass(getPrefix(target));
   }

   /**
    * This will determine the prefix path that matches the given
    * HTTP URI. The relative path is determined using the path
    * part of the URI. If no prefix is matched for the URI path
    * then this will return the root path, <code>/</code>.
    *
    * @param target the HTTP URI to resolve a path prefix for
    *
    * @return this returns the prefix path that is resolved  
    */
   public String getPrefix(String target){
      return getPrefix(new URIParser(target));
   }
   
   /**
    * This will determine the prefix path that matches the given
    * HTTP URI. The relative path is determined using the path
    * part of the URI. If no prefix is matched for the URI path
    * then this will return the root path, <code>/</code>.
    *
    * @param target the HTTP URI to resolve a path prefix for
    *
    * @return this returns the prefix path that is resolved  
    */
   private String getPrefix(URI target){
      return getPrefix(target.getPath());
   }
   
   /**
    * This is used to resolve the path prefix using a normalized
    * URI path. If no prefix is matched for the URI path then 
    * this will return the root, <code>/</code>, as a prefix.
    *
    * @param path the URI path to extract a relative path with
    *
    * @return the URI path relative to the resolved path prefix    
    */
   private String getPrefix(Path path){
      return resolver.getPrefix(path.getPath());
   }
}

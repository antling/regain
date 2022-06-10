/*
 * PatternMapper.java January 2004
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
import simple.util.net.Path;
import simple.util.net.URI;
import java.io.File;

/**
 * The <code>PatternMapper</code> provides a mapper that is used 
 * to perform mapping using patterns. This provides a scheme like
 * the Servlet wild card mapping scheme. This <code>Mapper</code> 
 * allows arbitrary path configurations to be mapped directly to
 * a service class, which can be autoloaded to serve content.
 * <p>
 * This uses the wild card characters * and ? to specify patterns
 * for matching URI paths with service class names. For example
 * take the pattern *.html, this will match any URI path that
 * ends with .html, such as /index.html or /example.html. Unlike
 * most Servlet engines this allows a ? mark character which 
 * specifies a single character. For example, index.??? can be
 * used to match /index.htm or /index.jsp.  
 * <p>
 * The specification of wild card patterns to class names is
 * done using the properties file <code>mapper.properties</code>.
 * This must is used by a <code>MapperEngine</code> to resolve
 * services to be loaded by resolving the service class name.
 * <pre>
 *
 *    *index.??_??.html = example.LocaleService
 *    /*secure*.* = example.SecureService
 *
 * </pre>
 * Taking the above example mappings as the Java properties file
 * loaded, matches for URI paths such as /index.en_US.html and
 * /index.fr_CA.html will match the <code>LocaleService</code>.
 * Any number of wild card characters can be specified and the
 * order they appear in the Java properties is signifigant. 
 * The first pattern has the lowest priority and the last has
 * the highest. For example taking the above specification, a
 * URI path such as /secure/index.en_US.html will match to the
 * <code>SecureService</code> as it appears last within the
 * Java properties file <code>mapper.properties</code>.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.load.MapperEngine
 * @see simple.util.Resolver
 */
public class PatternMapper implements Mapper {

   /**
    * This determines the prefix for arbitrary URI paths.
    */
   private PatternResolver resolver;

   /**
    * Constructor for the <code>PatternMapper</code>. This is used
    * to create a <code>Mapper</code> that can be used to resolve
    * service class names given an arbitrary URI path. This uses
    * a Java properties file located using the <code>Locator</code> 
    * object supplied with the context. These properties are used
    * to acquire the mappings for URI path to service class names.
    *
    * @param context used to locate the mapping properties file
    */
   public PatternMapper(Context context) {
      this(context.getLocator());
   }

   /**
    * Constructor for the <code>PatternMapper</code>. This is used
    * to create a <code>Mapper</code> that can be used to resolve
    * service class names given an arbitrary URI path. This uses
    * a Java properties file located using the <code>Locator</code> 
    * object supplied with the context. These properties are used
    * to acquire the mappings for URI path to service class names.
    *
    * @param lookup used to locate the mapping properties file
    */
   private PatternMapper(Locator lookup){
      this.resolver = new PatternResolver(lookup);
   }
   
   /**
    * This method is used to acquire a path given the unmodified
    * HTTP URI. One of the most confusing things about mapping 
    * within the Servlet Specification is the combination of
    * pattern mapping such as <code>*.jsp</code> and context
    * mapping such as <code>/context/</code>. When these are
    * used together determining the <code>getPathInfo</code> is
    * not intuitive. So this will simply return the path as it
    * is, normalized and escaped, this ensures it is intuitive.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return the specified HTTP URI path normalized and escaped
    */
   public String getPath(String target){
      return getPath(new URIParser(target));
   }

   /**
    * This method is used to acquire a path from the given URI.
    * This will simply extract the path from the <code>URI</code>
    * object using the <code>URI.getPath</code> method. This will
    * produce the URI path normalized and escaped.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return the specified HTTP URI path normalized and escaped
    */
   private String getPath(URI target){
      return target.getPath().getPath();
   }

   /**
    * This will resolve the service class name given a HTTP URI.
    * This will determine the class name by matching the URI with
    * the loaded patterns. If no match is found the this will
    * return null, otherwise the service class name is returned.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return this returns the service class name thats matched
    */
   public String getClass(String target){
      return getClass(new URIParser(target));
   }

   /**
    * This will resolve the service class name given a HTTP URI.
    * This will determine the class name by matching the URI with
    * the loaded patterns. If no match is found the this will
    * return null, otherwise the service class name is returned.
    *
    * @param target the HTTP URI to extract a relative path with
    *
    * @return this returns the service class name thats matched
    */
   private String getClass(URI target){
      return getClass(target.getPath());
   }

   /**
    * This will resolve the service class name given a URI path.
    * This will determine the class name by matching the URI with
    * the loaded patterns. If no match is found the this will
    * return null, otherwise the service class name is returned.
    *
    * @param path the HTTP URI to extract a relative path with
    *
    * @return this returns the service class name thats matched
    */
   private String getClass(Path path){
      return resolver.getClass(path.getPath());
   }
}

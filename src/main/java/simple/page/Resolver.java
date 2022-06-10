/*
 * Resolver.java December 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307 USA
 */

package simple.page;

import simple.http.serve.Locator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * The <code>Resolver</code> object provides the functionality used
 * to convert a request URI to a class name. Class names are resolved
 * from the issued request URI using a Java properties file and if
 * this fails to produce an name then the class name is resolved as
 * all text after the initial forward slash, "/".
 * <p>
 * The Java properties file contains name and value pairs which are
 * used to pair a request URI to a class name. So when a request 
 * URI is given to the <code>resolve</code> method the properties
 * file is checked if there is an entry, if there is then the class
 * name is returned. If no entry exists within the properties file
 * then the class name is returns a all text found after the first
 * forward slash. So "/example.Content" becomes "example.Content".
 * <p>
 * The name of the Java properties file that contains the matches
 * is "Content.properties". This file is located using the supplied
 * <code>Locator</code> instance. If there is no properties file 
 * found then this will exclusively use the request URI to search 
 * for a class name.
 *
 * @author Niall Gallagher
 */
final class Resolver {

   /**
    * The <code>Map</code> object that contains the matches.
    */
   private Map map;

   /**
    * Constructor for the <code>Resolver</code>. This creates the
    * instance using a supplied <code>Locator</code> object. The 
    * <code>Locator</code> supplied is used to load the properties
    * file used to resolve request URIs to class names.
    *
    * @param lookup used to search for the Java properties file
    */
   public Resolver(Locator lookup) {
      this.map = new HashMap();
      this.init(lookup);
   }

   /** 
    * This will attempt to acquire a Java properties file that is
    * used to resolve relative URI paths to class names. The Java
    * properties file is looked for using the supplied locator.
    * This will attempt to load the file in a case insensitive 
    * manner so the file can be named either <code>Content</code>
    * or <code>content</code>. 
    *
    * @param lookup used to search for the Java properties file
    */
   private void init(Locator lookup) {
      try {
         load(lookup);
      } catch(IOException e){
         return;
      }
   }

   /** 
    * This <code>load</code> method attempts to load the properties
    * file <code>Content.properties</code> using the given locator.
    * If the properties file exists then is is used to resolve 
    * the name of <code>Content</code> implementations that exist
    * within an SSI HTML file.
    * <p>
    * This will attempt to load the file in a case insensitive 
    * manner so the file can be named either <code>Content</code>
    * or <code>content</code>. If the file cannot be loaded this
    * will throw an <code>IOException</code>.
    *
    * @param lookup used to search for the Java properties file
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void load(Locator lookup) throws IOException {
      try {
         load(lookup, "Content.properties");
      }catch(IOException e) {
         load(lookup, "content.properties");
      }
   }

   /**
    * This will load the named file using the given locator. This
    * is used so that a properties file can be located using an
    * arbitrary search method. If the Java properties file cannot
    * be loaded this will throw an <code>IOException</code>. 
    *
    * @param lookup the locator used to locate the properties
    * @param name this is the name of the properties file loaded
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void load(Locator lookup, String name) throws IOException {
      map.putAll(lookup.getProperties(name));
   }

   /**
    * The <code>resolve</code> method is used to resolve a path to a
    * class name. This uses a Java properties file to acquire pairs  
    * that match a relative URI to a class name. The pairs are then
    * used to resolve an issued path. So if the relative URI loaded
    * was "/page.html" and it was matched to "demo.HTMLParser"
    * then the name returned from resolve for the name "/page.html"
    * would be "demo.HTMLParser".
    * <p>
    * If however the relative URI given is not found within the Java
    * properties file then this will return a class name taken from
    * everything found after the first slash, "/". So if the URI
    * given was "/demo.HTMLParser" then the string returned would be
    * "demo.HTMLParser".
    * 
    * @param path this is the relative URI that is to be parsed
    *
    * @return this is the resolved class name for the given URI
    */
   public String resolve(String path) {
      if(map.containsKey(path)){
         return (String)map.get(path);
      }
      if(path.startsWith("/")){
         path = path.substring(1);
      } 
      return path;
   }
}

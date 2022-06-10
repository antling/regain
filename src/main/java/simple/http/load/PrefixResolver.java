/*
 * PrefixResolver.java July 2003
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

import simple.http.serve.Locator;
import simple.util.PriorityQueue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.io.File;

/**
 * The <code>PrefixResolver</code> is used to determine a URI paths
 * service class name and its mapping prefix. The mappings used are
 * acquired from the <code>mapper.properties</code> file, which is
 * located using an instance of the <code>Locator</code> interface.
 * <p> 
 * This is used to implement a scheme similar to the Java Servlet
 * context path mapping scheme. In this scheme a prefix path is 
 * used to resolve a Servlet, and the remaining path part is then
 * used to acquire a resource relative the the Servlet context.
 *
 * @author Niall Gallagher
 */
final class PrefixResolver {

   /**
    * Contains the paths ordered by length decreasing.
    */ 
   private String[] list;

   /**
    * Contains a list of offsets used for optimization.
    */
   private int[] skip;
   
   /**
    * This map that contains the prefix to name pairs.
    */
   private Map map;

   /**
    * Constructor for the <code>PrefixResolver</code>. This uses
    * a Java properties file located with the <code>Locator</code>
    * object supplied. Once the properties file is located then 
    * class names can be resolved for arbitrary URI paths.
    *
    * @param lookup the locator used to discover the properties
    */
   public PrefixResolver(Locator lookup) {
      this(lookup, 256);
   }
   
   /**
    * Constructor for the <code>PrefixResolver</code>. This uses
    * a Java properties file located with the <code>Locator</code>
    * object supplied. Once the properties file is located then 
    * class names can be resolved for arbitrary URI paths.
    * <p>
    * This includes a parameter that enables a maximum expected
    * path length to be entered. This helps to optimize the
    * resolution of a path prefix. This should typically be at
    * least big enough to include the maximum possible path.
    *
    * @param lookup the object used to perform configuration
    * @param max this is the maximum path length expected
    */
   public PrefixResolver(Locator lookup, int max) {
      this.skip = new int[max];
      this.map = new HashMap();
      this.init(lookup);
   }

   /** 
    * This will attempt to acquire a Java properties file that is
    * used to resolve relative URI paths to class names. The Java
    * properties file is located using the <code>Locator</code>
    * supplied. This will search for the properties using the file
    * names "Mapper.properties" and "mapper.properties".
    *
    * @param lookup the locator used to discover the properties
    */
   private void init(Locator lookup) {
      try {
         load(lookup);
      }catch(IOException e){
      }finally{
         index();   
      }
   }

   /** 
    * This <code>load</code> method attempts to load the properties
    * file <code>Mapper.properties</code> using the given locator.
    * If the properties file exists then it is used to resolve 
    * the class name and prefix path to various relative URI paths.
    * <p>
    * This will attempt to load the file in a case insensitive 
    * manner so the file can be named either <code>Mapper</code>
    * or <code>mapper</code>. If the file cannot be loaded this
    * will throw an <code>IOException</code>.
    *
    * @param lookup this is the locator used to discover the file
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void load(Locator lookup) throws IOException {
      try {
         load(lookup,"Mapper.properties");
      }catch(IOException e) {
         load(lookup,"mapper.properties");
      }
   }
   
   /**
    * This will load the named file from within the given path. This
    * is used so that a properties file can be loaded from a locator
    * using the specified file name. If the Java properties file 
    * cannot be loaded this will throw an <code>IOException</code>. 
    *
    * @param lookup this is the locator used to discover the file
    * @param name this is the name of the properties file loaded
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void load(Locator lookup, String name) throws IOException {
      map.putAll(lookup.getProperties(name));
   }

   /**
    * Used to resolve the class name using a path prefix. This is
    * required to resolve the class name once the prefix of the
    * path is acquired from the <code>getPrefix</code> method. If
    * there is no match for the prefix then null is returned. 
    *
    * @param prefix the path prefix to acquire a class name for
    *
    * @return the class name that matches the prefix path given
    */  
   public String getClass(String prefix){
      return (String)map.get(prefix);
   }

   /**
    * Used to get the prefix path for the given relative URI path,
    * which must be normalized. This will attempt to match the
    * start of the given path to the highest directory path. For
    * example, given the URI path <code>/pub/bin/README</code>,
    * the start of the path will be compared for a prefix. So it
    * should match <code>/pub/bin/</code>, <code>/bin/</code>,
    * and finally <code>/</code> in that order. 
    *
    * @param normal the normalized URI path to get a prefix for
    *
    * @return the highest matched directory for the given path
    */
   public String getPrefix(String normal) {
      int size = normal.length();
      int off = 0;
      
      if(size < skip.length){
         off = skip[size];
      }
      for(int i = off; i < list.length; i++){
         if(normal.startsWith(list[i])){
            return list[i];
         }
      }
      return "/";
   }

   /**
    * Used to acquire the path relative to the prefix. This will
    * return the path as it is relative to the prefix resolved 
    * for the given normalized path. This will remove the start
    * of the given normalized path that matches the directory of
    * resolved prefix. 
    *
    * @param normal the normalized URI path to get a path for
    *
    * @return the full path once its prefix has been removed 
    */
   public String getPath(String normal){
      String prefix = getPrefix(normal); 
      int size = prefix.length() - 1;
      
      return normal.substring(size);
   }
   

   /**
    * Used to prepare the prefix paths so that they can be matched
    * with relative URI paths quickly. The Java properties file
    * used to specify the prefix paths with the class names will
    * be loaded unordered into a <code>Properties</code> object.
    * This ensures the properties keys are sorted for searching.
    */
   private void index() {
      index(map.keySet());
   }

   /**
    * Used to prepare the prefix paths so that they can be matched
    * with relative URI paths quickly. The Java properties file
    * used to specify the prefix paths with the class names will
    * be loaded unordered into a <code>Properties</code> object.
    * This ensures the properties keys are sorted for searching.
    *
    * @param set this contains the properties keys to be sorted
    */
   private void index(Set set){
      list = new String[set.size()];
      set.toArray(list);
      prepare(list);
      sort(list);
      optimize(skip);
   }

   /**
    * This is used to prepare the prefix paths so that they all
    * end with the <code>/</code> character. If the prefix paths
    * within the Java properties file do not correspond to a
    * directory path this will simply append a <code>/</code>.
    * For example if the properties file was as follows.
    *
    * <pre>
    *    /path=package.PathService
    *    /path/doc/=package.DocService
    * </pre>
    *
    * The prefix <code>/path</code>, which does not end in the
    * <code>/</code> character, becomes <code>/path/</code>. 
    * This ensures that relative paths will be predictable.
    *
    * @param list this is the list of prefix paths to be fixed  
    */
   private void prepare(String[] list){
      for(int i = 0; i < list.length; i++){
         Object data = map.remove(list[i]);
         
         if(!list[i].endsWith("/")){
            list[i] += "/";
         }
         map.put(list[i],data);
      }
   }

   /**
    * This method is used to sort the list of strings by length. 
    * Sorting the strings by length is done so that the selection 
    * of a suitable path prefix will match the highest matching
    * directory. For example if <code>/path/bin/index.html</code>
    * was the path and the prefix paths loaded were as follows.
    *
    * <pre>
    *    /path/=package.PathService
    *    /path/bin/=package.BinSerivce
    *    /path/doc/=package.DocService
    * </pre>
    *
    * Then the path prefix match should be the highest directory,
    * which would be <code>/path/bin/</code>. In order to make
    * the match rapidly then the paths should be searched in
    * order of length, so that when a prefix matches it is used.
    *
    * @param list contains the strings that are to be sorted
    */
   private void sort(String[] list){
      PriorityQueue queue = new PriorityQueue();

      for(int i = 0; i < list.length; i++){
         queue.add(list[i], list[i].length());
      }
      for(int i = 0; i < list.length; i++){
         list[i] = (String)queue.remove();
      }
   }

   /**
    * This method is used to optimize the searching for prefixes
    * by setting a list of offsets within a skip list. The skip
    * list contains an offset within each index. Each index in
    * the skip list corrosponds to a path length and the offset
    * within that index corrosponds to an offset into the list
    * of prefix paths. Setting up a skip list in this manner is
    * useful in determining where to start resolutions.
    * <p>
    * Taking the path <code>/pub/index.html</code> for example.
    * This path cannot possibly have a prefix path that has a
    * length larger than it, like <code>/pub/bin/example/</code>
    * as it is longer than it. So the skip list will basically
    * allow a path to determine how many prefixes it can skip
    * before the prefix size is less than or equal to its size.
    *
    * @param skip this is the list of offsets to be prepared
    */
   private void optimize(int[] skip) {
      int size = skip.length - 1; 
      int off = 0;
    
      while(off < list.length){
         if(list[off].length() < size){
            skip[size--] = off;
         }else {  
            while(off < list.length){
               if(list[off].length() < size) {               
                  break;
               }
               skip[size] = off++;
            }
            size--;
         }
      }
      while(size > 0){
         skip[size--] = off-1;
      }
   }
}

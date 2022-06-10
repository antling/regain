/*
 * DefaultMapper.java May 2003
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

import simple.http.serve.Context;

/**
 * The <code>DefaultMapper</code> is used to provide URI mapping
 * for Java class names using a specific URI path structure. This
 * uses only the path part of the URI to identify the class name.
 * The structure used is formed using an initial, optional, class
 * name followed by a path part. Examples are shown below.
 * <code><pre>
 *
 *    http://hostname/demo/DemoService.class/index.html
 *    http://hostname/demo/DemoService.class
 *    http://hostname/index.html
 *
 * </pre></code> 
 * All of the above path structures are valid. The first shows a
 * reference to the class <code>demo.DemoService</code> with the
 * path part <code>/index.html</code>. The class is identified 
 * by the ".class" extension, which terminates the name of the 
 * class name. If there is no ".class" this means the URI has no
 * reference to a class.
 *
 * @author Niall Gallagher
 */
final class DefaultMapper implements Mapper {

   /**
    * Constructor for the <code>DefaultMapper</code>. This is
    * implemented to comply with the requirement of the single
    * argument constructor for a <code>Mapper</code> object.
    */
   public DefaultMapper(Context context){
   }

   /** 
    * This extracts the class name from the issued URI string.
    * This performs the parsing in a thread safe manner so that
    * multiple threads can access the method simultaneously.  
    * <p>
    * For performance this does a quick check to see if the 
    * URI string contains ".class", this ensures that overhead
    * parsing the URI and normalizing the path is not done.
    *
    * @param target this is the URI that contains the mapping
    *
    * @return this will return the class name, null otherwise 
    */
   public String getClass(String target) {
      if(target.indexOf(".class") > 0) { 
         return getName(target);
      }
      return null;
   }   

   /** 
    * This extracts the class name from the issued URI string.
    * This performs the parsing in a thread safe manner so that
    * multiple threads can access the method simultaneously.  
    *
    * @param target this is the URI that contains the mapping
    *
    * @return this will return the class name, null otherwise     
    */
   private String getName(String target) {
      return new ClassParser(target).getName();
   }   

   /**
    * This extracts the path from the issued URI string. This
    * will return the whole URI path, normalized, if there is
    * no reference to a class within the URI path. However,
    * if there is a reference the path information following
    * the class reference is returned.
    *
    * @param target this is the URI that contains the path
    *
    * @return returns a normalized path resolved from the URI
    */
   public String getPath(String target) {
      return new ClassParser(target).getPath();
   }
}

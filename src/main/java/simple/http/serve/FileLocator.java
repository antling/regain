/*
 * FileLocator.java February 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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

package simple.http.serve;

import simple.util.FileProperties;
import java.util.Properties;
import java.io.IOException;
import java.io.File;

/**
 * The <code>FileLocator</code> provides an implementation of the
 * <code>Locator</code> interface for locating files from a list of
 * directories. This will use the list of directories to search for
 * named files. The list of files is searched by increasing index so
 * the first occurrence of a named file found in the search path will
 * be used. This accepts the name of the file to be used, so for 
 * example if the file required was <code>example.xml</code> then
 * the name alone should be supplied. If however path information 
 * is supplied it can be in a URI or platform specific format.
 *  
 * @author Niall Gallagher
 */
public class FileLocator implements Locator {

   /**
    * This is the list of directories within the search path.
    */
   private File[] list;

   /**
    * Constructor for the <code>FileLocator</code> object. This
    * no argument constructor will contain an empty search path
    * and as a result will only search for resources within the
    * current working directory.
    */
   public FileLocator() {
      this(new File[]{});
   }

   /**
    * Constructor for the <code>FileLocator</code> object. This
    * constructor requires a single path for searching. The path
    * is searched for the named resources and if found it is
    * used, however if the resource is not found in the given
    * path the current working directory is searched.
    *
    * @param path this is the path to search for the resources
    */
   public FileLocator(File path){
      this(new File[]{path});
   }

   /**
    * Constructor for the <code>FileLocator</code> object. This
    * constructor requires a list of directories with which to
    * search for resources. The resources are searched for in
    * increasing index, so index zero is the first directory 
    * searched followed by index one and so on. This uses the
    * current working directory if the search fails.
    *
    * @param list this is the list of files to search within
    */
   public FileLocator(File[] list){
      this.list = list;
   }

   /**
    * This is used to discover the location of a resource using
    * the name of the resource. This will return the location of
    * the resource in a system specific path format. This mirrors
    * the <code>FileContext.getRealPath</code> method, however it
    * should be supplied with only the name of the resource with
    * no path information, which keeps any code interacting with 
    * this portable across systems using different path formats.
    *
    * @param name the name of the resource that is to be located
    *
    * @return the location of the resource in the system format
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   public String getLocation(String name) throws LocateException {
      String real = getRealPath(name);
         
      for(int i = 0; i < list.length; i++) {         
         try {
            return getLocation(list[i], real);
         } catch(LocateException e) {
            continue;
         }
      }
      return getLocation(new File("."), real);
   }

   /**
    * This is used to discover the location of a resource using
    * the name of the resource. This will return the location of
    * the resource in a system specific path format. This mirrors
    * the <code>FileContext.getRealPath</code> method, however it
    * should be supplied with only the name of the resource with
    * no path information, which keeps any code interacting with 
    * this portable across systems using different path formats.
    *
    * @param base this is the directory to use for the search
    * @param name the name of the resource that is to be located
    *
    * @return the location of the resource in the system format
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   private String getLocation(File base, String name) throws LocateException {
      File file = new File(base, name);

      if(file.exists()){
         return file.getAbsolutePath();     
      }
      throw new LocateException("File not found");
   }

   /**
    * This is used to produce a <code>File</code> object pointing
    * to the location of the named resource. This method mirrors
    * the <code>FileContext.getFile</code> method, however it 
    * should be supplied with only the name of the resource with 
    * no path information, which keeps any code interacting with 
    * this portable across systems using different path formats.
    *
    * @param name the name of the resource that is to be located
    *
    * @return a <code>File</code> referencing the named resource
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   public File getFile(String name) throws LocateException {
      return new File(getLocation(name));
   }

   /**
    * This is used to produce a <code>Properties</code> object 
    * that contains the contents of the named Java properties file. 
    * This mirrors the <code>FileContext.getProperties</code> 
    * method, however it should be supplied with only the name of 
    * the resource with no path information, which keeps any code 
    * interacting with this portable across different platforms.
    *
    * @param name the name of the resource that is to be located
    *
    * @return a <code>Properties</code> object for the resource
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   public Properties getProperties(String name) throws LocateException {
      try {
         return new FileProperties(getFile(name));
      } catch(IOException e) {
         throw new LocateException("File not found");
      }
   }

   /**
    * This method is used to convert a URI style path into a
    * platform specific path. This is used to accommodate paths
    * supplied instead of resource names. The URI path format
    * is supported as it is platform independent and in general
    * is the typical path format used in web based systems.
    *
    * @param path this is a URI path this is to be converted 
    */
   private String getRealPath(String path) {
      return path.replace('/', File.separatorChar);
   }
}

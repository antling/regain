/*
 * FileProperties.java July 2003
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
 
package simple.util;

import java.io.FileInputStream;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

/** 
 * The <code>FileProperties</code> object is used as a convienience
 * class that is used to create a <code>Properties</code> object
 * from a file path. This will automatically load the Java property
 * file once instantiated. This is used so that the creation and 
 * use of a <code>java.util.Properties</code> file is much cleaner.
 *
 * @author Niall Gallagher
 */ 
public class FileProperties extends Properties {

   /**
    * Constructor for the <code>FileProperties</code> object. The
    * constructor will use the specified file as the contents of
    * its properties. The file name should be absolute, and in an
    * OS specific format. For example "C:\data.properties" on a
    * Windows filesystem, and "/pub/bin/data.properties" on UNIX.
    *
    * @param file this is the file that contains the properties
    *
    * @exception IOException if the properties can not be loaded
    */
   public FileProperties(String file) throws IOException {
      this(new FileInputStream(file));
   }
   
   /**
    * Constructor for the <code>FileProperties</code> object. The
    * constructor will use the specified base path to load the 
    * named properties file from. The file name given should be
    * relative to the issued base path. This is a convinience
    * method that enables the Java properties file to be loaded
    * without the need to specify an OS specific file path.
    *
    * @param base this is the directory to loade the file from
    * @param file this is the file that contains the properties
    *
    * @exception IOException if the properties can not be loaded
    */
   public FileProperties(File base, String file) throws IOException {
      this(new File(base, file));
   }
   
   /**
    * Constructor for the <code>FileProperties</code> object. The
    * constructor will use the specified file as the contents of
    * its properties. The file name should be absolute, and in an
    * OS specific format. For example "C:\data.properties" on a
    * Windows filesystem, and "/pub/bin/data.properties" on UNIX.
    *
    * @param file this is the file that contains the properties
    *
    * @exception IOException if the properties can not be loaded
    */
   public FileProperties(File file) throws IOException {
      this(new FileInputStream(file));
   }

   /**
    * Constructor for the <code>FileProperties</code> object. The
    * constructor will use the given <code>InputStream</code> to
    * load Java properties for this instance. This is used by the
    * other constructor methods to keep the object simple.
    *
    * @param file this is the file that contains the properties
    *
    * @exception IOException if the properties can not be loaded
    */
   private FileProperties(InputStream file) throws IOException {
      this.load(file);
   }
}

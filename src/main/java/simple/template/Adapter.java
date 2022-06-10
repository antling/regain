/*
 * Adapter.java December 2003
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

package simple.template;

import simple.http.load.LoaderManager;
import simple.http.load.Layout;
import simple.http.load.Loader;
import java.io.File;

/**
 * The <code>Adapter</code> provides an adaptation for callbacks 
 * issued through the <code>Loader</code> interface. The adaptation
 * provided simply maps the fully qualified class names of services
 * loaded, to a file system specific path. This assumes all classes
 * are loaded from the specified classpath. For example, if the 
 * classpath specified was <code>c:\classes\</code> and the class 
 * names were <code>demo.One</code> and <code>demo.Two</code>, then
 * the class file <code>c:\classes\demo\One.class</code> would be
 * the location for the type <code>demo.One</code>. 
 * <p> 
 * This does not verify whether the service types are actually
 * located within the specified classpath, it simply assumes that
 * the service is loaded from the classpath and maps the class
 * names accordingly. The <code>Monitor</code> can then determine
 * whether the class file exists from the <code>File</code>.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.load.Loader
 */
final class Adapter implements Loader {

   /**
    * This is the ultimate recipient of the update callback.
    */
   private Monitor monitor;

   /**
    * This represents the file classpath used by this object.
    */
   private String base;

   /**
    * Constructor for the <code>Adapter</code> object. The path
    * issued to this constructor must be a file system specific
    * path as obtained from <code>File.getAbsolutePath</code>  
    * or <code>File.getCanonicalPath</code>, this ensures that
    * the file system location of classes can be mapped
    *
    * @param monitor this receives the adapted update callback
    * @param base this is the file system specific classpath 
    */
   public Adapter(Monitor monitor, String base) {
      this.monitor = monitor;
      this.base = base;
   }

   /**
    * The <code>update</code> method is invoked when there is a
    * change in the layout. The change can be a link, load, 
    * unlink, or unload action. Once this is invoked, the names
    * of the classes are mapped into a file system specific
    * path and the update propagates to a <code>Monitor</code>.
    *
    * @param manager this is the manager that has been updated
    * @param layout this contains the current configuration of
    * the <code>LoaderManager</code>
    */
   public void update(LoaderManager manager, Layout layout) {
      update(layout.getNames(), layout.getClassNames());
   }      
   
   /**
    * This method performs the mapping from the fully qualified
    * package name of the classes to a file system specific path.
    * If the class was loaded from the specified classpath then
    * the mapping will provide the location of the byte codes
    * used to instantiate the loaded service. 
    *
    * @param name the list of names for loaded service instances
    * @param type the list of types that correspond to the names 
    */
   private void update(String name[], String[] type) {
      File[] file = new File[name.length];
      char split = File.separatorChar;

      for(int i = 0; i < name.length; i++){
         String text = type[i].replace('.', split);
         file[i] = new File(base, text + ".class");
      }
      monitor.update(name, file);
   }
}

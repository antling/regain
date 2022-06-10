/*
 * DisplayFactory.java July 2003
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
 
package simple.page;

import java.lang.reflect.Constructor;
import simple.http.serve.Context;

/**
 * The <code>DisplayFactory</code> retrieves a <code>Display</code>
 * implementation for the system. This is used so that an arbitrary
 * display can be imposed on the system using the command line. If
 * an SSI page is referenced then an instance is retrieved and the
 * display of that page is handled by the retrieved implementation.
 * This has a number of advantages. For one it enables the display
 * to be configured without any changes to code, so exceptions
 * and referenced <code>Content</code> objects can be managed.
 * <p>
 * In order to define a system wide implementation a property is
 * needed to define the object. This uses the <code>System</code>
 * properties to define the class name for the default instance.
 * The property is the <code>simple.page.display</code> property 
 * that can be set using an argument to the VM.
 * <pre>
 * java -Dsimple.page.display=demo.example.DemoDisplay
 * </pre>
 * This will set the <code>System</code> property to the class 
 * name <code>demo.example.DemoDisplay</code>. When the factory
 * method <code>getInstance</code> is invoked it will return an
 * implementation of this object or if the implementation cannot
 * be loaded by this classes class loader a default implementation,
 * <code>DefaultDisplay</code>, is returned instead. 
 * 
 * @author Niall Gallagher
 */
final class DisplayFactory {

   /**
    * This is used to produce the system wide <code>Display</code>
    * implementation so that all SSI pages can remain consistent.
    * This will use the <code>simple.page.display</code>
    * system property to define the class of the implementation 
    * that will be used for the system wide <code>Display</code>.
    * The property should contain the fully qualified class name
    * of the object and should be loadable by this classes class 
    * loader. If the specified class cannot be loaded then the
    * <code>DefaultDisplay</code> implementation is used.
    *
    * @param context context that the SSI page is served from
    *
    * @return the systems <code>Display</code> implementation
    */
   public static Display getInstance(Context context) {
      String property = "simple.page.display";
      String className = System.getProperty(property);

      if(className == null){ 
         return new DefaultDisplay(context);
      }
      try {
         return getInstance(context, className);
      } catch(Exception e) {
         return new DefaultDisplay(context);
      }
   }

   /**
    * This is used to create a <code>Display</code> instance with
    * the issued class name. The class name issued represents the
    * fully qualified package name of the implementation to be
    * used. The implementation must contain a single argument
    * constructor that takes a <code>Context</code> object of it
    * is to be instantiated by this method. If there is any 
    * problem instantiating the object an exception is thrown.
    * 
    * @param context this is the context used by the SSI page
    * @param className this is the name of the implementation
    *
    * @return an instance of the <code>Display</code> object
    */
   private static Display getInstance(Context context, String className) throws Exception{
      Constructor method = getConstructor(className);
      return (Display)method.newInstance(new Object[]{context});
   }

   /**
    * Here a <code>ClassLoader</code> is selected to load the class.
    * This will load the class specified using the loader user to 
    * load this class. If there are no problems in loading the class
    * a <code>Constructor</code> is created from the loaded class.
    * <p>
    * The constructor for any <code>Display</code> implementation
    * must contain a one argument constructor that takes a context
    * object as the argument. If such a constructor does not exist
    * then this will throw an <code>Exception</code>.
    * 
    * @param className the name of the display implementation 
    *
    * @return this returns a constructor for the specified class
    */
   private static Constructor getConstructor(String className) throws Exception {
      return getConstructor(Class.forName(className,
         false, DisplayFactory.class.getClassLoader()));
   }
   
   /**
    * Creates the <code>Constructor</code> for the implementation
    * so that an instance can be created. This will use the class
    * which has been previously loaded to acquire the constructor.
    * The constructor object acquired is for a single argument
    * constructor that takes a <code>Context</code> object.
    *
    * @param type this is the implementation class to be used
    *
    * @return this returns a constructor for the specified class
    */
   private static Constructor getConstructor(Class type) throws Exception {
      Class[] types = new Class[]{Context.class};
      return type.getDeclaredConstructor(types);
   }
}

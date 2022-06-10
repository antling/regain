/*
 * MapperFactory.java July 2003
 *
 * Copyright (C) July, Niall Gallagher <niallg@users.sf.net>
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

import java.lang.reflect.Constructor;
import simple.http.serve.Context;

/**
 * The <code>MapperFactory</code> retrieves the <code>Mapper</code>
 * implementation for the system. This is used so that a mapping
 * scheme can be imposed on the system using the command line. This
 * has a number of advantages. For one it enables a mapping to be
 * used without any changes to code, the <code>Mapper</code> object
 * only needs to be implemented and specified at the command line.
 * <p>
 * In order to define a system wide implementation a property is
 * needed to define the object. This uses the <code>System</code>
 * properties to define the class name for the default instance.
 * The property is the <code>simple.http.load.mapper</code>
 * property that can be set using an argument to the VM.
 * <pre>
 * java -Dsimple.http.load.mapper=demo.example.ExampleMapper
 * </pre>
 * This will set the <code>System</code> property to the class 
 * name <code>demo.example.ExampleMapper</code>. When the factory
 * method <code>getInstance</code> is invoked it will return an
 * implementation of this object or if the implementation cannot
 * be loaded by this classes class loader a default implementation
 * <code>DefaultMapper</code> is returned instead. 
 * 
 * @author Niall Gallagher
 */
public final class MapperFactory {

   /**
    * This is used to produce the system wide <code>Mapper</code>
    * implementation so that the <code>MapperEngine</code> objects
    * can remain consistant. This will use a system property 
    * <code>simple.http.load.mapper</code> to define the
    * class name of the implementation that will be used for the
    * system wide <code>Mapper</code>. The property should contain
    * the fully qualified class name of the object and should be
    * loadable by this classes class loader. If the specified class
    * cannot be loaded the <code>DefaultMapper</code> is used.
    *
    * @param context this is context used by the implementation
    *
    * @return the systems <code>Mapper</code> implementation 
    */
   public static Mapper getInstance(Context context) {
      String property = "simple.http.load.mapper";
      String className = System.getProperty(property);

      if(className == null){ 
         return new DefaultMapper(context);
      }
      try {    
         return getInstance(context, className);         
      }catch(Exception e){
         return new DefaultMapper(context);
      }
   }

   /**
    * This is used to create a <code>Mapper</code> instance with
    * the issued class name. The class name issued represents the
    * fully qualified package name of the implementation to be
    * used. The implementation must contain a single argument
    * constructor that takes a <code>Context</code> object of it
    * is to be instantiated by this method. If there is any 
    * problem instantiating the object an exception is thrown.
    * 
    * @param context the context used to create the mapper
    * @param className this is the name of the implementation
    *
    * @return an instance of the <code>Mapper</code> object
    */
   private static Mapper getInstance(Context context, String className) throws Exception{
      Constructor method = getConstructor(className);
      return (Mapper)method.newInstance(new Object[]{context});
   }

   /**
    * Here a <code>ClassLoader</code> is selected to load the class.
    * This will load the class specified using this classes class
    * loader. If there are no problems in loading the class then a
    * <code>Constructor</code> is created from the loaded class.
    * <p>
    * The constructor for any <code>Mapper</code> implementation
    * must contain a one argument constructor that takes a context
    * object as the argument. If such a constructor does not exist
    * then this will throw an <code>Exception</code>.
    * 
    * @param className the name of the mapper implementation 
    *
    * @return this returns a constructor for the specified class
    */
   private static Constructor getConstructor(String className) throws Exception {
      return getConstructor(Class.forName(className,
         false, MapperFactory.class.getClassLoader()));
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

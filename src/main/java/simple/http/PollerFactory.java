/*
 * PollerFactory.java February 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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
 
package simple.http;

import java.lang.reflect.Constructor;
import java.io.IOException;

/**
 * The <code>PollerFactory</code> retrieves the <code>Poller</code>
 * implementation for the system. This is used so that a polling
 * strategy can be imposed on the system using the command line. 
 * This has a number of advantages. For one it enables polling to
 * be configured without affecting the <code>PipelineHandler</code>.
 * <p>
 * In order to define a system wide implementation a property is
 * needed to define the object. This uses the <code>System</code>
 * properties to define the class name for the default instance.
 * The property is the <code>simple.http.poller</code> property 
 * that can be set using an argument to the VM.
 * <pre>
 * java -Dsimple.http.poller=demo.example.ExamplePoller
 * </pre>
 * This will set the <code>System</code> property to the class 
 * name <code>demo.example.ExamplePoller</code>. When the factory
 * method <code>getInstance</code> is invoked it will return an
 * implementation of this object or if the implementation cannot
 * be loaded by this classes class loader a default implementation
 * <code>DefaultPoller</code> is returned instead. 
 * 
 * @author Niall Gallagher
 */
final class PollerFactory {

   /**   
    * This is a static factory object used to create pollers. To
    * ensure that instances can be created rapidly the factory    
    * is created once to aggregate the Java reflection overhead.
    */
   private static Constructor factory;

   static {
      try {
         factory = getConstructor();
      }catch(Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * This will produce an implementation of the <code>Poller</code>
    * object. This is a reasonably fast factory method for producing
    * arbitrary <code>Poller</code> instances. This simply uses an
    * existing <code>Constructor</code> to reflectively create the
    * instance. However if the instance cannot be created due to a
    * constructor problem then this uses a default implementation.
    *
    * @param pipe this is the <code>Pipeline</code> that is wrapped
    *
    * @return returns a <code>Poller</code> for the given pipeline
    *
    * @throws IOException if there is a problem creating the poller
    */
   public static Poller getInstance(Pipeline pipe) throws IOException {
      if(factory == null) {
         return new DefaultPoller(pipe);
      }
      try {
         return getInstance(new Object[]{pipe});
      }catch(Exception e) {
         return new DefaultPoller(pipe);
      }
   }
   
   /**
    * This uses the factory <code>Constructor</code> to create the
    * <code>Poller</code> instance to be used. If for some reason
    * the poller cannot be created then this will throw an exception.
    * In order to be created the implementation must provide a 
    * single argument constructor that takes a <code>Pipeline</code>.
    *
    * @param list this is the set of constructor arguments used
    *
    * @return returns a <code>Poller</code> for the given arguments
    *
    * @throws IOException if there is a problem creating the poller
    */
   private static Poller getInstance(Object[] list) throws Exception {
      return (Poller)factory.newInstance(list);
   }

   /**
    * This is used to produce a <code>Constructor</code> factory to
    * create <code>Poller</code> instances. The goal is reduce the
    * overhead of Java Reflection by creating a single static 
    * factory that can be used to create instances. The object type
    * is determined by the <code>simple.http.poller</code> system
    * property. The property should contain the fully qualified 
    * class name of the object and should be loadable by the class 
    * loader of this class. If the specified class cannot be loaded
    * the <code>DefaultPoller</code> is used instead.
    *
    * @return a factory used to create <code>Poller</code> objects
    */
   private static Constructor getConstructor() throws Exception {
      String property = "simple.http.poller";
      String className = System.getProperty(property);

      if(className != null){ 
         return getConstructor(className);         
      }
      return null;      
   }

   /**
    * Here a <code>ClassLoader</code> is selected to load the class.
    * This will load the class specified using this classes class
    * loader. If there are no problems in loading the class then a
    * <code>Constructor</code> is created from the loaded class.
    * <p>
    * The constructor for any <code>Poller</code> implementation
    * must contain a one argument constructor that takes a pipeline
    * object as the argument. If such a constructor does not exist
    * then this will throw an <code>Exception</code>.
    * 
    * @param className the name of the poller implementation 
    *
    * @return this returns a constructor for the specified class
    */
   private static Constructor getConstructor(String className) throws Exception {
      return getConstructor(Class.forName(className,
         false, PollerFactory.class.getClassLoader()));
   }

   /**
    * Creates the <code>Constructor</code> for the implementation
    * so that an instance can be created. This will use the class
    * which has been previously loaded to acquire the constructor.
    * The constructor object acquired is for a single argument
    * constructor that takes a <code>Pipeline</code> object.
    *
    * @param type this is the implementation class to be used
    *
    * @return this returns a constructor for the specified class
    */
   private static Constructor getConstructor(Class type) throws Exception {
      Class[] types = new Class[]{Pipeline.class};
      return type.getDeclaredConstructor(types);
   }
}

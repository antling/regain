/*
 * ReflectionService.java July 2003
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
import simple.util.Introspector;

/**
 * The <code>ReflectionService</code> enables initialization to 
 * be achieved using arbitrary <code>prepare</code> methods. 
 * This will attempt to discover the <code>prepare</code> method 
 * implemented by the subclass before invoking it. The discovery 
 * is done using Java reflection to match the arguments to the 
 * <code>prepare</code> method. For example take
 * the <code>prepare</code> signatures illustrated below.
 * <pre>
 *
 *    public void prepare(Map) throws Exception
 *    public void prepare(List, String) throws Exception
 *
 * </pre>
 * The above methods will be discovered by checking the class of
 * the <code>Object</code> parameter that is issued to the
 * <code>prepare(LoaderEngine,&nbsp;Object)</code> method. The 
 * class of the object is determined using <code>getClass</code>.
 * If the parameter to the subclasses <code>prepare</code> is the
 * same class, a superinterface, or a superclass of that class 
 * then it will be invoked to initialize that instance.
 * <p>
 * This treats only a type of <code>Object[]</code> as a special
 * case. If the object parameter is an array of objects then
 * the discovery of the initialization method is performed by
 * matching the method with the class type of each of the
 * individual <code>Object.getClass</code> classes extracted
 * from the array. So if both prepare methods illustrated above
 * were implemented by a single subclass, then to invoke the
 * <code>prepare(List, String)</code> method, use the follwing.
 * <pre>
 *
 *    Object data = new Object[]{new Vector(), "text"};
 *
 * </pre>
 * This allows the <code>Service</code> implementation to have
 * several <code>prepare</code> methods and, depending on the 
 * type of the issued parameter, have the appropriate method to
 * be invoked when it is loaded by the <code>LoaderEngine</code>.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.Introspector
 */
public abstract class ReflectionService extends BasicService {

   /**
    * This is the engine used to load this service object.
    */ 
   protected LoaderEngine engine;

   /**
    * Constructor to the <code>ReflectionService</code>. This
    * will create a service that can have an arbitrary preparation
    * method. This will be used by a <code>LoaderEngine</code> to
    * create an instance using the context of that loader engine.
    *
    * @param context this is the context used by this instance.
    */
   public ReflectionService(Context context){
      super(context);
   }

   /**
    * This method is used as a driver to the <code>prepare</code>
    * method implemented by the subclass. The selection of the
    * <code>prepare</code> method is determined by the class of
    * the issued object, using <code>Object.getClass</code>. This
    * discovers the method using the class and its subclasses.
    *
    * @param engine this is the engine that this object loaded by
    * @param data this object's class determines the preparation
    *
    * @throws LoadingException thrown if preparation fails
    */
   public void prepare(LoaderEngine engine, Object data) throws LoadingException{
      try {
         if(data instanceof Object[]){
            prepare(getClass(), (Object[])data);
         } else{
            prepare(getClass(), data);
         }
      }catch(Exception e){
         throw new LoadingException("Prepare error");
      }finally {
         this.engine = engine;
      }   
   }

   /**
    * This discovers the <code>prepare</code> method that will be
    * used to initialize this instance. This will attempt to find
    * a match for <code>prepare</code> using the class types of
    * the argument to this method.
    * <p>
    * Any exceptions that are thrown from the implemented method
    * will propagate from this method. If no method is matched
    * then this returns quietly assuming initialization is not
    * required for the subclass implementation.
    *
    * @param type this is the type of the subclass implementation
    * @param data contains the argument to use in an invocation
    *
    * @throws Exception this is thrown if the preparation fails
    */
   private void prepare(Class type, Object data) throws Exception{
      new Introspector(type).invoke("prepare", this, data);
   }

   /**
    * This discovers the <code>prepare</code> method that will be
    * used to initialize this instance. This will attempt to find
    * a match for <code>prepare</code> using the class types of
    * the arguments to this method.
    * <p>
    * Any exceptions that are thrown from the implemented method
    * will propagate from this method. If no method is matched
    * then this returns quietly assuming initialization is not
    * required for the subclass implementation.
    *
    * @param type this is the type of the subclass implementation
    * @param data contains the arguments to use in an invocation
    *
    * @throws Exception this is thrown if the preparation fails
    */
   private void prepare(Class type, Object[] data) throws Exception{
      new Introspector(type).invoke("prepare", this, data);
   }
}

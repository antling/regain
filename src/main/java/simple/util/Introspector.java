/*
 * Introspector.java July 2003
 *
 * Copyright (C) 2003, Niall Gallagher <niallg@email.com>
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

import java.lang.reflect.Method;

/**
 * The <code>Introspector</code> provides a means to invoke methods
 * of arbitrary objects by matching a parameter list and name. The
 * method matching done by this is similar, but more primitave, to
 * the dynamic binding done by the Java runtime. This will basically 
 * attempt to find a method, in no specific order, that has the
 * name specified and that takes parameters that are compatiable 
 * to those given to the <code>invoke</code> methods.
 *
 * @author Niall Gallagher
 */
public class Introspector {

   /**
    * Contains the list of methods to be examined.
    */
   private Method[] list;

   /**
    * Constructor for the <code>Introspector</code> class. This 
    * will enable methods of the specified <code>Class</code> to be
    * invoked using only the name of the method and the arguments 
    * to that method. The target of the invocation must be either
    * the same type or a subclass of the class specified to this.
    *
    * @param type the object type the introspection is done with
    */
   public Introspector(Class type) {
      this.list = type.getMethods();
   }

   /**
    * This method performs the "discovery" of methods within the
    * instance class. This will attempt to find a match for a
    * method with the specified name and that takes a parameter
    * of the type represented by the issued <code>Class</code>.
    * <p>
    * The discovery of the method is done in no specific order. 
    * This will simply match aand retrieve the first method that
    * is compatable with the signature described. If no match is
    * found then this method will return null.
    *
    * @param name this is the name of the method to be invoked
    * @param type this is the type of the single parameter
    *
    * @return returns the <code>Method</code> that is discovered
    *
    * @throws Exception thrown if there is a problem matching  
    */
   public Method match(String name, Class type) throws Exception {
      return match(name, new Class[]{type});
   } 

   /**
    * This method performs the "discovery" of methods within the
    * instance class. This will attempt to find a match for a
    * method with the specified name and that takes a parameter
    * of the type represented by the <code>Class</code> array.
    * <p>
    * The discovery of the method is done in no specific order. 
    * This will simply match and retrieve the first method that 
    * is compatable with the signature described. If no match is
    * found then this method will return null.
    *
    * @param name this is the name of the method to be invoked
    * @param types these are the types of the method parameters
    *
    * @return returns the <code>Method</code> that is discovered
    */
   public Method match(String name, Class[] types){
      for(int i = 0; i < list.length; i++){
         if(list[i].getName().equals(name)){
            if(isMatch(list[i], types))
               return list[i];
         }
      }
      return null;
   }

   /**
    * Determines whether the issued method has a compatable list of
    * parameters with the list of <code>Class</code> objects. This
    * will simply check to see if any of the classes within the
    * parameter list, as of <code>Method.getParameterTypes</code>,
    * is either the same class, a superclass or a superinterface, of
    * the corrosponding classes within the <code>Class</code> array.
    *
    * @param method this is the object to check for compatibility
    * @param types list of classes used to determine compatibility
    * 
    * @return returns true if the method matches, false otherwise
    */
   private boolean isMatch(Method method, Class[] types){
      Class[] list = method.getParameterTypes();
      
      if(list.length == types.length){
         for(int i = 0; i < list.length; i++){
            if(types[i] != null)
               if(!list[i].isAssignableFrom(types[i]))
                  return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Performs a reflective invocation on a target object by means
    * of discovering a method compatible with the arguments. The
    * name of the method to be invoked coupled with the list of
    * arguments are used to "discover" a signature within the class
    * this <code>Introspector</code> instance was created to search.
    * <p>
    * If the target object is not of the same type as the type this
    * instance was specified to use, then this method will throw an
    * <code>IllegalArgumentException</code>. The target object 
    * therefore must be of the same type or a subclass of the type.
    *      
    * @param name this is the name of the method to search for
    * @param target this is the target object to be invoked
    * @param data a single argument to be passed to the method
    *
    * @return this returns whatever the invoked method returns
    *
    * @throws IllegalAccessException this is thrown if the method
    * discovered is not public
    * @throws InvocationTargetException this is thrown if the
    * discovered method throws an exception
    * @throws IllegalArgumentException thrown if the target object
    * is not compatable with the issued type
    * @throws NoSuchMethodException thrown if a matching method
    * cannot be found for the class
    */
   public Object invoke(String name, Object target, Object data) throws Exception {
      return invoke(name, target, new Object[]{data});
   }

   /**
    * Performs a reflective invocation on a target object by means
    * of discovering a method compatible with the arguments. The
    * name of the method to be invoked coupled with the list of
    * arguments are used to "discover" a signature within the class
    * this <code>Introspector</code> instance was created to search.
    * <p>
    * If the target object is not of the same type as the type this
    * instance was specified to use, then this method will throw an
    * <code>IllegalArgumentException</code>. The target object 
    * therefore must be of the same type or a subclass of the type.
    *      
    * @param name this is the name of the method to search for
    * @param target this is the target object to be invoked
    * @param list the list of arguments to be passed to the method
    *
    * @return this returns whatever the invoked method returns
    * 
    * @throws IllegalAccessException this is thrown if the method
    * discovered is not public
    * @throws InvocationTargetException this is thrown if the
    * discovered method throws an exception
    * @throws IllegalArgumentException thrown if the target object
    * is not compatable with the issued type
    * @throws NoSuchMethodException thrown if a matching method
    * cannot be found for the class
    */
   public Object invoke(String name, Object target, Object[] list) throws Exception {
      Class[] types = new Class[list.length];
      
      for(int i = 0; i < list.length; i++){
         if(list[i] != null)
            types[i] = list[i].getClass();
      }
      Method method = match(name, types);

      if(method == null){
         throw new NoSuchMethodException("No match found");
      }
      return method.invoke(target,list);
   }
}


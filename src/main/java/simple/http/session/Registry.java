/*
 * Registry.java May 2004
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

package simple.http.session;

import simple.util.lease.Cleaner;
import simple.util.net.Cookie;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>Registry</code> object is used to acquire and store
 * the <code>Module</code> objects used by the session management
 * system. This acts much like a factory in that it will create
 * a module if it does not exist. Once the module is created it
 * is registered as the name provided by the <code>Cookie</code>.
 * <p>
 * All modules have leased registration within this registry.
 * This means that once the lease expires the module is removed
 * from the system. So in order to maintain the module within
 * the registry the <code>Lease</code> associated with it must
 * be renewed peroidically.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.lease.Cleaner
 */
final class Registry implements Cleaner {

   /**
    * This is the factory used to create the module objects.
    */
   private ModuleFactory factory;

   /**
    * The internal registry used to store module objects.
    */
   private Map registry;

   /**
    * Constructor for the <code>Registry</code> object. This is
    * used to create an instance with a private internal map
    * of <code>Module</code> objects. This will simply create a
    * <code>java.util.HashMap</code> as its registry.
    */
   public Registry(){
      this(new HashMap());
   }

   /**
    * Constructor for the <code>Registry</code> object. This is
    * used to create an instance with the specified map as the
    * internal registry. If desired an external object can use
    * this to acquire the active <code>Module</code> objects.
    *
    * @param registry this is the internal registry to use
    */
   public Registry(Map registry){
      this.factory = new ModuleFactory(this);
      this.registry = registry;
   }

   /**
    * This method either returns an existing <code>Module</code>
    * object or creates and returns a new instance. Caution must
    * be taken when using module objects that are retrieved from
    * this method as the lease may expire shortly afterwards. To
    * ensure that the module has a sufficent lifetime after it
    * has been retrieved its <code>Lease</code> can be renewed.
    *
    * @param cookie the cookie used as the session reference
    */
   public synchronized Module lookup(Cookie cookie){
      return lookup(cookie, null);
   }

   /**
    * This method either returns an existing <code>Module</code>
    * object or creates and returns a new instance. Caution must
    * be taken when using module objects that are retrieved from
    * this method as the lease may expire shortly afterwards. To
    * ensure that the module has a sufficent lifetime after it
    * has been retrieved its <code>Lease</code> can be renewed.
    *
    * @param cookie the cookie used as the session reference
    * @param data this is used to initialize the storage object
    */
   public synchronized Module lookup(Cookie cookie, Object data){
      String name = cookie.getValue();
      Object module = registry.get(name);

      if(module == null){
         module = create(cookie, data);
         registry.put(name, module);
      }
      return (Module)module;
   }

   /**
    * This method produces a <code>Module</code> that can be
    * used to maintain a session. The instance returned is leased
    * for an initial peroid, which by default is one minute. Once
    * the module has been created the <code>Lease</code> should
    * be renewed to a suitable session timeout peroid.
    *
    * @param cookie the cookie used as the session reference
    * @param data this is used to initialize the storage object
    */
   private synchronized Module create(Cookie cookie, Object data){
      return factory.getInstance(cookie, data);
   }

   /**
    * This method removes the registry's reference to the named
    * module object. This ensures that the module and the session
    * variables contained within the module's store can be
    * collected by the garbage collector to recover its memory.
    *
    * @param name this is the name of the module to be destroyed
    */
   private synchronized Module remove(String name){
      return (Module)registry.remove(name);
   }

   /**
    * This removes and destroys the named module. During the life
    * time of this registry many <code>Module</code> objects will
    * be created. To ensure that they do not consume too much
    * resources, such as memory, they are leased. This means that
    * when the lease expires the module is destroyed.
    * <p>
    * This method implements the <code>Cleaner.clean</code>
    * method which enables the leasing system to notify this
    * registry when the modules lease has expired. Once this has
    * recieved notification the <code>Store</code> is destroyed
    * and the module is unreferenced for garbage collection.
    *
    * @param name this is the name of the module to be destroyed
    */
   public synchronized void clean(String name){
      Module module = remove(name);

      try {
         module.getStore().destroy();
      }catch(StoreException e){
         return;
      }
   }
}

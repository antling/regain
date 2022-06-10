/*
 * ModuleFactory.java May 2004
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

import simple.util.lease.LeaseManager;
import simple.util.lease.Lease;
import simple.util.lease.Cleaner;
import simple.util.net.Cookie;

/**
 * The <code>ModuleFactory</code> creates the <code>Module</code>
 * objects used by the session management system. This is used to
 * create a <code>Store</code> that can be leased for a specific
 * peroid of time. Once created the <code>Lease</code> for the
 * module object should be configured in such a manner that the
 * module will be kept active for the desired peroid of time.
 * 
 * @author Niall Gallagher
 * 
 * @see simple.http.session.StoreFactory
 */
final class ModuleFactory {

   /**
    * This manages the issued lease objects for all modules.
    */
   private LeaseManager manager;

   /**
    * This creates the store objects for each module object.
    */
   private StoreFactory factory;

   /**
    * Specifies an initial lease peroid for created modules.
    */
   private long peroid;

   /**
    * Constructor for the <code>ModuleFactory</code> object. To
    * create the factory object a <code>Cleaner</code> is needed
    * to ensure that lease expiry notification are issued to a 
    * known entity. This cleaner is responsible for cleaning up
    * after the module and destroying the <code>Store</code>.
    *
    * @param cleaner the cleaner used to recieve notifications
    */
   public ModuleFactory(Cleaner cleaner) {
      this(cleaner, 60000);
   }

   /**
    * Constructor for the <code>ModuleFactory</code> object. To
    * create the factory object a <code>Cleaner</code> is needed
    * to ensure that lease expiry notification are issued to a 
    * known entity. This cleaner is responsible for cleaning up
    * after the module and destroying the <code>Store</code>.
    *
    * @param cleaner the cleaner used to recieve notifications
    * @param peroid the initial lease peroid for all modules
    */
   public ModuleFactory(Cleaner cleaner, long peroid) {
      this.manager = new LeaseManager(cleaner);
      this.factory = new StoreFactory();
      this.peroid = peroid;
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
   public Module getInstance(Cookie cookie, Object data) {
      Lease lease = getLease(cookie.getValue(), peroid);
      Store store = getStore(cookie, data);

      return new Delegate(store, lease);
   }

   /**
    * This creates a <code>Lease</code> object for the specified
    * duration. The created lease will ensure that notification
    * is sent to the <code>Cleaner</code> when its duration has
    * expired. Notification is sent using the name of the session
    * which is retrieved from the session cookie value.
    *
    * @param name this is the name used to identify the module
    * @param duration this is the initial duration of the lease 
    */
   private Lease getLease(String name, long duration) {
      return manager.lease(name, duration);
   }

   /**
    * This creates the <code>Store</code> object used to store
    * the session variables. This will attempt to instantiate 
    * and initialize the implementation specified in the system
    * property <code>simple.http.session.store</code>. However,
    * if this cannot be done the default storage is used.
    *
    * @param cookie this represents the session identifier
    * @param data this is used to initialize the storage
    */
   private Store getStore(Cookie cookie, Object data) {
      Store store = factory.getInstance(cookie);

      try {
         store.prepare(data);
      }catch(StoreException e){
         return new DefaultStore(cookie);
      }
      return store;
   }

   /**
    * The <code>Delegate</code> inner class represente the module
    * produced by this factory. Representing the module object as
    * an inner class keeps the factory simple and ensures that 
    * the overall coupling within the system is kept minimal.
    * 
    * @see simple.http.session.Module
    */
   private class Delegate implements Module {

      /**
       * This is used to store all the session variables.
       */
      private Store store;

      /**
       * This is used to maintain this instance in the system.
       */
      private Lease lease;

      /**
       * Constructor for the <code>Delegate</code> object. This
       * object provides access to the leasing used to maintain
       * the module and the <code>Store</code> for the module.
       *
       * @param store this is used to store session variables
       * @param lease this is used to maintain this instance
       */
      public Delegate(Store store, Lease lease) {
         this.store = store;
         this.lease = lease;
      }

      /**
       * This method retrieves the <code>Store</code> to be used
       * to insert and retrieve session variables. This will be
       * the implementation retrieved from the internal factory.
       *
       * @return this returns the object used for data storage
       */
      public Store getStore() {
         return store;
      }

      /**
       * This provides the <code>Lease</code> used to maintain 
       * this instance. Once this lease expires the module will
       * be removed from the system and the store is destroyed.
       *
       * @return this returns the lease used to maintain this
       */
      public Lease getLease() {
         return lease;
      }
   }
}

/*
 * Delegate.java May 2004
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

import simple.util.lease.LeaseException;
import simple.util.lease.Lease;
import simple.util.net.Cookie;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>Delegate</code> object wraps a <code>Module</code> 
 * so that the lease and storage management can be aggregated
 * under the umbrella of a single interface. By design there is
 * no direct interface to session leasing, this keeps session
 * handling simple and uniform throughout the system. The only
 * direct interaction with the lease management mechanism is
 * through the <code>destroy</code> method, which cancels the
 * lease and schedules the session for garbage collection.
 * 
 * @author Niall Gallagher
 *
 * @see simple.http.session.Store
 */
final class Delegate implements Session {

   /**
    * This is the object used to store the session variables.
    */
   private Store store;
   
   /**
    * This is the lease used to maintain this session object.
    */
   private Lease lease;

   /**
    * Constructor for the <code>Delegate</code> object. This is
    * used to create a <code>Session</code> that will store data
    * using the provided storage object. Also this contains a 
    * handle for the lease used to maintain the session. 
    *
    * @param module this is the module for this session object
    * @param cookie this is the live reference to the session
    */
   public Delegate(Module module, Cookie cookie) {
      this.store = module.getStore();
      this.lease = module.getLease();
   }

   /**
    * This method is used to acquire a session variable that has
    * been previously stored with the <code>put</code> method.
    * If there is no value by the given name within the store
    * then this will return null. Also, if there is a problem
    * retrieving the data null is returned instead.
    *
    * @param name this is the name of the variable to retrieve
    *
    * @return returns the variable mapped to the provided name
    */
   public Object get(String name) {
      try {
         return store.get(name);
      }catch(StoreException e){
         return null;
      }
   }

   /**
    * This maps the given session variable to the provided name.
    * The session variable can later be retrieved from future
    * requests using the <code>get</code> method with its name.
    * This will delegate to the internal <code>Store</code>.
    *
    * @param name this is the name of the variable to be mapped
    * @param value this is the value mapped to the given name
    */
   public void put(String name, Object value) {
      try {
         store.put(name, value);
      }catch(StoreException e){
         return;
      }
   }

   /**
    * This method is used to determine whether there is a value
    * mapped to the specified name. If there is an existing
    * mapping for the name this returns true, otherwise false.
    * This will delegate to the internal <code>Store</code>.
    *
    * @return this returns true if the mapping already exists
    */    
   public boolean contains(String name) {
      try {
         return store.contains(name);
      }catch(StoreException e){
         return false;
      }
   }

   /**
    * This removes the mapping for the specified name. Once this
    * has been done the underlying session must not contain any
    * lingering reference to the mapped object. This acts as a
    * wrapper for the internal <code>Store</code> object.
    *
    * @param name this is the name of the variable to remove
    */
   public void remove(String name) {
      try {
         store.remove(name);
      }catch(StoreException e){
         return;
      }
   }

   /**
    * This is a convinience method that can be used to determine
    * whether there are any variables within the session. This
    * can be useful in determining if the session is new. This
    * uses the <code>Store.keySet</code> to determine emptiness.
    *
    * @return this returns true if the session has no mappings
    */
   public boolean isEmpty() {
      return keySet().isEmpty();
   }

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into a session can be retrieved with this
    * method. This will return a <code>Set</code> that contains
    * the names of all the session variables currently stored.
    * This will delegate to the internal <code>Store</code>.
    *
    * @return this returns the the keys for existing mappings
    */
   public Set keySet() {
      try {
         return store.keySet();
      }catch(StoreException e){
         return new HashSet();
      }
   }

   /**
    * In order to free up any resources consumed by this object
    * it must be destroyed when it is no longer useful. This can
    * be used to delete files, close JDBC connections, or free
    * up other such resources used for storing session data.
    */
   public void destroy() {
      try {
         lease.cancel();
      }catch(LeaseException e){
         return;
      }
   }
}

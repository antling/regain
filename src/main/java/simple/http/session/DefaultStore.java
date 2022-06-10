/*
 * DefaultStore.java May 2004
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

import simple.util.net.Cookie;
import java.util.Hashtable;
import java.util.Set;

/**
 * The <code>DefaultStore</code> object provides the simplest
 * implementation of a <code>Store</code>. Session variables are
 * stored in an internal <code>java.util.Hashtable</code> using
 * this object. This store is destroyed by clearing that table.
 *
 * @author Niall Gallagher
 */
final class DefaultStore implements Store {

   /**
    * Contains all session variables stored by this instance.
    */
   private Hashtable store;

   /**
    * Constructor for the <code>DefaultStore</code> object. This
    * provides a simple in memory storage that delegates to an
    * internal <code>java.util.Hashtable</code> for storing data.
    *
    * @param cookie this is used as the session reference
    */
   public DefaultStore(Cookie cookie) {
      this.store = new Hashtable();
   }

   /**
    * This method is used to prepare the <code>Store</code> for
    * use with an active session. This provides a dummy method
    * that enables this implementation to fullfil its interface.
    *
    * @param data the data used to initialize this instance
    */
   public void prepare(Object data) {}

   /**
    * This method is used to acquire a session variable that has
    * been previously stored with the <code>put</code> method.
    * If there is no value by the given name within the store
    * then this will return null.
    *
    * @param name this is the name of the variable to retrieve
    *
    * @return returns the variable mapped to the provided name
    */
   public Object get(String name) {
      return store.get(name);
   }

   /**
    * This maps the given session variable to the provided name.
    * The session variable can later be retrieved from future
    * requests using the <code>get</code> method with its name.
    * This will delegate to an internal <code>Hashtable</code>.
    *
    * @param name this is the name of the variable to be mapped
    * @param value this is the value mapped to the given name
    */
   public void put(String name, Object value) {
      store.put(name, value);
   }

   /**
    * This method is used to determine whether there is a value
    * mapped to the specified name. If there is an existing
    * mapping for the name this returns true, otherwise false.
    * This will delegate to an internal <code>Hashtable</code>.
    *
    * @return this returns true if the mapping already exists
    */
   public boolean contains(String name) {
      return store.containsKey(name);
   }

   /**
    * This removes the mapping for the specified name. Once this
    * has been done the underlying session must not contain any
    * lingering reference to the mapped object. This acts as a
    * wrapper for an internal <code>Hashtable</code> object.
    *
    * @param name this is the name of the variable to remove
    */
   public void remove(String name) {
      store.remove(name);
   }

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into a session can be retrieved with this
    * method. This will return a <code>Set</code> that contains
    * the names of all the session variables currently stored.
    * This will delegate to an internal <code>Hashtable</code>.
    *
    * @return this returns the the keys for existing mappings
    */
   public Set keySet() {
      return store.keySet();
   }

   /**
    * In order to free up any resources consumed by this object
    * it must be destroyed when it is no longer useful. This will
    * clear all internal references so that garbage collection
    * can begin to recover the memory consumed by those objects.
    */
   public void destroy() {
      store.clear();
   }
}


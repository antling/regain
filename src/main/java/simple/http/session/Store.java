/*
 * Store.java May 2004
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

import java.util.Set;

/**
 * The <code>Store</code> object provides the persistence scheme
 * used by sessions. In order to facilitate a range of storage
 * techniques for session data the <code>Session</code> must be
 * able to avail of arbitrary strategies. An implementation of
 * this interface can be used to store all objects given to
 * a session instance. For example, objects can be stored within
 * a relational database, a file, or some in-memory container.
 * <p>
 * To configure the system to use a specific implementation the
 * system property <code>simple.http.session.store</code> can be
 * used. If this is not specified then a default implementation
 * is used which simply stores the data within an internal map.
 * All <code>Store</code> implementations must have a single
 * argument constructor that takes a <code>Cookie</code>.
 *
 * @author Niall Gallagher
 */
public interface Store {

   /**
    * This method is used to prepare the <code>Store</code> for
    * use with an active session. This should contain sufficent
    * functionality to setup the storage environment so that the
    * <code>Session</code> object can transparently store and
    * retrieve session variables from the storage medium. If 
    * there is a problem initializing an exception is thrown.
    * 
    * @param data the data used to initialize this instance
    * 
    * @exception StoreException if there problem initializing
    */
   public void prepare(Object data) throws StoreException;

   /**
    * This method is used to acquire a session variable that has
    * been previously stored with the <code>put</code> method.
    * If there is no value by the given name within this store
    * then this will return null. Also, if there is a problem
    * retrieving the data from the underlying storage mechanism
    * this may thrown an <code>StoreException</code>.
    *
    * @param name this is the name of the variable to retrieve
    *
    * @return returns the variable mapped to the provided name
    *
    * @exception StoreException thrown if there is an I/O error
    */
   public Object get(String name) throws StoreException;

   /**
    * This maps the given session variable to the provided name.
    * An implementation of this must insert the variable into a
    * container in such a way that it can be retrieved at a later
    * stage using the <code>get</code> method. If the object can
    * not be stored an <code>StoreException</code> is thrown.
    *
    * @param name this is the name of the variable to be mapped
    * @param value this is the value mapped to the given name
    *
    * @exception StoreException if there is an storage problem
    */
   public void put(String name, Object value) throws StoreException;

   /**
    * This method is used to determine whether there is a value
    * mapped to the specified name. If there is an existing
    * mapping for the specified name this returns true, if not
    * then false is returned. An exception is thrown if there is
    * a problem accessing the underlying storage mechanism.
    *
    * @return this returns true if the mapping already exists
    *
    * @exception StoreException if there is an storage problem
    */
   public boolean contains(String name) throws StoreException;

   /**
    * This removes the mapping for the specified name. Once this
    * has been done the underlying storage must not contain any
    * reference to the mapped object. If there is a problem with
    * the underlying storage mechanism an exception is thrown.
    *
    * @param name this is the name of the variable to remove
    *
    * @exception StoreException if there is an storage problem
    */
   public void remove(String name) throws StoreException;

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into this store can be retrieved with this
    * method. This will return a <code>Set</code> that contains
    * the names of all the session variables currently stored.
    *
    * @return this returns the the keys for existing mappings
    *
    * @exception StoreException if there is an storage problem
    */
   public Set keySet() throws StoreException;

   /**
    * In order to free up any resources consumed by this object
    * it must be destroyed when it is no longer useful. This can
    * be used to delete files, close JDBC connections, or free
    * up other such resources used for storing session data.
    *
    * @exception StoreException if there is an storage problem
    */
   public void destroy() throws StoreException;
}

/*
 * Session.java May 2004
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
 * The <code>Session</code> object is used to store session data
 * relating to a specific HTTP client. The HTTP protocol does not
 * maintain state between requests, so in order to build up data
 * some identifier from the HTTP request must be used to acquire
 * session information. Typically the session identifier is taken
 * from a HTTP cookie, which can then be used to acquire data.
 * That data is stored in a <code>Session</code> implementation.
 * <p>
 * This interface hides the complexities of the session handling
 * system, which is broken into two separate entities. The first
 * is the <code>Store</code> which acts as the storage mechanism
 * for session variables, the other is the leasing mechanism,
 * which is used to determine when a session has expired. The
 * result is a very simple session management mechanism.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.session.Store
 */
public interface Session {

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
   public Object get(String name);

   /**
    * This maps the given session variable to the provided name.
    * The session variable can later be retrieved from future
    * requests using the <code>get</code> method with its name.
    *
    * @param name this is the name of the variable to be mapped
    * @param value this is the value mapped to the given name
    */
   public void put(String name, Object value);

   /**
    * This method is used to determine whether there is a value
    * mapped to the specified name. If there is an existing
    * mapping for the name this returns true, otherwise false.
    *
    * @return this returns true if the mapping already exists
    */   
   public boolean contains(String name);

   /**
    * This removes the mapping for the specified name. Once this
    * has been done the underlying session must not contain any
    * lingering reference to the mapped object.
    *
    * @param name this is the name of the variable to remove
    */
   public void remove(String name);

   /**
    * This is a convenience method that can be used to determine
    * whether there are any variables within the session. This
    * can be useful in determining if the session is new.
    *
    * @return this returns true if the session has no mappings
    */
   public boolean isEmpty();

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into a session can be retrieved with this
    * method. This will return a <code>Set</code> that contains
    * the names of all the session variables currently stored.
    *
    * @return this returns the the keys for existing mappings
    */
   public Set keySet();

   /**
    * In order to free up any resources consumed by this object
    * it must be destroyed when it is no longer useful. This can
    * be used to delete files, close JDBC connections, or free
    * up other such resources used for storing session data.
    */
   public void destroy();
}

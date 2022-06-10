/*
 * Manager.java June 2004
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
import simple.http.Profile;

/**
 * The <code>Manager</code> object provides the interface to the
 * session management system. This enables <code>Session</code>
 * objects to be created and retrieved from the system. Sessions
 * are created if the issued <code>Profile</code> does not have
 * a session reference cookie or if it contains a reference to a
 * dead session. If a reference to a dead session is found a new
 * one is created and is referred to with the existing cookie.
 * <p>
 * If the <code>Manager</code> creates a new session then it
 * invokes the <code>Store.prepare</code> method for a new 
 * instance. This method is given the argument provided in the
 * <code>getSession</code> method, which is either null if none
 * is provided or an object of the users choice. This allows the
 * <code>Store</code> the possibility of setting up JDBC 
 * connections by possibly passing it a URI to connection to
 * the database, for example "jdbc:mysql://host/database" could
 * be used to setup the connection to the remote database.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.session.Store
 */
public final class Manager {

   /**
    * This provides the implementation for the system. This
    * will either create or retrieve a session based on the
    * cookie objects provided by the <code>Profile</code>.
    */
   private static Activator activator;

   static {
      activator = new Activator();
   }

   /**
    * This will either retrieve an active session or create a
    * new one. The <code>Session</code> object is created if the
    * <code>Profile</code> does not have a reference to an active
    * session. If a session has expired and the session reference
    * is provided, this will create a new session using the
    * existing reference. Once a <code>Session</code> has been
    * created a <code>Cookie</code> is set with its reference.
    *
    * @param profile contains all the cookies sent by the client
    *
    * @return returns an active <code>Session</code> object
    */
   public static Session getSession(Profile profile) {
      return activator.activate(profile);
   }

   /**
    * This will either retrieve an active session or create a
    * new one. The <code>Session</code> object is created if the
    * <code>Profile</code> does not have a reference to an active
    * session. If a session has expired and the session reference
    * is provided, this will create a new session using the
    * existing reference. Once a <code>Session</code> has been
    * created a <code>Cookie</code> is set with its reference.
    *
    * @param profile contains all the cookies sent by the client
    * @param data this is used to initialize the storage object,
    * this can be anything from a URI to a <code>Map</code>
    *
    * @return returns an active <code>Session</code> object
    */
   public static Session getSession(Profile profile, Object data) {
      return activator.activate(profile, data);
   }
}

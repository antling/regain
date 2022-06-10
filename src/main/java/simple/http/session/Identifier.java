/*
 * Module.java June 2004
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
 * The <code>Identifier</code> is responsible for extracting and
 * creating the session identifier from the collection of cookies
 * sent with the HTTP request. The <code>Profile</code> object
 * contains all cookies sent with the HTTP request including the
 * session identity. So the task of identifying the session to
 * the session management system is a matter of locating the
 * cookie that contains the session identifier.
 * <p>
 * If the <code>Profile</code> does not contain a reference to a
 * session then this must create a new reference. This reference
 * must be a <code>simple.util.net.Cookie</code> object and must
 * be inserted into the <code>Profile</code> so that it will be
 * recieved by the client. The session management system will
 * then use that cookie value to reference the new session.
 *
 * @author Niall Gallagher
 */
interface Identifier {

   /**
    * This is used to acquire a session reference by either
    * creating the reference or extracting an existing one. The
    * session reference is a <code>Cookie</code>, which enables
    * the scope of the session, that is, the domain the session
    * resides within, to be configured. This is useful in a
    * server farm where more than one server handles requests.
    *
    * @param profile container for the session reference
    *
    * @return this returns the cookie referencing the session
    */
   public Cookie getIdentity(Profile profile);
}

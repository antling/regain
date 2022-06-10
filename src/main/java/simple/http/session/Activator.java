/*
 * Activator.java May 2004
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
import simple.http.Profile;

/**
 * The <code>Activator</code> object is responsible for creating
 * and retrieving <code>Session</code> objects. This deals with
 * <code>Module</code> objects, which it uses to create sessions
 * and renew the leases for the sessions. To ensure maximum
 * flexibility in renewing leases for the system this makes use
 * of a <code>Maintainer</code> component. This component is
 * used to renew the lease for a session once that session has
 * been referenced. If the session lease cannot be renewed this
 * will attempt to create a new one.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.session.Maintainer
 * @see simple.http.session.Identifier
 */
final class Activator {

   /**
    * This is used to manage session modules for the system.
    */
   private Registry registry;

   /**
    * This is used to renew the leases for session modules.
    */
   private Maintainer manager;

   /**
    * This is used to identify the session reference cookie.
    */
   private Identifier lookup;

   /**
    * Constructor for the <code>Activator</code> object. This is
    * used to create a session management system that will lease
    * the sessions created for a peroid of time. Each time this
    * instance is used to activate the session the lease is
    * renewed for the same fixed peroid of time.
    */
   public Activator(){
      this(new Registry());
   }

   /**
    * Constructor for the <code>Activator</code> object. This is
    * used to create a session management system that will lease
    * the sessions created for a peroid of time. Each time this
    * instance is used to activate the session the lease is
    * renewed for the same fixed peroid of time.
    *
    * @param registry this is used to manage session modules
    */
   private Activator(Registry registry) {
      this.manager = MaintainerFactory.getInstance();
      this.lookup = IdentifierFactory.getInstance();
      this.registry = registry;
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
    * @param profile contains all the cookies with a request
    *
    * @return returns an active <code>Session</code> object
    */
   public Session activate(Profile profile) {
      return activate(profile, null);
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
    * @param profile contains all the cookies with a request
    * @param data this is used to initialize the storage object
    *
    * @return returns an active <code>Session</code> object
    */
   public Session activate(Profile profile, Object data) {
      return activate(lookup.getIdentity(profile), data);
   }

   /**
    * This will either retrieve an active session or create a
    * new one. The <code>Session</code> object is created if the
    * issued <code>Cookie</code> is not a reference to an active
    * session. If a session has expired the session reference
    * provided is used to reference the new session.
    *
    * @param cookie the cookie used as the session reference
    * @param data this is used to initialize the storage object
    *
    * @return returns an active <code>Session</code> object
    */
   private Session activate(Cookie cookie, Object data) {
      for(int i = 0; i < 2; i++) {
         try {
            Module module = retrieve(cookie, data);
            return new Delegate(module, cookie);
         }catch(LeaseException e){
            continue;
         }
      }
      throw new IllegalStateException("Lease expired");
   }

   /**
    * This will retrieve or create a <code>Module</code> object
    * to represent a <code>Session</code>. The module is created
    * if the issued <code>Cookie</code> is not a reference to an
    * active session. Once the module is created the lease for
    * it is renewed using the <code>Maintainer</code> object.
    *
    * @param cookie the cookie used as the session reference
    * @param data this is used to initialize the storage object
    *
    * @return returns an active <code>Module</code> object
    */
   private Module retrieve(Cookie cookie, Object data) throws LeaseException {
      Module module = registry.lookup(cookie, data);
      try {
         Lease lease = module.getLease();
         Store store = module.getStore();

         manager.renew(lease, store);
      }catch(LeaseException e){
         throw new LeaseException("Lease expired");
      }
      return module;
   }
}


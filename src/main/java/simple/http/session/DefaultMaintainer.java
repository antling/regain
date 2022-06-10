/*
 * DefaultMaintainer.java May 2004
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

/**
 * The <code>DefaultMaintainer</code> object provides a lease
 * management component that can be used to renew the lease for
 * a specific <code>Store</code>. This ensures that the server
 * does not get clogged by with garbage sessions that have not
 * been referenced for the renewal duration. A twenty minute 
 * lease renewal is sufficent to allow users to linger on a page
 * for sufficent time before their session is purged, it is also
 * a reasonable length of time to consider the session redundant.
 * 
 * @author Niall Gallagher
 */
final class DefaultMaintainer implements Maintainer {

   /**
    * To ensure that sessions do not linger in the VM for very
    * long the maximum length of time a store will remain in
    * the system without being referenced is twenty minutes. 
    */
   private static final long DEFAULT_PEROID = 1200000;

   /** 
    * This method is used to determine the length of time the
    * session should linger between references. The duration of
    * time a session spends lingering within the system without 
    * being collected by the garbage collector is determined by 
    * the <code>Lease</code> object. If this is renewed for a
    * specific peroid of time then the <code>Store</code> will
    * remain within the system for that time peroid.
    * 
    * @param store this is the store that has been referenced
    * @param lease this is the lease associated with the store
    *
    * @exception LeaseException thrown if the lease has expired
    */
   public void renew(Lease lease, Store store) throws LeaseException {
      lease.renew(DEFAULT_PEROID);
   }
}

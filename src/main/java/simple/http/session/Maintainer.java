/*
 * Maintainer.java May 2004
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
 * The <code>Maintainer</code> is used to determine how long
 * a session is to remain active between references. A session is
 * referenced when a HTTP client sends the session identifier with
 * a request. Once the session is referenced this is used to
 * determine, possibly using session variables, the duration that
 * the <code>Lease</code> should be renewed for. This typically
 * should be in the region of ten to twenty minutes to limit the
 * window of opportunity for session hijacking and to ensure that
 * garbage sessions do not consume resources for very long.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.lease.Lease
 */
public interface Maintainer {

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
   public void renew(Lease lease, Store store) throws LeaseException;
}

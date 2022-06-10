/*
 * Module.java May 2004
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

import simple.util.lease.Lease;

/**
 * The <code>Module</code> object is used internally to represent
 * a session. Each session uses a <code>Store</code> to insert and
 * retrieve session variables from, this allows an arbitrary means
 * of storage to be used. Because sessions and session variables
 * consume large amounts of data, the module is leased. When the
 * lease expires the store and all its contents are cleared so the
 * garbage collector can recover the resources.
 *
 * @author Niall Gallagher
 */
interface Module {

   /**
    * This method retrieves the <code>Store</code> to be used to
    * insert and retrieve session variables. The means of storing
    * the data is arbitrary and is dependant of the configuration
    * used. A <code>Session</code> instance is required to store
    * all session variables in the provided <code>Store</code>.
    *
    * @return the store that contains the session variables
    */
   public Store getStore();

   /**
    * This provides the <code>Lease</code> used to maintain the
    * session variables. This lease must be frequently renewed to
    * ensure that the session variables are maintained. The event
    * used to renew the lease is typically a client HTTP request.
    *
    * @return the lease that is used to maintain this module
    */
   public Lease getLease();
}

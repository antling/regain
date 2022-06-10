/*
 * LeaseHandler.java May 2004
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

package simple.util.lease;

/**
 * The <code>LeaseHandler</code> forms the interface to the lease
 * management system. There are four actions permitted for leased
 * resources, including lease issuing, renewals, cancellation, and
 * querying. The actions are abstracted into a single interface
 * so that various lease management systems can be introduced.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.lease.DefaultHandler
 */
interface LeaseHandler {

   /**
    * This method will establish a lease for the named resource.
    * If the lease duration expires before it is renewed then a
    * notification is sent, typically to a <code>Cleaner</code>
    * implementation, to signify that the resource should be
    * released. The established lease can also be canceled.
    * 
    * @param name this is the name of the leased resource 
    * @param duration this is the duration of the new lease
    *
    * @exception LeaseException if the lease could not be done
    */
   public void lease(String name, long duration) throws LeaseException;

   /**
    * This ensures that the named resource is maintained for the
    * specified duration. The duration is in milliseconds so the
    * actual expiry time is the <code>System</code> time plus the
    * duration. The accuracy of this method should be exact.
    *
    * @param name this is the name of the resource to be renewed
    * @param duration this is the length of time to renew for
    *
    * @exception LeaseException if the expiry has been passed
    */
   public void renew(String name, long duration) throws LeaseException;

   /**
    * This will cancel the lease and release the resource. This 
    * has the same effect as the <code>renew</code> method with
    * a zero length duration. Once this has been called the
    * <code>Cleaner</code> used should be notified immediately.
    * If the lease has already expired this throws an exception.
    *
    * @param name this is the name of the resource to release
    *
    * @exception LeaseException if the expiry has been passed
    */
   public void cancel(String name) throws LeaseException;

   /**
    * Determines the duration remaining before the lease expires. 
    * The expiry is given as the number of milliseconds remaining
    * before the lease expires. If this value is negative it 
    * should be assumed that the lease has already expired.
    *
    * @param name this is the name of the resource to query
    *
    * @return the number of milliseconds remaining in the lease
    *
    * @exception LeaseException if the lease expiry has passed
    */
   public long getExpiry(String name) throws LeaseException;
}

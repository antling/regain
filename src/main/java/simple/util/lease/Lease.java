/*
 * Lease.java May 2004
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
 * The <code>Lease</code> object is used to keep a named resource
 * active. This provides a very simple lease that can be used to
 * track the activity of a resource or system. Keeping track of
 * activity allows resources to be maintained until such time
 * that they are no longer required, allowing the server to clean
 * up any allocated memory, file descriptors, or other such data.
 *
 * @author Niall Gallagher 
 */
public interface Lease {

   /**
    * This ensures that the leased resource is maintained for the
    * specified duration. The duration is in milliseconds so the
    * actual expiry time is the <code>System</code> time plus the
    * duration. The accuracy of this method should be exact.
    *
    * @param duration this is the length of time to renew for
    *
    * @exception LeaseException if the expiry has been passed
    */
   public void renew(long duration) throws LeaseException;

   /**
    * This will cancel the lease and release the resource. This 
    * has the same effect as the <code>renew</code> method with
    * a zero length duration. Once this has been called the
    * <code>Cleaner</code> used should be notified immediately.
    * If the lease has already expired this throws an exception.
    *
    * @exception LeaseException if the expiry has been passed
    */
   public void cancel() throws LeaseException;

   /**
    * Determines the duration remaining before the lease expires. 
    * The expiry is given as the number of milliseconds remaining
    * before the lease expires. If this value is negative it 
    * should be assumed that the lease has already expired.
    *
    * @return the number of milliseconds remaining in the lease
    *
    * @exception LeaseException if the lease expiry has passed
    */
   public long getExpiry() throws LeaseException;

   /**
    * Provides the name of the resource that this lease represents.
    * This can be used to identify the resource should the need
    * arise. Also, this provides a convenient means of identifying
    * leases when using or storing it as an <code>Object</code>.
    *
    * @return this returns the name of the resource represented
    */
   public String toString();   
}

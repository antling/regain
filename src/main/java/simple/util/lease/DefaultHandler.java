/*
 * DefaultHandler.java May 2004
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
 * The <code>DefaultHandler</code> represents the default lease
 * management system. There are four actions permitted for leased
 * resources, including lease issuing, renewals, cancellation, and
 * querying. The actions are abstracted into a single interface
 * so that various lease management systems can be introduced.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.lease.LeaseManager
 */
class DefaultHandler implements LeaseHandler {

   /**
    * Performs the scheduling and updates for contracts.
    */
   private Processor handler;

   /**
    * Constructor for the <code>DefaultHandler</code> object. 
    * This creates a lease management system that can be used to
    * manage leases and perform notifications to the specified
    * <code>Cleaner</code> object when the issued lease expires.
    *
    * @param cleaner the cleaner that will receive notification
    */
   public DefaultHandler(Cleaner cleaner) {
      this.handler = new Processor(cleaner);
   }

   /**
    * This method will establish a lease for the named resource.
    * If the lease duration expires before it is renewed then a
    * notification is sent, to the issued <code>Cleaner</code>
    * implementation, to signify that the resource should be
    * released. The established lease can also be canceled.
    * 
    * @param name this is the name of the leased resource 
    * @param wait this is the duration of the new lease
    *
    * @exception LeaseException if the lease could not be done
    */
   public void lease(String name, long wait) {
      handler.lease(new Entry(name, wait));
   }

   /**
    * This ensures that the named resource is maintained for the
    * specified duration. The duration is in milliseconds so the
    * actual expiry time is the <code>System</code> time plus the
    * duration. The accuracy of this method will be exact.
    *
    * @param name this is the name of the resource to be renewed
    * @param wait this is the duration of time to renew for
    *
    * @exception LeaseException if the expiry has been passed
    */
   public void renew(String name, long wait) throws LeaseException{      
      handler.update(new Entry(name, wait));
   }

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
   public void cancel(String name) throws LeaseException{
      handler.update(new Entry(name));
   }

   /**
    * This will return the time that the lease will expire. The
    * expiry is given as the number of milliseconds remaining
    * before the lease expires. If this value is negative it 
    * should be assumed that the lease has already expired.
    *
    * @param name this is the name of the resource to query
    *
    * @return the number of milliseconds remaining in the lease
    *
    * @exception LeaseException if the lease expiry has passed
    */
   public long getExpiry(String name) throws LeaseException{
      return handler.lookup(name).getExpiry();
   }

   /**   
    * The provides the <code>Contract</code> implementation for
    * this lease management system. This implementation can be
    * used to perform lease renewals by specifying the duration
    * the contract is for, if the duration is not specified or
    * is less than or equal to zero then the lease will expire.
    *
    * @see simple.util.lease.Contract 
    */
   private class Entry implements Contract {

      /**
       * The name of the resource that this contract is for.
       */
      private String name;

      /**
       * This is the time that the contract expires.
       */
      private long expiry;

      /**
       * Constructor for the <code>Entry</code> object. This is
       * used to perform a cancellation of the lease. Since the
       * duration of the lease is not specified it is zero.
       *
       * @param name the resource that this contract is for
       */
      public Entry(String name) {
         this.expiry = System.currentTimeMillis();
         this.name = name;
      }

      /**
       * Constructor for the <code>Entry</code> object. This is
       * used to perform a renewal of the lease. The specified
       * time is the duration that this contract is valid for.
       *
       * @param name the resource that this contract is for
       * @param wait this is the duration this contract is for
       */
      public Entry(String name, long wait) {
         this.expiry = wait + System.currentTimeMillis();
         this.name = name;
      }

      /**
       * This returns the name of the resource this represents. 
       * This will be used to determine the equality of other
       * instances and is also to generate the has code for the
       * instance, which allows it to be stored in hash tables.
       *
       * @return returns the resource name that this represents
       */
      public String getName() {
         return name;
      }

      /**
       * This method will return the number of milliseconds that
       * remain in the contract. If the value returned is less 
       * than or equal to zero then it can be assumed that the 
       * lease has expired, if greater the lease is active.
       *
       * @return returns the duration in milliseconds remaining
       */
      public long getExpiry() {
         return expiry - System.currentTimeMillis(); 
      }

      /**
       * Determines whether the object is equal to the contract. 
       * A contract is equal to either a <code>Contract</code> 
       * with the same name or a string with the contract name.
       *
       * @param value the object to be compared for equality
       * 
       * @return true if the is or has the same resource name
       */
      public boolean equals(Object value) {
         if(value != null) {
            return value.equals(name);
         }
         return false;
      }

      /**
       * Returns the hash code generated for this instance. The
       * hash code for an instance must be the same as the hash
       * code generated by the resource name string. This is so
       * that contracts can be stored in hash containers.
       *
       * @return the hash code generated for this instance
       */
      public int hashCode() {
         return name.hashCode();
      }
   }
}

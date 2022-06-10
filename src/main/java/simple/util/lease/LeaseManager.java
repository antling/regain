/*
 * LeaseManager.java May 2004
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
 * The <code>LeaseManager</code> is used to issue a lease for a
 * named resource. This is effectively used to issue a request
 * for a named resource to be released when a lease has expired.
 * The use of a <code>Lease</code> simplifies the interface to
 * the notification and also enables other objects to manage the
 * lease without any knowledge of the resource it represents.
 *
 * @author Niall Gallagher
 */
public class LeaseManager {

   /**
    * This is the handler used to interface with the system.
    */
   private LeaseHandler handler;

   /**
    * Constructor for the <code>LeaseManager</code> object. This
    * instance is created using a specified notification handler.
    * The specified <code>Cleaner</code> will be notified when
    * the lease for a named resource expires, which will allow
    * the cleaner object to perform a clean up for that resource.
    *
    * @param cleaner the cleaner object receiving notifications
    */
   public LeaseManager(Cleaner cleaner) {
      this.handler = new DefaultHandler(cleaner);
   }

   /**
    * This method will issue a <code>Lease</code> object that
    * can be used to manage the release of a named resource. If
    * the lease duration expires before it is renewed then the 
    * notification is sent, typically to a <code>Cleaner</code>
    * implementation, to signify that the resource should be
    * recovered. The issued lease can also be canceled.
    * 
    * @param name this is the name of the leased resource 
    * @param duration this is the duration of the issued lease
    *
    * @return a lease that can be used to manage notification
    */
   public Lease lease(String name, long duration) {
      try {
         handler.lease(name, duration);
      } catch(LeaseException e) {
         return null;
      }
      return new DefaultLease(handler, name);
   }
}

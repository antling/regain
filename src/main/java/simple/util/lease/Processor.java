/*
 * Processor.java May 2004
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

import java.util.Hashtable;

/**
 * The <code>Processor</code> object provides the implementation of
 * the lease management system. This is responsible for issuing and
 * updating leases. In all, lease management requires two threads,
 * one to manage the scheduling of lease contracts and one to do 
 * the notifications when a lease expires. Once a lease has expired
 * this will send the notification to the <code>Cleaner</code>.
 *
 * @author Niall Gallagher
 */
final class Processor extends Thread {

   /**
    * This contains an ordered list of the expired contracts.
    */
   private MessageQueue ready;

   /**
    * Used to interface with the lease scheduling system
    */
   private Notifier pending;

   /**
    * Contains all the contracts that are currently active.
    */
   private Hashtable active;

   /**
    * Used to recieve notification of expired leases.
    */
   private Cleaner cleaner;

   /**
    * Constructor for the <code>Processor</code> object. This can
    * be used to issue, update, and expire leases. When a lease
    * expires notification is sent to the <code>Cleaner</code>
    * object provided. This allows an implementation independant
    * means to clean up once a specific lease has expired.
    *
    * @param cleaner this will recieve expiration notifications
    */
   public Processor(Cleaner cleaner) {
      this(cleaner, new MessageQueue());
   }

   /**
    * Constructor for the <code>Processor</code> object. This can
    * be used to issue, update, and expire leases. When a lease
    * expires notification is sent to the <code>Cleaner</code>
    * object provided. This allows an implementation independant
    * means to clean up once a specific lease has expired.
    *
    * @param cleaner this will recieve expiration notifications
    * @param ready this is used to gather the expired contracts
    */
   private Processor(Cleaner cleaner, MessageQueue ready){            
      this.pending = new Notifier(ready);
      this.active = new Hashtable();
      this.cleaner = cleaner;
      this.ready = ready;
      this.start();
   }

   /**
    * This method will establish a lease for the named resource.
    * If the lease duration expires before it is renewed then a
    * notification is sent, to the issued <code>Cleaner</code>
    * implementation, to signify that the resource should be
    * released. The established lease can also be canceled.
    * 
    * @param lease this is the contract that contains details
    */
   public synchronized void lease(Contract lease) {
      active.put(lease, lease);
      pending.update(lease);
   }

   /**
    * This ensures that the lease contract is maintained for the
    * contract duration. The duration is in milliseconds so the
    * actual expiry time is the <code>System</code> time plus the
    * duration. The accuracy of this method will be exact.
    *
    * @param lease this is the contract that contains details
    *
    * @exception LeaseException if the expiry has been passed
    */
   public synchronized void update(Contract lease) throws LeaseException {
      if(!active.containsKey(lease)){
         throw new LeaseException("Lease expiry");
      }
      active.put(lease, lease);
      pending.update(lease);      
   }

   /**
    * This is used to acquire an active <code>Contract</code>. If
    * the named contract has expired before this is invoked then
    * this will throw an exception to indicate the expiry. This
    * is used to query the details of the lease like its expiry. 
    *
    * @param name this is the name of the contract to acquire
    *
    * @exception LeaseException if the expiry has been passed
    */
   public synchronized Contract lookup(String name) throws LeaseException {
      if(!active.containsKey(name)) {
         throw new LeaseException("Lease expiry");
      }
      return (Contract)active.get(name);
   }

   /**
    * Once a lease duration has expired this method is invoked. 
    * This will determine whether the lease contract specified 
    * has been updated since the notification was generated, if
    * the lease contract has been updated in the meantime then
    * notification is not sent and the issued lease is ignored.
    *
    * @param lease this is the contract that contains details    
    */
   private synchronized void expire(Contract lease) {
      String name = lease.getName();
      Object value = active.get(name);

      if(lease == value) {
         active.remove(name);
         cleaner.clean(name);
      }
   }   

   /**
    * This acquires expired lease contracts from the ready queue
    * and attempts to generate an expiry notification. This thread
    * is used so that a deadlock situation is avoided. This also 
    * has an added benifit of seperating the processing of leases
    * and the generation of notifications, which can be time
    * consuming if the <code>Cleaner.clean</code> is implemented
    * is such a way. This should spend most of its time sleeping.
    */
   public void run() {
      while(true){
         try {
            Object top = ready.dequeue();
            expire((Contract)top);
         }catch(InterruptedException e){
            continue;
         }   
      }
   }

   /**
    * The <code>Notifier</code> abstracts the scheduling system 
    * to a single object. This is used to gather the scheduling
    * and notification functionality together. Updating lease
    * contracts can be done using the <code>update</code> method
    * and when a contract has expired it is enqueued into the
    * provided queue so that it can be processed asynchronously. 
    *
    * @see simple.util.lease.Monitor
    */
   private class Notifier implements Monitor {

      /**
       * Performs the actual scheduling of lease contracts.
       */
      private LeaseScheduler pending;
   
      /**
       * Used to enqueue lease contracts that have expired.
       */
      private MessageQueue ready;

      /**
       * Constructor for the <code>Notifier</code> object. This
       * will use the provided queue to perform notification of
       * expired lease contracts. This avoids a situation where
       * deadlock could occur between the communicating threads.
       *
       * @param ready this is used to enqueue expired contracts
       */
      public Notifier(MessageQueue ready) {
         this.pending = new LeaseScheduler(this);
         this.ready = ready;
      }

      /**
       * This schedules the contract for the contract duration. 
       * If an update is made for a contract more that once then
       * the contract is simply updated. When the duration of
       * the contract expires the scheduler will notify this
       * object using the <code>expire</code> method.
       *
       * @param lease this is the lease contract to be updated
       */
      public void update(Contract lease){
         pending.update(lease);
      }

      /**
       * This is invoked by the <code>LeaseScheduler</code> to
       * signify that the contract duration has expired. Expired
       * contracts are enqueued in such a way to avoid deadlock,
       * which can occur between the two communicating threads.
       *
       * @param lease this is the contract that has expired
       */
      public void expire(Contract lease) {
         ready.enqueue(lease);
      }
   }
}

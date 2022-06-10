/*
 * Monitor.java May 2004
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
 * A <code>Monitor</code> object is used to receive notification
 * when a scheduled contract has expired. This acts as a monitor
 * to determine whether the <code>Cleaner</code> should actually
 * be given notification. In a concurrent setting the messages
 * issued by lease holders and the scheduling system must be 
 * synchronized in such a way that a renewal is atomic. So the
 * monitor is used to determine whether incoming renewals are to
 * be accepted or rejected based on expired contracts.
 *
 * @author Niall Gallagher
 */
interface Monitor {

   /**
    * The method determines if the <code>Cleaner</code> should
    * be given notification. Organization is required so that
    * if a lease renewal was requested at the time of this
    * notification that only one event should succeed, so the
    * lease should expire before the renewal or the renewal
    * must succeed and notification must be postponed.
    *
    * @param lease this is a contract for a leased resource
    */
   public void expire(Contract lease);
}

/*
 * Cleaner.java May 2004
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
 * The <code>Cleaner</code> represents an object that is used to
 * clean up after the named resource. Typically this is used when
 * a <code>Lease</code> referring a resource has expired meaning
 * that any memory, file descriptors, or other such limited data
 * should be released for the named resource. The resource names
 * used should be distinct over time to avoid conflicts.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.lease.Lease
 */
public interface Cleaner {

   /**
    * This method is used to clean up after a the named resource.
    * To ensure that the leasing infrastructure operates properly
    * this should not block releasing resources. If required this
    * should spawn a thread to perform time consuming tasks.    
    *
    * @param name this is the name of the resource to clean
    */
   public void clean(String name);
}

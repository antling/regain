/*
 * ProcessPermission.java January 2003
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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
 
package simple.util.process;

import java.security.BasicPermission;

/**
 * The <code>ProcessPermission</code> is used to provide access 
 * to the <code>ProcessQueue</code>. This will grant permission
 * to use the <code>getInstance</code> method, which in effect
 * will provide access to all the methods of the process queue.
 * The permission that is required is the "execute" permission.
 * <p>
 * This is required because the <code>ProcessQueue</code> is a
 * singleton instance, so if any foreign code is loaded it must
 * have permissions before using <code>execute</code> on the
 * <code>ProcessQueue</code>, which can cause deadlock.
 *
 * @author Niall Gallagher
 */
public final class ProcessPermission extends BasicPermission{

   /**
    * Constructor for the <code>LoaderPermission</code> requires
    * an action string. The action that can be granted by this
    * is the "execute" action. This can be used within security
    * policy files as it follows the same naming scheme as the
    * <code>BasicPermission</code> object. 
    *
    * @param action this is the action that is to be granted
    */
   public ProcessPermission(String action){
      super(action);
   }
}

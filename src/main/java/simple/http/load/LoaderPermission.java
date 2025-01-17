/*
 * LoaderPermission.java September 2002
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
 
package simple.http.load;

import java.security.BasicPermission;

/**
 * The <code>LoaderPermission</code> is used to provide access to
 * the functions of the <code>LoaderManager</code>. This will
 * grant permission to use the methods provided the correct
 * actions strings are given. The permissions that can be granted
 * are the "load", "update", and "link" actions. 
 * <p>
 * This is required because the <code>LoaderManager</code> is 
 * given to the <code>Service</code> instances on creation. This
 * ensures that loaded code can be restricted from changing the
 * configuration of the <code>LoaderManager</code>.
 *
 * @author Niall Gallagher
 */
public final class LoaderPermission extends BasicPermission{

   /**
    * Constructor fot the <code>LoaderPermission</code> requires
    * an action string. The actions that can be granted are the 
    * "load", "update" and, "link" actions. This can be used 
    * within security policy files as it follows the same naming
    * scheme as the <code>BasicPermission</code>. 
    *
    * @param action this is the action that is to be granted
    */
   public LoaderPermission(String action){
      super(action);
   }
}

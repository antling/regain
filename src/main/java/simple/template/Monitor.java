/*
 * Monitor.java December 2003
 *
 * Copyright (C) 2003, Niall Gallagher <niallg@users.sf.net>
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

package simple.template;

import java.io.File;

/**
 * The <code>Monitor</code> is used by the <code>Adapter</code> to
 * translate the updates from a mapper engine. This enables objects
 * to receive adapted updates from the <code>MapperEngine</code>. 
 * The adapted update provides the names of loaded services and the
 * paths within the current file system of the class byte codes.
 *
 * @author Niall Gallagher
 */
interface Monitor {

   /**
    * This is an adapted update used by the <code>Adapter</code> 
    * to convey file system specific information regarding the
    * service implementations loaded. The two lists issued to an
    * implementation of this contain descriptive information in
    * parallel indexes regarding the service names and paths.
    *
    * @param name the list of names for loaded service instances
    * @param file the list of files that correspond to the names
    */
   public void update(String[] name, File[] file); 
}

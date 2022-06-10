/*
 * Reference.java May 2003
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
 
package simple.page;

import simple.util.net.Parameters;

/**
 * The <code>Reference</code> object encapuslates all information
 * contained within an include statement. The parameters can
 * be extracted using the methods of the <code>Parameters</code>
 * interface. The <code>getContentPath</code> method reveals the
 * source referenced within the include statement. 
 *
 * @author Niall Gallagher
 */
public interface Reference extends Parameters {

   /**
    * This returns the content path that is referenced within 
    * the &lt;include&gt; statement. The content path can be
    * used to acquire a <code>Content</code> object.
    *
    * @return this returns the path that is referenced
    */
   public String getContentPath();
}

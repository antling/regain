/*
 * Database.java December 2003
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

import java.util.Set;

/**
 * The <code>Database</code> interface is used to represent a source
 * of data for a template. It also serves the purpose of providing 
 * a means to to share data between controllers. The database object
 * contains a subset of the methods provided by a <code>Map</code>.
 * 
 * @author Niall Gallagher   
 */
public interface Database {

   /**
    * The <code>put</code> method is used to insert a mapping in
    * the database that pairs the issued name with the issued
    * value. The value can be referenced in future by its name.
    *
    * @param name this is the name of the value being inserted
    * @param value this is the named value that is inserted
    */
   public void put(String name, Object value);

   /**
    * The <code>get</code> method is used to retrieve the value
    * mapped to the specified name. If a value does not exist
    * matching the given name, then this returns null.
    * 
    * @param name this is the name of the value to be retrieved
    *  
    * @return returns the value if it exists or null otherwise
    */
   public Object get(String name);

   /**
    * The <code>remove</code> method is used to remove the 
    * named value from the database. This method either removes
    * the value or returns silently if the name does not exits.
    *
    * @param name this is the name of the value to be removed
    */
   public void remove(String name);

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into thhis database can be retrieved with 
    * this method. This will return a <code>Set</code> that 
    * contains the names of all the mappings added to this.
    *
    * @return this returns all the keys for existing mappings
    */
   public Set keySet();

   /**
    * The <code>contains</code> method is used to determine if
    * a mapping exists for the given name. This returns true if
    * the mapping exists or false otherwise.
    *
    * @param name this is the name of the mapping to determine
    *
    * @return returns true if a mapping exists, false otherwise
    */
   public boolean contains(String name);   
}

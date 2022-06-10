/*
 * PlainDatabase.java January 2004
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

package simple.template.velocity;

import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import simple.template.Database;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>PlainDatabase</code> provides an adapter database
 * implementation for the <code>Context</code> object. This will
 * be unsynchronized and delegates to a <code>Context</code>
 * object internally, which can be specified on instantiation. 
 *
 * @author Niall Gallagher
 */
class PlainDatabase implements Database {

   /**
    * The internal storage used by this instance.
    */
   protected Context context;
   
   /**
    * Constructor for the <code>PlainDatabase</code> object. This
    * will create a database without any specific inner storage. 
    * This should be used by subclasses that wish to specify the
    * storage that is used by the instance.
    */
   protected PlainDatabase() {
   }
   
   /**
    * Constructor for the <code>PlainDatabase</code> object. This
    * will create a database that uses the specified context as
    * its internal storage. This should be used as an adapter for
    * the <code>org.apache.velocity.context.Context</code> object.
    *
    * @param context this contains a set of mappings to be used
    */
   public PlainDatabase(Context context) {
      this.context = context;
   }
   
   /**
    * The <code>put</code> method is used to insert a mapping in
    * the database that pairs the issued name with the issued
    * value. The value can be referenced in future by its name.
    * Properties set with this method will be set within this
    * instances context object, which is available to subclasses.
    *
    * @param name this is the name of the value being inserted
    * @param value this is the named value that is inserted
    */
   public void put(String name, Object value) {
      context.put(name, value);
   }

   /**
    * The <code>get</code> method is used to retrieve the value
    * mapped to the specified name. If a value does not exist
    * matching the given name, then this returns null. All the 
    * property values retrieved by this method are obtained 
    * from an internal context that is accessible to subclasses.
    * 
    * @param name this is the name of the value to be retrieved
    *  
    * @return returns the value if it exists or null otherwise
    */
   public Object get(String name) {
      return context.get(name);
   }

   /**
    * The <code>remove</code> method is used to remove the 
    * mapping from the database. This method either removes
    * the value or returns silently if the name does not exist.
    * This removes the mapping from the internal context.
    *
    * @param name this is the name of the value to be removed
    */
   public void remove(String name) {
      context.remove(name);
   }

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into this database can be retrieved with 
    * this method. This will return a <code>Set</code> that 
    * contains the names of all the mappings added to this.
    *
    * @return this returns all the keys for existing mappings
    */
   public Set keySet(){
      return keySet(context.getKeys());
   }

   /**
    * To ascertain what mappings exist, the names of all values
    * previously put into this database can be retrieved with 
    * this method. This will return a <code>Set</code> that 
    * contains the names of all the mappings added to this.
    *
    * @param list this is the list of keys used by the context
    *
    * @return this returns the the keys for existing mappings
    */
   private Set keySet(Object[] list) {
      Set set = new HashSet();
      
      for(int i = 0; i < list.length; i++){
         set.add(list[i]);
      }
      return set;
   }

   /**
    * The <code>contains</code> method is used to determine if
    * a mapping exists for the given name. This returns true if
    * the mapping exists or false otherwise. 
    *
    * @param name this is the name of the mapping to determine
    *
    * @return returns true if a mapping exists, false otherwise
    */
   public boolean contains(String name) {
      return context.containsKey(name);
   } 
}

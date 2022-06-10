/*
 * Attributes.java February 2001
 *
 * Copyright (C) 2001, Niall Gallagher <niallg@users.sf.net>
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
 
package simple.http;

import java.util.Enumeration;
import java.net.InetAddress;

/**
 * This is an <code>Attributes</code> object which is used to convey 
 * attributes for a given connection. This is used to describe any 
 * specific attributes that a connection may have, for example any 
 * security policy etc. It also allows the identity of the connection 
 * to be known via the I.P.
 * <p>
 * This is used by the <code>Request</code> and <code>Response</code>
 * objects to convey information to the <code>ProtocolHandler</code>.
 * The attributes should be set when the <code>Pipeline</code> is
 * created. The methods of this MUST be synchronized so that it can 
 * be used concurrently by multiple threads.
 *
 * @author Niall Gallagher
 */ 
public interface Attributes {

   /**
    * This will retrive an attribute from this <code>Attributes</code> 
    * class. The attributes in an <code>Attributes</code> object are 
    * identified by a <code>String</code> name. If the attribute 
    * specified by name does not exist then this returns 
    * <code>null</code>, otherwise the correct object.
    *
    * @param name the name of the attribute to be retrived
    *
    * @return the attribute that is referenced by the name
    */ 
   public Object getAttribute(String name);

   /**
    * This can be used to determine if an attribute of a given name
    * is present in the <code>Attributes</code> object. If there is 
    * concurrent access to this object then then this may not be 
    * accurate when the <code>getAttribute</code> method is invoked, 
    * for this reason the return value for get should always be 
    * checked to see if its <code>null</code>.
    *
    * @param name the name of the attribute the is being queried
    *
    * @return true if the attribute exists, false if it does not
    */ 
   public boolean hasAttribute(String name);

   /**
    * This corrosponds to the identity of the host that created
    * the connection. The domain name can be retrived from this.
    * The <code>InetAddress</code> can convey all information 
    * on the source of the <code>Attributes</code> object.
    *
    * @return the identity of the peer that created connection
    */ 
   public InetAddress getInetAddress();

   /**
    * This removes the attribute from this object. This may cause 
    * any <code>Enumeration</code> of the names to become stale.
    *
    * @param name the name of the attribute to be removed
    */ 
   public void removeAttribute(String name);

   /**
    * This can be used to set an attribute. The attribute will
    * contain the name specified. This may not update any existing
    * <code>Enumeration</code> of the attribute names that was
    * previously retrived.
    *
    * @param name the name of the attribute that is being added
    * @param obj the value of the attribute that is being added
    */ 
   public void setAttribute(String name, Object obj);

   /**
    * This will provide an <code>Enumeration</code> object for the
    * attribute names for this object. This may not update if there 
    * are concurrent remove and set operations when this is used.
    *
    * @return an <code>Enumeration</code> of the names of the
    * attributes
    */ 
   public Enumeration getAttributeNames();
}

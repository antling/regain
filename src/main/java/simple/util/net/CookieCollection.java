/*
 * CookieCollection.java February 2001
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
 
package simple.util.net;

import java.io.Serializable;

/**
 * The <code>CookieCollection</code> object is used to represent
 * a collection of one or more <code>Cookie</code>'s. This
 * collection of cookies iterates through the set of cookies 
 * using the <code>hasMore</code> and <code>next</code> methods.
 * The <code>reset</code> method is used to start from the first
 * cookie. This allows the collection to be used again.
 * <p>
 * The <code>CookieCollection</code> is <code>Serializable</code>
 * so it can be stored. This allows the user to keep the cookies
 * from the list for permenant storage. This does not extend the
 * <tt>Collection</tt> interface because it is a simple iterator
 * that is used with the <tt>CookieParser</tt> object.
 *
 * @author Niall Gallagher
 */
public interface CookieCollection extends Serializable {   

   /**
    * This is used to determine wheather or not there are any
    * more <code>Cookies</code> left in the collection. If this
    * returns false the collection may be reiterated through 
    * using the <code>reset</code> method. If this returns true
    * then the <code>next</code> method will produce a valid
    * <code>Cookie</code> object.
    *
    * @return this returns true if there are any more left
    */
   public boolean hasMore();

   /**
    * The <code>next</code> method is used to retrive the next
    * <code>Cookie</code> in the list. This guarantees to return
    * a valid <code>Cookie</code> if the <code>hasMore</code>
    * method returns true.
    *
    * @return this returns a valid <code>Cookie</code> object 
     * if the <code>hasMore</code> method returns true
    */
   public Cookie next();

   /**
    * This is used so that the collection of <code>Cookies</code>
    * can be reiterated. This allows the collection to be reused.    
    */
   public void reset();
}

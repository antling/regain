/*
 * Header.java February 2001
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

/**
 * This is a header object that can be used to represent a HTTP header. 
 * The <code>Header</code> is represented by a name and value pair. 
 * This also allows the name of the <code>Header</code> to be determined 
 * in a case insensitive manner. The <code>Header</code> also provides a 
 * <code>toString</code> method which converts the name/value pair to a 
 * <code>String</code>.
 *
 * @author Niall Gallagher
 */ 
interface Header {        
   
   /**
    * This is used to retrive the value of the <code>Header</code>.
    *
    * @return the value that this <code>Header</code> contains
    */ 
   public String getValue();   
   
   /**
    * This is used to retrive the name of the <code>Header</code>.
    *
    * @return the name that this <code>Header</code> contains
    */ 
   public String getName();
   
   /**
    * This allows the name of the <code>Header</code> to be compared.
    * The comparson of the <code>String</code>s is done in a case
    * insensitive manner, as HTTP/1.1 standard suggests.
    *
    * @param str the <code>String</code> to compare with the name
    *
    * @return true if the name matches the value given
    */ 
   public boolean nameMatches(String str);
   
   /**
    * This returns the "name: value" <code>String</code> that
    * corrosponds to the name and value of this <code>Header</code>
    * exactly as it is.
    *
    * @return the <code>String</code> value of this
    * <code>Header</code> as a HTTP header
    */ 
   public String toString();
}


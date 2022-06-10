/*
 * PlainHeader.java February 2001
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
 * This is a <code>Header</code> that can be used to represent 
 * a HTTP header. The header is represented by a name and value 
 * pair. This also allows the name of the header to be determined 
 * in a case insensitive manner. The header object also provides 
 * a <code>toString</code> method which converts the name/value 
 * pair to a <code>String</code>. This is a read only object.
 *
 * @author Niall Gallagher
 */ 
class PlainHeader implements Header {   

   /**
    * This is a cache of the <code>toString</code>.
    */
   private String str;

   /**
    * This is the name of the HTTP header.
    */
   private String name;
   
   /**
    * This is the value that this header has. 
    */
   private String value;

   /**
    * This is a copy constructor that will enable a generic
    * <code>Header</code> implementation to be synchronized
    * so it can be accessed safely by several threads.
    *
    * @param head this is the <code>Header</code> object that
    * will be synchronized
    */
   public PlainHeader(Header head) {
      this(head.getName(),head.getValue());
   }

   /**
    * This will create a <code>PlainHeader</code> object that 
    * is an implementation of <code>Header</code>, this is a 
    * read-only object.
    *
    * @param name the name of the <code>header</code> object
    * @param value the value that this <code>header</code> has
    */ 
   public PlainHeader(String name, String value){
      this.name = name;
      this.value = value;
   }

   /**
    * This is used to retrive the value of the header.
    *
    * @return the value that this header contains
    */ 
   public String getValue(){
      return value;
   }

   /**
    * This is used to retrive the name of the header.
    *
    * @return the name that this header contains
    */ 
   public String getName(){
      return name;
   }

   /**
    * This allows the name of the header to be compared. The 
    * comparson of the <code>String</code>'s is done in a case
    * insensitive manner, as HTTP/1.1 standard suggests.
    *
    * @param name the <code>String</code> to compare with
    *
    * @return true if the name matches the value given
    */ 
   public boolean nameMatches(String name) {
      if(name == null) return false;
      return this.name.equalsIgnoreCase(name);
   }

   /**
    * This returns the "name: value" <code>String</code> that 
    * corrosponds to the name and value of this header as it is.
    *
    * @return the <code>String</code> value of this header
    */ 
   public synchronized String toString(){
      if(str == null){
         str = name + ": "+value;
      }
      return str;
   }
}

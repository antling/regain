/*
 * GenericHeader.java February 2001
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
 * This is a <code>GenericHeader</code> object that can be used
 * to represent the headers that can be found in a HTTP message 
 * header. This can be used to access the HTTP message headers
 * conveniently. 
 * <p>
 * This does not include the request and status line of a request 
 * or response. The <code>GenericHeader</code> is used to represent
 * the set of headers of the form "Header-Name: header-value".
 *
 * @author Niall Gallagher
 */ 
public interface GenericHeader {
   
   /**
    * This can be used to determine how many HTTP message headers
    * this object contains. The <code>headerCount</code> represents 
    * the number of individual HTTP message headers that this has.
    *
    * @return returns the number of HTTP message headers this has
    */ 
   public int headerCount();
   
   /**
    * This can be used to find the first occurrence of the specified 
    * HTTP message header. This will search through the list of 
    * HTTP message headers that this contains and when it encounters 
    * a HTTP message header with the name specified it returns the 
    * index of that HTTP message header. The index will change when 
    * a remove is used. So the index is valid only for the until the 
    * next remove method or possible the next add method.
    *
    * @param name name of the HTTP message header being searched for
    *
    * @return returns the position of the first HTTP message header
    */ 
   public int indexOf(String name);
   
   /**
    * This can be used to find the first occurrence of the specified 
    * HTTP message header from a given index. This will search through 
    * the list of HTTP message headers that occur after the index. 
    * When it encounters a HTTP message header with the name specified 
    * it returns the index of that HTTP message header. The index will 
    * change when a remove is used. So the index is valid only until a 
    * remove or add method is used.
    *
    * @param name name of the HTTP message header being searched for
    * @param from the index from which the search will start
    *
    * @return this returns the position of the HTTP message header
    */ 
   public int indexOf(String name, int from);   
      
   /**
    * This can be used to add a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */ 
   public void add(String name, String value);
   
   /**
    * This can be used to set a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.    
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */    
   public void set(String name, String value);

   /**
    * This can be used to add a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */    
   public void add(String name, int value);

   /**
    * This can be used to set a HTTP message header to this object.
    * The name and value of the HTTP message header will be used to
    * create a HTTP message header object which can be retrieved using
    * the <code>indexOf</code> in combination with the get methods.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.       
    *
    * @param name the name of the HTTP message header to be added
    * @param value the value the HTTP message header will have
    */ 
   public void set(String name, int value);
   
   /**
    * This is used as a convenience method for adding a header that
    * needs to be parsed into a HTTP-date string. This will convert
    * the date given into a date string defined in RFC 2616 sec 3.3.1.
    *
    * @param name the name of the HTTP message header to be added
    * @param date the value the HTTP message header will have when
    * parsed into RFC 1123 format
    */
   public void addDate(String name, long date);

   /**
    * This is used as a convenience method for setting a header that
    * needs to be parsed into a HTTP-date string. This will convert
    * the date given into a date string defined in RFC 2616 sec 3.3.1.
    * This will perform a <code>removeAll</code> using the issued
    * header name before the header value is set.
    *
    * @param name the name of the HTTP message header to be added
    * @param date the value the HTTP message header will have when
    * parsed into RFC 1123 format
    */
   public void setDate(String name, long date);
   
   /**
    * This can be used to remove the HTTP message header at the
    * specified index. This will invalidate any value received by 
    * an <code>indexOf</code> method previous to this. If the 
    * index specified is not valid then an 
    * <code>IndexOutOfBoundsException</code> may be thrown.
    *
    * @param off index of the HTTP message header to be removed
    */ 
   public void remove(int off);

   /**
    * This can be used to remove all HTTP message headers with the
    * specified name. This will search through the list of HTTP 
    * message header an remove the HTTP message headers from the 
    * list. This will invalidate any previous indexes received.
    *
    * @param name name of the message headers to be removed
    */ 
   public void removeAll(String name);   
   
   /**
    * This is used to get the text value of the HTTP message header
    * at the specified index. This is a convenience method that 
    * avoids having to deal with a HTTP message header object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the HTTP message header value 
    *
    * @return this returns the text value value that the header
    */ 
   public String getValue(int off);

   /**
    * This is used to get the name value of the HTTP message header
    * at the specified index. This is used in conjunction with the
    * <code>getValue(int)</code> method so that the contents of the 
    * HTTP message header can be fully examined.
    *
    * @param off the offset of the HTTP message header name value 
    *
    * @return this returns the name of the header at that index 
    */
   public String getName(int off);
   
   /**
    * This can be used to get the value of the HTTP message header 
    * at the specified index. This is a convenience method that 
    * avoids having to deal with a HTTP message header object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the date HTTP message header value 
    *
    * @return this returns the date as a long from the parsed value 
    * of that HTTP message header
    */
   public long getDate(int off);

   /**
    * This can be used to get the value of the first message header
    * that has the specified name. This is a convenience method that 
    * avoids having to deal with a HTTP message header object and
    * the <code>indexOf</code> methods. This returns null if theres
    * not a HTTP message header.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns the value that the HTTP message header
    */ 
   public String getValue(String name);
   
   /**
    * This can be used to get the values of HTTP message headers
    * that have the specified name. This is a convenience method that 
    * will present that values as tokens extracted from the header.
    * This has obvious performance benifits as it avoids having to 
    * deal with <code>substring</code> and <code>trim</code> calls.
    * <p>
    * The tokens returned by this method are ordered according to
    * there HTTP quality values, or "q" values, see RFC 2616 section
    * 3.9. This also strips out the quality parameter from tokens
    * returned. So "image/html; q=0.9" results in "image/html". If
    * there are no "q" values present then order is by appearence.
    * <p> 
    * The result from this is either the trimmed header value, that
    * is, the header value with no leading or trailing whitespace
    * or an array of trimmed tokens ordered with the most preferred
    * in the lower indexes, so index 0 is has higest preference.
    *
    * @param name the name of the headers that are to be retrieved
    *
    * @return ordered array of tokens extracted from the header(s)
    */ 
   public String[] getValues(String name);

   /**
    * This can be used to get the date of the first message header
    * that has the specified name. This is a convenience method that 
    * avoids having to deal with parsing the value of the requested
    * HTTP message header. This also avoids having to deal with the
    * <code>indexOf</code> methods. This returns -1 if theres not a
    * HTTP message header.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns the date as a long from the parsed value 
    * of that HTTP message header
    */ 
   public long getDate(String name);   

   /**
    * This is used to see if there is a HTTP message header with the
    * given name in this container. If there is a HTTP message header
    * with the specified name then this returns true otherwise false.
    *
    * @param name the HTTP message header to get the value from
    *
    * @return this returns true if the HTTP message header exists
    */  
   public boolean contains(String name);

   /**
    * This is used to clear all HTTP message headers from the 
    * message header. This will leave no data remaining, i.e.
    * <code>headerCount</code> is zero after this method is 
    * invoked, this is a convenience method.
    */ 
   public void clear();
}

/*
 * Profile.java November 2002
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
 
package simple.http;

import simple.util.net.Cookie;

/**
 * The <code>Profile</code> is used by the <code>Request</code> to
 * examine the cookies that have been issued with the HTTP request.
 * The purpose of this interface is to provide implementations of
 * the <code>ProtocolHandler</code> with a simple HTTP state control
 * mechanism. This provides access to the cookies issued using the
 * <code>simple.http.net.Cookie</code> object.
 * <p>
 * The HTTP State Management Mechanism, RFC 2109, specifies Cookie 
 * and Set-Cookie headers that can be used by HTTP servers to manage
 * state across multiple requests. The <code>Profile</code> object
 * abstracts this by setting up a context between the 
 * <code>Request</code> and <code>Response</code>. The various 
 * <code>setCookie</code> methods enable the <code>Profile</code> 
 * to be used to add Set-Cookie headers to the HTTP response header 
 * once it commits and cookie objects can be examined using the
 * <code>getCookie</code> and <code>getValue</code> methods.
 *
 * @author Niall Gallagher
 */
public interface Profile {

   /**
    * The <code>setCookie</code> method is used to set a cookie value 
    * with the cookie name. This will add a cookie to the profile 
    * stored under the <code>Cookie.getName</code> value. Once the 
    * <code>Response</code> object commits then the header will be
    * given to the client with the Set-Cookie header.  
    *
    * @param cookie this is the <code>simple.util.net.Cookie</code>
    * that is to be used in a Set-Cookie response header
    */
   public void setCookie(Cookie cookie);

   /**
    * The <code>setCookie</code> method is used to set a cookie value 
    * for a specified name. This will add a cookie to the profile 
    * stored under the specified name as a <code>Cookie</code> object. 
    * Once the <code>Response</code> object commits then the header 
    * will be given to the client with the Set-Cookie header. This is 
    * a convienience method that avoids having to create a cookie.
    *
    * @param name the name of the cookie to be given to the client
    * @param value the value that the cookie is to be given    
    */
   public void setCookie(String name, String value);

   /**
    * The <code>setCookie</code> method is used to set a cookie value 
    * for a specified name. This will add a cookie to the profile 
    * stored under the specified name as a <code>Cookie</code> object. 
    * Once the <code>Response</code> object commits then the header 
    * will be given to the client with the Set-Cookie header. This is 
    * a convienience method that avoids having to create a cookie.
    *
    * @param name the name of the cookie to be given to the client
    * @param value the value that the cookie is to be given
    * @param path specifies the path the cookie object is to have
    */
   public void setCookie(String name, String value, String path);

   /**
    * This returns the <code>Cookie</code> object stored under the
    * specified name. This is used to retieve cookies that have 
    * been issued with the HTTP request and set using the
    * <code>setCookie</code> methods. 
    *
    * @param name this is the name of the cookie to be retreived
    * 
    * @return returns the <code>Cookie</code> by the given name
    */
   public Cookie getCookie(String name);

   /**
    * The <code>getSetCookies</code> method is used to retrieve the
    * cookies that have been added to the profile. This is useful
    * so that the <code>ProtocolHandler</code> can determine the
    * cookies add using the <code>setCookie</code> methods. The 
    * cookies returned from this method are the only cookies sent
    * to the HTTP client using the Set-Cookie header.
    *
    * @return this returns an array of <code>Cookie</code> with 
    * the cookies set with this instance    
    */
   public Cookie[] getSetCookies();

   /**
    * The <code>getCookies</code> method returns all cookies that
    * exist within the profile. The cookies returned include all
    * cookies retreived from the HTTP request header and all
    * cookies set using the current <code>Profile</code> instance.
    *
    * @return this returns an array of <code>Cookie</code> of all
    * cookies within the current profile    
    */
   public Cookie[] getCookies();

   /**
    * This is used to retreive the value of the <code>Cookie</code>
    * stored under the specified name. This is a convienience 
    * method that avoids having to deal with the cookie object
    * directly. If the cookis does not exist ths returns null.
    *
    * @param name this is the name of the cookie to be retreived
    * 
    * @return the valie of the specified <code>Cookie</code>
    */
   public String getValue(String name);

   /**
    * This method removes and returns the removed cookie object
    * from the <code>Profile</code>. If the a <code>Cookie</code>
    * by the specified name does not exist then null is returned.
    *
    * @param name this name of the cookie that is to be removed
    *    
    * @return the <code>Cookie</code> instance removed from this
    * profile or null is it did not exist.    
    */
   public Cookie remove(String name);

   /**
    * Checks to see if the cookie of the specified name exists 
    * in the <code>Profile</code>.  
    *
    * @param name the name of the <code>Cookie</code> object
    *
    * @return this returns true of the specified cookie exists 
    */
   public boolean contains(String name);
   
   /**
    * Checks to see if the <code>Profile</code> is empty. If empty
    * this represents a new client or a browser that does not
    * accept Set-Cookie headers for privacy.
    *
    * @return this returns true if there are no cookies
    */ 
   public boolean isEmpty();
   
   /**
    * Returns the number of cookies that exist within the profile.
    * If the <code>size</code> is zero this <code>isEmpty</code>.
    *
    * @return the number of cookies within the profile
    */   
   public int size();
   
   /**
    * Removes all cookies from the profile. This is useful if 
    * the HTTP response does not want to send any cookies.
    */
   public void clear();
}

/*
 * Cookie.java February 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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
 * The <code>ProfileCookie</code> is used to provide a means for a
 * callback to be made to the source <code>Profile</code> when a
 * property has changed in the cookie. This is used to reduce the
 * effort required to make quick changes to existing cookies. So
 * rather than having to acquire the instance, make the change,
 * and finally set the cookie, this allows the cookie to be set
 * in such a way that the <code>Profile</code> is automatically
 * updated. This means changes in any cookie sent with the request
 * will be sent as a Set-Cookie header with the response. 
 * 
 * @author Niall Gallagher
 */ 
class ProfileCookie extends Cookie {

   /**
    * Represents the source profile this instance uses.
    */
   private Profile profile;

   /**
    * Represents the cookie instance within the profile.
    */
   private Cookie cookie;

   /** 
    * Constructor of the <code>ProfileCookie</code> object. This
    * acts as a proxy object for a <code>Cookie</code> instance 
    * obtained from the issued <code>Profile</code> source.
    *
    * @param profile the source profile of this cookie instance
    * @param cookie the cookie object that will be maintained
    */
   public ProfileCookie(Profile profile, Cookie cookie){
      this.profile = profile;
      this.cookie = cookie;
   }
   
   /** 
    * This returns the version for this cookie. This acts as a
    * wrapper for the internal instance, and invokes the same
    * method of the source <code>Cookie</code> instance.
    *
    * @return this returns the version number for the cookie
    */
   public int getVersion() {
      return cookie.getVersion();
   }

   /** 
    * This enables the version of the <code>Cookie</code> to be
    * set. This acts as a wrapper for the internal instance, and
    * invokes the <code>setVersion</code> method of the internal 
    * cookie. The change in the cookie version requires that 
    * the <code>Profile</code> object is updated.
    *
    * @param version this is the version number for the cookie
    */
   public void setVersion(int version) {
      profile.setCookie(cookie);
      cookie.setVersion(version);
   }   
   
   /** 
    * This returns the name for this cookie. This acts as a
    * wrapper for the internal instance, and invokes the same
    * method of the source <code>Cookie</code> instance.
    *
    * @return the nanme obtained from the internal instance
    */
   public String getName() {
      return cookie.getName();
   }

   /** 
    * This returns the value for this cookie. This acts as a
    * wrapper for the internal instance, and invokes the same
    * method of the source <code>Cookie</code> instance.
    *
    * @return the value obtained from the internal instance
    */
   public String getValue() {
      return cookie.getValue();
   }

   /**
    * This enables the value of the cookie to be changed. This
    * acts as a wrapper for the internal instance, and invokes
    * the <code>getValue</code> method of that object. This
    * will update the <code>Profile</code> object to ensure 
    * it is sent as a Set-Cookie header in the response.
    *
    * @param value this is the new value of the cookie object
    */    
   public void setValue(String value) {
      profile.setCookie(cookie);
      cookie.setValue(value);
   }

   /**
    * This determines whether the cookie is secure. This acts 
    * as a wrapper for the internal instance, and invokes the 
    * same method of the source <code>Cookie</code> instance.
    *
    * @return this returns true if the "secure" token is set
    */
   public boolean getSecure() {
      return cookie.getSecure();
   }

   /**
    * This is used to determine if the client browser should send
    * this cookie over a secure protocol. This acts as a wrapper 
    * for the internal instance, and invokes the same method of
    * the internal <code>Cookie</code> object. This will update 
    * the <code>Profile</code> object to ensure it is sent as a 
    * Set-Cookie header in the response.
    *
    * @param secure if true then the cookie should be protected 
    */
   public void setSecure(boolean secure) {
      profile.setCookie(cookie);
      cookie.setSecure(secure);
   }

   /**
    * This returns the number of seconds a cookie lives for. This 
    * acts as a wrapper for the internal instance, and invokes 
    * the same method of the source <code>Cookie</code> instance.
    *
    * @return returns the duration in seconds the cookie lives
    */
   public int getExpiry() {
      return cookie.getExpiry();
   }

   /**
    * This allows a lifetime to be specified for the cookie. This
    * acts as a wrapper for the internal instance, and invokes the 
    * same method of the internal <code>Cookie</code> object. This 
    * will update the <code>Profile</code> object to ensure it is 
    * sent as a Set-Cookie header in the response.
    *
    * @param expiry the duration in seconds the cookie lives
    */
   public void setExpiry(int expiry){
      profile.setCookie(cookie);
      cookie.setExpiry(expiry);
   }
   
   /** 
    * This returns the path for the internal cookie. This acts as 
    * a wrapper for the internal instance, and invokes the same 
    * method of the source <code>Cookie</code> instance.     
    *
    * @return this returns the path value of the internal cookie
    */   
   public String getPath() {
      return cookie.getPath();
   }

   /**
    * This is used to set the cookie path for this cookie. This
    * acts as a wrapper for the internal instance, and invokes the 
    * same method of the internal <code>Cookie</code> object. This 
    * will update the <code>Profile</code> object to ensure it is 
    * sent as a Set-Cookie header in the response.
    *
    * @param path this is the path value for the cookie object
    */
   public void setPath(String path) {
      profile.setCookie(cookie);
      cookie.setPath(path);
   }

   /** 
    * This returns the domain for this cookie. This acts as 
    * a wrapper for the internal instance, and invokes the same 
    * method of the source <code>Cookie</code> instance.     
    *
    * @return this returns the domain value from the cookie
    */
   public String getDomain() {
      return cookie.getDomain();
   }

   /**
    * This enables the domain for the cookie to be set. This
    * acts as a wrapper for the internal instance, and invokes the 
    * same method of the internal <code>Cookie</code> object. This 
    * will update the <code>Profile</code> object to ensure it is 
    * sent as a Set-Cookie header in the response.
    *
    * @param domain this is the new domain value for the cookie 
    */
   public void setDomain(String domain) {
      profile.setCookie(cookie);
      cookie.setDomain(domain);
   }
   
   /**
    * This will give the correct string value of this cookie. This
    * will generate the cookie text with only the values that were
    * given with this cookie. If there are no optional attributes
    * like $Path or $Domain these are left blank. This returns the
    * encoding as it would be for the HTTP Cookie header.
    *
    * @return this returns the Cookie header encoding of this
    */ 
   public String toClientString(){
      return cookie.toClientString();
   }

   /**
    * The <code>toString</code> method converts the cookie to the
    * Set-Cookie value. This can be used to send the HTTP header
    * to a client browser. This uses a format that has been tested
    * with various browsers. This is required as some browsers
    * do not perform flexible parsing of the Set-Cookie value.
    * 
    * @return this returns a Set-Cookie encoding of the cookie 
    */
   public String toString(){
      return cookie.toString();
   }
}

/*
 * Cookie.java February 2001
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
 * This class is used to represent a generic cookie. This exposes
 * the fields that a cookie can have. The <code>Cookie</code> object
 * implements <code>java.io.Serializable</code> which means that it
 * can be stored. This is useful when the server wants to remember
 * the <code>Cookies</code> that it has received.
 * <p>
 * By default the version of the <code>Cookie</code> is set to 1.
 * The version can be configured using the <code>setVersion</code>
 * method. The domain, path, security, and expiry of the cookie can
*  also be set using their respective set methods.
 * <p>
 * The <code>toString</code> method allows the <code>Cookie</code>
 * to be converted back into text form. This text form converts the
 * cookie according to the Set-Cookie header form. This is done so
 * that a created <code>Cookie</code> instance can be converted
 * to a string which can be used as a a HTTP header.
 *
 * @author Niall Gallagher
 */
public class Cookie implements Serializable {

   /**
    * The name attribute of this <code>Cookie</code>.
    */
   private String name;

   /**
    * The value attribute of this <code>Cookie</code>.
    */
   private String value;

   /**
    * Represents the value of the path for this cookie.
    */
   private String path;

   /**
    * Represents the value of the domain attribute.
    */
   private String domain;

   /**
    * Determines whether the cookie should be secure.
    */
   private boolean secure;

   /**
    * Represents the value of the version attribute.
    */
   private int version = 1;

   /**
    * Represents the duration in seconds of the cookie.
    */
   private int expiry = -1;

   /**
    * Constructor of the <code>Cookie</code> that does not need
    * the name or value to be set. This allows the object to be
    * extended without the need to supply the name and value of
    * the cookie. If this constructor is used then the name and
    * values retrieved should not be null values.
    */
   protected Cookie() {
   }

   /**
    * Constructor of the <code>Cookie</code> that uses a default
    * version of 1, which is used by RFC 2109. This contains none
    * of the optional attributes, such as domain and path. These
    * optional attributes can be set using the set methods.
    * <p>
    * The name must conform to RFC 2109, which means that it can
    * contain only ASCII alphanumeric characters and cannot have
    * commas, white space, or semicolon characters.
    *
    * @param name this is the name of this <code>Cookie</code>
    * @param value this is the value of this <code>Cookie</code>
    */
   public Cookie(String name, String value) {
      this(name, value, "/");
   }

   /**
    * Constructor of the <code>Cookie</code> that uses a default
    * version of 1, which is used by RFC 2109. This allows the
    * path attribute to be specified for on construction. Other
    * attributes can be set using the set methods provided.
    * <p>
    * The name must conform to RFC 2109, which means that it can
    * contain only ASCII alphanumeric characters and cannot have
    * commas, white space, or semicolon characters.
    *
    * @param name this is the name of this <code>Cookie</code>
    * @param value this is the value of this <code>Cookie</code>
    * @param path the path attribute of this <code>Cookie</code>
    */
   public Cookie(String name, String value, String path) {
      this.value = value;
      this.name = name;
      this.path = path;
   }

   /**
    * This returns the version for this cookie. The version is
    * not optional and so will always return the version this
    * cookie uses. If no version number is specified this will
    * return a version of 1, to comply with RFC 2109.
    *
    * @return the version value from this <code>Cookie</code>
    */
   public int getVersion() {
      return version;
   }

   /**
    * This enables the version of the <code>Cookie</code> to be
    * set. By default the version of the <code>Cookie</code> is
    * set to 1. It is not advisable to set the version higher
    * than 1, unless it is known that the client will accept it.
    * <p>
    * Some old browsers can only handle cookie version 0. This
    * can be used to comply with the original Netscape cookie
    * specification. Version 1 complies with RFC 2109.
    *
    * @param version this is the version number for the cookie
    */
   public void setVersion(int version) {
      this.version = version;
   }

   /**
    * This returns the name for this cookie. The name and value
    * attributes of a cookie define what the <code>Cookie</code>
    * is for, these values will always be present. These are
    * mandatory for both the Cookie and Set-Cookie headers.
    * <p>
    * Because the cookie may be stored by name, the cookie name
    * cannot be modified after the creation of the cookie object.
    *
    * @return the name from this <code>Cookie</code> object
    */
   public String getName() {
      return name;
   }

   /**
    * This returns the value for this cookie. The name and value
    * attributes of a cookie define what the <code>Cookie</code>
    * is for, these values will always be present. These are
    * mandatory for both the Cookie and Set-Cookie headers.
    *
    * @return the value from this <code>Cookie</code> object
    */
   public String getValue() {
      return value;
   }

   /**
    * This enables the value of the cookie to be changed. This
    * can be set to any value the server wishes to send. Cookie
    * values can contain space characters as they are transmitted
    * in quotes. For example a value of <code>some value</code>
    * is perfectly legal. However for maximum compatibility
    * across the different plaforms such as PHP, JavaScript and
    * others, quotations should be avoided. If quotations are
    * required they must be added to the string. For example a
    * quoted value could be created as <code>"some value"</code>.
    *
    * @param value this is the new value of this cookie object
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * This determines whether the cookie is secure. The cookie
    * is secure if it has the "secure" token set, as defined
    * by RFC 2109. If this token is set then the cookie is only
    * sent over secure channels such as SSL and TLS and ensures
    * that a third party cannot intercept and spoof the cookie.
    *
    * @return this returns true if the "secure" token is set
    */
   public boolean getSecure() {
      return secure;
   }

   /**
    * This is used to determine if the client browser should send
    * this cookie over a secure protocol. If this is true then
    * the client browser should only send the cookie over secure
    * channels such as SSL and TLS. This ensures that the value
    * of the cookie cannot be intercepted by a third party.
    *
    * @param secure if true then the cookie should be protected
    */
   public void setSecure(boolean secure) {
      this.secure = secure;
   }

   /**
    * This returns the number of seconds a cookie lives for. This
    * determines how long the cookie will live on the client side.
    * If the expiry is less than zero the cookie lifetime is the
    * duration of the client browser session, if it is zero then
    * the cookie will be deleted from the client browser.
    *
    * @return returns the duration in seconds the cookie lives
    */
   public int getExpiry() {
      return expiry;
   }

   /**
    * This allows a lifetime to be specified for the cookie. This
    * will make use of the "max-age" token specified by RFC 2109
    * the specifies the number of seconds a browser should keep
    * a cookie for. This is useful if the cookie is to be kept
    * beyond the lifetime of the client session. If the valie of
    * this is zero then this will remove the client cookie, if
    * it is less than zero then the "max-age" field is ignored.
    *
    * @param expiry the duration in seconds the cookie lives
    */
   public void setExpiry(int expiry){
      this.expiry = expiry;
   }

   /**
    * This returns the path for this cookie. The path is in both
    * the Cookie and Set-Cookie headers and so may return null
    * if there is no domain value. If the <code>toString</code>
    * or <code>toClientString</code> is invoked the path will
    * not be present if the path attribute is null.
    *
    * @return this returns the path value from this cookie
    */
   public String getPath() {
      return path;
   }

   /**
    * This is used to set the cookie path for this cookie. This
    * is set so that the cookie can specify the directories that
    * the cookie is sent with. For example if the path attribute
    * is set to <code>/pub/bin</code>, then requests for the
    * resource <code>http://hostname:port/pub/bin/README</code>
    * will be issued with this cookie. The cookie is issued for
    * all resources in the path and all subdirectories.
    *
    * @param path this is the path value for this cookie object
    */
   public void setPath(String path) {
      this.path = path;
   }

   /**
    * This returns the domain for this cookie. The domain is in
    * both the Cookie and Set-Cookie headers and so may return
    * null if there is no domain value. If either the
    * <code>toString</code> or <code>toClientString</code> is
    * invoked the domain will not be present if this is null.
    *
    * @return this returns the domain value from this cookie
    */
   public String getDomain() {
      return domain;
   }

   /**
    * This enables the domain for this <code>Cookie</code> to be
    * set. The form of the domain is specified by RFC 2109. The
    * value can begin with a dot, like <code>.host.com</code>.
    * This means that the cookie is visible within a specific
    * DNS zone like <code>www.host.com</code>. By default this
    * value is null which means it is sent back to its origin.
    *
    * @param domain this is the domain value for this cookie
    */
   public void setDomain(String domain) {
      this.domain = domain;
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
      return "$Version="+version+"; "+name+"="+
       value+ (path==null?"":"; $Path="+
      path)+ (domain==null? "":"; $Domain="+
       domain);
   }

   /**
    * The <code>toString</code> method converts the cookie to the
    * Set-Cookie value. This can be used to send the HTTP header
    * to a client browser. This uses a format that has been tested
    * with various browsers. This is required as some browsers
    * do not perform flexible parsing of the Set-Cookie value.
    * <p>
    * Netscape and IE-5.0 cant or wont handle <code>Path</code>
    * it must be <code>path</code> also Netscape can not handle
    * the path in quotations such as <code>"/path"</code> it must
    * be <code>/path</code>. This value is never in quotations.
    * <p>
    * For maximum compatibility cookie values are not transmitted
    * in quotations. This is done to ensure that platforms like
    * PHP, JavaScript and various others that don't comply with
    * RFC 2109 can transparently access the sent cookies.
    *
    * @return this returns a Set-Cookie encoding of the cookie
    */
   public String toString(){
      return name+"="+value+"; version="+
      version +(path ==null ?"":"; path="+path)+
        (domain ==null ?"": "; domain="+domain)+
       (expiry < 0 ? "" : "; max-age="+expiry)+
         (secure ? "; secure;" : ";");
   }
}

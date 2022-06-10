/*
 * DefaultIdentifier.java June 2004
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

package simple.http.session;

import java.security.SecureRandom;
import simple.util.net.Cookie;
import simple.http.Profile;

/**
 * The <code>DefaultIdentifier</code> object provides a secure
 * implementation of the <code>Identifier</code>. This generates a
 * cryptographically strong pseudo-random number for each session
 * reference. This ensures that the session references cannot be
 * hijacked opening up a severe security hole in a HTTP server.
 * <p>
 * A <code>SecureRandom</code> object can be provided for use by
 * this. If one is not provided a self-seeding instance is used.
 * Each random number is a sixty four bit <code>long</code>
 * represented in hexidecimal format for readibility. This ensures
 * that the returned session <code>Cookie</code> is linked to the
 * <code>Profile</code> in such a way that updates are propagated.
 *
 * @author Niall Gallagher
 */
final class DefaultIdentifier implements Identifier {

   /**
    * To ensure that the session reference can be extracted
    * from the <code>Profile</code> it must contain a known
    * identifier. This identifier is reasonably unique.
    */
   private static final String IDENTITY = "SESSIONID";

   /**
    * Generates a random number to create secure identifiers.
    */
   private SecureRandom random;

   /**
    * Constructor for the <code>DefaultIdentifier</code> object.
    * This will obtain a <code>SecureRandom</code> instance that
    * contains the implementation from the highest priority
    * installed provider that has a <code>SecureRandom</code>
    * implementation, this uses the default algorithm.
    */
   public DefaultIdentifier() {
      this(new SecureRandom());
   }

   /**
    * Constructor for the <code>DefaultIdentifier</code> object.
    * This uses the provided <code>SecireRandom</code> object to
    * generate random numbers as session identifiers. This is
    * done so that the session references cannot be hijacked. A
    * secure random number is practically impossible to guess.
    *
    * @param random this is the random number generator used
    */
   public DefaultIdentifier(SecureRandom random){
      this.random = random;
   }

   /**
    * This is used to acquire a session reference by either
    * creating the reference or extracting an existing one. The
    * session reference is a <code>Cookie</code>, which enables
    * the scope of the session, that is, the domain the session
    * resides within. This is useful in clusters when more
    * than one HTTP server will be handling requests.
    *
    * @param profile container for the session reference
    *
    * @return this returns the cookie referencing the session
    */
   public Cookie getIdentity(Profile profile) {
      if(!profile.contains(IDENTITY)) {
         profile.setCookie(IDENTITY, getRandom());
      }
      return profile.getCookie(IDENTITY);
   }

   /**
    * This method generates the random session identifier. The
    * identifier is generated as a native <code>long</code> and
    * is converted into a hexidecimal string for readibility.
    *
    * @return this returns the random session identifier
    */
   private String getRandom() {
      long value = random.nextLong();
      return Long.toHexString(value);
   }
}

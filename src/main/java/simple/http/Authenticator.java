/*
 * Authenticator.java November 2002
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

import simple.util.parse.PrincipalParser;
import simple.util.net.Principal;
import simple.util.cache.LRUList;

/**
 * This is used to provide quick access to principals that have
 * authorized themselves using the RFC 2616 Authorization header.
 * This caches the issued <code>Principal</code> instances so that
 * the same authorization credentials will not need to be parsed
 * twice. It is often the case that when a HTTP client views a
 * restricted page it will have to retrieve embeded images which
 * requires a HTTP pipeline to have authorization for each
 * request, this requires that the header be parsed several times.   
 * 
 * @author Niall Gallagher
 */
final class Authenticator {

   /**
    * This ensures that the retreival of the instances can be done
    * quickly. Each time the <code>getPrincipal</code> method is
    * invoked the result is cached so that each time the same
    * ticket is used it does not need to be parsed.
    */
   private static final LRUList cache;

   static {
      cache = new LRUList();
   }

   /**
    * This retreives a <code>simple.util.net.Principal</code>
    * instance that represents the user and password from the
    * decoded authorization credentials. The credentials can be
    * retrieved from <code>Request.getValue</code> for the 
    * Authorization header. This minimizes the parsing required 
    * using a cache for the most recently used principals.
    *
    * @return returns the <code>Principal</code> represented by
    * the authorization credentials
    */
   public static Principal getPrincipal(String ticket){
      Object principal = cache.lookup(ticket); 
                                   
      if(principal == null){       
         principal = new PrincipalParser(ticket);
         cache.insert(ticket, principal);
      }
      return (Principal)principal;
   }
}

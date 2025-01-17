/*
 * Principal.java November 2002
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
 
package simple.util.net;

/**
 * The <code>Principal</code> interface is used to describe a 
 * user that has a name and password. This should not be 
 * confused with the <code>java.security.Principal</code> 
 * interface which does not provide <code>getPassword</code>.
 *
 * @author Niall Gallagher
 */
public interface Principal {

   /**
    * The <code>getPassword</code> method is used to retreive 
    * the password of the principal. This is the password 
    * tag in the RFC 2616 Authorization credentials expression.
    *
    * @return this returns the password for this principal
    */
   public String getPassword();
   
   /**
    * The <code>getName</code> method is used to retreive 
    * the name of the principal. This is the name tag in 
    * the RFC 2616 Authorization credentials expression.
    *
    * @return this returns the name of this principal
    */
   public String getName();
}

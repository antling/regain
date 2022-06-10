/*
 * Match.java March 2002
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
 
package simple.util;

import java.io.Serializable;

/**
 * This object is used with the <code>Resolver</code> to store
 * and retrive the pattern match pairs. This is used so that 
 * the resolver can return the pairs in a consistent and simple
 * manner. This provides an organized way to interact with the
 * <code>Resolver</code>. This is <code>Serializable</code> so 
 * it is possible to store any retrived implementation.
 *
 * @author Niall Gallagher
 */
public interface Match extends Serializable{

   /**
    * This is the pattern that this match was stored under
    * in the <code>Resolver</code>. The pattern is typically
    * a <code>String</code> that contains wild characters so 
    * that it can be matched with certain <code>String</code>s.
    *
    * @return this returns the wild pattern for the match
    */
   public String getPattern();
   
   /**
    * This is match that the <code>getPattern</code> result
    * resolves for. The <code>Resolvr</code> returns this
    * <code>String</code> when the <code>resolve</code> method 
    * is given a <code>String</code> that can be matched using 
    * the pattern.
    *
    * @return the matched <code>String</code> for the pattern
    */
   public String getMatch();
}

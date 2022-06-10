/*
 * Layout.java November 2002
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
 
package simple.http.load;

import java.io.Serializable;
import simple.util.Match;

/**
 * The <code>Layout</code> represents the layout of the links and 
 * resources within a specific <code>LoaderManager</code>. This
 * is given to <code>Loader</code> objects when the state of the
 * <code>LoaderManager</code> changes. This can be used to
 * determine the state of the engine and provide a graphical
 * display so that an administration client can be used to load
 * and unload resources as well as create links to resources.
 * 
 * @author Niall Gallagher
 */
public interface Layout extends Serializable {

   /**
    * This is used to retrieve the links that have been made with
    * the <code>LoaderManager</code>. This contains the pattern
    * and name matches made. The order of this array is 
    * signifigant as it corrosponnds to the order in which the
    * <code>LoaderManager</code> will resolve resources.
    *
    * @return an ordered array of <code>Match</code> objects that
    * contain the links made
    */
   public Match[] getMatches();

   /**
    * This is used to retrieve the fully qualified class names of
    * the resources loaded by the <code>LoaderManager</code>. This
    * contains the class names that match by index the names of
    * the resources retrieved with <code>getNames</code>.
    *
    * @return an array of strings that is parallel to the names
    * retrieved from the <code>getNames</code> method
    */
   public String[] getClassNames();

   /**
    * This is used to retrieve the unique names of resource
    * instances loaded by the <code>LoaderManager</code>. The list
    * of names returns matches by index the class type of the
    * instance as retrieved by <code>getClassNames</code>.
    *
    * @return an array of strings that is parallel to the class
    * names retrieved from the <code>getClassNames</code> method    
    */
   public String[] getNames();
}

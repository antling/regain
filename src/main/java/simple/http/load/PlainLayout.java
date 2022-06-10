/*
 * PlainLayout.java November 2002
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

import simple.util.Match;

/**
 * The <code>PlainLayout</code> this provides an implementation of 
 * the <code>Layout</code> interface. This is acquires a snapshot
 * of the <code>LoaderEngine</code> contents using an instance
 * of the <code>Profile</code> issued by the engine once there has
 * been a change to the configuration. 
 * <p>
 * This is used in by the <code>Processor</code> which will 
 * acquires the <code>Profile</code> from the loader engine when
 * there is a change in the service links or the loaded services.
 *
 * @author Niall Gallagher
 */
class PlainLayout implements Layout {

   /**
    * The class names that run parallel to the service names.
    */
   private String[] classes;

   /**
    * The names of the <code>Service</code> instances loaded.
    */
   private String[] names;

   /**
    * The links that have been made to the loaded services.
    */
   private Match[] matches;

   /**
    * Constructor for the <code>PlainLayout</code> uses the issued
    * <code>Profile</code> to retrieve the layout contents. This
    * uses the profiles methods to retrieve the information so 
    * that it can be serialized. 
    *
    * @param profile this is the <code>Profile</code> instance 
    * that represents the managers state
    */
   public PlainLayout(Profile profile) {
      classes = profile.getClassNames();
      matches = profile.getMatches();
      names = profile.getNames();
   }

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
   public Match[] getMatches(){
      return matches;
   }

   /**
    * This is used to retrieve the fully qualified class names of
    * the resources loaded by the <code>LoaderManager</code>. This
    * contains the class names that match by index the names of
    * the resources retrieved with <code>getNames</code>.
    *
    * @return an array of strings that is parallel to the names
    * retrieved from the <code>getNames</code> method
    */
   public String[] getClassNames(){
      return classes;
   }

   /**
    * This is used to retrieve the unique names of resource
    * instances loaded by the <code>LoaderManager</code>. The list
    * of names returns matches by index the class type of the
    * instance as retrieved by <code>getClassNames</code>.
    *
    * @return an array of strings that is parallel to the class
    * names retrieved from the <code>getClassNames</code> method    
    */
   public String[] getNames(){
      return names;
   }
}

/*
 * LoadingException.java February 2001
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
 
package simple.http.load;

/**
 * The <code>LoadingException</code> is thrown when the loading
 * of a <code>Service</code> fails for some reason. This is
 * thrown by the <code>LoaderEngine</code> when the class for
 * the <code>Service</code> cannot be found or if it cannot be 
 * instantiated because it does not have a suitable constructor.
 * This is also thrown if the initialization of the service
 * could not be done.
 *
 * @author Niall Gallagher
 */
public class LoadingException extends Exception {

   /**
    * This empty constructor is used if there is no 
    * explanation of the loading exception required.
    */
   public LoadingException(){
      super();
   }

   /**
    * This constructor is used if there is a description
    * of the event that caused the exception required.
    * 
    * @param desc this is a description of the exception
    */
   public LoadingException(String desc){
      super(desc);
   }
}

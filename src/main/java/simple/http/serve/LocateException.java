/*
 * LocateException.java February 2004
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
 
package simple.http.serve;

import java.io.IOException;

/**
 * The <code>LocateException</code> is thrown when the discovery
 * of a resource by a <code>Locator</code> fails. This is used to
 * indicate that either the resource could not be found or that
 * it was found and its contents are not accessible.
 *
 * @author Niall Gallagher
 */
public class LocateException extends IOException {

   /**
    * This empty constructor is used if there is no 
    * explanation of the locating exception required.
    */
   public LocateException(){
      super();
   }

   /**
    * This constructor is used if there is a description
    * of the event that caused the exception required.
    * 
    * @param desc this is a description of the exception
    */
   public LocateException(String desc){
      super(desc);
   }
}

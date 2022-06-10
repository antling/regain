/*
 * FormatException.java March 2002
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
 
package simple.http.serve;

/**
 * The <code>FormatException</code> is thrown is there was a
 * problem aquiring contents from a <code>Format</code> object.
 * The exception is thrown if the <code>Format</code> instance
 * does not implement support for contents for the requested
 * resource or if there was a problem generating the contents.
 *
 * @author Niall Gallagher
 */
public class FormatException extends Exception {

   /**
    * This empty constructor is used if there is no 
    * explanation of the format exception required.
    */
   public FormatException(){
      super();
   }
   
   /**
    * This constructor is used if there is a description
    * of the event that caused the exception required.
    * 
    * @param desc this is a description of the exception
    */
   public FormatException(String desc){
      super(desc);
   }
}

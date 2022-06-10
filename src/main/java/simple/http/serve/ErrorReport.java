/*
 * ErrorReport.java February 2004
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

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * The <code>ErrorReport</code> is used to describe HTTP errors.
 * Reporting the various HTTP errors requires that status codes
 * and textual descriptions of the errors are provided. This
 * will pair a provided status code with a short description of
 * the error that HTTP status code represents as outlined by
 * RFC 2612 section 6.1.1. 
 * <p>
 * The <code>getCause</code> method will provide a description
 * of the error using the <code>printStackTrace</code> method.
 * This enables the cause of the error to be identified within
 * the source code of the file that threw the exception.
 *
 * @author Niall Gallagher
 */
final class ErrorReport implements Report {

   /**
    * This is used to load the Error.properties file for 
    * a list of the matching HTTP error messages.
    */
   protected static ResourceBundle error;   

   static {      
      try { 
         error = ResourceBundle.getBundle(
         "simple.http.serve.Error", Locale.getDefault());
      }catch(MissingResourceException e){
         e.printStackTrace();
      }   
   }   
   
   /**
    * This captures the stack trace of the issued exception.
    */
   private ExceptionBuffer cause;   

   /**
    * This is the status code that the report represents.
    */
   private int code; 

   /**
    * Constructor for the <code>ErrorReport</code> object. This
    * will create an instance that uses the exception and the 
    * HTTP status code provided to describe the error event. 
    *
    * @param cause this is the exception that caused the error
    * @param code this is the HTTP status code of the error
    */
   public ErrorReport(Exception cause, int code){
      this.cause = new ExceptionBuffer(cause);
      this.code = code;
   }

   /**
    * Returns the HTTP status code that this report represents.
    * This will typically describe an error using one of the 4xx
    * or 5xx HTTP status codes. For details on the specific types 
    * of status codes used by HTTP/1.1 see RFC 2616 section 6.1.1.
    *
    * @return the HTTP status code this report represents
    */
   public int getCode(){
      return code;
   }

   /**
    * Returns a short description of what caused this report.
    * This will typically describe an error using one of the 4xx
    * or 5xx HTTP status codes. For details on the specific types 
    * of status codes used by HTTP/1.1 see RFC 2616 section 6.1.1.   
    *
    * @return the short description of what caused the report
    */
   public String getText(){
      try {
         return error.getString(String.valueOf(code)); 
      }catch(MissingResourceException e){
         return "Unknown";
      }      
   }

   /**
    * This is used to acquire a detailed message describing the
    * cause of the error. This will display exceptions caught
    * while processing a HTTP request. The exception message is
    * acquired from the <code>printStackTrace</code> method.
    *
    * @return a detailed description of what caused the report
    */
   public String getCause(){
      return cause.getCause();
   }
}

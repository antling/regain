/*
 * StatusReport.java February 2004
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
 * The <code>StatusReport</code> is used to describe HTTP status
 * messages. Reporting the various HTTP status messages requires
 * that status codes and textual descriptions of the status are
 * provided. This will pair a provided status code with a short 
 * description of the status that HTTP status code represents as
 * outlined by RFC 2612 section 6.1.1. 
 * <p>
 * The <code>getCause</code> method will provide an empty
 * description of the cause, as changes in status are typically
 * driven by the service implementations and therefore are not
 * directly caused by any specific event.
 *
 * @author Niall Gallagher
 */
final class StatusReport implements Report {

   /**
    * This is used to load the Error.properties file for 
    * a list of the matching HTTP status messages.
    */
   protected static ResourceBundle status;   

   static {      
      try { 
         status = ResourceBundle.getBundle(
         "simple.http.serve.Error", Locale.getDefault());
      }catch(MissingResourceException e){
         e.printStackTrace();
      }   
   }    
   
   /**
    * This is the status code that the report represents.
    */
   private int code;

   /**
    * Constructor for the <code>StatusReport</code> object. This
    * will create an instance that uses the HTTP status code 
    * provided to describe the status report. 
    *
    * @param code this is the HTTP status code of the report
    */
   public StatusReport(int code){
      this.code = code;
   }

   /**
    * Returns the HTTP status code that this report represents.
    * This can be used to describe a chenge in status using any
    * of the valid HTTP status codes described in RFC 2616 section
    * 6.1.1, typically this will be a 2xx, 3xx, 4xx, or 5xx code.
    *
    * @return the HTTP status code this report represents
    */
   public int getCode(){
      return code;
   }

   /**
    * Returns a short description of what caused this report.
    * This can be used to describe a chenge in status using any
    * of the valid HTTP status codes described in RFC 2616 section
    * 6.1.1, typically this will be a 2xx, 3xx, 4xx, or 5xx code.
    *
    * @return the short description of what caused the report
    */
   public String getText(){
      try {
         return status.getString(String.valueOf(code)); 
      }catch(MissingResourceException e){
         return "Unknown";
      }      
   }

   /**
    * This is used to acquire a detailed message describing the
    * cause of the error. For this implementation there will be
    * no direct cause for the status report as this is typically
    * used as a result of a change in status by the service.
    *
    * @return this provides an empty string for the cause
    */
   public String getCause(){
      return "";
   }
}

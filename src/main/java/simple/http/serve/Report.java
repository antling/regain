/*
 * Report.java February 2004
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

/**
 * The <code>Report</code> interface is used to describe a HTTP
 * response message. Reporting the various HTTP error and status
 * messages requires that a status code and textual description
 * be provided in the response, also depending on the type of
 * response a description of the cause can be provided in the
 * HTTP message body. 
 * <p>
 * The <code>getCode</code> method must return a valid HTTP 
 * status code so that it can be used to describe the response,
 * also the <code>getText</code> method must supply a short
 * description of the response. For example, a description for
 * the 404 HTTP status code would be 'File Not Found', for more
 * information of valid descriptions see RFC 2616 section 6.1.1.
 *
 * @author Niall Gallagher
 */
public interface Report {

   /**
    * Returns the HTTP status code that this report represents.
    * This will typically describe an error using one of the 4xx
    * or 5xx HTTP status codes. For details on the specific types 
    * of status codes used by HTTP/1.1 see RFC 2616 section 6.1.1.
    *
    * @return the HTTP status code this report represents
    */
   public int getCode();

   /**
    * Returns a short description of what caused this report.
    * This will typically describe an error using one of the 4xx
    * or 5xx HTTP status codes. For details on the specific types 
    * of status codes used by HTTP/1.1 see RFC 2616 section 6.1.1.   
    *
    * @return the short description of what caused the report
    */
   public String getText();

   /**
    * This is used to acquire a detailed message describing the
    * cause of the error. Typically this will display exceptions
    * caught while processing a HTTP request. The exception can
    * be reported using the <code>printStackTrace</code> method.
    *
    * @return a detailed description of what caused the report
    */
   public String getCause();
}

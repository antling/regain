/*
 * StatusLine.java February 2001
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
 
package simple.http;

/**
 * The <code>StatusLine</code> is used to represent a HTTP status 
 * line. This provides several convinence methods that can be used 
 * to manipulate a HTTP status line. see the RFC (RFC 2616) for the 
 * syntax of a status line.
 *
 * @author Niall Gallagher
 */ 
public interface StatusLine {

   /**
    * This represents the status code of the HTTP response. 
    * The response code represents the type of message that is
    * being sent to the client. For a description of the codes
    * see RFC 2616 section 10, Status Code Definitions. 
    *
    * @return the status code that this HTTP response has
    */ 
   public int getCode();
     
   /**
    * This method allows the status for the response to be 
    * changed. This MUST be reflected the the response content
    * given to the client. For a description of the codes see
    * RFC 2616 section 10, Status Code Definitions.
    *
    * @param code the new status code for the HTTP response
    */ 
   public void setCode(int code);

   /**
    * This can be used to retrive the text of a HTTP status
    * line. This is the text description for the status code.
    * This should match the status code specified by the RFC.
    *
    * @return the message description of the response
    */ 
   public String getText();

   /**
    * This is used to set the text of the HTTP status line.
    * This should match the status code specified by the RFC.
    *
    * @param text the descriptive text message of the status
    */ 
   public void setText(String text);

   /**
    * This can be used to get the major number from a HTTP
    * version. The major version corrosponds to the major 
    * type that is the 1 of a HTTP/1.0 version string.
    *
    * @return the major version number for the response
    */ 
   public int getMajor();

   /**
    * This can be used to specify the major version. This
    * should be the major version of the HTTP request.
    *
    * @param major this is the major number desired
    */ 
   public void setMajor(int major);

   /**
    * This can be used to get the minor number from a HTTP
    * version. The major version corrosponds to the minor
    * type that is the 0 of a HTTP/1.0 version string.
    *
    * @return the major version number for the response
    */ 
   public int getMinor();
   
   /**
    * This can be used to specify the minor version. This
    * should not be set to zero if the HTTP request was 
    * for HTTP/1.1. The response must be equal or higher.
    *
    * @param minor this is the minor number desired
    */ 
   public void setMinor(int minor);
}

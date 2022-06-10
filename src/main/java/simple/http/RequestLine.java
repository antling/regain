/*
 * RequestLine.java February 2001
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
 * The <code>RequestLine</code> is used to represent a HTTP 
 * request line. The  methods provided for this can be used 
 * to provide easy access to the components of a HTTP request 
 * line. For the syntax of a HTTP request line see RFC 2616.
 *
 * @author Niall Gallagher
 */ 
public interface RequestLine {

   /**
    * This can be used to get the URI specified for this HTTP
    * request. This corrosponds to the /index part of a 
    * http://www.domain.com/index URL but may contain the full
    * URL. This can be set using <code>setURI</code>.
    *
    * @return the URI that this HTTP request is targeting
    */ 
   public String getURI();

   /**
    * This can be used to set the URI for this HTTP request.
    * The <code>getURI</code> will return the String entered
    * which can be a full HTTP URL or a relative path URL.
    *
    * @param uri the URI that this HTTP request is to use
    */  
   public void setURI(String uri);

   /**
    * This can be used to get the HTTP method for this
    * request. The HTTP specification RFC 2616 specifies the
    * HTTP request methods in section 9, Method Definitions.
    *
    * @return the request method for this request
    */ 
   public String getMethod();

   /**
    * This is used to set the method for this HTTP request
    * object. For a list of possible string values that
    * can be used see RFC 2616 section 9, Method Definitions.
    *
    * @param method the desired method for this
    */ 
   public void setMethod(String method);

   /**
    * This can be used to get the major number from a HTTP 
    * version. The major version corrosponds to the major 
    * type that is the 1 of a HTTP/1.0 version string.
    *
    * @return the major version number for the request
    */ 
   public int getMajor();

   /**
    * This can be used to specify the major version for the
    * HTTP request. Specifying the major version has little
    * effect on the semantics of the request.
    *
    * @param major this is the major number desired
    */ 
   public void setMajor(int major);

   /**
    * This can be used to get the major number from a HTTP
    * version. The major version corrosponds to the major 
    * type that is the 0 of a HTTP/1.0 version string.
    *
    * @return the major version number
    */ 
   public int getMinor();

   /**
    * This can be used to specify the minor version for the
    * HTTP request. Specifying the minor version will effect
    * the manner in which the request is processed.
    *
    * @param minor this is the minor number desired
    */ 
   public void setMinor(int minor);
}

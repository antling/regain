/*
 * Request.java February 2001
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

import simple.http.session.Session;
import simple.util.net.Parameters;
import simple.util.net.Principal;
import simple.util.net.Cookie;
import simple.util.net.MimeType;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;

/**
 * This interface is used to represent a HTTP request. This defines 
 * the attributes that a HTTP request has such as a request line and 
 * the headers that come with the message header. 
 * <p>
 * The <code>Request</code> is used to provide an interface to the 
 * HTTP <code>InputStream</code> and message header. The stream can
 * have certain characteristics, these characteristics are available 
 * by this object. The <code>Request</code> provides methods that 
 * allow the <code>InputStream</code>'s semantics to be known, for 
 * example if the stream is keep-alive or if the stream has a length.
 * <p>
 * The <code>Request</code> origin is also retrievable from the
 * <code>Request</code> as is the <code>Attributes</code> object
 * which defines specific connection attributes.
 * <p>
 * It is important to note that the <code>Request</code> controls
 * the processing of the HTTP pipeline. The next HTTP request is 
 * not processed until the request has read all of the content body
 * within the <code>InputStream</code>. The stream must be fully
 * read or closed for the next request to be processed. 
 *
 * @author Niall Gallagher
 */ 
public interface Request extends RequestLine, GenericHeader {       

   /**
    * This is used to read the content body. The specifics of the data
    * that is read from this <code>InputStream</code> can be determined
    * by the <code>getContentLength</code> method. If the data sent by
    * the client is chunked then it is decoded, see RFC 2616 section
    * 3.6. The <code>InputStream</code> must be synchronized so that 
    * multiple threads can use the stream in a concurrent environment.
    *
    * @exception IOException signifies that there is an I/O problem
    *
    * @return <code>InputStream</code> containing the message body
    */ 
   public InputStream getInputStream() throws IOException;
  
   /**
    * This provides access to HTML form and query parameters. This is
    * used to provide access to parameters by reading the parameters
    * from the <code>InputStream</code>. If the data cannot be read
    * from the stream then an <code>IOException</code> is thrown.
    * If the data on the stream is not of the correct MIME type then
    * an empty parameters object is returned. The MIME type for the 
    * parameters is <code>application/x-www-form-urlenoded</code>.
    * 
    * @return this returns all HTML form and query parameter data 
    *
    * @exception IOException thrown if there is an I/O problem   
    */
   public Parameters getParameters() throws IOException;

   /**
    * This is used to provide quick access to the parameters. This
    * avoids having to acquire the <code>Parameters</code> object.
    * This basically acquires the parameters object and invokes 
    * the <code>getParameters</code> method with the given name.
    * 
    * @param name this is the name of the parameter value
    *
    * @exception IOException thrown if there is an I/O problem       
    */
   public String getParameter(String name) throws IOException;

   /**
    * This is used to acquire the Content-Type for any HTTP message
    * body that may be sent with the request. If the header is not
    * sent with the HTTP message header this returns null. This
    * should be used with a <code>contains</code> invocation to see
    * if the Content-Type header is in the HTTP header. The MIME
    * type is returned is a read-only <code>MimeType</code> object.
    *
    * @return this returns the Content-Type value if it exists
    */
   public MimeType getMimeType();

   /** 
    * This provides <code>Locale</code> for the Accept-Language value.
    * This returns the first language preference from the header. If
    * the set of values are required the <code>LanguageParser</code>
    * from the <code>simple.util.parse</code> package can be used.
    * If the header does not exist this will return the default
    * <code>Locale</code> from <code>Local.getDefault</code>.
    * 
    * @return the <code>Locale</code> preference of the client
    */
   public Locale getLanguage();

   /**
    * This method can be used to retrieve cookies from the header.
    * This is a convenience method that avoids having to parse the 
    * Cookie header values. This will basically call the
    * <code>getProfile</code> method and from the <code>Profile</code>
    * invoke <code>get</code> using the specified name.
    *
    * @param name this is the name of the cookie to be retrieved
    *
    * @return returns a <code>simple.util.net.Cookie</code> object 
    */
   public Cookie getCookie(String name);

   /**
    * The <code>Profile</code> represents the collection of cookies
    * sent with this HTTP request. This represents a profile 
    * established between the server and client using either domain 
    * or path values, see RFC 2109, HTTP State Management Mechanism.
    * <p>
    * The profile is shared with the <code>Response</code> which 
    * will write each cookie set in the profile as a Set-Cookie value 
    * once the response has committed. This enables simple
    * management of state with the browser by using the profile. 
    * 
    * @return this returns a <code>Profile</code> for the client
    */
   public Profile getProfile();   

   /**
    * This is a convenience method that is used to retrieve the
    * client authorized to this server. This is used to that the HTTP
    * Basic authorization scheme could be used. This will check to 
    * see if the Authorization header is present, if it is then this    
    * will return a <code>simple.util.net.Principal</code> that
    * identifies the client without having to decode the header.
    *
    * @return this returns a HTTP authorized client if one exists
    */
   public Principal getPrincipal();

   /**
    * This can be used to retrieve certain attributes about this 
    * <code>Request</code>. The <code>Attributes</code> contains 
    * certain properties about the <code>Request</code>. For 
    * example if this <code>Request</code> came over a secure 
    * line then there may be security attributes.
    *
    * @return attributes of this <code>Request</code> object
    */ 
   public Attributes getAttributes();

   /**
    * This method is used to acquire a <code>Session</code> for the
    * request. The object retrieved provides a container for data
    * associated to the connected client. This allows the request
    * to perform more complex operations based on knowledge that is
    * built up through a series of requests. The session is known
    * to the system using a <code>Cookie</code>, which contains
    * the session reference. This cookie value should not be 
    * modified as it used to reference the active session object.
    *
    * @return returns an active <code>Session</code> object
    */
   public Session getSession();
   
   /**
    * This is a convenience method that is used to determine whether 
    * or not this message has the <code>Connection: close</code> 
    * header. If the close token is present then this stream is not
    * a keep-alive connection. If this has no <code>Connection</code> 
    * header then the keep-alive status is determined by the HTTP
    * version, that is, HTTP/1.1 is keep-alive by default, HTTP/1.0
    * is not keep-alive by default.
    *
    * @return returns true if this has a keep-alive stream
    */ 
   public boolean isKeepAlive();
   
   /**
    * Used to get the address from where this <code>Request</code> came 
    * from. This information can be retrieved from <code>Attributes</code>
    * object using the <code>getAttributes</code> method. This can be
    * used to log HTTP requests for the server. This can also provide 
    * security, where requests from specific locations are denied.
    *
    * @return the I.P. address this <code>Request</code> came from
    */ 
   public InetAddress getInetAddress();

   /**
    * This is a convenience method that can be used to determine
    * the length of the message body. This will determine if there
    * is a <code>Content-Length</code> header, if it does then the
    * length can be determined, if not then this returns -1.
    *
    * @return the content length, or -1 if it cannot be determined
    */
   public int getContentLength();

   /** 
    * This method is used so that the original HTTP header can 
    * be reconstructed This returns a <code>String</code> that 
    * contains each header formatted according to the HTTP/1.1 
    * header format. The header will contain the request line 
    * followed by each header and ended with the CRLF.
    *
    * @return the HTTP request header in string format
    */
   public String toString();
}

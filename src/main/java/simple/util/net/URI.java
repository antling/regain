/*
 * URI.java February 2001
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
 
package simple.util.net;

import java.util.Enumeration;

/** 
 * The <code>URI</code> interface is used to represent a generic
 * uniform resource identifier . This interface allows each section 
 * of the uniform resource identifier to be represented. A generic
 * uniform resource identifier syntax is represented in RFC 2616 
 * section 3.2.2 for the HTTP protocol, this allows similar URI's
 * for example ftp, gopher, https, tftp. The syntax is
 * <code><pre>
 *
 *    URI = [scheme "://"] host [ ":" port ] [ path [ "?" query ]]
 *
 * </pre></code>
 * This interface reprenents the host, port, path and query part
 * of the uniform resource identifier. The parameters are also 
 * represented by the URI. The parameters in a URI consist of name 
 * and value pairs in the path segment of the URI.
 * <p>
 * This will normalize the path part of the uniform resource
 * identifier. A normalized path is one that contains no back
 * references like "./" and "../". The normalized path will not
 * contain the path parameters.
 * <p>
 * The <code>setPath</code> method is used to reset the path this 
 * uniform resource identifier has, it also resets the parameters.
 * The parameters are extracted from the new path given.
 *
 * @author Niall Gallagher
 */
public interface URI {

   /**
    * This allows the scheme of the URL given to be returned.
    * If the URI does not contain a scheme then this will
    * return null. The scheme of the URI is the part that
    * specifies the type of protocol that the URI is used
    * for, an example <code>gopher://domain/path</code> is
    * a URI that is intended for the gopher protocol. The
    * scheme is the string <code>gopher</code>.
    * 
    * @return this returns the scheme tag for the URI if
    * there is one specified for it
    */
   public String getScheme();
   
   /**
    * This allows the scheme for the uri to be specified. 
    * If the URI does not contain a scheme then this will
    * attach the scheme and the <code>://</code> identifier
    * to ensure that the <code>URI.toString</code> will
    * produce the correct syntax. 
    * <p>
    * Caution must be taken to ensure that the port and
    * the scheme are consistant. So if the original URI
    * was <code>http://domain:80/path</code> and the scheme
    * was changed to <code>ftp</code> the port number that
    * remains is the standard HTTP port not the FTP port.
    *
    * @param scheme this specifies the protocol this URI
    * is intended for
    */
   public void setScheme(String scheme);

   /** 
    * This is used to retrive the domain of this URI. The 
    * domain part in the URI is an optional part, an example
    * <code>http://domain/path?querypart</code>. This will 
    * return the value of the domain part. If there is no 
    * domain part then this will return null otherwise the 
    * domain value found in the uniform resource identifier.
    *
    * @return the domain part of this uniform resource 
    * identifier this represents
    */
   public String getDomain();

   /** 
    * This will set the domain to whatever value is in the 
    * string parameter. If the string is null then this URI 
    * objects <code>toString</code> method will not contain 
    * the domain. The result of the <code>toString</code> 
    * method will be <code>/path/path?query<code>. If the 
    * path is non-null this URI will contain the path.
    *
    * @param domain this will be the new domain of this 
    * uniform resource identifier, if it is not null
    */ 
   public void setDomain(String domain);

   /** 
    * This is used to retrive the port of the uniform resource 
    * identifier. The port part in this is an optional part, an 
    * example <code>http://host:port/path?querypart</code>. This 
    * will return the value of the port. If there is no port then 
    * this will return <code>-1</code> because this represents 
    * an impossible uniform resource identifier port. The port 
    * is an optional part.
    *   
    * @return this returns the port of the uniform resource
    * identifier
    */   
   public int getPort();

   /** 
    * This will set the port to whatever value it is given. If 
    * the value is 0 or less then the <code>toString</code> will
    * will not contain the optional port. If port number is above
    * 0 then the <code>toString</code> method will produce a URI
    * like <code>http://host:123/path</code> but only if there is 
    * a valid domain. 
    *
    * @param port the port value that this URI is to have
    */    
   public void setPort(int port);
   
   /** 
    * This is used to retrive the path of this URI. The path part 
    * is the most fundemental part of the URI. This will return 
    * the value of the path. If there is no path part then this 
    * will return a Path implementation that represents the root
    * path represented by <code>/</code>. 
    * 
    * @return the path that this URI contains, this value will not
    * contain any back references such as "./" and "../" or any
    * path parameters
    */
   public Path getPath();

   /** 
    * This will set the path to whatever value it is given. If the 
    * value is null then this <code>URI.toString</code> method will 
    * not contain the path, that is if path is null then it will be
    * interpreted as <code>/</code>.
    * <p>
    * This will reset the parameters this URI has. If the value 
    * given to this method has embedded parameters these will form
    * the parameters of this URI. The value given may not be the 
    * same value that the <code>getPath</code> produces. The path
    * will have all back references and parameters stripped.
    *
    * @param path the path that this URI is to be set with
    */    
   public void setPath(String path);

   /** 
    * This will set the path to whatever value it is given. If the 
    * value is null then this <code>URI.toString</code> method will 
    * not contain the path, that is if path is null then it will be
    * interpreted as <code>/</code>.
    * <p>
    * This will reset the parameters this URI has. If the value 
    * given to this method has embedded parameters these will form
    * the parameters of this URI. The value given may not be the 
    * same value that the <code>getPath</code> produces. The path
    * will have all back references and parameters stripped.
    *
    * @param path the path that this URI is to be set with
    */    
   public void setPath(Path path);

   /** 
    * This is used to retrive the query of this URI. The query part 
    * in the URI is an optional part. This will return the value 
    * of the query part. If there is no query part then this will 
    * return an empty <code>Parameters</code> object. The query is 
    * an optional member of a URI and comes after the path part, it
    * is preceeded by a question mark, <code>?</code> character. 
    * For example the following URI contains <code>query<code> for
    * its query part, <code>http://host:port/path?query</code>.
    * <p>
    * This returns a <code>simple.util.net.Parameters</code> object
    * that can be used to interact directly with the query values.
    * The <code>Parameters</code> object is a read-only interface
    * to the query parameters, and so will not affect the URI.
    *
    * @return a <code>Parameters</code> object for the query part
    */ 
   public Parameters getQuery();
   
   /** 
    * This will set the query to whatever value it is given. If the 
    * value is null then this <code>URI.toString</code> method will 
    * not contain the query. If the query was <code>abc<code> then 
    * the <code>toString</code> method would produca a string like
    * <code>http://host:port/path?abc</code>. If the query is null 
    * this URI would have no query part.
    * 
    * @param query the query that this uniform resource identifier
    * is to be set to if it is non-null
    */    
   public void setQuery(String query);

   /** 
    * This will set the query to whatever value it is given. If the 
    * value is null then this <code>URI.toString</code> method will 
    * not contain the query. If the <code>Parameters.toString<code>
    * returns null then the query will be empty. This is basically
    * the <code>setQuery(String)</code> method with the string value
    * from the issued <code>Parameters.toString</code> method.
    * 
    * @param query a <code>Parameters</code> object that contains 
    * the name value parameters for the query
    */ 
   public void setQuery(Parameters query);

   /**
    * This extracts the parameter names from the uniform resource
    * identifier represented by this object. The parameters that a  
    * uniform resource identifier contains are embedded in the path 
    * part of the URI. If the path contains no parameters then this
    * will return an empty <code>Enumeration</code>. 
    * <p>
    * This will produce unique name and value parameters. Thus if the 
    * URI contains several path segments with similar parameter names
    * this will return the deepest parameter. For example if the URI
    * represented was <code>http://domain/path1;x=y/path2;x=z</code>
    * the value for the parameter named <code>x</code> would be
    * <code>z</code>.
    *
    * @return this will return the parameter names found in the URI
    */
   public Enumeration getParameterNames();

   /**
    * This will return the value of the parameter with the given name.
    * The parameters stored by this URI will bve unique so this will
    * produce the parameter found deepest within the path segment, see
    * <code>getParameterNames</code>. This will return null if the 
    * parameter does not exist. The <code>setPath</code> method will
    * reset the parameters the URI contains.
    *
    * @param name this is the name of the parameter to be retrived
    *
    * @return this will return the parameter value or null if the 
    * parameter does not exist
    */
   public String getParameter(String name);


   /** 
    * This is used to convert this URI object into a <code>String</code> 
    * object. This will only convert the parts of the URI that exist, so 
    * the URI may not contain the domain or the query part and it will 
    * not contain the path parameters. If the URI contains all these 
    * parts then it will return somthing like 
    * <pre>
    * scheme://host:port/path/path?querypart
    * </pre>
    * <p>
    * It can return <code>/path/path?querypart</code> style relative 
    * URI's. If any of the parts are set to null then that part will be 
    * missing, for example if <code>setDomain</code> method is invoked
    * with a null parameter then the domain and port will be missing
    * from the resulting URI. If the path part is set to null using the
    * <code>setPath</code> then the path will be <code>/</code>. An 
    * example URI with the path part of null would be
    * <pre>
    * scheme://host:port/?querypart
    * </pre>
    *
    * @return the URI with only the path part and the non-null optional
    * parts of the uniform resource identifier
    */
   public String toString();
}


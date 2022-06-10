/*
 * Context.java March 2002
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

import java.io.IOException;
import java.util.Properties;
import java.util.Locale;
import java.io.File;

/**
 * The <code>Context</code> interface is used to give a view of the
 * file system to the <code>ResourceEngine</code>. This provides the 
 * information to the <code>ResourceEngine</code> that it needs in order
 * to serve content to the client browser. This provides the path
 * translations for the HTTP request URI. This also provides the
 * <code>Format</code> object that is used to create directory 
 * listings and error messages.
 * <p>
 * This object essentially provides a mechanism that allows the file
 * engine to convert the HTTP request URI into OS system paths and
 * system objects such as the <code>File</code> object. A context
 * is rooted a a certain directory in the system. This directory is
 * where the resources are gathered from. For example suppose that
 * a <code>Context</code> implementation is rooted at the directory
 * "c:\web\html\" on a DOS system. Now if the target of the browser 
 * was "http://some.host/web/pub/README". The context needs to be
 * consulted to convert "/web/pub/README" into the real path within
 * the system. So <code>Context.getRealPath</code> is invoked with
 * the path "/web/pub/README", which responds with the system path
 * "c:\web\html\web\pub\README". Also if this was a UNIX system 
 * with the same context rooted at "/home/user/html" then the same
 * URL would result in "/home/user/html/web/pub/README".
 * <p>
 * The meaning of HTTP URI in this instance is the request URI
 * from a HTTP/x.x request, as RFC 2616 and RFC 2396 defines it
 *
 * <pre> 
 * Request-Line = Method SP Request-URI SP HTTP-Version CRLF
 *
 * Request-URI = "*" | absoluteURI | abs_path | authority
 * absoluteURI = "http:" "//" host [":" port] [abs_path ["?" query]] 
 * abs_path = "/" path_segments         
 * path_segments = segment *( "/" segment )
 * </pre> 
 *
 * So the <code>Context</code> object must be prepared to accept 
 * the request URI that come in the form outlined above. These can
 * include formats like 
 *
 * <pre> 
 * http://some.host/pub;param=value/bin/index.html?name=value
 * http://some.host:8080/index.en_US.html
 * some.host:8080/index.html
 * /usr/bin;param=value/README.txt
 * /usr/bin/compress.tar.gz
 * </pre>
 *
 * The <code>Context</code> implementation should be able to 
 * directly take a Request-URI as defined in RFC 2616 and translate 
 * this into a path compatible with the OS specific file system. 
 * This keeps the objects semantics simple and explicit, although 
 * at the expense of performance.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.parse.URIParser
 * @see simple.util.parse.PathParser
 */
public interface Context {

   /**
    * This is used to retrieve the base path of the context. The
    * base path of the context is that path that that this will
    * retrieve system information from. This represents a base
    * that the request URI paths are served from on the system.
    * For instance a base of "c:\path" would translate a URI
    * path of "/index.html" into "c:\path\index.html". Every
    * resource request must be relative to the context path
    * this allows the <code>ResourceEngine</code> to map the URIs
    * onto the specific OS. The base path is the OS file system
    * specific path. So on UNIX it could be "/home/user/" and
    * on a DOS system it could be "c:\web\html" for example.
    *
    * @return this returns the base path of the context
    */
   public String getBasePath();
   
   /**
    * This is used to translate the HTTP request URI into the OS
    * specific path that it represents. This will convert the 
    * URI to a format that the system can use and also represents
    * the resource path on that system. So if for example the
    * context path was "c:\path" on a DOS system and the HTTP URI 
    * given was "/index.html" this returns "c:\path\index.html".
    * If a UNIX system was running the VM and the context base
    * was for example "/home/" then this would return the UNIX
    * path "/home/index.html" for the same request URI.
    *
    * @param target this is the HTTP request URI path that is to 
    * be translated into the OS specific path
    * 
    * @return this returns the OS specific path name for the 
    * translate request URI
    */
   public String getRealPath(String target);

   /**
    * This is used to translate the HTTP request URI into the URI
    * path normalized and without query or parameter parts. This
    * is used so that the resource requested by the client can be
    * discovered. For example this will convert the HTTP request
    * URI "http://hostname/bin;param=value/../index.html?query" 
    * into the relative URI path /index.html. This is useful if 
    * a logging mechanism requires the name of the resource that
    * was requested, it can also be used help find the resource.
    *
    * @param target this is the HTTP request URI that is to be
    * converted into a normalized relative URI path
    *
    * @return the HTTP request URI as a normalized relative path
    */
   public String getRequestPath(String target);

   /**
    * This is used to translate the HTTP request URI into the a
    * <code>File</code> object that it represents. This will convert 
    * the URI to a format that the system can use and then create    
    * the <code>File</code> object for that path. So if for example 
    * the context path was "c:\path" on a DOS system and the HTTP 
    * URI given was "/index.html" this returns the <code>File</code> 
    * "c:\path\index.html". This is basically for convenience as the
    * same could be achieved using the <code>getRealPath</code> and
    * then creating the <code>File</code> from that OS specific path.
    *
    * @param target this is the HTTP request URI path that is used
    * to retrieve the <code>File</code> object
    * 
    * @return returns the <code>File</code> for the given path
    */
   public File getFile(String target);

   /**
    * This will parse the HTTP request URI specified and return the 
    * <code>Locale</code> for that resource. The <code>Locale</code>
    * is extracted from the target by examining the path segment of
    * the HTTP request URI. The path segment is the abs_path token
    * defined in RFC 2396. It is extracted from a second extension
    * in the file name. So for example if the HTTP request URI was
    * "http://some.host/usr;param=value/index.en_US.html" then the
    * file name "index.en_US.html" would have the second file
    * extension en_US converted into a <code>Locale</code>. This
    * will not interfere if the file name was "compressed.tar.gz",
    * it will simply ignore the "tar" second file extension and
    * return <code>Locale.getDefault</code>. 
    *
    * @param target the request URI to be parsed for its locale
    *
    * @return this will return the locale for the specified URI
    */ 
   public Locale getLocale(String target);
   
   /**
    * This method will extract the type attribute of this URI. The
    * MIME type of the request URI is extracted from the name of the
    * target. The name for the <code>Context</code> is the last path
    * segment is the token defined by RFC 2396 as path_segments. So
    * for example if the target was "some.host:8080/bin/index.html"
    * then the name for that resource would be "index.html". Once
    * the name has been extracted the MIME is defined by the file
    * extension which, for the example is text/html.
    * <p>
    * Implementations of the <code>Context</code> may also choose to
    * implement a method that consults the underlying resource and
    * inspect its contents to determine its MIME type. Or for a MAC
    * it may contain its MIME type. If the MIME type cannot be found
    * by any of the above methods RFC 2616 suggests that the resource
    * be given the MIME type application/octetstream. This should also
    * make not predictions as to how the file will be served. So if 
    * the target references a directory this does not mean that the
    * <code>Format.getMimeType</code> can be used.
    *
    * @param target the request URI to be parsed for its type    
    *
    * @return the type of the file this path refers to
    */ 
   public String getMimeType(String target);
   
   /**
    * This will parse and return the file name that this request URI
    * references. The name for the <code>Context</code> is the last 
    * path segment is the token defined by RFC 2396 as path_segments. 
    * So for example if the target was "some.host:8080/home/user/"
    * then the name for that resource would be "user". If the path 
    * references the root path "/" then null should be returned.
    *
    * @param target the request URI to be parsed for its name
    *
    * @return this will return the name that this references
    */ 
   public String getName(String target);
   
   /**
    * This is an all in one method that allows all the information 
    * on the target URI to be gathered at once. The motivation for
    * this method is primarily convenience. However it is also used
    * to increase the performance of the <code>ResourceEngine</code>
    * when the <code>Context</code> implementation is synchronized.
    * This will enable the <code>ResourceEngine</code> to gather the
    * information on the target by acquiring the lock for the object
    * instance only once.
    *
    * @param index this is the object that will gather the component
    * parts of the specified request URI
    * @param target this is the request URI that is to be parsed
    */
   public void doIndex(Indexable index, String target);

   /**
    * This enables the contents of some resource to be acquired using
    * a request URI. The <code>Content</code> is a resource that can
    * be transmitted using an <code>OutputStream</code> which avoids
    * having to deal with <code>FileInputStream</code> objects for
    * files and also facilitates dynamic content. The purpose of this
    * is to discourage the use of chaining which does not result
    * in transferring control, that is, avoid the Java Servlet include
    * technique used by <code>javax.servlet.RequestDispatcher</code>. 
    *
    * @param target the request URI that refers to a resource
    *
    * @return this returns the specified resource wrapped in the
    * <code>Content</code> interface
    *
    * @throws IOException this is thrown if the resource does not
    * exist or cannot be accessed     
    */
   public Content getContent(String target) throws IOException;

   /**
    * This provides a convenient way for a Java properties file to
    * be loaded. This resolves the target URI to a relative file
    * within the context to be the same as the <code>getFile</code>
    * method would return. Once the file has been acquired the Java
    * properties file is loaded, each time, there is no caching of
    * the loaded properties. This ensures that changes to a loaded
    * object does not affect other users of the properties file.
    *
    * @param target the request URI that refers to the properties
    *
    * @return returns a populated <code>Properties</code> object
    * using the specified Java properties file
    *
    * @throws IOException this is thrown if the resource does not
    * exist or cannot be accessed     
    */
   public Properties getProperties(String target) throws IOException;  

   /**
    * Each <code>Context</code> must supply a <code>Locator</code> to
    * enable the system to locate configuration information and other
    * resources that reside outside the context path. This is useful 
    * when there are Java properties and XML configuration files
    * required by objects interacting with a <code>Context</code>.
    * The <code>Locator</code> employs a search to locate resources,
    * which are identified either by name or using aliases.
    *
    * @return this returns the locator used by this context object 
    */
   public Locator getLocator();
 
   /**
    * Each <code>Context</code> object must be coupled with an instance
    * of the <code>Format</code> object. This is required because each
    * <code>ResourceEngine</code> needs to serve the directory listing 
    * and the error messages in a consistent format. The resources of 
    * the instances can thus be pooled by comparing the equality of the
    * various <code>Context</code> objects. When there is an object that 
    * requires a <code>ResourceEngine</code> it can create an instance
    * of the <code>Context</code> and using a static factory method 
    * <code>ResourceEngine</code> with the context object there is a 
    * search for an active instance of the <code>ResourceEngine</code>.
    * If one is found that uses a similar context object then it is
    * returned to the caller. This enables instances and thus resources 
    * to be shared transparently.
    *
    * @return this returns the format used with this context object
    */
   public Format getFormat();

   /**
    * This enables <code>Context</code> objects to be compared for
    * equality. Instances should be considered equal if they use the
    * same base path and <code>Format</code> objects. The resources 
    * of the instances can thus be pooled by comparing the equality 
    * of the various <code>Context</code> objects. When there is an 
    * object that requires a <code>ResourceEngine</code> it can 
    * create an instance of the <code>Context</code> and using a 
    * static factory method <code>ResourceEngine</code> with the 
    * context object there is a search for an active instance of the 
    * <code>ResourceEngine</code>. If one is found that uses a similar 
    * context object then it is returned to the caller. This enables 
    * instances and thus resources to be shared transparently.
    *
    * @param obj the <code>Context</code> to be compared for equality
    *
    * @return true if the <code>Context</code> object is equal
    */
   public boolean equals(Object obj);

   /**
    * Because the <code>Context</code> objects can be compared for
    * equality they also may be stored in a hash container. This
    * enables the <code>Context</code> to be easily stored and
    * retrieved from a hash container. The <code>hashCode</code>
    * method should generate a code that has a relationship to
    * both the base path and the <code>Format</code> object.
    *
    * @return this returns a has code for the implementation
    */ 
   public int hashCode();
   
}

/*
 * FileContext.java March 2002
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

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import simple.util.cache.Cache;
import simple.util.parse.URIParser;
import simple.util.FileProperties;
import simple.util.net.Path;
import simple.util.net.URI;
import java.io.IOException;
import java.util.Properties;
import java.util.Locale;
import java.io.File;

/**
 * The <code>FileContext</code> provides an implementation of the 
 * <code>Context</code> object that provides a direct mapping from
 * a request URI as defined in RFC 2616 to an OS specific target.
 * This uses a <code>File</code> object to define the mapping
 * for the request URI paths. Using a <code>File</code> object 
 * allows the <code>FileContext</code> to be easily used with both
 * DOS and UNIX systems.
 * <p>
 * This <code>Context</code> implementation uses a properties file
 * to obtain mappings for the <code>getMimeType</code> method. The
 * file used is "Mime.properties". This determines the MIME type
 * of the request URI by matching file extension of the resource
 * with the MIME type as defined in the "Mime.properties" file.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.parse.URIParser
 * @see simple.util.parse.PathParser
 */
public class FileContext implements Context {

   /**
    * This is used to load the Mime.properties file for a list
    * of the matching mime-types and file extensions.
    */ 
   protected static ResourceBundle mime;   
   
   static {      
      try { 
         mime = ResourceBundle.getBundle(
         "simple.http.serve.Mime", Locale.getDefault());
      }catch(MissingResourceException e){
         e.printStackTrace();
      }   
   }

   /**
    * This is used to locate the configuration information.
    */
   protected Locator lookup;

   /**
    * This is the format instance used by this instance.
    */
   protected Format format;

   /**
    * This is used to cache the content objects created.
    */
   protected Cache cache;

   /**
    * This will be used to fetch the real OS system paths.
    */
   protected File base;
   
   /**
    * Constructor for creating an instance that operates from
    * the given current working path. This instance will use
    * the current path to translate the HTTP request URIs
    * into the OS specific path. This uses an implementation
    * of the <code>Format</code> from the static factory method
    * <code>FormatFactory.getInstance</code>. This will enable
    * instances to share the same format across the system.
    */
   public FileContext() {
      this(new File("."));
   }

   /**
    * Constructor for creating an instance that operates from
    * the given OS specific base path. This instance will use
    * the given base path to translate the HTTP request URIs
    * into the OS specific path. This uses an implementation
    * of the <code>Format</code> from the static factory method
    * <code>FormatFactory.getInstance</code>. This will enable
    * instances to share the same format across the system.
    *
    * @param base this is the OS specific base path for this
    */
   public FileContext(File base) {
      this(base, FormatFactory.getInstance());
   }
   
   /**
    * Constructor for creating an instance that operates from
    * the given OS specific base path. This instance will use
    * the given base path to translate the HTTP request URIs
    * into the OS specific path. This uses an implementation
    * of the <code>Format</code> from the static factory method
    * <code>FormatFactory.getInstance</code>. This will enable
    * instances to share the same format across the system.
    *
    * @param base this is the OS specific base path for this
    */
   public FileContext(File base, Format format) {
      this.lookup = new FileLocator(base);
      this.cache = new Cache();
      this.format = format;
      this.base = base;
   }
   
   /**
    * This is used to retrieve the base path of the context. The
    * base path of the context is that path that that this will
    * retrieve system information from. This represents a base
    * that the request URI paths are served from on the system.
    * For instance a base of "c:\path" would translate a URI
    * path of "/index.html" into "c:\path\index.html". Every
    * resource request must be relative to the context path
    * this allows the <code>FileEngine</code> to map the URIs
    * onto the specific OS. The base path is the OS file system
    * specific path. So on UNIX it could be "/home/user/" and
    * on a DOS system it could be "c:\web\html" for example.
    *
    * @return this returns the base path of the context
    */
   public String getBasePath() {
      return base.getAbsolutePath();
   }

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
   public String getRealPath(String target){
      return getFile(target).getAbsolutePath();            
   }
  
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
   public String getRequestPath(String target){
      return getPath(target).toString();
   }
  
   /**
    * This is used to translate the request URI path into the OS
    * specific path that it represents. This will convert the 
    * path to a format that the system can use and also represents
    * the resource path on that system. So if for example the
    * context path was "c:\path" on a DOS system and the URI path 
    * given was "/index.html" this returns "c:\path\index.html".
    * If a UNIX system was running the VM and the context base
    * was for example "/home/" then this would return the UNIX
    * path "/home/index.html" for the same request path.
    *
    * @param path this is the URI path that is to be translated 
    * into the OS specific path
    * 
    * @return this returns the OS specific path name for the 
    * translated URI path
    */   
   protected String getRealPath(Path path){
      return getFile(path).getAbsolutePath();
   }

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
   public File getFile(String target) {
      return getFile(getPath(target));
   }
   
   /**
    * This is used to translate the request URI path into the a
    * <code>File</code> object that it represents. This will convert 
    * the path to a format that the system can use and then create    
    * the <code>File</code> object for that path. So if for example 
    * the context path was "c:\path" on a DOS system and the request
    * URI given was "/index.html" this returns the <code>File</code> 
    * "c:\path\index.html". This is basically for convenience as the
    * same could be achieved using the <code>getRealPath</code> and
    * then creating the <code>File</code> from that OS specific path.
    *
    * @param path this is the URI path that is used to retrieve the
    * <code>File</code> object
    * 
    * @return returns the <code>File</code> for the given path
    */   
   protected File getFile(Path path) {
      return new File(base, path.toString().replace(
         '/', File.separatorChar));      
   }

   /**
    * This is used to translate the HTTP request URI into the a
    * <code>Path</code> object that it represents. This enables the
    * HTTP request URI to be examined thoroughly an allows various
    * other files to be examined relative to it. For example if the
    * URI referenced a path "/usr/bin/file" and some resource
    * in the same directory is required then the <code>Path</code>
    * can be used to acquire the relative path. This is useful if
    * links within a HTML page are to be dynamically generated. The
    * <code>Path.getRelative</code> provides this functionality.
    * 
    * @param target this is the HTTP request URI path that is used
    * to retrieve the <code>Path</code> object
    *
    * @return returns the <code>Path</code> for the given path
    */
   public Path getPath(String target){
      return new URIParser(target).getPath();
   }
   
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
   public Locale getLocale(String target){      
      return getLocale(getPath(target));
   }
   
   /**
    * This will parse the request URI path specified and return the 
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
    * @param path the path part of the request URI to have its     
    * locale determined
    *
    * @return this will return the locale for the specified path
    */ 
   protected Locale getLocale(Path path){
      String place = path.getCountry();
      String lang = path.getLanguage();

      if(lang == null){
         return Locale.getDefault();
      }else if(place == null){
         return new Locale(lang,"");
      }
      return new Locale(lang,place);  
   }
   
   /**
    * This method will extract the type attribute of this URI. The
    * MIME type of the request URI is extracted from the name of the
    * target. The name for the <code>Context</code> is the last path
    * segment is the token defined by RFC 2396 as path_segments. So
    * for example if the target was "some.host:8080/bin/index.html"
    * then the name for that resource would be "index.html". Once
    * the name has been extracted the MIME is defined by the file
    * extension, which for the example is text/html.
    *
    * @param target the request URI to be parsed for its type    
    *
    * @return the type of the given request URI path refers to
    */ 
   public String getMimeType(String target){
      return getMimeType(getPath(target));
   }
   
   /**
    * This method will extract the type attribute of this path. The
    * MIME type of the request path is extracted from the name of the
    * target. The name for the <code>Context</code> is the last path
    * segment is the token defined by RFC 2396 as path_segments. So
    * for example if the target was "some.host:8080/bin/index.html"
    * then the name for that resource would be "index.html". Once
    * the name has been extracted the MIME is defined by the file
    * extension, which for the example is text/html.
    *
    * @param path path that is to have its MIME type determined
    *
    * @return the type of the given resource path refers to
    */ 
   protected String getMimeType(Path path){
      try{            
         String key = path.getExtension();
         if(key != null){ 
            return mime.getString(key);
         }
      }catch(MissingResourceException e){
      }
      return "application/octetstream"; 
               
   }   
   
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
   public String getName(String target){
      return getPath(target).getName();
   }

   /**
    * This is an all in one method that allows all the information 
    * on the target URI to be gathered at once. The motivation for
    * this method is primarily convenience. However it is also used
    * to increase the performance of the <code>FileEngine</code>
    * when the <code>Context</code> implementation is synchronized.
    * This will enable the <code>FileEngine</code> to gather the
    * information on the target by acquiring the lock for the object
    * instance only once.
    *
    * @param index the object that will gather the component parts
    * of the specified request URI
    * @param target this is the request URI that is to be parsed
    */
   public void doIndex(Indexable index, String target){
      doIndex(index, getPath(target));
   }

   /**
    * This is an all in one method that allows all the information 
    * on the target path to be gathered at once. The motivation for
    * this method is primarily convenience. However it is also used
    * to increase the performance of the <code>FileEngine</code>
    * when the <code>Context</code> implementation is synchronized.
    * This will enable the <code>FileEngine</code> to gather the
    * information on the target by acquiring the lock for the object
    * instance only once.
    *
    * @param index the object that will gather the component parts 
    * of the specified request path
    * @param path this is the request path that is to be parsed
    */
   protected void doIndex(Indexable index, Path path){
      index.setRealPath(getRealPath(path));
      index.setBasePath(getBasePath());
      index.setMimeType(getMimeType(path));
      index.setLocale(getLocale(path));
      index.setName(path.getName());
      index.setFile(getFile(path));  
   }

   /**
    * This retrieves a <code>Content</code> instance that wraps the
    * specified resource. This returns a <code>Content</code> instance
    * that buffers a file up to 1 kilobyte in size. If the resource  
    * is larger a 1 kilobyte buffer is used to transfer the contents.
    * 
    * @param target this is the request URI that identifies the file
    *
    * @throws IOException this is thrown if the file resource does
    * not exist or cannot be accessed 
    */
   public Content getContent(String target) throws IOException{
      return getContent(target, 1024);      
   }
   
   /**
    * This method allows a size to be specified for the maximum
    * buffer size. If the file resource is less than the buffer size 
    * then the entire contents of the file are buffered within the
    * issued implementation. This will allocate up to 1 kilobyte for
    * files that are larger than the specified buffer size.
    * <p>
    * This implementation of the <code>getContent</code> method will
    * cache the created <code>Content</code> object. This is done to
    * increase the performance of the <code>Context</code> for 
    * objects that are requested frequently. The buffer size that
    * can be specified provides a guide for how much memory should
    * be taken up with the content object. If the content has been
    * previously cached then this will not be used.
    * 
    * @param target this is the request URI that identifies the file 
    * @param size the maximum buffer size to use for the created 
    * <code>Content</code>, if the content is cached this is not used
    *
    * @throws IOException this is thrown if the file resource does
    * not exist or cannot be accessed 
    */   
   protected Content getContent(String target, int size) throws IOException{
      if(!isCachable(target)){
         return getInstance(target,size);
      }
      Object data = cache.lookup(target);

      if(data != null) {
         return (Content)data;
      } 
      data = getInstance(target,size);
      cache.cache(target, data);
      return (Content)data;
   }

   /**
    * This is used to create the <code>Content</code> instances. The
    * <code>getInstance</code> method can be used by subclasses that
    * want to introduce dynamic <code>Content</code> objects. This
    * enables the <code>getContent</code> method to cache the
    * resulting instances without having to know what types they are.
    * <p>
    * By default the <code>FileContext</code> will produce objects
    * that will write static content as it appears on the underlying
    * file system. This uses the specified size to ensure that the
    * buffers used by the content are not larger than the maximum.
    *
    * @param target this is the request URI that identifies the file 
    * @param size the maximum buffer size to use for the content
    *
    * @throws IOException this is thrown if the file resource does
    * not exist or cannot be accessed     
    */
   protected Content getInstance(String target, int size) throws IOException{   
      if(getFile(target).length() <= size){
         return new BufferContent(this,target);
      }
      return new StreamContent(this,target);
   }
   
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
   public Properties getProperties(String target) throws IOException{
      return new FileProperties(getFile(target));
   }

   /**
    * This method is used to determine whether the target URI is
    * cachable. The request URI is considered cachable if it has
    * no parameters as defined in RFC 2396 as params. Also if the 
    * request URI contains no a query string and has a scheme a
    * domain or escaped sequences. Basically it should not contain
    * any variable information like queries and parameters that will
    * change from request to request and should be more than a path.
    *
    * @param target this is the HTTP request URI to be inspected
    *
    * @return this returns true if this is a cachable request URI
    */
   private boolean isCachable(String target){     
      return target.indexOf(';')<0 &&   
         target.indexOf('?')<0;                
   }  

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
   public Locator getLocator() {
      return lookup;
   }

   /**
    * Each <code>Context</code> object must be coupled with an instance
    * of the <code>Format</code> object. This is required because each
    * <code>FileEngine</code> needs to serve the directory listing and
    * the error messages in a consistent format. The resources of the
    * instances can thus be pooled by comparing the equality of the
    * various <code>Context</code> objects. When there is an object 
    * that requires a <code>FileEngine</code> it can create an instance
    * of the <code>Context</code> and using the static factory method 
    * <code>FileEngine.getInstance</code> with the context object there
    * is a search for an active instance of the <code>FileEngine</code>.
    * If one is found that uses a similar context object then it is
    * returned to the caller. This enables instances and thus resources 
    * to be shared transparently.
    *
    * @return this returns the format used with this context object
    */
   public Format getFormat() {
      return format;
   }  
   
   /**
    * This enables <code>Context</code> objects to be compared for
    * equality. Instances should be considered equal if they use the
    * same base path and <code>Format</code> objects. The resources 
    * of the instances can thus be pooled by comparing the equality 
    * of the various <code>Context</code> objects. When there is an 
    * object that requires a <code>FileEngine</code> it can create 
    * an instance of the <code>Context</code> and using the static 
    * factory method <code>FileEngine.getInstance</code> with the 
    * context object there is a search for an active instance of the 
    * <code>FileEngine</code>. If one is found that uses a similar 
    * context object then it is returned to the caller. This enables 
    * instances and thus resources to be shared transparently.
    *
    * @param obj the <code>Context</code> to be compared for equality
    *
    * @return true if the <code>Context</code> object is equal
    */
   public boolean equals(Object obj){
      if(obj instanceof FileContext){
         return equals((FileContext)obj);
      }
      return false;
   }

   /**
    * This enables <code>Context</code> objects to be compared for
    * equality. Instances should be considered equal if they use the
    * same base path and <code>Format</code> objects. The resources 
    * of the instances can thus be pooled by comparing the equality 
    * of the various <code>Context</code> objects. When there is an 
    * object that requires a <code>FileEngine</code> it can create 
    * an instance of the <code>Context</code> and using the static 
    * factory method <code>FileEngine.getInstance</code> with the 
    * context object there is a search for an active instance of the 
    * <code>FileEngine</code>. If one is found that uses a similar 
    * context object then it is returned to the caller. This enables 
    * instances and thus resources to be shared transparently.
    *
    * @param context the context that is to be compared for equality
    *
    * @return true if the <code>Context</code> object is equal
    */
   public boolean equals(FileContext context){
      return format.equals(context.format) &&
         base.equals(context.base);
   }

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
   public int hashCode(){
      return format.hashCode() +
         base.hashCode();
   }
}


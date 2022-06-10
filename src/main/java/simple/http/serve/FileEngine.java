/*
 * FileEngine.java February 2001
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
 
package simple.http.serve;

import simple.util.cache.Cache;
import simple.http.Request;
import simple.http.Response;
import java.io.File;

/**
 * The <code>FileEngine</code> is used to produce implementations
 * of the <code>Resource</code> object to represent files on the
 * underlying filesystem. This will produce <code>Resource</code>
 * objects that map onto the suggested URI relative to some
 * given <code>Context</code>. The <code>Context</code> must be 
 * given as a root so all requests for <code>Resource</code>'s are 
 * retrieved relative to that root. 
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
 * So the <code>FileEngine</code> object will accept the request URI 
 * that come in the form outlined above. These can include formats 
 * like 
 *
 * <pre> 
 * http://some.host/pub;param=value/bin/index.html?name=value
 * http://some.host:8080/index.en_US.html
 * some.host:8080/index.html
 * /usr/bin;param=value/README.txt
 * /usr/bin/compress.tar.gz
 * </pre>
 *
 * The <code>FileEngine</code> object will directly take a request 
 * URI as defined in RFC 2616 and translate this into a system 
 * specific <code>Resource</code>. This keeps the objects semantics 
 * simple and explicit, although at the expense of performance. 
 *
 * @author Niall Gallagher
 */
public class FileEngine implements ResourceEngine{

   /**
    * Each <code>FileEngine</code> operates using a context.
    */
   private Context context;
    
   /**
    * Each <code>Resource</code> is cached for quick retrieval.
    */
   private Cache cache;

   /**
    * Constructor for the <code>FileContext</code>. This uses the 
    * current working directory to serve the <code>Resource</code>.
    * This will retrieve and represent OS specific resources such 
    * as files using <code>Resource</code> implementations. This
    * also keeps a cache of the <code>Resource</code> objects.
    */    
   public FileEngine() {
      this(new FileContext());
   }

   /**
    * Constructor takes a <code>Context</code> implementation and
    * operates relative to that implementation. This will retrieve
    * and represent OS specific resources such as files using 
    * <code>Resource</code> implementations. This also keeps a
    * cache of the <code>Resource</code> objects requested.
    *
    * @param context this is the <code>Context</code> that this
    * implementation will work under
    */ 
   public FileEngine(Context context){
      this.cache = new Cache();
      this.context = context;
   }

   /**
    * Constructor takes a <code>Context</code> implementation and
    * operates relative to that implementation. This will retrieve
    * and represent OS specific resources such as files using 
    * <code>Resource</code> implementations. This also keeps a
    * cache of the <code>Resource</code> objects requested.
    * <p>
    * This constructor method allows the cache specifications to
    * be given to the <code>FileEngine</code>. The specifications
    * allowed are the number of regions and the number of items 
    * within a region, see <code>simple.util.cache.Cache</code>.
    *
    * @param context this is the <code>Context</code> that this
    * implementation will work under
    * @param lock specifies the number of locks within the cache
    * @param list specifies the size of each list in the cache    
    */ 
   public FileEngine(Context context, int lock, int list){
      this.cache = new Cache(lock, list);
      this.context = context;
   }

   /**
    * This will look for and retrieve the requested resource. The 
    * target given must be in the form of a request URI. This will
    * locate the resource and return the <code>Resource</code>
    * implementation that will handle the target.
    *
    * @param target the URI style path that represents the target 
    * <code>Resource</code>
    *
    * @return this returns the <code>Resource</code> object to
    * handle the desired target
    *
    * @throws IllegalArgumentException if the path given is not 
    * relative URI style
    */
   public Resource lookup(String target){
      return lookup(target,context.getFile(target));
   }

   /**
    * This will look for and retrieve the requested resource. The 
    * target given must be in the form of a request URI. This will
    * locate the resource and return the <code>Resource</code>
    * implementation that will handle the target.
    *
    * @param target the URI style path that represents the target 
    * <code>Resource</code>
    * @param file this is the <code>File</code> for the resource
    *
    * @return this returns the <code>Resource</code> object to
    * handle the desired target
    *
    * @throws IllegalArgumentException if the path given is not 
    * relative URI style
    */
   private Resource lookup(String target, File file){
      if(!isCachable(target)){
         return getResource(target,file); 
      }         
      Object data = cache.lookup(target);       

      if(data != null) {
         return (Resource)data;
      } 
      data = getResource(target,file);      

      if(file.exists()){ /* don't cache errors*/
         cache.cache(target, data);            
      }         
      return (Resource)data;

   }
   
   /**
    * This will look for and retrieve the requested resource. The 
    * target given must be in the form of a request URI. This will
    * locate the resource and return the <code>Resource</code>
    * implementation that will handle the target.
    *
    * @param target the URI style path that represents the target 
    * <code>Resource</code>
    * @param file the OS specific file object for the resource
    *
    * @return this creates the <code>Resource</code> object to
    * handle the desired target
    *
    * @throws IllegalArgumentException if the path given is not 
    * relative URI style
    */   
   protected Resource getResource(String target, File file) {      
      if(file.isDirectory()) {
         return new DirectoryResource(context,target);
      }else if(file.isFile()){
         return new FileResource(context,target);
      } else return new BasicResource(context){
         protected void process(Request req, Response resp){
            handle(req, resp, 404);
         }
      }; 
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
}

/*
 * FileResource.java February 2001
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

import simple.http.Response;
import simple.http.Request;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

/**
 * The <code>FileResource</code> provides an implementation of the
 * <code>Resource</code> that can be used to acquire generic files. 
 * The <code>FileResource</code> is an <code>IndexedResource</code>. 
 * This means that all the meta information on the file is acquired 
 * once the resource is instantiated.
 * <p>
 * This <code>Resource</code> caches the resource that it represents. 
 * This allows the contents to be emitted to the client quickly. If 
 * the size of the <code>File</code> is greater than the maximum 
 * possible size of a body that can be cached then this will cache 
 * certain resources which enable the <code>Resource</code> to be 
 * acquired quicker even though that resource is not actually cached. 
 * By default the maximum size of a file that can be cached is 8Kb.
 *
 * @author Niall Gallagher 
 */
final class FileResource extends CachedResource {
   
   /**
    * The files meta information is taken from the path that is 
    * specified. The <code>IndexedResource</code> constructor will 
    * provide the meta information for the specified resource. 
    * Once the meta information of the resource has been provided 
    * then the resource is loaded into an internal byte buffer, if 
    * it is less that 8Kb in length. If the resource is too large 
    * to be fully buffered then the resource can cache useful objects.
    *
    * @param target this is the HTTP request URI for this resource
    * @param context the root context of this file resource
    */ 
   public FileResource(Context context, String target){
      super(context, target);     
   }
   
   /**
    * This method handles the HTTP request and response. When a 
    * HTTP request is generated it is given to this method. This
    * will then either respond with the appropriate error message
    * or process the request.
    * <p>
    * If the file requested does not exist then this will use the
    * <code>BasicResource.handle(Request,Response,int)</code> to
    * generate the appropriate response.
    * <p>
    * If the method used is HEAD then this will write only the
    * headers and will subsequently close the pipeline. However
    * this will not handle POST, OPTIONS, TRACE, DELETE or PUT
    * requests and will generate a "501 Not Implemented" message
    * if attempted, see RFC 2616 sec 5.1.1.
    *
    * @param req the <code>Request</code> to be processed
    * @param resp the <code>Response</code> to be processed
    *
    * @exception Exception throw if theres an I/O error
    */   
   protected void process(Request req, Response resp)throws Exception {
      if(req.getDate("If-Modified-Since")< getLastModified()){
         resp.setDate("Date", System.currentTimeMillis());
         resp.setDate("Last-Modified",getLastModified());
         resp.set("Content-Type",type);
         resp.setContentLength(getLength());
      
         if(req.getMethod().equals("HEAD")){
            resp.commit(); 
         }else if(req.getMethod().equals("GET")){
            write(resp.getOutputStream());  
            resp.getOutputStream().close(); 
         }else{
            handle(req,resp,501); 
         }
      }else {
         handle(req,resp,304);
      }
   }    
}

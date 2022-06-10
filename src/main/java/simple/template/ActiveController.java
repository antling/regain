/*
 * ActiveController.java December 2003
 *
 * Copyright (C) 2003, Niall Gallagher <niallg@users.sf.net>
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

package simple.template;

import simple.http.load.ReflectionService;
import simple.http.serve.Context;
import simple.http.Response;
import simple.http.Request;

/**
 * The <code>ActiveController</code> exploits the asynchronous
 * ability of the <code>simple.http</code> framework. This enables
 * the <code>Controller</code> to process the HTTP transaction in a 
 * separate thread. This will allow the subclass to implement the
 * <code>process</code> method. This method will be called from an 
 * asynchronous thread, so it allows the <code>Resource</code> to
 * continue processing some job even when the HTTP response has been
 * sent and committed. Further requests on the same HTTP pipeline can
 * be processed while this is still active processing some task.
 * <p>
 * A HTTP pipeline contains many HTTP requests so when one is taken
 * from the pipeline and handed to the <code>Controller</code> the 
 * response must be waited for until the next request is handled, 
 * this is an ordering rule of the pipeline. However there may be 
 * need to continue with processing after the response has been dealt
 * with. This <code>Controller</code> implementation allows the 
 * subclass to take as long as it needs in handling the response and
 * to continue processing after the response has been committed.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.Response
 */
public class ActiveController extends BasicController {

   /**
    * Constructor for the <code>ActiveController</code> is used so
    * that the <code>Context</code> can be acquired. This enables 
    * the implementation to retrieve resources from the underlying
    * file system. The <code>Context</code> is required for format
    * of errors and directory listing. If the context is null the
    * implementation will not generate HTTP error messages.
    *
    * @param context this is the <code>Context</code> that is 
    * used to generate error messages and acquire files with
    */
   public ActiveController(Context context){
      super(context);
   }

   /**
    * This enables the <code>Resource</code> to be handled in a 
    * separate thread of execution. The <code>Request</code> and
    * <code>Response</code> objects are given to the super class 
    * <code>process</code> method where they can be processed.
    * <p>
    * The <code>Service.handle</code> can be implemented without 
    * risk of unhandled exceptions as they can be handled by the
    * <code>handle(Request,Response,int)</code> method, using 
    * the HTTP 500 Internal Server Error response status.
    *
    * @param req the <code>Request</code> to be processed
    * @param resp the <code>Response</code> to be processed 
    */
   public void handle(final Request req, final Response resp) {
      new Thread(new Runnable(){
         public void run(){
            try {
              process(req, resp);
            }catch(Exception e) {
               handle(req, resp, 500);
            }
         }
      }).start();
   }
}


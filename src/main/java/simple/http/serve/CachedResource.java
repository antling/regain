/*
 * CachedResource.java February 2001
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;

/**
 * The <code>CachedResource</code> is used to represent a filesystem
 * <code>Resource</code> that can be cached. This will either cache
 * the whole file or certain resources that allow the file to be 
 * used with less overhead.
 * <p>
 * If the <code>File</code> is larger than 8KB then it will not be 
 * cached fully, however a swap buffer will be set aside for use
 * when the next client requests the resource. If the length is 
 * less than 8KB then the contents of the <code>File</code> is
 * fully cached.
 * <p>
 * This extends the <code>IndexedResource</code> and so provides
 * all the meta information about the requested <code>File</code>
 * from its name. 
 *  
 * @author Niall Gallagher
 */
abstract class CachedResource extends IndexedResource{

   /**
    * This is the maximum size of a file that can be cached.
    */
   private static final int MAX_SIZE = 8192;

   /**
    * This will contain the contents of the <code>File</code>.
    */
   private volatile Content cache;

   /**
    * Creates a default <code>CachedResource</code> that is used 
    * to represent a <code>Resource</code> that can be cached. The
    * <code>Resource</code> that can be cached is a file system 
    * object and is based on the <code>IndexedResouce</code>. The
    * <code>File</code> that this represents can be fully cached
    * if it is less than 8KB. If the <code>File</code> is larger
    * that this only certain resources will be cached which will
    * make retrieving the contents of the <code>File</code> less
    * painful.
    *
    * @param target the HTTP request URI that represents this
    * @param context this is the root context of the resource
    */
   protected CachedResource(Context context, String target){
      super(context, target);
   }

   /**
    * This initialized the resources that are to be cached by this
    * object. This is important for performance. If the resource 
    * that this represents is larger that 8KB then this whole body
    * of the file cannot be cached. So this method is used to 
    * ensure that if the file is less than 8KB that it is cached
    * straight away so that the <code>write</code> method does
    * not have to synchronize any parts.
    *
    * @exception IOException thrown if the target specified
    * could not be read
    */
   private synchronized void init() throws IOException {
      if(file.length() > MAX_SIZE) {
         cache = new StreamContent(context,target);
      } else if(cache == null) {
         cache = new BufferContent(context,target);
      }
   }

   /**
    * This is the method that is used to emit the cached resource.
    * If this is the first time the resource is being used then
    * this will either load the contents of the <code>File</code>
    * into a byte buffer, or if it is too big it will swap bytes
    * from the <code>FileInputStream</code> to the given 
    * <code>OutputStream</code> using a fixed size byte buffer 
    * of 1KB. This method is not synchronized and so many threads
    * can access it concurrently without problems. Synchronization
    * is not needed in this method.
    *
    * @param out the <code>OutputStream</code> that the cached
    * body will be written to
    *
    * @exception IOException thrown if there is a I/O error    
    */
   protected void write(OutputStream out) throws IOException {
      if(cache == null) init();
      cache.write(out);
   }
}

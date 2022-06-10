/*
 * StreamContent.java November 2002
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

import simple.util.net.Parameters;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * The <code>StreamContent</code> object wraps a file which is
 * too large to be buffered fully. Buffering resources requires a 
 * large amount of memory, this <code>Content</code> implementation 
 * ensures that the entire file is not stored in memory. 
 * <p>
 * This allocates a buffer 1 kilobyte in size which is used to 
 * transfer the contents of a <code>File</code> object to the 
 * issued <code>OutputStream</code>. The <code>getMimeType</code> 
 * method returns the value of <code>Context.getMimeType</code>.
 * 
 * @author Niall Gallagher
 */      
class StreamContent extends IndexedContent{

   /**
    * Constructor for the <code>StreamContent</code> creates an
    * instance of the <code>Content</code> for large files. This uses
    * the <code>Context.doIndex</code> method to retrieve a the meta
    * information of the <code>File</code> represented by this.
    *
    * @param context the <code>Context</code> used for indexing
    * @param target this is the request URI that refers to the file
    */
   public StreamContent(Context context, String target){
      super(context, target);
   }

   /**
    * The <code>write</code> method writes the contents of the file
    * to the issued <code>OutputStream</code>. This is done using the
    * allocated buffer to swap bytes from an <code>InputStream</code>
    * to the <code>OutputStream</code>. This does not use parameters.
    *
    * @param out the <code>OutputStream</code> to write the contents
    *
    * @exception IOException this is thrown if the issued stream has
    * an I/O problem writing the contents
    */
   public void write(OutputStream out) throws IOException{
      write(out, null);
   }

   /**
    * The <code>write</code> method writes the contents of the file
    * to the issued <code>OutputStream</code>. This is done using the
    * allocated buffer to swap bytes from an <code>InputStream</code>
    * to the <code>OutputStream</code>. This does not use parameters.
    *
    * @param out the <code>OutputStream</code> to write the contents
    * @param data the issued options are ignored as this is static
    *
    * @exception IOException this is thrown if the issued stream has
    * an I/O problem writing the contents
    */   
   public void write(OutputStream out, Parameters data) throws IOException{
      InputStream file = getInputStream();
      byte[] buf = new byte[1024];
   
      for(int count = 0; ;) {
         count = file.read(buf);
         if(count < 0) break;
         out.write(buf,0,count);
      }
      file.close();
   }

   /**
    * This will return the MIME type for the contents of the file.
    * This returns the same string as <code>getMimeType</code>
    * for the request URI identifying the resource.
    *
    * @return the MIME type for the file resources contents
    */
   public String getMimeType(){
      return type;
   }
}

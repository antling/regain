/*
 * BufferContent.java November 2002
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
 * The <code>BufferContent</code> represents content that is stored 
 * within an internal buffer. This acquires the meta data of the file
 * using the issued <code>Context</code> object and buffers the
 * contents using a byte array. This is provided so that files can 
 * be cached in memory so that repeated requests for a popular file 
 * can be done quicker.
 * <p>
 * This implementation of the <code>Content</code> interface is used
 * when the <code>Context.getContent</code> method is given a size
 * that is less than the size of the file. For a caching scheme the
 * size of the file should not be too large to conserve memory.
 *
 * @author Niall Gallagher
 */
class BufferContent extends IndexedContent{  
 
   /**
    * The buffer that contains the contents of the file.
    */
   private byte[] cache;
 
   /**
    * Constructor for the <code>BufferContent</code> acquires the
    * bytes for the specified file using an <code>InputStream</code>
    * retrieved from the <code>IndexedContent</code>. This is then
    * used to acquire the bytes of the file and cache them in main 
    * memory so that invocations of the <code>write</code> method 
    * can be done without refering to the file system.
    *
    * @param context the <code>Context</code> used for indexing
    * @param target the request URI that refers to the file
    * 
    * @exception IOException this is thrown if the input stream
    * experiences an I/O problem
    */
   public BufferContent(Context context, String target) throws IOException{
      super(context, target);
      init(getLength());
   }

   /**
    * This method is used by the constructor to store the contents
    * of the <code>File</code> in main memory. The file is read 
    * using a <code>FileInputStream</code>, all bytes are read until
    * the stream returns the end of stream character.
    *
    * @param size this is the length of the file to be buffered
    *
    * @exception IOException this is thrown if the input stream
    * experiences an I/O problem
    */
   private void init(int size) throws IOException{         
      init(getInputStream(),size);
   }   

   /**
    * This method is used by the constructor to store the contents
    * of the <code>File</code> in main memory. The file is read 
    * using a <code>FileInputStream</code>, all bytes are read until
    * the stream returns the end of stream character.
    *
    * @param size this is the length of the file to be buffered
    * @param file the contents of the file that is to be buffered
    *
    * @exception IOException this is thrown if the input stream
    * experiences an I/O problem
    */
   private void init(InputStream file, int size) throws IOException{   
      byte[] buf = new byte[size];

      for(int off = 0; off < size;) {
         int left = size - off;         
         int count = file.read(buf,off,left); 
         if(count < 0) break;
         off += count;
      }   
      cache = buf;
      file.close();
   }
   
   /**
    * The <code>write</code> method writes the contents of the file
    * to the issued <code>OutputStream</code>. This is done using the
    * allocated buffer to which involves a single write operation.
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
    * allocated buffer to which involves a single write operation.
    *
    * @param out the <code>OutputStream</code> to write the contents
    * @param data the issued options are ignored as this is static      
    *
    * @exception IOException this is thrown if the issued stream has
    * an I/O problem writing the contents
    */  
   public void write(OutputStream out, Parameters data) throws IOException{
      out.write(cache);
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

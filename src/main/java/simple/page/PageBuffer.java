/*
 * PageBuffer.java December 2002
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
  
package simple.page;

import simple.http.serve.Context;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.File;

/**
 * The <code>PageBuffer</code> is used so that the SSI file can be
 * parsed from memory rather than having to reload it on every 
 * instance. This helps to increase the performance of the
 * <code>PageContent.write</code> method. This reads the contents
 * of the file in UTF-8 format, which keeps the output consistent.
 * <p>
 * This is used with the <code>PageContent</code> so that the SSI
 * page can be written to the <code>PageParser</code> from memory
 * rather than having to re-read it from the file system.
 *
 * @author Niall Gallagher
 */
final class PageBuffer {

   /**
    * This contains the contents of the SSI page buffered.
    */
   private char[] cache;

   /**
    * After conversion, this is the number of characters.
    */     
   private int length;

   /**
    * This references the SSI page buffered by this.
    */ 
   private File file;

   /**
    * Constructor for the <code>PageBuffer</code>. This will read
    * the contents of the file <code>Context.getFile</code> with
    * the given request URI. If the file cannot be read then
    * this will throw an <code>IOException</code>.
    *
    * @param context this is the <code>Context</code> for the page
    * @param name this is the request URI which points to the page
    *
    * @exception IOException thrown if there is an I/O problem
    */
   public PageBuffer(Context context, String name) throws IOException {
      this.file = context.getFile(name); 
      init(file, file.length());     
   }
 
   /**
    * This will read the contents of the given <code>File</code>.
    * The given file is read from the underlying file system
    * in UTF-8 format. If the file cannot be read then this will
    * throw an <code>IOException</code>.
    *
    * @param path this is the SSI page that is to be buffered
    * @param size this is the size of the page to be buffered
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void init(File path, long size) throws IOException {          
      init(new FileInputStream(path), (int)size);
   }

   /**
    * This will read the contents of the <code>InputStream</code>.
    * The given stream is used to acquire the contents of the 
    * SSI page. If the file cannot be read then this will throw
    * an <code>IOException</code>.
    *
    * @param page this is the SSI page that is to be buffered
    * @param size this is the size of the page to be buffered
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void init(InputStream page, int size) throws IOException {          
      init(new InputStreamReader(page, "utf-8"), size);
   } 

   /**
    * This will read the contents of the given <code>Reader</code>.
    * The given reader is used to acquire the contents of the 
    * SSI page. If the file cannot be read then this will throw
    * an <code>IOException</code>. The uses the issued reader to
    * acquire the contents of the file in UTF-8 format.
    * <p>
    * Because the UTF-8 format consists of 1, 2, and 3 byte words
    * the length of a file cannot be determined in characters. So
    * This will create a buffer that can hold the maximum possible
    * number of characters (1 byte per character). 
    *
    * @param page this is the SSI page that is to be buffered
    * @param size this is the size of the page to be buffered
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void init(Reader page, int size) throws IOException {
      char[] buf = new char[size];      
      
      for(int off = 0; off < size;) {               
         int left = buf.length - off;
         int count = page.read(buf,off,left);         
         if(count < 0) break; 
         length += count;        
         off += count;
      }       
      cache = buf;
      page.close();   
   }

   /**
    * This writes the contents of the buffered page to the given 
    * <code>Writer</code>. If there is a problem with writing the
    * buffered file an <code>IOException</code> is thrown.
    *
    * @param out used to write the contents of the buffered page
    *
    * @exception IOException thrown if there is an I/O problem
    */
   public void write(Writer out) throws IOException {
      out.write(cache, 0, length);
   }
}

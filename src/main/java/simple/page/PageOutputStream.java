/*
 * PageOutputStream.java December 2002
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

import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;

/**
 * The <code>PageOutputStream</code> is used to write characters and 
 * bytes to an <code>OutputStream</code>. This provides buffering of 
 * characters written so that the conversion process between the 
 * characters and bytes is not done very frequently. The converted
 * characters are written to the underlying stream using UTF-8.
 * <p>
 * The <code>flush</code>  method will not flush the underlying 
 * <code>OutputStream</code> so that buffering will not be affected. 
 * This is used by the <code>PageWriter</code> as a bridge between 
 * the byte stream and the UTF-8 page written with the writer.
 *
 * @author Niall Gallagher
 *
 * @see simple.page.PageWriter
 */ 
class PageOutputStream extends OutputStream {

   /**
    * This is the underlying stream that all data is written to.
    */
   private OutputStream direct;
   /**
    * This is used to convert the written characters to bytes.
    */
   private Writer encode;

   /**
    * Constructor for the <code>PageOutputStream</code> object. This
    * is used to create an instance of the stream that uses a 512
    * character buffer. The character buffer is used to buffer any
    * characters written so that frequent invocations of the
    * character converter are not required.
    *
    * @param out the underlying <code>OutputStream</code> used
    *
    * @exception IOException if the UTF-8 charset is not supported
    */
   public PageOutputStream(OutputStream out) throws IOException{
      this(out, 512);
   }
   
   /**
    * Constructor for the <code>PageOutputStream</code> object. This
    * is used to create an instance of the steam with a specified
    * size for the character buffer used. The character buffer is 
    * used to buffer any characters written, for performance.
    *
    * @param out the underlying <code>OutputStream</code> used
    * @param size this is the size of the character buffer to use
    *
    * @exception IOException if the UTF-8 charset is not supported
    */
   public PageOutputStream(OutputStream out, int size) throws IOException{
      this(size, new ProxyOutputStream(out));
   }
   
   /**
    * Constructor for the <code>PageOutputStream</code> object. This
    * is used to create an instance of the steam with a specified
    * size for the character buffer used. The character buffer is 
    * used to buffer any characters written, for performance.
    *
    * @param out the underlying <code>OutputStream</code> used
    * @param size this is the size of the character buffer to use
    *
    * @exception IOException if the UTF-8 charset is not supported
    */
   private PageOutputStream(int size, OutputStream out) throws IOException {
      this.encode = new OutputStreamWriter(out, "utf-8");
      this.encode = new BufferedWriter(encode, size);
      this.direct = out;
   }

   /**
    * This is used to write characters to the underlying stream. The
    * characters written to the underlying stream are encoded using
    * the UTF-8 format. This encoding is required so that the SSI
    * page can be written in a consistant manner.
    * <p>
    * This will buffer the characters written until an explicit use
    * of the <code>flush</code> method or until one of the other
    * <code>write</code> methods is invoked. 
    *
    * @param buf the array of characters to write to the stream
    * @param off the offset within the array of characters to write
    * @param len this is the number of characters to write
    *
    * @exception IOException is thrown if there is an I/O problem
    */
   public void write(char[] buf, int off, int len) throws IOException {
      encode.write(buf, off, len);
   }

   /**
    * This is used to write bytes to the underlying stream. This will
    * flush any characters written previously so that the output to
    * the underlying stream is sequential. The data written to the
    * underlying stream must be encoded in UTF-8 format. This must be
    * done to ensure that the client recieves coherant output.
    *    
    * @param octet the byte that is to be written to the stream
    *
    * @exception IOException is thrown if there is an I/O problem    
    */
   public void write(int octet) throws IOException {
      encode.flush();
      direct.write(octet);
   }

   /**
    * This is used to write bytes to the underlying stream. This will
    * flush any characters written previously so that the output to
    * the underlying stream is sequential. The data written to the
    * underlying stream must be encoded in UTF-8 format. This must be
    * done to ensure that the client recieves coherant output.
    *    
    * @param buf an array of bytes to write to the underlying stream
    * @param off the offset within the array of to begin writing
    * @param len this is the number of bytes to be written
    *
    * @exception IOException is thrown if there is an I/O problem    
    */
   public void write(byte[] buf, int off, int len) throws IOException {
      encode.flush();
      direct.write(buf, off, len);
   }

   /**
    * This will flush any characters that are buffered to the stream.
    * This will not flush the underlying <code>OutputStream</code>
    * so that any buffering used by the stream is not affected.    
    * 
    * @exception IOException is thrown if there is an I/O problem        
    */
   public void flush() throws IOException {
      encode.flush();
   }

   /**
    * This will flush an close the underlying output stream. This
    * ensures that if there are any characters buffered that they
    * are flushed to the stream before the stream is closed.
    *
    * @exception IOException is thrown if there is an I/O problem        
    */
   public void close() throws IOException {
      encode.close();
   }
}

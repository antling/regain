/*
 * ProxyOutputStream.java December 2003
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
 
package simple.template.velocity;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * The <code>ProxyOutputStream</code> object is used to prevent the
 * flushing of an underlying <code>OutputStream</code>. This will
 * effectively protect the <code>flush</code> method of the output
 * stream, so as to avoid performance degradations that result
 * from flushing all cascading buffers.
 * <p>
 * The intended usage of this is with buffered streams or writers
 * that need to buffer content before writing to a stream. In this
 * scenario the <code>ProxyOutputStream</code> can wrap a stream
 * in such a manner that flushing affects only the outer stream.
 * 
 * @author Niall Gallagher
 *
 * @see java.io.BufferedOutputStream#flushBuffer
 */
class ProxyOutputStream extends FilterOutputStream {

   /**
    * Constructor for the <code>ProxyOutputStream</code>. The
    * super class constructor is called by this to create a
    * stateless proxy, that delegates to the internal stream,
    * except for the <code>flush</code> method.
    *
    * @param out the stream that this object will write to 
    */
   public ProxyOutputStream(OutputStream out) {
      super(out);
   }

   /**
    * Replaces the super class <code>write</code> method so the
    * byte array is given directly to the underlying stream.
    * The <code>FilterOutputStream</code> writes the array one
    * byte at a time, this is clearly a performance hit when 
    * there are large numbers of bytes to be written at a time.
    *
    * @param buf array of bytes to write to the internal stream
    * @param off offset within the array of to begin writing
    * @param len this is the number of bytes to be written
    *
    * @exception IOException thrown if there is an I/O problem    
    */
   public void write(byte[] buf, int off, int len) throws IOException {
      out.write(buf, off, len);
   }

   /**
    * Replaces the super class <code>flush</code> method so the
    * bytes remain within the internal stream. The use of the
    * flush mechanism is avoided to improve the performance of
    * cascading buffers. This keeps bytes buffered within the
    * internal stream and ensures that it can never be flushed.
    */
   public void flush() {}
}

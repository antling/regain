/*
 * ProxyOutputStream.java July 2003
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
 
package simple.page;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * The <code>ProxyOutputStream</code> object is used to improve the
 * performance of cascading SSI pages. When an SSI page references
 * an object specified by an include statement that object must be 
 * written with the issued stream. The stream used by the referenced
 * object is a <code>PageOutputStream</code>.  This means that if
 * an SSI page references an SSI page, the cascading of the issued
 * streams can degrade performance.
 * <p>
 * In order to avoid the performance degradation resulting from 
 * cascading SSI references, the <code>flush</code> method must be
 * blocked. This will ensure that the primary stream does not get
 * flushed frequently by the UTF-8 character encoders used by the 
 * SSI pages.
 *
 * @author Niall Gallagher
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
    * the SSI page generation. This keeps bytes buffered in
    * all stream encoders used, which avoids having to invoke
    * the costly translation process for every flush used.
    */
   public void flush() {}
}

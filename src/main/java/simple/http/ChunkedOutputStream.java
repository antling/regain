/*
 * ChunkedOutputStream.java February 2001
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
 
package simple.http;

import java.io.OutputStream;
import java.io.IOException;

/**
 * The <code>ChunkedOutputStream</code> is used to write data in the
 * chunked transfer encoding to an underlying <code>OutputStream</code>.
 * This is a <code>MonitoredOutputStream</code> and so notifies an
 * <code>OutputMonitor</code> of any events regarding the writing of
 * the data. The chunked encoding procedure can be found in RFC 2616.
 * <pre>
 *
 *    chunked-body   = *chunk last-chunk trailer CRLF
 *    chunk          = chunk-size [ chunk-extension ] CRLF
 *                     chunk-data CRLF
 *    chunk-size     = 1*HEX
 *    last-chunk     = 1*("0") [ chunk-extension ] CRLF
 *
 *    chunk-extension= *( ";" chunk-ext-name [ "=" chunk-ext-val ] )
 *    chunk-ext-name = token
 *    chunk-ext-val  = token | quoted-string
 *    chunk-data     = chunk-size(OCTET)
 *    trailer        = *(entity-header CRLF) 
 *
 * </pre>
 * The above BNF description of the chunked transfer encoding is from
 * RFC 2616, section 3.6.1. This encoding is self delimiting, so as
 * a <code>MonitoredOutputStream</code> this is only required to give
 * notification of <code>notifyFinished</code> when the stream is 
 * closed and <code>notifyError</code> if there is a problem writing.
 *
 * @author Niall Gallagher
 */
class ChunkedOutputStream extends MonitoredOutputStream { 

   /**
    * This is used to quickly create a byte array of the length. The
    * write space within this buffer is the first eight bytes which
    * is enough to allow the maximum integer value stored within it.
    */
   private byte[] size = {'0', '0', '0', '0', '0', 
                          '0', '0', '0',  13, 10};

   /**
    * This is used to quickly calculate the hexidecimal chunk size.
    */
   private byte[] index = {'0', '1', '2', '3', '4', '5','6', '7',
                          '8', '9', 'a', 'b', 'c', 'd','e', 'f'};

   /**
    * This is the zero length chunk that is used to end the stream.  
    */
   private byte[] zero = {'0', 13, 10, 13, 10};

   /**
    * Constructor for the <code>ChunkedOuptutStream</code>. This is
    * used to create an <code>OutputStream</code> that can write data
    * in the chunked transfer encoding to the issued stream. This 
    * is will write data in chunks to the stream and notify the given
    * <code>OutputMonitor</code> of any significant events that occur
    * during the writing of the data, see <code>OutputMonitor</code>.
    *
    * @param out this is the stream that the chunks are written to
    * @param mon this is the monitor that will received notifications
    */
   public ChunkedOutputStream(OutputStream out, OutputMonitor mon){
      super(out, mon);
   }
   
   /**
    * The <code>writeBytes</code> method is used to write data to the to
    * the underlying <code>OutputStream</code>. This will write the data
    * directly to the stream so that it will cascade well when buffering
    * is performed at the socket and pipeline level.
    * <p>
    * For optimal performance this makes use of an internal size buffer
    * which can be used to write the hexidecimal length of the chunk to.
    * This can take a hexidecimal length of <code>ffffffff</code>, which
    * is also the maximum integer length. This buffer also contains the
    * required <code>CRLF</code> terminal for the size line.
    *
    * @param buf this is the data that is to be written to the stream
    * @param off this is the offset within the array to begin writing
    * @param len this is the number of bytes to be written 
    *
    * @exception IOException if there is a problem writing the data 
    */
   protected void writeBytes(byte[] buf, int off, int len) throws IOException {
      int pos = 7;

      if(len > 0) {
         for(int num = len; num > 0; num >>>= 4){      
            size[pos--] = index[num & 0xf];
         }
         out.write(size, pos + 1, 9 - pos);      
         out.write(buf, off, len);
         out.write(size, 8, 2);
      }
   }

   /**
    * The <code>flushBytes</code> method flushes any bytes that are
    * be buffered by the underlying <code>OutputStream</code>. This
    * basically uses the output streams <code>flush</code> method.
    *
    * @exception IOException if there is an I/O problem flushing
    */
   protected void flushBytes() throws IOException {
      out.flush();      
   }
   
   /**
    * This is used to perform the final write of data to the stream.
    * This will write the zero length size line to the stream. If 
    * the underlying <code>OutputStream</code> causes an error then 
    * the monitor is notified of a <code>notifyError</code> event.
    *
    * @exception IOException if there is an I/O problem writing
    */
   protected void doClose() throws IOException {
      out.write(zero);
      mon.notifyFinished(out);
   }
}

/*
 * ChunkedInputStream.java February 2001
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

import java.io.InputStream;
import java.io.IOException;

/**
 * The <code>ChunkedInputStream</code> is reads an decodes a stream
 * using the chunked transfer coding. This is used so that any data
 * sent in the chunked transfer coding can be decoded, and event
 * notification can be sent to an <code>OutputMonitor</code>. For
 * details on notification see <code>MonitoredInputStream</code>.
 * <pre>
 *
 *    length := 0
 *    read chunk-size, chunk-extension (if any) and CRLF
 *    while (chunk-size &gt; 0) {
 *       read chunk-data and CRLF
 *       append chunk-data to entity-body
 *       length := length + chunk-size
 *       read chunk-size and CRLF
 *    }
 *    read entity-header
 *    while (entity-header not empty) {
 *       append entity-header to existing header fields
 *       read entity-header
 *    }
 *
 * </pre>
 * The above algorithm is taken from RFC 2616 section 19.4.6. This
 * coding scheme is used in HTTP pipelines so that dynamic content,
 * that is, content with which a length cannot be determined does
 * not require a connection close to delimit the message body. 
 *
 * @author Niall Gallagher
 */
class ChunkedInputStream extends MonitoredInputStream {

   /**
    * The is the terminating token for the stream trailer.
    */
   private static final byte[] token = {
   13, 10, 13, 10};
   
   /**
    * This is used to accumulate the size line, and to skip.
    */
   private byte buf[] = new byte[1024];

   /**
    * The number of bytes remaining from the current chunk.
    */
   private int chunk;

   /**
    * Determines whether the stream has a trailer.
    */
   private boolean foot;   
   
   /**
    * Constructor for the <code>ChunkedInputStream</code>. This will
    * create a <code>MonitoredInputStream</code> that is used to 
    * read and decode chunked encoded data from an underlying stream.
    *
    * @param in the underlying stream that is used to read bytes
    * @param mon this is used to recieve notification of events
    */
   public ChunkedInputStream(InputStream in, InputMonitor mon) {   
      this(in, mon, false);
   }

   /**
    * Constructor for the <code>ChunkedInputStream</code>. This will
    * create a <code>MonitoredInputStream</code> that is used to 
    * read and decode chunked encoded data from an underlying stream.
    *
    * @param in the underlying stream that is used to read bytes
    * @param mon this is used to recieve notification of events
    * @param foot this determines wheter the stream has a trailer
    */
   public ChunkedInputStream(InputStream in, InputMonitor mon, boolean foot) {
      super(in, mon);
      this.foot = foot;
   }

   /**
    * The <code>readBytes</code> method is used to read and decode the
    * chunked data from the <code>InputStream</code>. This will read
    * the decoded data into the given buffer. The decoding of the data
    * is done using the BNF description given in RFC 2616 secion 3.6.1.
    * <p>
    * This performs the same operation as the <code>readBytes</code>
    * except this performs a blocking operation. So if the underlying
    * stream is blocking then this will block. See <code>read</code>.
    *
    * @return this returns the octet read from the underlying stream
    *
    * @exception IOException if there is an exception read the data    
    */
   protected int readByte() throws IOException {
      int octet = -1;
      
      while(octet < 0) {
         if(chunk < 0) {
            break;
         } else if(chunk > 0){
            octet = in.read();         

            if(--chunk == 0) {
               doControl();
            }
            break;         
         }else {
            nextChunk();
         }
      }
      return octet;
   }

   /**
    * The <code>readBytes</code> method is used to read and decode the
    * chunked data from the <code>InputStream</code>. This will read
    * the decoded data into the given buffer. The decoding of the data
    * is done using the BNF description given in RFC 2616 secion 3.6.1.
    * <pre>
    *
    *    chunked-body   = *chunk last-chunk trailer CRLF
    *    chunk          = chunk-size [ chunk-extension ] CRLF
    *                     chunk-data CRLF
    *    chunk-size     = 1*HEX
    *    last-chunk     = 1*("0") [ chunk-extension ] CRLF
    *
    *    chunk-extension= *(";" chunk-ext-name [ "=" chunk-ext-val ])
    *    chunk-ext-name = token
    *    chunk-ext-val  = token | quoted-string
    *    chunk-data     = chunk-size(OCTET)
    *    trailer        = *(entity-header CRLF) 
    *
    * </pre>    
    * This will decode the data so that a continous stream of data is
    * presented. If a trailer exists then it is read from the stream
    * but not returned as data. Ignoring the trailer is permitted as
    * RFC 2616 section 3.6.1 states the the trailer can contian only
    * optional meta-data.
    *
    * @param buf the buffer that is to be filled with the read data
    * @param off this is the start of the region to write the bytes
    * @param len this is the length of the region to write the bytes     
    *
    * @return this returns the number of bytes read from the stream
    *
    * @exception IOException if there is an exception reading data
    */
   protected int readBytes(byte[] buf, int off, int len) throws IOException {
      if(off < 0 || off > buf.length || len < 0) {
         throw new IndexOutOfBoundsException();
      } else if(off + len > buf.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      } else if(len <= 0) {      
         return 0;
      }
      return parseRead(buf,off,len);
   }
   
   /**
    * The <code>parseRead</code> method is used to read and decode the
    * chunked data from the <code>InputStream</code>. This will read
    * the decoded data into the given buffer. The decoding of the data
    * is done using the BNF description given in RFC 2616 secion 3.6.1.
    * <pre>
    *
    *    chunked-body   = *chunk last-chunk trailer CRLF
    *    chunk          = chunk-size [ chunk-extension ] CRLF
    *                     chunk-data CRLF
    *    chunk-size     = 1*HEX
    *    last-chunk     = 1*("0") [ chunk-extension ] CRLF
    *
    *    chunk-extension= *(";" chunk-ext-name [ "=" chunk-ext-val ])
    *    chunk-ext-name = token
    *    chunk-ext-val  = token | quoted-string
    *    chunk-data     = chunk-size(OCTET)
    *    trailer        = *(entity-header CRLF) 
    *
    * </pre>    
    * This will decode the data so that a continous stream of data is
    * presented. If a trailer exists then it is read from the stream
    * but not returned as data. Ignoring the trailer is permitted as
    * RFC 2616 section 3.6.1 states the the trailer can contian only
    * optional meta-data.
    *
    * @param buf the buffer that is to be filled with the read data
    * @param off this is the start of the region to write the bytes
    * @param len this is the length of the region to write the bytes     
    *
    * @return this returns the number of bytes read from the stream
    *
    * @exception IOException if there is an exception reading data
    */
   private int parseRead(byte[] buf, int off, int len) throws IOException {
      for(int count = 0; count < len;){
         if(chunk < 0) {
            return count > 0 ? count : -1;
         } else if(chunk > 0) {         
            int read = Math.min(len - count, chunk); 
            int size = in.read(buf, off, read); 
            
            if(size < 0) { 
               return count > 0 ? count : -1;
            } else if(size == 0) {  
               return count;
            } else if(size == chunk){ 
               doControl();
            }
            chunk -= size; 
            off += size; 
            count += size; 
         }else {
            nextChunk();
         }
      }
      return len; 
   }

   /**
    * The <code>nextChunk</code> method is used to read the size of
    * the next chunk. If the zero length chunk is read then this
    * will read the footer from the sream, if there is one. If the
    * zero length chunk is encountered then <code>chunk</code> is
    * set to -1 so signity that there are no more bytes left.
    *
    * @exception this is thrown if there is a problem reading
    */
   private void nextChunk() throws IOException {
      int size = doLength();
      
      if(size < 0 && foot) {
         doFooter();
      }
      if(size < 0 && !foot){
         doControl();
      }
      chunk = size;     
   }

   /**
    * The <code>doFooter</code> method is used to read the footer of
    * the chunked encoded stream. The footer is an optional portion
    * of a chunked encoded stream. This will read the remaining data
    * from the stream until the CRLF CRLF control sequence is found.
    * <p>
    * If the <code>InputStream</code> returns -1 before the end of
    * the foorer has been reached then this will throw an exception.
    *
    * @exception IOException if the footer cannot be fully read 
    */
   private void doFooter() throws IOException {
      int off = 0;
      
      while(true){
         int read = in.read();
         int oct = (byte)read;
         
         if(read < 0) {
            throw new IOException("Bad footer");
         }
         if(oct != token[off++]) { 
            off = 0;            
         } 
         if(off == token.length) {
            break;
         }
      }
   }

   /**
    * The <code>doLength</code> method is used to read the length of
    * the chunk from the <code>InputStream</code>. This makes sure
    * not to read any more than the data that constitutes the length
    * of the chunk. The structure of the length is shown below.
    * <pre>
    * 
    *    length = 1*HEX [chunk-extension] CRLF
    *
    * </pre>
    * This will accumulate the size line within the internal buffer
    * so that it may be parsed by the <code>parseSize</code> method.
    * It the length of the size line exceeds the buffer size then
    * this will throw an <code>IOException</code>.
    *
    * @return the length of the next chunk or -1 for a zero length
    *
    * @exception IOException thrown if there is a parsing problem
    */
   private int doLength() throws IOException {      
      int off = 0;
      
      while(true){
         int read = in.read(); 
         byte octet = (byte)read;
         
         if(read < 0 || off >= buf.length) {
            throw new IOException("Bad length");
         }
         if(read == 10){
            return parseSize(off-1);
         } 
         buf[off++] = octet;                  
      }
   }
   
   /**
    * The <code>doControl</code> method is used to read a control
    * sequence from the underlying <code>InputStream</code>. This
    * is used to read the CRLF sequence from the end of the chunk.
    * 
    * @exception IOException thrown if the CRLF could not be read
    */
   private void doControl() throws IOException {
      if(in.read() != 13 || in.read() != 10){
         throw new IOException("Bad control");
      }
   }
   
   /**
    * The <code>parseSize</code> method is used to convert the size
    * in hexidecimal to a decimal <code>int</code>. This will use
    * the specified number of bytes from the internal buffer and
    * parse each character read as a hexidecimal character. When a
    * non-hexidecimal character is first encountered this stops.
    * <p>
    * This will throw an <code>IOException</code> if the string is
    * not a valid hex string, such as <code>10a</code>. If a zero
    * size length is parsed, that is, the string <code>0</code>
    * then this will return -1, otherwise it returns a valid size.
    *
    * @param count this is the number of bytes left in the buffer
    *
    * @return this returns the size, parsed from the hex string
    *
    * @exception IOException thrown if the hex string was invalid
    */
   private int parseSize(int count) throws IOException {      
      int size = 0;
      
      for(int off = 0; off < count; off++){   
         int octet = toDecimal(buf[off]);
            
         if(octet < 0 && off == 0) {
            throw new IOException("Bad length");
         } else if(octet < 0){
            break;
         }
         size <<= 4; 
         size ^= octet; 
      }  
      return size == 0? -1:size;
   }

   /**
    * This performs a conversion from a character to an integer. If
    * the character given, as a <code>byte</code>, is a hexidecimal
    * char this will convert it into its integer equivelant. So a 
    * char of <code>A</code> is converted into <code>10</code>.
    *
    * @param octet this is an ISO 8869-1 hexidecimal character
    *
    * @return returns the hex character into its decinal value
    */
   private int toDecimal(byte octet){
      if(octet >= 'A' && octet <= 'Z') {
         return (octet - (int)'A') + 10;
      }
      if(octet >= '0' && octet <= '9') {
         return octet - (int)'0';   
      } 
      if(octet >= 'a' && octet <= 'f') {
         return (octet - (int)'a') + 10;
      }
      return -1;
   }

   /**
    * The <code>skipBytes</code> method is used to skip a specified
    * number of bytes from the <code>InputStream</code>. This is 
    * what performs the <code>skip</code> method. This is uses to
    * read bytes from the stream using the <code>parseRead</code>
    * method. If there is a problem skipping the bytes an exception
    * is thrown, and the <code>notifyError</code> event is issued.
    * 
    * @param amount number of bytes to skip from the input stream
    *
    * @return returns the number of bytes skipped from the stream
    *
    * @exception IOException signifies that there is an I/O error
    */
   protected long skipBytes(long amount) throws IOException {
      long space = buf.length; 
      long count = amount;   

      while(amount > 0) {
         long min = Math.min(amount, space);
         int num = parseRead(buf, 0, (int)min);
         if(num < 0) {
            break;
         }                         
         amount -= num;
      }            
      return count - amount;
   }

   /**
    * The <code>readyBytes</code> method is used to determine the
    * number of bytes that can be read from the stream without
    * blocking. This basically determines the mimimum of the size
    * of the chunk, the number of remaining bytes in the chunk, 
    * and the available bytes from the <code>InputStream</code>.
    * 
    * @return the number of bytes available without blocking I/O
    *
    * @exception IOException signifies that there is an I/O error   
    */
   protected int readyBytes() throws IOException {
      int size = chunk < 0 ? 0 : chunk;
      return Math.min(size, in.available());
   } 

   /** 
    * The <code>doClose</code> method is used to perform a close
    * on the stream. This will skip the available bytes on the
    * stream in an attempt to read the zero length chunk. If it
    * fails to read the zero length chunk, an error is issued.
    *
    * @exception IOException thrown if skipping causes an error
    */
   protected void doClose() throws IOException {
      skip(available());
      if(chunk >= 0) 
         mon.notifyError(in);
   }   
}

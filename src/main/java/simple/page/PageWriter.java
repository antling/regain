/*
 * PageWriter.java December 2002
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

import simple.util.net.Parameters;
import simple.http.serve.Context;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;

/**
 * The <code>PageWriter</code> is a <code>Writer</code> object that is
 * used to parse HTML files for <code>&lt;include&gt;</code> tags. This 
 * is used so the Server Side Include (SSI) expressions can be used in
 * HTML files. This enables the contents from the HTML to be written 
 * to an underlying <code>OutputStream</code> having the contents
 * of referenced <code>Content</code> objects written to the output.
 * The start of the <code>&lt;include&gt;</code> tag is as follows.
 * <pre>
 *
 *    include = "&lt;include" LWS source LWS "&gt;" 
 *    source  = "source" LWS "=" LWS path
 *    path    = *("/" alphanumeric)
 *    LWS     = "\r" | "\n" | "\t" | " "
 *
 * </pre>
 * The <code>path</code> is the portion of the tag that is used to get
 * the <code>Content</code> object. Once the tag has been retrieved from
 * the HTML file it is used to reference a <code>Content</code> object
 * within the <code>Context</code> object. The contents from referenced
 * <code>Content</code> objects are written to the underlying stream
 * instead of the tags. So for the following entry to a HTML file the
 * contents of the /demo/ContentObject are written to the stream.
 * <pre>
 *
 *    &lt;include source=&quot;/demo/ContentObject&quot;&gt;
 *       &lt;param name=&quot;Color&quot; value=&quot;Orange&quot;&gt;
 *       &lt;param name=&quot;Font&quot; value=&quot;Courier&quot;&gt;
 *       &lt;param name=&quot;Width&quot; value=200&gt;
 *       &lt;param name=&quot;Height&quot; value=100&gt;
 *    &lt;/include&gt;
 *
 * </pre>
 * One thing that must be noted is that this will write the contents
 * of the parsed HTML file as UTF-8 to the underlying stream. If there
 * are <code>Content</code> objects referenced by the HTML file then it
 * is up to the author of the HTML file to ensure that the contents
 * referenced are written as UTF-8 also. This will also not allow any
 * of the referenced <code>Content</code> objects to flush the stream
 * so that it does not impact the performance of a buffering.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.serve.Context
 * @see simple.http.serve.Content
 */
final class PageWriter extends Writer {

   /**
    * This is the token that us used in the HTML file for SSI.
    */
   private static final char[] start = {
   '<', 'i', 'n', 'c', 'l', 'u', 'd', 'e'};
   
   /**
    * This is the token that us used in the HTML file for SSI.
    */
   private static final char[] end = {
   'i', 'n', 'c', 'l', 'u', 'd', 'e', '>'};

   /**
    * This handles the use of the <code>Content</code> objects.
    */
   private DisplayHandler handler;

   /**
    * This is used so that chars a well as bytes can be used.
    */
   private PageOutputStream out;
   
   /**
    * When the tags are read from the HTML it is buffered.
    */
   private char[] text = new char[0];
   
   /**
    * Determines how much data from the HTML has been stored.
    */
   private int textPos;
      
   /**
    * Used to determine how many chars match the end tag.
    */
   private int endPos;
   
   /**
    * Used to determine how many chars match the start tag.
    */
   private int startPos;

   /**
    * Constructor for the <code>PageWriter</code> creates a writer 
    * that can be used to parse and write HTML files with SSI tags.
    * This enables characters to be written to the underlying
    * <code>OutputStream</code> in the UTF-8 character encoding. 
    * The HTML written with this <code>Writer</code> will be parsed.
    *
    * @param context this is the <code>Context</code> the SSI uses
    * @param out the underlying <code>OutputStream</code> to use
    * @param data all extra parameters to be issued to the content
    *
    * @exception IOException if the UTF-8 charset is not supported
    */
   public PageWriter(Context context, OutputStream out, Parameters data) 
      throws IOException {      
      this(new DisplayHandler(context, data), out);
   }
   
   /**
    * Constructor for the <code>PageWriter</code> creates a writer 
    * that can be used to parse and write HTML files with SSI tags.
    * This enables characters to be written to the underlying
    * <code>OutputStream</code> in the UTF-8 character encoding. 
    * The HTML written with this <code>Writer</code> will be parsed.
    *
    * @param handler this is used to display the parsed content
    * @param out the underlying <code>OutputStream</code> to use
    *
    * @exception IOException if the UTF-8 charset is not supported
    */
   public PageWriter(DisplayHandler handler, OutputStream out) 
      throws IOException {
      this.out = new PageOutputStream(out);
      this.handler = handler;
   }

   /**
    * This <code>write</code> method provides the output method for
    * all other <code>write</code> methods. This ensures that the data
    * written will be parsed for <code>&lt;include&gt;</code> tags. If
    * tags are found then this will use the <code>Context</code> object
    * issued to extract and emit <code>Content</code> data. This is
    * basically what is used to perform the Server Side Include (SSI).
    * <p>
    * This method is not coupled to any specific output format, the 
    * data can be written in a line by line fashion or in arbitrary 
    * size chunks. If the <code>Content</code> object referred to by 
    * the name within the <code>&lt;include&gt;</code> tag cannot be 
    * found, an exception is thrown from this method.
    *
    * @param buf this is the content that will be parsed for the tags
    * @param off this is the read offset that is used to begin parsing
    * @param len this is the number of characters that are to be used
    *
    * @exception IOException this is thrown if there is an I/O error
    */   
   public void write(char[] buf, int off, int len) throws IOException {
      if(off < 0 || off > buf.length || len < 0) {
         throw new IndexOutOfBoundsException();
      } else if(off + len > buf.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }
      parseWrite(buf, off, len);
   }
   
   /**
    * This <code>parseWrite</code> method is used to read the data and
    * parse it for <code>&lt;include&gt;</code> tags. If tags are found
    * then this will use the <code>Context</code> issued to extract and
    * emit <code>Content</code> data. This is basically what is used
    * to perform the Server Side Include (SSI) functionality. This can 
    * accomodate any size of an include statement, by expanding the 
    * size of the buffer used to store the HTML tag. 
    *
    * @param buf this is the content that will be parsed for the tags
    * @param off this is the read offset that is used to begin parsing
    * @param len this is the number of characters that are to be used
    *
    * @exception IOException this is thrown if there is an I/O problem
    */   
   private void parseWrite(char[] buf, int off, int len) throws IOException {
      if(textPos + len > text.length) { 
         ensureCapacity(textPos + len); 
      }
      parseWrite(off, buf, off, len);
   }

   /**
    * This <code>parseWrite</code> method is used to read the data and
    * parse it for <code>&lt;include&gt;</code> tags. If tags are found
    * then this will use the <code>Context</code> object issued to 
    * extract and emit <code>Content</code> data. This is what is used
    * to perform the Server Side Include (SSI) functionality.
    * <p>
    * This method is not coupled to any specific output format, the 
    * data can be written in a line by line fashion or in arbitrary size 
    * chunks. If the <code>Content</code> object referred to by the name 
    * within the <code>&lt;include&gt;</code> tag cannot be found then 
    * an exception is thrown from this method.
    *
    * @param mark this refers the beginning of the write offset
    * @param buf this is the content that will be parsed for the tags
    * @param off this is the read offset that is used to begin parsing
    * @param len this is the number of characters that are to be used
    *
    * @exception IOException this is thrown if there is an I/O problem
    */
   private void parseWrite(int mark, char[] buf, int off, int len) throws IOException {
      for(int count = 0; count < len; count++, off++) {
         char data = Character.toLowerCase(buf[off]);
         
         if(startPos == start.length) {          
            text[textPos++] = buf[off];

            if(end[endPos++] == data) {      
               if(endPos == end.length) {        
                  handler.handle(out, text, textPos);
                  endPos = textPos = startPos = 0;                  
                  mark = off + 1;                
               }
            } else{
               endPos = 0;                       
            }
         } else {                                
            if(start[startPos++] == data) {  
               if(startPos == start.length)     
                  if(count > start.length) {     
                     int pos = off + 1 - start.length; 
                     out.write(buf, mark, pos - mark); 
                     mark = pos;
                  }
               text[textPos++] = buf[off];      
            } else {
               if(textPos > 0) {                  
                  if(textPos > count) {           
                     out.write(text, 0, textPos);  
                  } else {
                     out.write(buf, mark, off - mark);  
                  }
                  textPos = 0;                    
                  mark = off;                   
               }
               startPos = 0;                     
            }
         }
      }
      int size = off - mark;

      if(textPos > 0) {
         if(textPos < len) {
            out.write(buf, mark, size - textPos);
         }
      } else if(size > 0) {
         out.write(buf, mark, size);
      }
   }

   
   
   /** 
    * This ensure that there is enough space in the buffer to allow 
    * for more <code>char</code>'s to be added. If the buffer is 
    * already larger than min then the buffer will not be expanded 
    * at all. This allows an SSI include statement to be as large
    * as needed to add sufficent parameters.
    *
    * @param min the minimum size needed for the buffer
    */  
   private void ensureCapacity(int min) {
      if(text.length < min) {
         int size = text.length * 2;
         int max = Math.max(min, size);
         char[] temp = new char[max];         
         System.arraycopy(text,0,temp,0,textPos); 
         text = temp;
      }
   }   

   /**
    * This method is provided to flush the <code>Writer</code> to 
    * the underlying <code>OutputStream</code>. This does not flush
    * the bytes buffered in the <code>OutputStream</code> so that 
    * performance enhancements obtained by buffering are maintained.
    *
    * @exception IOException this is unlikely to ever be thrown
    */
   public void flush() throws IOException {
      out.flush();
   }

   /**
    * The <code>close</code> method will close the underlying stream.
    * This can be invoked to close the <code>OutputStream</code>. If
    * there are any buffered bytes then these are flushed first.
    *
    * @exception IOException this is thrown if there is an I/O error
    */   
   public void close() throws IOException {
      out.close();
   }   
}

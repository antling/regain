/*
 * DocumentFrame.java October 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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

package simple.template;

import java.io.StringWriter;

/**
 * The <code>DocumentFrame</code> object is used to encapsulate a
 * documents contents, such that the <code>Document</code> methods
 * cannot be used. This can be useful if a document object is to
 * be embedded within a document object, as it prevents the use
 * of the <code>put</code> and <code>remove</code> methods, which
 * can alter the state of the object. This will refresh the data
 * produced by the document on every use, ensuring it is current.
 * <p>
 * This class is used so that the <code>Document.toString</code>
 * method can be written easily. This means that it can not use
 * that method, so instead, it uses <code>Document.write</code>
 * and captures the output of that method in an internal buffer.
 *
 * @author Niall Gallagher 
 */
public class DocumentFrame {

   /**
    * An internal object used to render the source document.
    */
   private FrameBuffer store;

   /**
    * The document used to generate this frame's contents.
    */   
   private Document source;

   /**
    * Constructor for the <code>DocumentFrame</code>. This is
    * used to capture the output of the issued document, which
    * will be updated on every call to <code>toString</code>.
    *
    * @param source the document to capture the output from
    */
   public DocumentFrame(Document source) {
      this.store = new FrameBuffer();
      this.source = source;
   }

   /**
    * Generates an up to date copy of the documents contents.
    * This is used by the <code>DocumentFrame</code> to render 
    * the contents of the source document. If there are any
    * exceptions thrown during rendering this returns null.
    *
    * @return an up to date copy of the documents contents
    */
   public String toString() {
      return store.toString();
   }

   /**
    * This private inner class is used so that the methods of the
    * <code>StringWriter</code> are not available to the user of
    * the object. This will basically write the contents of the
    * document to an internal buffer and return that content.
    */
   private class FrameBuffer extends StringWriter {

      /**   
       * This is the buffer used to capture the document output.
       */
      private StringBuffer buf;

      /**
       * Constructor for the <code>FrameBuffer</code>. This is
       * used to capture the contents of the source document
       * into a <code>StringBuffer</code>, which is returned.      
       */
      public FrameBuffer() {
        this.buf = getBuffer();
      }

      /**
       * Generates an up to date copy of the documents contents.
       * This is used to render the contents of the document. If
       * there are any errors during rendering this returns null.
       *
       * @return an up to date copy of the documents contents
       */
      public String toString() {
         if(buf.length() > 0){
            buf.setLength(0);
         }
         try {
            source.write(this);
         } catch(Exception e) {
            return null;         
         }
         return buf.toString();         
      }
   }
}

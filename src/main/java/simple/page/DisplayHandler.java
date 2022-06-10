/*
 * DisplayHandler.java May 2003
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

import simple.http.serve.Context;
import simple.http.serve.Content;
import simple.util.net.Parameters;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * The <code>DisplayHandler</code> object provides a means for the
 * SSI statements found in a HTML page to be processed. This will
 * parse an include statement retrieved from an SSI page and 
 * create a <code>Parameters</code> object using the individual
 * parameters extracted from the statement. These parameters are
 * then paired with another parameters source to create a larger
 * union of parameters. This composite <code>Parameters</code>
 * object is then issued to the referenced <code>Content</code>.
 *
 * @author Niall Gallagher
 */
final class DisplayHandler {

   /**
    * This is the display object used by this handler.
    */
   private Display display;

   /**
    * This is the primary parameters object to be used.
    */
   private Parameters data;

   /**
    * Constructor for the <code>DisplayHandler</code>. This creates
    * a handler object for dispatching a <code>Parameters</code>
    * object composed of SSI parameters and another source. This
    * will ensure that the <code>Parameters</code> object given to
    * the constructor will be the dominant parameters issued.
    *
    * @param context contains the <code>Content</code> objects
    * @param data this is the primary <code>Parameters</code>
    */
   public DisplayHandler(Context context, Parameters data){
      this(DisplayFactory.getInstance(context), data);
   }
   
   /**
    * Constructor for the <code>DisplayHandler</code>. This creates
    * a handler object for dispatching a <code>Parameters</code>
    * object composed of SSI parameters and another source. This
    * will ensure that the <code>Parameters</code> object given to
    * the constructor will be the dominant parameters issued.
    *
    * @param display provides the formatting for the page display
    * @param data this is the primary <code>Parameters</code>
    */
   public DisplayHandler(Display display, Parameters data){
      this.display = display;
      this.data = data;
   }

   /**
    * The <code>handle</code> method is used to process the writing
    * of referenced <code>Content</code> objects. This will handle
    * any <code>Exception</code> thrown during the processing of
    * writing. Exceptions can range from a syntactical error in
    * the SSI statement to errors within the <code>Content</code>
    * object itself.
    * <p>
    * If any exceptions are thrown from the <code>Content</code>
    * they are caught and reported to the connected client. Also
    * if there is an error parsing the syntax of the statement an
    * exception is reported, all exceptions will propagate.
    *
    * @param out the output stream the referenced content uses
    * @param text this is the SSI include statement to be parsed
    * @param count the number of characters in the SSI statement 
    *
    * @exception IOException this throws a less descriptive error
    * to the caller, the client receives the actual exception
    */
   public void handle(OutputStream out, char[] text, int count) throws IOException{
      try {
         handle(out, new PageParser(text, count));
      }catch(Exception cause){
         handle(out, cause);
      }
   }   

   /** 
    * This <code>handle</code> method will write the content that
    * is referenced to the stream. The <code>Reference</code> 
    * object issued contains the name of the <code>Content</code>
    * that is to be written to the stream. This will also ensure
    * that all parameters are given to the content object.
    *
    * @param out the stream the referenced content writes to
    * @param entry this contains the details of the reference
    *
    * @exception IOException if there is an I/O error writing   
    */
   private void handle(OutputStream out, Reference entry) throws IOException {
      display.handle(out, new Composite(data, entry));
   }  

   /**
    * This <code>handle</code> method is used to handle problems
    * that occur during the display of a reference. This will 
    * delegate the task to the system wide <code>Display</code> 
    * implementation, this ensures that errors are handled in
    * a consistent manner and also allows additional features
    * to be added such as logging.
    * <p>
    * If <code>Display.handle</code> throws an exception then
    * this is propagated to the caller of the page, this action
    * effectively aborts the processing of the page and allows
    * the caller to take the appropriate actions.
    *
    * @param out the stream the referenced content writes to
    * @param cause this is the exception that was thrown
    *
    * @exception IOException thrown to abort the SSI page
    */
   private void handle(OutputStream out, Exception cause) throws IOException{
      display.handle(out, cause);
   }
}

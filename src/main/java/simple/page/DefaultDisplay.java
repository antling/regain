/*
 * DefaultDisplay.java July 2003
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
import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The <code>DefaultDisplay</code> object is used to facilitate 
 * the display of referenced objects and errors. This provides 
 * a default implementation of the <code>Display</code> interface
 * so that SSI pages can be rendered without the need to provide
 * a custom implementation of the interface. 
 * <p>
 * If an exception is thrown by a referenced <code>Content</code>
 * object then that exception is reported by printing the stack 
 * trace to the issued <code>OutputStream</code>. The format of 
 * the exception reporting is shown in the example below.
 * <pre>
 *
 * &lt;BR&gt;&lt;PRE&gt;
 *    java.io.FileNotFoundException: /path/README
 *       at java.io.FileInputStream.open(Native Method)
 *       at java.io.FileInputStream.(FileInputStream.java:103)
 *       at java.io.FileInputStream.(FileInputStream.java:66) 
 * &lt;/PRE&gt;&lt;BR&gt;    
 *
 * </pre>
 * The above is an example of an error message that is written 
 * to the issued <code>OutputStream</code>. To ensure that the 
 * data is consistent the exception is written in UTF-8.
 *
 * @author Niall Gallagher
 */
final class DefaultDisplay implements Display {

   /**
    * This is the context that the page is referenced from. 
    */
   private Context context;

   /**
    * Constructor for the <code>DefaultDisplay</code> object. 
    * This uses the issued <code>Context</code> object to access
    * the referenced objects. This ensures that all content is
    * taken from the same context as the referenced SSI page.
    *
    * @param context this is the context used by the SSI page
    */
   public DefaultDisplay(Context context) {
      this.context = context;
   }

   /**
    * Handles the display the referenced <code>Content</code>
    * object. This method uses the <code>Reference</code> object
    * to acquire the source string within the include statement.
    * The source string is then used to acquire the appropriate
    * <code>Content</code> object from the <code>Context</code>
    * and write its contents to the issued output stream.
    *
    * @param out this is the stream connected to the client
    * @param data this contains the parameters for the content
    *
    * @exception IOException if there is an I/O error writing 
    */
   public void handle(OutputStream out, Reference data) throws IOException{
      String name = data.getContentPath();
      context.getContent(name).write(out, data);
   }

   /**
    * Handles the display of any <code>Exception</code> that
    * is thrown from the SSI page. This uses the stream to
    * convey the error that occurred while writing the page.
    * For simplicity the stack trace of the exception issued
    * is printed to the SSI page as a HTML statement. This
    * method ensures that the character encoding is UTF-8 so
    * that the output is compatible with the page content.
    *
    * @param out this is the stream connected to the client
    * @param cause this is the exception to be displayed
    *
    * @exception IOException if there is an I/O error writing
    */
   public void handle(OutputStream out, Exception cause) throws IOException{
      handle(new PrintStream(out, true, "utf-8"), cause);                  
   }

   /**
    * This will write an error message to the issued stream. The
    * message is written in HTML, as this is the intended format
    * of SSI pages. This is clearly one of the most important
    * features of the <code>simple.page</code> package as it 
    * enables SSI page errors to be debugged without hassle.
    * <p>
    * In the interest of making the SSI pages easy to use, the
    * most sensible way to report the exceptions is to write to
    * to the connected client, or the browser application.
    *
    * @param out this is the stream the data is written with
    * @param cause this is the exception that is to be written
    */
   private void handle(PrintStream out, Exception cause) {
      out.println("<BR><PRE>");
      cause.printStackTrace(out);
      out.println("</PRE><BR>");
   }
}

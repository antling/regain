/*
 * Display.java July 2003
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

import java.io.OutputStream;
import java.io.IOException;

/**
 * The <code>Display</code> object provides a means for formatting
 * the display of an SSI page. Implementations of this object can
 * be used to configure the output of SSI pages by implementing
 * the <code>handle</code> methods, which are invoked by the page.
 * <p>
 * This also enables any <code>Exception</code> thrown from a
 * referenced <code>Content</code> object to be given a specific
 * HTML format. For example the stack trace of the exception can
 * be printed using the <code>printStackTrace</code> method.
 * <p>
 * Implementations of the <code>Display</code> interface must
 * have a constructor that takes a <code>Context</code> object
 * as its argument. If this single argument constructor is not
 * provided then the implementation cannot be loaded by the page.
 * The <code>simple.page.display</code> system property is used
 * to acquire the name of the implementation to be used.
 *
 * @author Niall Gallagher
 */
public interface Display {
   
   /**
    * Handles the display the referenced <code>Content</code>
    * object. This method uses the <code>Reference</code> object
    * to acquire the source string within the include statement.
    * The source string can then be used to either acquire a
    * <code>Content</code> object from the <code>Context</code>
    * or provide some alternative means of display.
    * <p>
    * The <code>Reference</code> object given to this method
    * contains all the parameters found within the statement,
    * however certain parameters may be overridden if they are
    * within a nested reference. That is, if the SSI page that
    * was originally referenced was given a set of parameters
    * within the <code>write(OutputStream, Parameters)</code> 
    * method, then those parameters will have highest priority. 
    *
    * @param out this is the stream connected to the client
    * @param data this contains the parameters for the content
    *
    * @exception IOException if there is an I/O error writing 
    */
   public void handle(OutputStream out, Reference data)
      throws IOException;

   /**
    * Handles the display of any <code>Exception</code> that
    * is thrown from the SSI page. This uses the stream to
    * convey the error that occurred while writing the page.
    * For simplicity the stack trace of the exception could 
    * be printed to the SSI page as a HTML statement. If
    * however there is a security risk then a simple message
    * conveying the fact that an error occurred can be used.
    * <p>
    * There are two ways this method can be implemented. The
    * first is return quietly letting the SSI page continue 
    * and the second is to throw an <code>IOException</code>
    * which will effectively abort the writing of the page.
    *
    * @param out this is the stream connected to the client
    * @param cause this is the cause of the SSI page problem
    *    
    * @exception IOException if there is an I/O error writing 
    * or if the desired semantics are to abort SSI page
    */
   public void handle(OutputStream out, Exception cause) 
      throws IOException;
}

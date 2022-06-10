/*
 * Document.java December 2003
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

package simple.template;

import java.io.OutputStream;
import java.io.Writer;

/**
 * A <code>Document</code> object represents a template with a 
 * set of properties. This represents a template loaded from some
 * source that can have specific properties set to configure a
 * rendering of the template acquired from the templating system.
 * <p>
 * This interface attempts to encapsulate the functionality 
 * provided by the templating system in an independant manner.
 * This achieves two things, it ensures that the controller is
 * not tightly coupled to the templating system and ensures that
 * firmiliarity with the templating system's API is not required.
 *
 * @author Niall Gallagher
 */
public interface Document extends Database {

   /**
    * Displays the contents of the generated template output to
    * the issued <code>Writer</code>. This encapsulates the means
    * of rendering the template to a single method. Internally
    * the properties that are set within the document will be
    * used to configure the template, enabling dynamic output.
    * <p>
    * If there are any problems parsing the template or emitting
    * its contents an exception is thrown. However, if it is
    * successfully processed it will be written to the issued
    * output, which should remain unflushed for performance.
    *
    * @param out the output to write the template rendering to
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   public void write(Writer out) throws Exception;

   /**
    * Displays the contents of the generated template output to
    * the <code>OutputStream</code>. This encapsulates the means
    * of rendering the template to a single method. Internally
    * the properties that are set within the document will be
    * used to configure the template, enabling dynamic output.
    * <p>
    * If there are any problems parsing the template or emitting
    * its contents an exception is thrown. However, if it is
    * successfully processed it will be written to the issued
    * output, which should remain unflushed for performance. The
    * output is written using the UTF-8 charset.
    *
    * @param out the output to write the template rendering to
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   public void write(OutputStream out) throws Exception;

   /**
    * Displays the contents of the generated template output to
    * the <code>OutputStream</code>. This encapsulates the means
    * of rendering the template to a single method. Internally
    * the properties that are set within the document will be
    * used to configure the template, enabling dynamic output.
    * <p>
    * If there are any problems parsing the template or emitting
    * its contents an exception is thrown. However, if it is
    * successfully processed it will be written to the issued
    * output, which should remain unflushed for performance. 
    *
    * @param out the output to write the template rendering to
    * @param charset the charset to write the template with
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   public void write(OutputStream out, String charset) throws Exception;

   /**
    * Produces the generated template output as a string. This
    * is useful for embedding documents within each other
    * creating a layout effect. For example, if a templated
    * HTML page required content from different sources, then
    * a document could be added, as a property, for display.
    * <p>
    * This is very useful if the output is to be used as an
    * SQL query, or an email message. The rendering is stored
    * conveniently for use as a string and does not require
    * a <code>Writer</code> to capture the output.   
    *
    * @return document output if successful, null otherwise
    */
   public String toString();
}

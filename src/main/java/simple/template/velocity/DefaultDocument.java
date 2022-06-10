/*
 * DefaultDocument.java December 2003
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

import org.apache.velocity.context.Context;
import org.apache.velocity.Template;
import simple.template.DocumentFrame;
import simple.template.Document;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.Writer;

/**
 * The <code>DefaultDocument</code> provides an implementation
 * of the <code>Document</code> interface. This provides a
 * reasonably quick implementation that performs an internal 
 * buffering of the content before emitting it to the issued
 * output. The buffering serves to increase the performance of
 * the template generation and also enables character encoding
 * to be done without incurring frequent encoding incursions.
 *
 * @author Niall Gallagher
 */
final class DefaultDocument extends PlainDatabase implements Document{

   /**
    * This is used to capture the output of the document.
    */
   private DocumentFrame frame;

   /**
    * The template instance used to generate the document.
    */
   private Template template;

   /**
    * Constructor for the <code>DefaultDocument</code> object.
    * This creates a document using the issued template object.
    * The document is initialized using the properties of the
    * issued <code>Context</code>. These properties can be
    * overridden without affecting the containers database.
    * 
    * @param template template object used to generate output
    * @param context properties used to configure the output 
    */
   public DefaultDocument(Template template, Context context){
      this.frame = new DocumentFrame(this);
      this.template = template;
      this.context = context;
   }

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
    * output, which will remain unflushed for performance. The
    * output is written using the UTF-8 charset.
    *
    * @param out the output to write the template rendering to
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   public void write(OutputStream out) throws Exception {
      write(out, "utf-8");
   }
   
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
    * output, which will remain unflushed for performance. The
    * output is written using the specified charset.
    *
    * @param out the output to write the template rendering to
    * @param charset the charset used to write the template 
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   public void write(OutputStream out, String charset) throws Exception {
      write(new ProxyOutputStream(out), charset);
   }
   
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
    * output, which will remain unflushed for performance. The
    * output is written using the specified charset.
    *
    * @param out the output to write the template rendering to
    * @param charset the charset used to write the template 
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   private void write(ProxyOutputStream out, String charset) throws Exception{
      write(new OutputStreamWriter(out, charset));
   }
   
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
    * output, which will remain unflushed for performance.
    *
    * @param out the output to write the template rendering to
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   public void write(Writer out) throws Exception {
      template.merge(context, out);
   }
   
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
    * output, which is then flushed.
    *
    * @param out the output to write the template rendering to
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */   
   private void write(OutputStreamWriter out) throws Exception {
      write(new BufferedWriter(out));
   }
   
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
    * output, which is then flushed.
    *
    * @param out the output to write the template rendering to
    *
    * @throws Exception thrown if there is a problem parsing or 
    * emitting the template 
    */
   private void write(BufferedWriter out) throws Exception {
      template.merge(context, out);
      out.flush();
   }

   /**
    * Produces the generated template output as a string. This
    * is useful for embedding documents within each other
    * creating a layout effect. For example, if a templated
    * HTML page required content from different sources, then
    * a document could be added to it for display.
    * <p>
    * This is very useful if the output is to be used as an
    * SQL query, or an email message. The rendering is stored
    * conveniently for use as a string and does not require
    * a <code>Writer</code> to capture the output.   
    *
    * @return document output if successful, null otherwise
    */
   public String toString() {
      return frame.toString();
   }
}

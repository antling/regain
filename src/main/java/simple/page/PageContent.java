/*
 * PageContent.java December 2002
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
import simple.http.serve.Content;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;

/**
 * The <code>PageContent</code> object represents an implementation of
 * the <code>Content</code> interface for Server Side Include (SSI) 
 * functionality. This implementation will parse a HTML file given to
 * its constructor. Parsing of the HTML file involves scanning the 
 * file for &lt;include&gt; tags. When a tag has been found then this
 * uses the <code>Context.getContent</code> method to retrieve a
 * <code>Content</code> implementation to embed in the output.
 * <p>
 * This is used in conjunction with the <code>PageFactory</code> to
 * produce SSI content for the <code>FactoryContext</code>. This will
 * reference the <code>Context.getContent</code> using a URI embedded 
 * in the HTML file, which enables static as well as dynamic content
 * to be embedded in the resulting output. The URI strings embedded
 * in the HTML file are of the following syntax.
 * <pre>
 *
 *    tag     = begin LWS content LWS end;
 *    begin   = "&lt;" "include" "&gt;"
 *    end     = "&lt;" "/" "include" "&gt;"
 *    LWS     = "\r" | "\n" | "\t" | " "
 *    content = path | class
 *    path    = *("/" alphanumeric)
 *    class   = "/" name
 *
 * </pre>
 * So the content is basically resolved as a class name or the name 
 * of a static resource on the underlying file system. The path used
 * for referencing static content on the underlying file system must
 * be given in the form of a request URI so it can be used with the 
 * <code>Context</code> object. The references to a class name must
 * be preceded by a forward slash, again so that it can be used
 * with the <code>Context.getContent</code> method. So a class name
 * of "demo.DemoContent" is referenced as "/demo.DemoContent".
 * 
 * @author Niall Gallagher
 */
public class PageContent implements Content {

   /**
    * This acts as a buffer for the contents of the SSI file.
    */
   private PageBuffer page;

   /**
    * This is used to embed the contents of SSI references.
    */
   private Context context;

   /**
    * Constructor for the <code>PageContent</code>. This will read
    * the contents of the SSI file into a buffer so that the 
    * <code>PageContent.write</code> method can parse the SSI file
    * from memory rather than re-reading it from the file system.
    * <p>
    * This reads the contents of <code>Context.getFile</code> with
    * the given request URI string. If there is a problem reading
    * the file then an <code>IOException</code> is thrown.
    *
    * @param context this is the context this page is loaded with
    * @param target this is a request URI referring to the SSI page
    *
    * @exception IOException thrown if there is an I/O problem
    */
   public PageContent(Context context, String target) throws IOException {
      this.page = new PageBuffer(context, target);
      this.context = context;
   }    

   /**
    * This writes the contents of the referenced page to the given
    * <code>OutputStream</code>. The contents of the referenced page
    * is parsed before being written to the stream. This method will
    * not use any parameters for <code>Content</code> objects that
    * are referenced by the SSI page.
    *
    * @param out this is the <code>OutputStream</code> to write to
    *
    * @exception IOException thrown if there is an I/O problem
    */
   public void write(OutputStream out) throws IOException {
      write(out, null);
   }
   
   /**
    * This writes the contents of the referenced page to the given
    * <code>OutputStream</code>. The contents of the referenced page
    * is parsed before being written to the stream. This method will
    * pass the given parameter to each <code>Content</code> object
    * referenced by the SSI page.
    *
    * @param out this is the <code>OutputStream</code> to write to
    * @param data an arbitrary object given to each content instance
    *
    * @exception IOException thrown if there is an I/O problem
    */
   public void write(OutputStream out, Parameters data) throws IOException {           
      write(new PageWriter(context, out, data)); 
   }

   /**
    * This will write the contents of the buffered SSI page to the
    * given <code>Writer</code>. This must flush the data written
    * to the <code>Writer</code> so the <code>OutputStream</code>
    * can receive the contents before the method ends.
    *
    * @param out a writer that writes the SSI page to a stream
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void write(Writer out) throws IOException {
      page.write(out);
      out.flush();
   }

   /**
    * Returns the resulting MIME type of the written data. The MIME
    * type for the SSI page is intended to be HTML, also the HTML
    * files that are parsed must be written in UTF-8 format. So the
    * resulting MIME type is <code>text/html; charset=UTF-8</code>.
    *
    * @return this returns the MIME type for the resulting contents
    */
   public String getMimeType() {
      return "text/html; charset=utf-8";
   }
}

/*
 * PrintContent.java December 2002
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
import simple.http.serve.Content;
import simple.http.serve.Context;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;

/**
 * The <code>PrintContent</code> provides a means for providing the
 * <code>Content</code> objects referenced by the SSI pages. This
 * provides the <code>write</code> and <code>getMimeType</code>
 * methods for the <code>Content</code>. The output for this type
 * of content should be HTML and will be encoded in UTF-8 format.
 * <p>
 * A single abstract method must be implemented to provide the 
 * output for the <code>PrintContent</code>. The <code>write</code>
 * method provided is given a <code>PrintStream</code> which will
 * write the data (unbuffered) directly to the issued stream.
 *
 * @author Niall Gallagher
 */
public abstract class PrintContent implements Content {

   /**
    * This is the <code>Context</code> that created this.
    */
   protected Context context;

   /**
    * This is the request URI that references this object.
    */
   protected String target;

   /**
    * Constructor for the <code>PrintContent</code> provides the
    * standardized signature required by the <code>PageContext</code>.
    * This enables <code>Content</code> objects to be created that
    * will write the content using UTF-8 format. This ensures that
    * it can be used in conjunction with the SSI pages.
    *
    * @param context the <code>Context</code> that created this
    * @param target the request URI that references this object
    */
   protected PrintContent(Context context, String target){
      this.context = context;
      this.target = target;
   }

   /**
    * Delegates to <code>write(PrintStream,&nbsp;Parameters)</code> 
    * method. The creates a <code>PrintStream</code> object using the 
    * issued <code>OutputStream</code>, the <code>PrintStream</code> 
    * will write data directly to the underlying stream unbuffered. 
    * This can be overloaded if performance needs to be maximized.
    *
    * @param out this is the <code>OutputStream</code> written to
    *
    * @exception IOException this is thrown if there is an I/O error
    */
   public void write(OutputStream out) throws IOException {
      write(out, null);
   }
   
   /**
    * Delegates to <code>write(PrintStream,&nbsp;Parameters)</code> 
    * method. The creates a <code>PrintStream</code> object using the 
    * issued <code>OutputStream</code>, the <code>PrintStream</code> 
    * will write data directly to the underlying stream unbuffered. 
    * This can be overloaded if performance needs to be maximized.   
    *
    * @param out this is the <code>OutputStream</code> written to
    * @param data contains parameters that can be used by the object
    *
    * @exception IOException this is thrown if there is an I/O error
    */
   public void write(OutputStream out, Parameters data) throws IOException {
      write(new PrintStream(out, false, "utf-8"), data);
   }

   /**
    * Returns the content type used. Because the intended use of
    * the <code>PrintContent</code> is for embedding code within the
    * SSI pages, this returns a content type of <code>text/html</code>.
    * The charset type returned by this is <code>UTF-8</code> as that
    * is the content type that the <code>PrintStream</code> uses.
    *
    * @return this returns the content type of the instance
    */
   public String getMimeType() {
      return "text/html; charset=utf-8";
   }

   /**
    * This is where the contents of the implementation writes the to
    * the issued <code>OutputStream</code>. The print stream issued
    * can be used to write strings and characters to the stream in
    * UTF-8 format. The output of this method should be HTML but if
    * it is not then the <code>getMimeType</code> method must be
    * overloaded to reflect the intended output.
    *
    * @param out the <code>PrintStream</code> to be written to
    * @param data contains parameters that can be used by the object
    *
    * @exception IOException if there are any I/O problems writing
    */
   public abstract void write(PrintStream out, Parameters data) 
      throws IOException;
}

/*
 * PageContext.java December 2002
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

import simple.http.serve.FactoryContext;
import simple.http.serve.FormatFactory;
import simple.http.serve.Context;
import simple.http.serve.Format;
import java.io.IOException;
import java.io.File;

/**
 * The <code>PageContext</code> provides a convenience object that
 * is used in the place of the <code>FactoryContent</code> object.
 * This will create a <code>Context</code> object for serving 
 * content objects using the <code>PageFactory</code>. The request
 * URI strings issued to the <code>getContent</code> methods will
 * result in a search for an implementation.
 * <p>
 * This will return a <code>PageContent</code> for any request URI
 * that has the <code>x-ssi-html</code> MIME type. So when the
 * "Mime.properties" file is loaded this type must be linked to a
 * specific extension, typically ".shtml". Other implementations
 * are loaded from the context path, that is, the context path
 * given on construction, see <code>PageFactory</code> for details
 * on how the loading procedure for content objects is resolved.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.serve.Context
 */
public class PageContext extends FactoryContext {

   /**
    * Constructor for the <code>PageContext</code>. This creates 
    * an instance that uses the current directory as the context
    * path. This constructor also uses the <code>Format</code>
    * produced from the <code>FormatFactory.getInstance</code>.
    * This will throw an <code>IOException</code> if the base
    * path <code>File</code> cannot be converted to a suitable
    * <code>URL</code> for a <code>URLClassLoader</code>.
    *
    * @exception IOException if the base <code>File</code> cant
    * be converted into a <code>URL</code>
    */
   public PageContext() throws IOException {
      this(new File("."));
   }
   
   /**
    * Constructor for the <code>PageContext</code>. This creates 
    * an instance that uses the given directory as the context
    * path. This constructor also uses the <code>Format</code>
    * produced from the <code>FormatFactory.getInstance</code>.
    * This will throw an <code>IOException</code> if the base
    * path <code>File</code> cannot be converted to a suitable
    * <code>URL</code> for a <code>URLClassLoader</code>.
    *
    * @param base this is the context path used by this object
    *
    * @exception IOException if the base <code>File</code> cant
    * be converted into a <code>URL</code>
    */
   public PageContext(File base) throws IOException {
      this(base, FormatFactory.getInstance());
   }
   
   /**
    * Constructor for the <code>PageContext</code>. This creates 
    * an instance that uses the given directory as the context
    * path. This constructor uses the <code>Format</code> issued
    * to produce the error messages. This will throw an 
    * <code>IOException</code> if the base path <code>File</code>
    * cannot be converted to a suitable <code>URL</code> for a 
    * <code>URLClassLoader</code>.
    *
    * @param base this is the context path used by this object
    * @param format this is used to produce error messages
    *
    * @exception IOException if the base <code>File</code> cant
    * be converted into a <code>URL</code>
    */
   public PageContext(File base, Format format) throws IOException {
      super(base, format, new PageFactory(base));
   }
}

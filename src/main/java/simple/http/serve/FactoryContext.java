/*
 * FactoryContext.java December 2002
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
  
package simple.http.serve;

import simple.util.cache.Cache;
import java.io.IOException;
import java.io.File;

/**
 * The <code>FactoryContext</code> is a context implementation that can
 * be used to serve dynamic <code>Content</code> objects. This uses a
 * <code>ContentFactory</code> implementation to produce instances
 * from the <code>getContent</code> methods. The instances are produced 
 * by the <code>FileContext</code> using the <code>getInstance</code>
 * method. This means that all instances produced are cached.
 * <p>
 * Caching the content instances provides quicker access to objects 
 * that are requested frequently. It also has the effect of requiring
 * the <code>Content</code> instances to obey a lifecycle. The content
 * objects are loaded and instantiated by this <code>Context</code> for
 * use within a limited peroid of time. So implementations should not
 * contain instance data that must persist beyond its liftime.
 * <p>
 * The <code>Content</code> objects produced from this should rely on
 * the data given to it in the <code>write(OutputStream,Object)</code>
 * method for state. This ensures that if there are multiple objects
 * using the same <code>Content</code> instance that they will not
 * influence each other, and that the content produced relies on the
 * individual data objects given. 
 * 
 * @author Niall Gallagher
 */
public class FactoryContext extends FileContext {

   /**
    * This is used to acquire <code>Content</code> implementations.
    */
   protected ContentFactory factory;

   /**
    * Constructor for the <code>FactoryContext</code>. This will
    * produce dynamic <code>Content</code> implementations. The
    * objects produced by the <code>getContent</code> method are
    * created using a <code>ContentFactory</code>. This uses an
    * implementation of the <code>Format</code> from the static
    * factory method <code>FormatFactory.getInstance</code>.
    *
    * @param factory used to produce dynamic content objects    
    */
   public FactoryContext(ContentFactory factory) {
      this(new File("."), factory);
   }

   /**
    * Constructor for the <code>FactoryContext</code>. This will
    * produce dynamic <code>Content</code> implementations. The
    * objects produced by the <code>getContent</code> method are
    * created using a <code>ContentFactory</code>. This uses an
    * implementation of the <code>Format</code> from the static
    * factory method <code>FormatFactory.getInstance</code>.
    *
    * @param base the OS specific base path for this instance
    * @param factory used to produce dynamic content objects
    */
   public FactoryContext(File base, ContentFactory factory){
      this(base, FormatFactory.getInstance(), factory);
   }
   
   /**
    * Constructor for the <code>FactoryContext</code>. This will
    * produce dynamic <code>Content</code> implementations. The
    * objects produced by the <code>getContent</code> method are
    * created using a <code>ContentFactory</code>. This uses an
    * implementation of the <code>Format</code> from the static
    * factory method <code>FormatFactory.getInstance</code>.   
    *
    * @param base the OS specific base path for this instance
    * @param format the format that is used by this instance
    * @param factory used to produce dynamic content objects
    */ 
   public FactoryContext(File base, Format format, ContentFactory factory){
      super(base, format);      
      this.factory = factory;
   }
   
   /**
    * This creates instances of the <code>Content</code> object using 
    * the issued <code>ContentFactory</code>. This method is invoked
    * from the <code>FileContext.getContent</code> method, so the
    * caching used by this method applies to all instances created.
    * If the factory cannot produce a <code>Content</code> instance
    * for the issued request URI then the superclass method is used.
    *
    * @param target the request URI used to reference the resource
    *
    * @throws IOException this is thrown if there is an I/O problem
    */
   protected Content getInstance(String target, int size) throws IOException {
      try {
         return factory.getContent(this,target);
      }catch(ContentException e){
         return super.getInstance(target,size);      
      }
   }
}

/*
 * IndexedResource.java February 2001
 *
 * Copyright (C) 2001, Niall Gallagher <niallg@users.sf.net>
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import java.io.File;

/**
 * The <code>IndexedResource</code> is used to provide any specific
 * meta data for any file based <code>Resource</code>s. This provides 
 * methods that allow the <code>Resource</code> to be indexed by the
 * <code>Context</code> object, this allows the server to build meta
 * information on a resource without having to refer to a centralized 
 * table that matches the resource with its meta information. 
 * <p>
 * Such approaches are used with servers like Apache that can have 
 * information stored in a file to describe the Language, Type etc. 
 * of a resource. This however uses a scheme that discovers the meta 
 * information of the <code>Resource</code> by parsing the request URI.
 * The <code>File</code>s that are indexed can have locale extensions 
 * to that the <code>Resource</code>s can be discriminated upon based 
 * on preferred Language.
 *
 * @author Niall Gallagher
 */ 
public abstract class IndexedResource extends BasicResource implements Indexable {
   
   /**
    * This is the name of the file that is to be indexed.
    */
   protected String name;
   
   /**
    * This is the request URI that generated this instance.
    */
   protected String target;
   
   /**
    * This is the name of the requested resource path.
    */
   protected String path;
   
   /**
    * This is the context path for the requested resource.
    */
   protected String base;    
   
   /**
    * This is the type of the file, i.e. MIME meta data.
    */
   protected String type;
   
   /**
    * This is the locale of the resource, like en_US.
    */
   protected Locale locale;
   
   /**
    * This provides direct access to the indexed resource.
    */
   protected File file;

   /**
    * Creates a default indexed object that is indexed based on 
    * the path name that it is given. The <code>Context</code> is
    * used to parse the path and set the meta data. The meta data
    * of the <code>Resource</code> is set based on a set of rules
    * specified by the <code>Context</code>. The path is broken 
    * into its separate components like its name, type, path etc. 
    * This will create the <code>Resource</code> relative to the 
    * specified directory. i.e. the <code>Resource</code> is 
    * created from a file that is rooted at the base directory.
    *
    * @param context the context that this resource is relative to
    * @param target this is the HTTP request URI this represents
    */    
   protected IndexedResource(Context context, String target) {
      super(context);
      context.doIndex(this, target);
      this.target = target;
   }
   
   /**
    * This will set the <code>Locale</code> for this object. This 
    * will be set to the <code>Locale.getDefault</code> if there 
    * is no specified locale for this <code>Resource</code>.
    * <p>
    * The <code>Locale</code> will be determined and set by the
    * <code>Context</code>. The <code>Context</code> sets the
    * locale based on the file name.
    *
    * @param locale the locale for this <code>Resource</code> 
    */    
   public void setLocale(Locale locale){
      this.locale = locale;
   }

   /**
    * This allows the MIME type of this <code>Indexable</code> 
    * object to be set. The MIME type is specified by the
    * <code>Context.getMimeType</code> method for a specific
    * request URI. This should have a value and perhaps some
    * parameters like the charset, "text/html; charset=UTF-8".
    *
    * @param type the MIME type this object is to be set to
    */ 
   public void setMimeType(String type){
      this.type = type;
   }
   
   /**
    * This allows the name for this object to be set. The name 
    * usually refers to the last entry in the path. So if the
    * path specified was "/pub/doc/README" the name is "README".
    * If the path is "/" then the name can only be null.
    *
    * @param name the name for this <code>Resource</code>
    */ 
   public void setName(String name) {
      this.name = name;
   }
   
   /**
    * This is used to set the path that this object refers to. 
    * This should be the fully qualified normalized path. This
    * refers to the OS system specific path that this represents.
    *
    * @param path this is the path this object refers to
    */ 
   public void setRealPath(String path){
      this.path = path;
   }
   
   /**
    * Every resource that the <code>Context</code> object refers to
    * is relative to some base path. This is used to set the base
    * path of the <code>Context</code> object that configured this
    * <code>Indexable</code> implementation. The base path will be
    * a system specific path like "c:\path" on a DOS system and 
    * "/home/user" on a UNIX system.
    *
    * @param base this is the base path of the <code>Context</code>
    * that was used to set this <code>Indexable</code>
    */
   public void setBasePath(String base){
      this.base = base;
   }

   /**
    * The <code>Context</code> object maps the request URI of a 
    * resource into the <code>File</code> object using the method
    * <code>Context.getFile</code>. This method receives the file
    * object that the resource represents.
    *
    * @param file the <code>File</code> object that this object
    * represents
    */
   public void setFile(File file){
      this.file = file;      
   }

   /**
    * This is a simple convienience method that enables subclasses
    * to retrieve the <code>FileInputStream</code> for the file.
    * 
    * @return this returns the <code>FileInputStream</code> that
    * represents the targeted file
    *
    * @throws IOException thrown if the file does not exist
    */
   protected InputStream getInputStream() throws IOException{
      return new FileInputStream(file);
   }

   /**
    * This returns the date of the last modification of the file.
    * This date is returned as the long, this is the number of
    * milliseconds since January 1 1970. This is equivelant to
    * using the <code>File.lastModified</code> method.
    *
    * @return the date of the last modification of the file
    */
   protected long getLastModified(){
      return file.lastModified();
   }
   
   /**
    * This method is used to retrieve the length of the file that
    * this <code>Content</code> represents. The size of the file
    * is assumed not to be larger than the maximum integer value,
    * if it is possible for the length to exceed this length the
    * <code>File.length</code> method should be used.
    *
    * @return this returns the length of the file as an integer  
    */
   protected int getLength(){
      return (int)file.length();
   }
}

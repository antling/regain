/*
 * Indexable.java February 2001
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

import java.util.Locale;
import java.io.File;

/**
 * The <code>Indexable</code> interface defines something that 
 * can be indexed, that is the meta data for the resource can 
 * be set using the various methods provided. 
 * <p>
 * This is used in conjunction with the <code>Context</code> 
 * to set the meta data for a specific <code>Indexeable</code> 
 * object. An <code>Indexeable</code> object implements this 
 * interface so that the <code>Context</code> can set the meta 
 * data for it when the <code>Context.doIndex</code> method is 
 * invoked.
 *
 * @author Niall Gallagher
 */
public interface Indexable {

   /**
    * This allows the MIME type of this <code>Indexable</code> 
    * object to be set. The MIME type is specified by the
    * <code>Context.getMimeType</code> method for a specific
    * request URI. This should have a value and perhaps some
    * parameters like the charset, "text/html; charset=UTF-8".
    *
    * @param type the MIME type this object is to be set to
    */ 
   public void setMimeType(String type);
   
   /**
    * This will set the locale for this indexable object the 
    * locale will be set to the <code>Locale.getDefault</code> 
    * if there is no specified <code>Locale</code> for this.
    *
    * @param locale the locale for this object
    */ 
   public void setLocale(Locale locale);

   /**
    * This allows the name for this object to be set. The
    * name usually refers to the last entry in the path. 
    * So if the path was "/usr/bin/" the name is "bin".
    *
    * @param name this is the name of this object
    */ 
   public void setName(String name);
   
   /**
    * This is used to set the path that this object refers to. 
    * This should be the fully qualified normalized path. This
    * refers to the OS system specific path that this represents.
    *
    * @param path this is the path this object refers to
    */ 
   public void setRealPath(String path);

   /**
    * Every resource that the <code>Context</code> object refers to
    * is relative to some base path. This is used to set the base
    * path of the <code>Context</code> object that configured this
    * <code>Indexable</code> implementation. The base path will be
    * a system specific path like "c:\path" on a DOS system and 
    * "/home/user" on a UNIX system.
    *
    * @param path this is the base path of the <code>Context</code>
    * that was used to set this <code>Indexable</code>
    */
   public void setBasePath(String path);

   /**
    * The <code>Context</code> object maps the request URI of a 
    * resource into the <code>File</code> object using the method
    * <code>Context.getFile</code>. This method receives the file
    * object that the resource represents.
    *
    * @param file the <code>File</code> object that this object
    * represents
    */
   public void setFile(File file);
}

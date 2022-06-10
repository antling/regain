/*
 * Mapper.java May 2003
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
 
package simple.http.load;

/**
 * The <code>Mapper</code> is intended to provide a mapping from 
 * a URI to a class name. This enables objects to be referenced
 * when a specific URI format is used. The encoding of the URI is
 * known by the implementation so that it can parse and extract a
 * fully qualified package name for a Java class.   
 * <p>
 * The format of the URI is specific to the implementation. The
 * object is responsible for its own format, which could be a 
 * variation on any of the URI formats shown below.
 * <code><pre>
 *
 *    http://hostname/demo/ServiceObject.class/path/index.html
 *    /path;name=demo.ServiceObject/index.html
 *    /path/index.html?name=demo.ServiceObject
 *
 * </pre></code>
 * The <code>getClass</code> method is required to take a full
 * HTTP URI, this could include the scheme, domain, and port of
 * the URI, similar to the <code>Context</code> object methods.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.load.MapperEngine
 */
public interface Mapper {

   /**
    * This method is used to transform a URI to a class name.
    * If the URI cannot be converted to a class name then a
    * null is returned. The returned class name should be the
    * fully qualified package name of the object. 
    *
    * @param target this is the URI that is to be converted
    *
    * @return a fully qualified class name, null otherwise
    */
   public String getClass(String target);
   
   /**
    * This method is used to determine the normalized path
    * of the issued HTTP URI. Encoding a class name into the
    * request URI means that the URI will have to be modified
    * in some way. To ensure that the modified paths can be
    * used to reference local resources this method can be
    * used to extract the normalized path from the HTTP URI.
    *
    * @param target this is the URI that is to be converted
    *
    * @return returns the normalized resolved URI path part
    */
   public String getPath(String target);
}

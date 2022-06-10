/*
 * Container.java January 2004
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

/**
 * The <code>Container</code> object represents an interface to 
 * the templating system used by the <code>TemplateEngine</code>.
 * A container represents a logical database of information that
 * can be shared between controller objects and documents. It
 * is also used to acquire documents using a URI path structure.
 * 
 * @author Niall Gallagher
 */
public interface Container extends Database {

   /**
    * Looks for the named template and wraps the template within
    * a new <code>Document</code> instance. Resolving the location
    * of the template is left up the templating system, typically
    * this requires a file path reference to locate the template.
    * <p>
    * The document created by this method is transient, that is,
    * it is a unique instance. This means that changes to the 
    * properties of any created document object affect only that 
    * instance. By default this assumes the UTF-8 encoding. 
    * 
    * @param path this is the path used to locate the template
    *
    * @return the specified template wrapped within a document
    *
    * @throws Exception this is thrown if the is a problem with
    * locating or rendering the specified template
    */
   public Document lookup(String path) throws Exception;

   /**
    * Determines whether the named template exists. This is used
    * to determine if the <code>lookup</code> method will locate
    * a template given the specified path. If the template is
    * accessable this returns true, otherwise false is returned. 
    *
    * @param path this is the path used to locate the template
    *
    * @return true if the template exists, false if it does not
    */
   public boolean exists(String path);
}

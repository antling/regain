/*
 * TemplateMapper.java December 2003
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

import simple.http.load.MapperFactory;
import simple.http.load.Mapper;
import simple.http.serve.Context;

/**
 * The <code>TemplateMapper</code> is provided for convenience
 * to hide the details of the <code>MapperFactory</code> object. 
 * This is used to uncover the mapping scheme used to resolve
 * both service types and path information, which is useful in 
 * determining what paths are being referenced from a given HTTP
 * URI. The <code>TemplateEngine</code> uses a mapping scheme 
 * to resolve the controller to use in serving a request, so if
 * the HTTP URI is to be understood, its mapping must be known.
 *
 * @author Niall Gallagher
 *
 * @see simple.template.TemplateEngine
 */
final class TemplateMapper {

   /**
    * This is an instance of the system wide implementation.
    */
   private Mapper mapper;

   /**
    * Constructor for the <code>TemplateMapper</code> object. 
    * This basically creates a wrapper for an instance of the 
    * system wide mapper implementation, which is used to load
    * controller objects and resolve path information. This is
    * used for convenience, to hide the details of mapping.
    *
    * @param context this is used to initialize the mapper
    */
   public TemplateMapper(Context context) {
      this.mapper = MapperFactory.getInstance(context);
   }

   /**
    * This provides a bridge between the mapping performed by 
    * templating system and the URI scheme used by the system
    * wide mapper implementation. The URI scheme used to
    * reference controllers will not always map directly onto 
    * a template location, so this is method translates a URI
    * reference to a template path.
    *
    * @param target an unmodified URI that is to be mapped 
    *
    * @return the mapping obtained from the issued HTTP URI
    */
   public String getPath(String target) {
      return mapper.getPath(target);
   }

   /**
    * This method is used to transform a URI to a class name.
    * If the URI cannot be converted to a class name then a
    * null is returned. The returned class name will be the
    * fully qualified package name of the object. 
    *
    * @param target this is the URI that is to be converted
    *
    * @return a fully qualified class name, null otherwise
    */
   public String getClass(String target) {
      return mapper.getClass(target);
   }
}

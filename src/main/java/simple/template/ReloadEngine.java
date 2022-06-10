/*
 * ReloadEngine.java December 2003
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

import simple.http.load.MapperEngine;
import simple.http.serve.Context;
import java.io.File;

/**
 * The <code>ReloadEngine</code> is used to resolve resources from
 * a HTTP request URI. The <code>TemplateEngine</code> uses this to
 * perform all mapping, so that controller services can be loaded
 * without the need to perform any administration. The controllers 
 * are loaded from a specified file system classpath.
 *
 * @author Niall Gallagher
 */
class ReloadEngine extends MapperEngine {

   /**
    * Constructor for the <code>ReloadEngine</code> object. The
    * instance created by this provides a mapping resource engine
    * that loads and instantiates referenced services using the
    * <code>resolve</code> method to resolve the class names for
    * services. Each loaded controller service is instantiated
    * using an <code>Environment</code> object. This will act as
    * a proxy for the issued <code>Container</code> instance.
    *
    * @param context this is the context used by this engine
    * @param system the interface to the templating system used
    * @param base this is the local file system class path used
    * 
    * @throws Exception if there is a problem on initialization
    */
   public ReloadEngine(Context context, Container system, File base) throws Exception{
      super(context, base, new Delegate(system, context));
   }
}

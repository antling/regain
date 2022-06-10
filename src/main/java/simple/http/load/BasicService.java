/*
 * BasicService.java March 2002
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
 
package simple.http.load;

import simple.http.serve.BasicResource;
import simple.http.serve.Context;

/**
 * This abstract class is for convienence. This is used to provide
 * an abstract base class for the <code>Service</code> object that
 * has error handling functions implemented. It is assumed that 
 * the implementations of the <code>Service</code> will use the
 * <code>BasicService</code> to handle the occurence of errors.
 *
 * @author Niall Gallagher
 */
public abstract class BasicService extends BasicResource implements Service{

   /**
    * Constructor for the <code>BasicService</code> is used so
    * that the <code>Context</code> can be acquired. This enables 
    * the implementation to retrive resources from the underling
    * file system. The <code>Context</code> is required for format
    * of errors and directory listing. If the context is null the
    * implementation will not generate HTTP error messages.
    *
    * @param context this is the <code>Context</code> that is used
    * to generate error messages and acquire files with
    */
   public BasicService(Context context) {
      super(context);
   }

   /**
    * Many <code>Service</code> implementations will not require
    * initialization, this provides an empty implementation of the
    * <code>prepare</code> method. Services wishing to include
    * some initialization functionality can extend include an
    * implementation of the <code>prepare</code> method.
    *
    * @param engine this is the engine that this was loaded by
    * @param data an object that can be used to that can be used
    * to configure itself the service
    *
    * @exception LoadingException may be thrown by the subclass
    */               
   public void prepare(LoaderEngine engine, Object data)
      throws LoadingException {}
}

/*
 * HandlerFactory.java February 2001
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

import simple.http.ProtocolHandler;

/**
 * The <code>HandlerFactory</code> is used to retrieve a
 * <code>ProtocolHandler</code> to handle HTTP transactions. The
 * <code>ProtocolHandler</code> retrieved uses the implementation
 * of the <code>ResourceEngine</code> to produce delegate handlers
 * for the HTTP transaction.
 * <p>
 * A useful purpose for the <code>HandlerFactory</code> 
 * is also to tie the <code>ProtocolHandler</code> implementation
 * to an interface rather than an object implementation that may
 * become obsolete due to new requirements.
 *
 * @author Niall Gallagher
 */
public class HandlerFactory {

   /**
    * This will produce a <code>ProtocolHandler</code> that uses
    * the <code>ResourceEngine</code> to retrieve delegate handlers
    * for processing HTTP transactions. The delegate handlers will
    * be given the <code>Request</code> and <code>Response</code>
    * objects given to the <code>ProtocolHandler</code> interface.
    *
    * @param engine this is the <code>ResourceEngine</code> that
    * produces the delegate handlers
    *
    * @return this returns a <code>ProtocolHandler</code> that
    * can be used to process HTTP transactions
    */
   public static ProtocolHandler getInstance(ResourceEngine engine){
      return new ResourceProcessor(engine);
   }
}

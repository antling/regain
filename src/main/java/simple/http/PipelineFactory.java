/*
 * Poller.java February 2001
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
  
package simple.http;

import java.io.IOException;
import java.net.Socket;

/** 
 * The <code>PipelineFactory</code> enables any object that is
 * using a <code>PipelineHandler</code> to produce the object
 * desired for processing with a <code>Socket</code>. Once a
 * TCP connection is established the <code>Pipeline</code> is 
 * produced and handed to the <code>PipelineHandler</code>.
 * <p>
 * The <code>FilterPipeline</code> object enables subclassed 
 * <code>Pipeline</code> objects to be created, used with 
 * this interface increased functionality can be added to 
 * the connection, like security or buffering.
 * 
 * @author Niall Gallagher
 */
public interface PipelineFactory {

   /**
    * This will produce a <code>Pipeline</code> with the
    * desired functionality once the implementation 
    * wraps the given <code>Socket</code>.
    *
    * @param sock this is the <code>Socket</code> that will
    * be used to create the implementation
    *
    * @throws IOException if the implementation could not
    * be produced for some reason
    */
   public Pipeline getInstance(Socket sock)
      throws IOException;
}

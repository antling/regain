/*
 * PipelineHandler.java February 2001
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

/**
 * The <code>PipelineHandler</code> interface is used to represent
 * an object that handles <code>Pipeline</code> objects in sequence
 * with a <code>ProtocolHandler</code>. This seperates the concrete
 * implementation class from the abstraction.
 * <p>
 * This <code>PipelineHandler</code> interface represents an object 
 * that is used to process <code>Pipeline</code> objects. The intended 
 * use of this is that it be used in conjunction with an implementation
 * of a <code>ProtocolHandler</code> implementation. The design is such 
 * that this will generate <code>Request</code> and <code>Response</code>
 * objects which will be passed to the <code>ProtocolHandler</code> for
 * processing.
 * <p>
 * The <code>PipelineHandler</code> implementation may employ a method
 * of handling the <code>Pipeline</code> in such a way that when the
 * <code>InputStream</code> does not produce any information for a 
 * certain length of time the connection is severed to recover file 
 * descriptors and other resources occupied by the possibly dead
 * connection.
 *
 * @author Niall Gallagher
 */ 
public interface PipelineHandler {
   
   /**
    * Used to process the <code>Pipeline</code> which is a full duplex 
    * communication link which may contain several http requests. This 
    * will be used to read the requests from the <code>Pipeline</code> 
    * and to pass these requests to a <code>ProtocolHandler</code> for 
    * processing.
    * <p>
    * This will process the <code>Pipeline</code> and produce the HTTP
    * <code>Request</code> and <code>Response</code> implementations
    * that will be given to a <code>ProtocolHandler</code>.    
    *
    * @param pipe this is the <code>Pipeline</code> to be processed
    *
    * @throws IOException thrown if there is an I/O error
    * @throws InterruptedException thrown on an interrupt
    */ 
   public void process(Pipeline pipe) throws IOException, InterruptedException;
}

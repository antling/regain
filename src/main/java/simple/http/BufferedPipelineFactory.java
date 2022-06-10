/*
 * BufferedPipelineFactory.java Spetember 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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
 * The <code>BufferedPipelineFacotry</code> object is used to buffer
 * the output for network performance. The output channel is buffered 
 * so that the TCP packets that are sent using the sockets output 
 * stream have a size that is sufficently large. The motive for using 
 * output buffering for the TCP packets can be found in Performance 
 * Interacrions Between P-HTTP and TCP-implementations 1997. 
 * <p>
 * The implementation is by default created as a factory object for
 * creating <code>BufferedPipeline</code> instances. This is done so
 * that it can be used to dynamically issue buffered pipelines using
 * the <code>ConnectionHandlerFactory</code> object.
 *
 * @author Niall Gallagher
 */
public class BufferedPipelineFactory implements PipelineFactory{

   /**
    * This is the default size of the output byte buffer.
    */
   private int size;
   
   /**
    * This creates a <code>PipelineFactory</code> that will issue
    * instances of the <code>Pipeline</code> that buffers output
    * so that 1Kb packets are sent to the underlying socket.
    */
   public BufferedPipelineFactory(){
      this(1024);
   }

   /**
    * This creates a <code>PipelineFactory</code> that will issue
    * instances of the <code>Pipeline</code> that buffers output
    * to a specified size so that packets that are sent to the 
    * underlying socket are sufficently large.
    *
    * @param size this is the size of the output buffer used
    */
   public BufferedPipelineFactory(int size){
      this.size = size;
   }
   
   /**
    * This will produce a <code>Pipeline</code> with the buffering
    * <code>OutputStream</code>. Once the implementation wraps the 
    * given <code>Socket</code>.
    *
    * @param sock this is the <code>Socket</code> that will
    * be used to create the implementation
    *
    * @throws IOException if the implementation could not
    * be produced for some reason
    */
   public Pipeline getInstance(Socket sock) throws IOException{
      return new BufferedPipeline(sock, size);
   }
}

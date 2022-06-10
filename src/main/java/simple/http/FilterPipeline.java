/*
 * FilterPipeline.java February 2001
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

import java.util.Enumeration;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This is a <code>FilterPipeline</code> object that is used to wrap
 * the <code>Pipeline</code> it is given. This contains methods that 
 * act as proxies to the given <code>Pipeline</code>
 * <p>
 * The <code>FilterPipeline</code> enables the <code>Pipeline</code>
 * to be subclassed easily. Subclasses may want to implement buffering
 * or security features to the <code>Pipeline</code>'s that it uses.
 * The <code>PipelineFactory</code> can produce these implementations
 * to be processed with the <code>PipelineHandler</code>.
 *
 * @author Niall Gallagher
 */ 
public class FilterPipeline extends Pipeline {
   
   /**
    * This is the <code>Pipeline</code> that is wrapped.
    */
   protected Pipeline pipe;

   /**
   /**
    * This constructor allows the <code>FilterPipeline</code> to be 
    * extended in such a way that it does not do any initialization 
    * of the object itself. This is used if there is no need for a
    * <code>Socket</code> or <code>Hashtable</code> reference.
    * This is used by the <code>Poller</code> object.         
    */
   protected FilterPipeline(){
   }

   /**
    * This wraps the <code>Pipeline</code> given. This will contain
    * the same <code>Socket</code> object as the <code>Pipeline</code>
    * it is given. Any sub classes of the <code>Pipeline</code> may 
    * use this to enable it to attach some extra functionality to the
    * <code>Pipeline</code> like buffering or security.
    *
    * @param pipe the <code>Pipeline</code> that is being wrapped
    *
    * @throws IOException if the object could not be created
    */ 
   public FilterPipeline(Pipeline pipe) throws IOException{
      this.table = pipe.table;
      this.sock = pipe.sock;      
      this.pipe = pipe;
   }
   /**
    * Retrieves the <code>InputStream</code>. This is a communication 
    * channel between the client and the server. The stream returned
    * is the same as the <code>Pipeline.getInputStream</code>.
    *
    * @throws IOException thrown if there is an I/O problem.
    *
    * @return an <code>InputStream</code> from the client, i.e. the
    * <code>Pipeline</code>
    */ 
   public synchronized InputStream getInputStream() throws IOException {
      return pipe.getInputStream();
   }

   /**
    * Retrieves the <code>OutputStream</code>. This is a communication
    * channel from the server to the client. The stream returned is
    * the same as the <code>Pipeline.getOuputStream</code>.
    *
    * @throws IOException thrown if there is an I/O problem
    *
    * @return an <code>OutputStream</code> to the client, i.e. the
    * <code>Pipeline</code>
    */ 
   public synchronized OutputStream getOutputStream() throws IOException {
      return pipe.getOutputStream();
   }

   /**
    * This corresponds to the identity of the host that created
    * the connection. The domain name can be retrieved from this.
    *
    * @return the identity of the peer that created connection
    */ 
   public synchronized InetAddress getInetAddress() {
      return pipe.getInetAddress();
   }

   /**
    * This will retrieve an attribute from this attributes class.
    * The attributes in an attributes object are identified by a
    * String name. If the attribute specified by name does not
    * exist then this returns null, otherwise the correct object.
    *
    * @param name the name of the attribute to be retrieved
    *
    * @return the attribute that is referenced by the name
    */ 
   public synchronized Object getAttribute(String name) {
      return pipe.getAttribute(name);
   }

   /**
    * This can be used to determine if an attribute of a given name
    * is present int the attribute object. If there is concurrent
    * access to this object then then this may not be accurate when
    * the attribute method is invoked, for this reason the return
    * value for get should always be checked to see if its null.
    *
    * @param name the name of the attribute the is being queried
    *
    * @return true if the attribute exists, false if it does not
    */ 
   public synchronized boolean hasAttribute(String name) {
      return pipe.hasAttribute(name);
   }

   /**
    * This removes the attribute from this object. This may cause 
    * any <code>Enumeration</code> of the names to become stale.
    *
    * @param name the name of the attribute to be removed
    */ 
   public synchronized void removeAttribute(String name) {
      pipe.removeAttribute(name);
   }

   /**
    * This can be used to set an attribute. The attribute will
    * contain the name specified. This may not update any existing
    * <code>Enumeration</code> of the attribute names that was
    * previously retrieved.
    *
    * @param name the name of the attribute that is being added
    * @param obj the value of the attribute that is being added
    */ 
   public synchronized void setAttribute(String name, Object obj) {
      pipe.setAttribute(name, obj);
   }

   /**
    * This will provide an <code>Enumeration</code> object for the
    * attribute names for this object. This may not update if there 
    * are concurrent remove and set operations when this is used.
    *
    * @return an <code>Enumeration</code> of the names of the
    * attributes
    */ 
   public synchronized Enumeration getAttributeNames() {
      return pipe.getAttributeNames();
   }  
 
   /**
    * This is a close method that ensures the communication link 
    * is shutdown. Closes the <code>InputStream</code> and the
    * <code>OutputStream</code> of the <code>Pipeline</code>. This 
    * method will not propagate any exceptions.
    */ 
   public synchronized void close() {
      pipe.close();
   } 
}

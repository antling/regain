/*
 * Pipeline.java February 2001
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

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This is a <code>Pipeline</code> object that is used to represent 
 * a HTTP <code>Pipeline</code>. This contains methods that allow
 * attributes to be associated with a <code>Pipeline</code>. It is
 * implements the <code>Attributes</code> interface which allows
 * objects to be stored with the <code>Pipeline</code>.
 * <p>
 * This implements a close that allows the <code>Pipeline</code> to 
 * be closed. This ensures that both objects are closed i.e. both 
 * the <code>InputStream</code> and the <code>OutputStream</code> 
 * will be closed after the <code>close</code> method is invoked. 
 *
 * @author Niall Gallagher
 */ 
public class Pipeline implements Attributes {

   /**
    * This is used to store any connection attributes. 
    */
   protected Hashtable table;

   /**
    * This is the <code>Socket</code> that contains the 
    * input and output streams.
    */
   protected Socket sock;

   /**
    * This constructor allows the <code>Pipeline</code> to be extended
    * in such a way that it does not involve ant initialization of the
    * <code>Pipeline</code> itself. This is used if there is no need
    * for a <code>Socket</code> or <code>Hashtable</code> reference.
    * This is used by <code>FilterPipeline</code>.         
    */
   protected Pipeline(){
   }

   /**
    * This creates a <code>Pipeline</code> from a <code>Socket</code> 
    * object. Any sub classes of the <code>Pipeline</code> object may 
    * use this constructor to ensure that all the data is initialized 
    * for the <code>Pipeline</code>.  
    *
    * @param sock the <code>Socket</code> that contains the streams
    *
    * @throws IOException if the object could not be created
    */    
   public Pipeline(Socket sock) throws IOException {
      this.table = new Hashtable(); 
      this.sock = sock;    
   } 

   /**
    * Retrieves the <code>InputStream</code>. This is a communication 
    * channel between the client and the server. The stream returned
    * is the same as the <code>Socket.getInputStream</code>.
    *
    * @throws IOException thrown if there is an I/O problem.
    *
    * @return an <code>InputStream</code> from the client, i.e. the
    * <code>Pipeline</code>
    */ 
   public synchronized InputStream getInputStream() throws IOException {
      return sock.getInputStream();
   }

   /**
    * Retrieves the <code>OutputStream</code>. This is a communication
    * channel from the server to the client. The stream returned is
    * the same as the <code>Socket.getOuputStream</code>.
    *
    * @throws IOException thrown if there is an I/O problem
    *
    * @return an <code>OutputStream</code> to the client, i.e. the
    * <code>Pipeline</code>
    */ 
   public synchronized OutputStream getOutputStream() throws IOException {
      return sock.getOutputStream();
   }

   /**
    * This corresponds to the identity of the host that created
    * the connection. The domain name can be retrieved from this.
    *
    * @return the identity of the peer that created connection
    */ 
   public synchronized InetAddress getInetAddress() {
      return sock.getInetAddress();
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
      return table.get(name);
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
      return table.containsKey(name);
   }

   /**
    * This removes the attribute from this object. This may cause 
    * any <code>Enumeration</code> of the names to become stale.
    *
    * @param name the name of the attribute to be removed
    */ 
   public synchronized void removeAttribute(String name) {
      table.remove(name);
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
      table.put(name, obj);
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
      return table.keys();
   }  
 
   /**
    * This is a close method that ensures the communication link 
    * is shutdown. Closes the <code>InputStream</code> and the
    * <code>OutputStream</code> of the <code>Socket</code>. This 
    * method will not propagate any exceptions.
    */ 
   public synchronized void close() {
      try {    
         sock.close();
      } catch(IOException e){
         e.printStackTrace();
      }
   }
}

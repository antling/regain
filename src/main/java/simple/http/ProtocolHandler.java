/*
 * ProtocolHandler.java February 2001
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

/**
 * This is a <code>ProtocolHandler</code> that is used to process
 * HTTP <code>Request</code> and <code>Response</code> objects. The
 * intended purpose of the <code>ProtocolHandler</code> object is
 * to process <code>Request</code> and <code>Response</code> objects,
 * that is, respond to the given <code>Request</code>.
 * <p>
 * Any implementation should make this method thread safe as the
 * <code>Request</code> and <code>Response</code> objects will be
 * passed in concurrently with perhaps many other instances.
 * <p>
 * The <code>ProtocolHandler</code> is entirely responsible for the
 * HTTP message headers and body. It is up to the implementation to
 * ensure that it complies to RFC 2616 or any previous specification.
 * All headers and the status line can be modified by this object.
 *
 * @author Niall Gallagher
 */
public interface ProtocolHandler {

   /**
    * Used to pass the <code>Request</code> and <code>Response</code>
    * to the <code>ProtocolHandler</code> for processing. Any
    * implementation of this interface must ensure that this is
    * thread safe, it will be invoked concurrently.
    * <p>
    * The <code>Request</code> and <code>Response</code> objects are
    * in control of the HTTP pipeline. If the next HTTP request is
    * to be processed the <code>Response</code> MUST be committed
    * and the <code>Request</code> MUST have its content fully read
    * or the <code>InputStream</code> closed if there is content.
    *
    * @param req the <code>request</code> object to be processed
    * @param resp the <code>Response</code> object to be processed
    */
   public void handle(Request req, Response resp);
}

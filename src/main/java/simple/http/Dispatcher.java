/*
 * Dispatcher.java February 2001
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
 * This is a <code>Dispatcher</code> object that is used to execute
 * an HTTP transaction. This will build a <code>Request</code> and
 * a <code>Response</code> object which will then be passed to a
 * <code>ProtocolHandler</code> where the <code>Request</code>
 * will be examined and a <code>Response</code> generated as the
 * <code>ProtocolHandler</code> sees it.
 *
 * @author Niall Gallagher
 */
final class Dispatcher implements Runnable {

   /**
    * The <code>ProtocolHandler</code> issued the callback with
    * the <code>Request</code> and <code>Response</code>.
    */
   private ProtocolHandler handler;

   /**
    * This is the encapsulation of the <code>Request</code>.
    */
   private Request req;

   /**
    * This is the encapsulation of the <code>Response</code>.
    */
   private Response resp;

   /**
    * This is used to create a <code>Dispatcher</code> that is to be
    * used to execute the transaction, the <code>Dispatcher</code>
    * is a <code>Runnable</code> object.
    *
    * @param handler the <code>ProtocolHandler</code> that is given
    * the HTTP <code>Request</code> and <code>Response</code>
    * objects to be processed as a transaction
    * @param poll the <code>Poller</code> that read
    * <code>Request</code> from the <code>Pipeline</code>
    * @param mon the is to be notified of any I/O events that occur
    *
    * @throws IOException if there is a problem with the
    * <code>Pipeline</code>
    */
   public Dispatcher(ProtocolHandler handler, Poller poll,
            Monitor mon) throws IOException {
      this.handler = handler;
      this.init(poll, mon);
   }

   /**
    * This is executed to process the HTTP <code>Request</code>
    * using the <code>ProtocolHandler</code> given. When this
    * method ends the transaction may still be being processed.
    * The transaction ends when the <code>Monitor</code> is
    * notified that both the <code>Request</code> and
    * <code>Response</code> notify that they have finished.
    */
   public void run() {
      handler.handle(req, resp);
   }

   /**
    * This creates the <code>Request</code> and <code>Response</code>
    * objects that are used to encapsulate the HTTP <code>Request</code>
    * and <code>Response</code> information. This will also send a
    * <code>HTTP/1.1 100 Continue</code> provisional response if there
    * is an <code>Expect: 100-continue</code> in the HTTP/1.1 request.
    *
    * @param poll the <code>Poller</code> that read <code>Request</code>
    * from the <code>Pipeline</code>
    * @param mon the is to be notified of any I/O events that occur
    *
    * @throws IOException the <code>MonitoredResponse</code>
    * throws the exception
    */
   private void init(Poller poll, Monitor mon) throws IOException {
      req = new MonitoredRequest(poll, poll, mon);
      resp = new MonitoredResponse(req, poll, mon);

      if(req.contains("Expect")) {
         if(req.getValue("Expect").equalsIgnoreCase("100-continue")) {
            String text = "HTTP/1.1 100 Continue\r\n\r\n";
            byte[] body = text.getBytes("iso-8859-1");

            poll.getOutputStream().write(body);
         }
      }
   }
}

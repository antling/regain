/*
 * NullOutputStream.java February 2001
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

import java.io.OutputStream;
import java.io.IOException;

/**
 * The purpose of this <code>OutputStream</code> is to provide an 
 * <code>OutputStream</code> that does nothing. If a stream is 
 * needed for some task but the stream is not writing to anything
 * the <code>NullOutputStream</code> is used.
 * <p>
 * This acts similar to a <code>MonitoredOutputStream</code> in
 * that it notifies an <code>OutputMonitor</code>. However, this
 * will not wait for an I/O event before notification, once this 
 * has been created a <code>notifyFinished</code> is issued. 
 *
 * @author Niall Gallagher
 */ 
class NullOutputStream extends OutputStream {

   /** 
    * Constructor for the <code>NullOutputStream</code>. This is
    * used to provide a <code>notifyFinished</code> to this given
    * <code>OutputMonitor</code> so signify output is finished.
    *
    * @param out the stream that that data is to be written to
    * @param mon the monitor that will be notified of I/O events
    */
   public NullOutputStream(OutputStream out, OutputMonitor mon) {
      mon.notifyFinished(out);
   }

   /**
    * This is an empty implementation of <code>write</code>. This
    * is the only abstract of the <code>OutputStream</code> object
    * and is delegated to by other <code>write</code> methods.
    *
    * @param octet this is the byte that is intended to be written
    *
    * @exception IOException this exception will never be thrown
    */ 
   public void write(int octet) throws IOException {}
}

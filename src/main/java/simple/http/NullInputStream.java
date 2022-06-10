/*
 * NullInputStream.java February 2001
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

import java.io.InputStream;
import java.io.IOException;

/**
 * The purpose of the <code>NullInputStream</code> is to provide
 * an empty implementation of an <code>InputStream</code> object. 
 * This should be used where an <code>InputStream</code> is used 
 * but where there is nothing to be read.
 * <p>
 * This acts similar to a <code>MonitoredInputStream</code> in
 * that it notifies an <code>InputMonitor</code>. However, this
 * will not wait for an I/O event before notification, once this 
 * has been created a <code>notifyFinished</code> is issued. 
 *
 * @author Niall Gallagher
 */ 
class NullInputStream extends InputStream {

   /** 
    * Constructor for the <code>NullInputStream</code>. This is
    * used to provide a <code>notifyFinished</code> to this given
    * <code>InputMonitor</code> so signify output is finished.
    *
    * @param in the stream that that data is to be read from 
    * @param mon the monitor that will be notified of I/O events
    */
   public NullInputStream(InputStream in, InputMonitor mon) {
      mon.notifyFinished(in);
   }

   /**
    * This is an empty implementation of <code>read</code>. This
    * is the only abstract of the <code>InputStream</code> object
    * and is delegated to by other <code>read</code> methods.
    *
    * @return always return -1 to signify the end of the stream
    *
    * @exception IOException this exception will never be thrown
    */ 
   public int read() throws IOException {
      return -1;
   }
}

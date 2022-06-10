/*
 * InputMonitor.java February 2001
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

/**
 * It is important to note that once one of these methods is invoked 
 * by an <code>InputStream</code> or an object that is monitoring an
 * <code>InputStream</code> then the <code>InputStream</code> must 
 * not be used from that point on, that is, no more reading.
 *
 * @author Niall Gallagher
 */ 
interface InputMonitor {    

   /**
    * This method will not do any thing but mark the stream as
    * finished. This indicates that some object is finished with
    * this <code>InputStream</code> the inpur stream should not 
    * be used further.
    *
    * @param in the <code>InputStream</code> being monitored
    */    
   public void notifyFinished(InputStream in);

   /**
    * This will notify that the stream in should be closed. The 
    * stream will be closed by the <code>InputMonitor</code> and 
    * not the object that is notifying the monitor, this mabye 
    * closed asynchronously.
    *
    * @param in the <code>InputStream</code> being monitored
    */    
   public void notifyClose(InputStream in);

   /**
    * This will notify that the stream has encountered an error. 
    * There should be no further interaction with the stream
    * after this has been invoked. The stream will subsequently 
    * be closed by this.
    *
    * @param in the <code>InputStream</code> being monitored    
    */ 
   public void notifyError(InputStream in);
}


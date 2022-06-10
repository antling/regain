/*
 * Monitor.java February 2001
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
import java.io.InputStream;
import java.io.IOException;

/**
 * This is a <code>Monitor</code> object that is used to reprocess 
 * a <code>Poller</code>. When a request has been taken from a the
 * <code>Pipeline</code> being polled, and a reponse has been issued,
 * then this means that that <code>Pipeline</code> is no longer being
 * used, and so the <code>Pipeline</code> is ready to be used in the 
 * next request. 
 * <p>
 * If however there was an error in processing the current request 
 * then this <code>Monitor</code> will be notified. In the case that 
 * there was an error in processing a request/response transaction 
 * using this <code>Poller</code> then the <code>Poller</code> will 
 * not be reprocessed. The <code>Poller</code> is only reprocessed 
 * when the transaction is finished, indicated by a 
 * <code>notifyFinished</code> for both the <code>OutputStream</code> 
 * and the <code>InputStream</code> of the <code>Pipeline</code>. 
 * <p>
 * Also if there was a <code>notifyClose</code> event then the 
 * <code>Poller</code> will be closed by calling the <code>close</code> 
 * method. In this case the <code>Poller</code> will not be reprocessed. 
 * <p>
 * In summary this is used to indicate when the next HTTP message 
 * header is the next group of bytes on the <code>Pipeline</code> 
 * and the <code>Pipeline</code>'s output for the previous request 
 * has been made.
 *
 * @author Niall Gallagher
 */ 
final class Monitor implements InputMonitor, OutputMonitor {

   /**
    * A bitwise mask indicating close or error notification.
    */
   private static int CLOSE_STREAM = 0x1; 

   /**
    * This is a bitwise mask indicating input notification.
    */
   private static int INPUT_NOTIFIED = 0x2; 

   /**
    * This is a bitwise mask indicating input notification.
    */
   private static int OUTPUT_NOTIFIED = 0x4; 

   /**
    * A bitwise mask indicating output and input notification.
    */
   private static int BOTH_NOTIFIED = 0x6; 

   /**
    * A bitwise mask indicating close and input notification.
    */
   private static int INPUT_CLOSE = 0x3;  

   /**
    * A bitwise mask indicating output and close notification.
    */
   private static int OUTPUT_CLOSE = 0x5; 

   /**
    * Processes the <code>Poller</code> when its finished.
    */
   private PollerHandler handler;
  
   /**
    * This is the <code>Poller</code> object for this 
    * <code>Monitor</code>.
    */
   private Poller poller;
   
   /**
    * This accumulates the events that this <code>Monitor</code>
    * has recived, for example <code>notifyFinished</code>.
    */
   private int mask;

   /**
    * Creates a monitor to monitor access to the <code>Pipeline</code>. 
    * When an <code>InputStream</code> or <code>OutputStream</code> 
    * gives any event notification to this <code>Monitor</code> it 
    * reprocesses the <code>Poller</code> for that <code>Pipeline</code>.
    * <p>
    * The reprocessing is done only if both the <code>InputStream</code> 
    * and the <code>OutputStream</code> issue <code>notifyFinished</code>. 
    * events. If a <code>notifyError</code> or a <code>notifyClose</code> 
    * event is issued by any of the monitored streams then the 
    * <code>Poller</code> is closed and not reprocessed.
    *
    * @param handler the <code>PollerHandler</code> that will
    * process the <code>Processor</code>
    * @param poller the <code>Poller</code> top be reprocessed
    */ 
   public Monitor(PollerHandler handler, Poller poller) {
      this.handler = handler;
      this.poller = poller;
   }

   /**
    * This will pass the <code>Poller</code> back to the
    * <code>PiplineProcesor</code> when the <code>Poller</code> is 
    * ready. The <code>Poller</code> is considered ready when both 
    * the <code>InputMonitor</code> and the <code>OutputMonitor</code> 
    * recive the <code>notifyFinished</code> notification. The
    * <code>Poller</code> will only be reprocessed if there is no
    * notification of an error using <code>notifyError</code> or
    * close using <code>notifyClose</code>.
    */ 
   private void reprocess() {
      try {
         handler.notifyWait(poller);
      }catch(Exception io) {
         poller.close();
      }
   }

   /**
    * This method will not do any thing but mark the stream as
    * finished. This indicates that some object is finished with
    * this <code>InputStream</code> this allows the
    * <code>Poller</code> to be reprocessed.
    *
    * @param in this is the <code>InputStream</code> that is being
    * monitored
    */  
   public synchronized void notifyFinished(InputStream in){    
      if((mask & BOTH_NOTIFIED)==BOTH_NOTIFIED) {
         return; 
      } else if((mask & OUTPUT_CLOSE) == OUTPUT_CLOSE){    
         poller.close();
      } else if((mask & OUTPUT_NOTIFIED) >0) {
         reprocess();
      }
      mask |= INPUT_NOTIFIED;
   }


   /**
    * This will notify that the stream in should be closed. The 
    * stream will be closed by the <code>InputMonitor</code> and
    * not the object that is notifying the <code>InputMonitor</code>.
    *
    * @param in this is the <code>InputStream</code> being
    * monitored
    */  
   public synchronized void notifyClose(InputStream in){
      if((mask & BOTH_NOTIFIED) == BOTH_NOTIFIED) {
         return; 
      } else if((mask &OUTPUT_NOTIFIED) >0){
         poller.close();
      }     
      mask |= INPUT_CLOSE; 
   }

   /**
    * This will notify that the stream has encountered an error.
    * There should be no further interaction with the
    * <code>InputStream</code> after this has been invoked. The
    * stream will subsequently be closed by the
    * <code>InputMonitor</code> when both I/O totally ceases.
    *
    * @param in this is the <code>InputStream</code> being
    * monitored
    */ 
   public synchronized void notifyError(InputStream in){
      if((mask &BOTH_NOTIFIED) == BOTH_NOTIFIED) {
         return; 
      } else if((mask & OUTPUT_NOTIFIED) >0){
         poller.close();
      } 
      mask |= INPUT_CLOSE;
   }
   
   /**
    * This will simply mark this stream as finished. This means
    * that the <code>OutputStream</code> or object that was
    * monitoring this <code>OutputStream</code> is now finished
    * using it. The <code>OutputStream</code> out must not be 
    * used by the object that made the invocation from this point
    * on, because the <code>Poller</code> will now control the 
    * stream.
    *
    * @param out this is the <code>OutputStream</code> that is 
    * being monitored
    */ 
   public synchronized void notifyFinished(OutputStream out){
      if((mask & BOTH_NOTIFIED) ==BOTH_NOTIFIED) {
         return; 
      } else if((mask & INPUT_CLOSE) == INPUT_CLOSE){         
         poller.close();
      } else {
         flushOutput(out); 
         if((mask & INPUT_NOTIFIED) >0) {
            reprocess();
         }
      } 
      mask |= OUTPUT_NOTIFIED;
   }

   /**
    * When the monitor has notified that it is finished and that the 
    * <code>Poller</code> is to be reprocessed then the response needs 
    * to be flushed to the client. This will attempt to flush the 
    * response that may be buffered in the <code>OutputStream</code> 
    * to the client. This is an important step.
    *
    * @param out the <code>OutputStream</code> to be flushed
    */     
   private void flushOutput(OutputStream out) {
      try {
         out.flush();
      }catch(Exception io){
         poller.close();         
      }
   }

   /**
    * This will notify that this <code>OutputStream</code> should be
    * closed. It is the task of the <code>OutputMonitor</code> and 
    * not the object that is making the invocation to close the
    * <code>OutputStream</code>.
    *
    * @param out this is the <code>OutputStream</code> being
    * monitored
    */ 
   public synchronized void notifyClose(OutputStream out){
      if((mask & BOTH_NOTIFIED) == BOTH_NOTIFIED) {
         return; 
      } else if((mask & INPUT_NOTIFIED) > 0){
         poller.close();
      }      
      mask |= OUTPUT_CLOSE;
   }

   /**
    * This will notify the monitor that an error occured when
    * writing to the <code>OutputStream</code>. The
    * <code>OutputMonitor</code> will in its own time (mabye
    * asynchronously) close the <code>OutputStream</code>.
    *
    * @param out this is the <code>OutputStream</code> being
    * monitored
    */ 
   public synchronized void notifyError(OutputStream out){
      if((mask & BOTH_NOTIFIED) == BOTH_NOTIFIED) {
         return; 
      } else if((mask & INPUT_NOTIFIED) >0){
         poller.close();
      } 
      mask = OUTPUT_CLOSE;
   }
}


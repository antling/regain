/*
 * BasicPoller.java March 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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
import java.io.InputStream;

/**
 * The <code>BasicPoller</code> object provides an implementation 
 * of the <code>Poller.process</code> method which uses a quick
 * Boyer Moore token search scheme. This method of searching for
 * the terminating CRLF CRLF sequence is much quicker than doing
 * a simple scan through the consumed bytes, typically this will 
 * be about three times quicker than a conventional scan.
 * <p>
 * To enable subclass implementations to influence the events
 * dispatched to the <code>PollerHandler</code> this provides a
 * means to do so. The <code>wait</code> method is invoked when
 * no data has been read from the HTTP pipeline. When this is
 * invoked the current phase is also provided. This can be used
 * to determine how many polls have been empty.
 *
 * @author Niall Gallagher
 */ 
abstract class BasicPoller extends BufferPoller {  

   /**
    * This is the character sequence that terminates a request.
    */ 
   private static final byte[] token = {
   13, 10, 13, 10};   

   /**
    * The delta array that is used to search for the terminal.
    */
   private static final int[] delta = {
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 4, 1, 4, 4,  
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
   4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

   /**
    * This is the point at which the header is searched from.
    */   
   private int off = token.length;

   /**
    * This determines how many times a poll read zero bytes.
    */
   private int phase;
   
   /**
    * Constructor for the <code>BasicPoller</code> object. This
    * creates an implementation that reads the contents of the
    * pipeline using a quick Boyer Moore token search scheme.
    * This can be subclassed to introduce scheduling for the
    * poller. Scheduling is determined by the phase counter.
    *
    * @param pipe the pipeline that contains the HTTP requests
    *
    * @throws IOException if the pipeline can not be polled
    */ 
   public BasicPoller(Pipeline pipe) throws IOException{
      super(pipe);
   }

   /**
    * This performs a non-blocking read of the HTTP header. This
    * will never encounter a case where it will block. It makes 
    * sure that each read is less than or equal to the number of
    * available bytes within the stream. The necessity of having
    * a non-blocking read is obvious when the number of threads
    * is less than the number of HTTP pipelines. 
    * <p>   
    * This delegates to the <code>wait</code> method when no more
    * bytes can be read in a non-blocking manner. Also, this will
    * delegate to the <code>ready</code> method when the HTTP
    * header has been fully read from the pipeline.
    *
    * @param handler this is handles all events that can occur
    *
    * @exception IOException if an I/O error occurs in reading 
    * @exception InterruptedException if an interrupt is issued
    */ 
   public synchronized void process(PollerHandler handler) 
      throws IOException, InterruptedException{
      int avail = data.available();
      
      if(avail > 0) {
         phase = 0;                
         
         while(avail > 0 || (avail=data.available())>0){
            if(buf.length > 8192){
               throw new IOException("Header too long");
            }
            if(buf.length < off + 512){
               ensureCapacity(off + 512);
            }
            int min = Math.min(avail,512); 
            int num = data.read(buf,count,min);
            
            if(num <= 0){
               break;
            }            
            while(off <= count+num){
               int pos = token.length;      
               
               while(token[pos-1] == buf[off-1]){
                  if(pos > 1){
                     pos--;
                     off--;
                  }else {
                     off = --off + token.length;
                     data.unread(buf,off,num-(off-count));
                     count = off;
                     ready(handler);
                     off = token.length;
                     count = 0;
                     return;
                  }
               }
               off += delta[buf[off -1]]; 
            }
            count+=num;
            avail-=num;
         }
      } else{
         phase++;
      }         
      wait(handler, phase);               
   }

   /**
    * This is invoked when the HTTP header has been consumed from
    * the pipeline in full. Implementations of this method will
    * typically invoke the handlers <code>notifyReady</code> 
    * method to indicate that a request can be processed from the
    * pipeline, however extra functionality can be introduced.
    *
    * @param handler this is handles all events that can occur
    *
    * @exception IOException if an I/O error occurs in reading 
    * @exception InterruptedException if an interrupt is issued
    */
   protected abstract void ready(PollerHandler handler)
      throws InterruptedException, IOException;

   /**
    * This is invoked when no more bytes can be read from the
    * pipeline without blocking. Implementations of this method
    * will typically invoke the handlers <code>notifyWait</code> 
    * method to indicate that the poller should be put into the
    * wait queue for a period of time. The length of time that
    * the poller waits for is up to the subclass implementation.
    *
    * @param handler this is handles all events that can occur
    * @param phase the number of unsuccessful polls attempted
    *
    * @exception InterruptedException if an interrupt is issued
    */ 
   protected abstract void wait(PollerHandler handler, int phase) 
      throws InterruptedException;
}

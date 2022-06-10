/*
 * PulseScheduler.java February 2001
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
 
package simple.util.schedule;

/**
 * The <code>PulseScheduler</code> is a scheduler that is used when
 * the time accuracy is not of great importance. This will ensure that
 * the scheduled items can be dequeued in pulses. 
 * <p>
 * The aim of this type of <code>Scheduler</code> is to allow a large 
 * number of items to be enqueued using a large number of threads. 
 * The fact that there is a large number of threads means that the 
 * <code>Registry</code> will issue a large number of interrupts to 
 * dequeuers. The aim of the <code>PulseScheduler</code> is to ensure 
 * that the number of thread interrupts issued is kept low. 
 * <p>
 * It achives this by ensuring that threads that are enqueuing objects
 * have timeouts that are rounded to the nearest pulse. The effect that
 * this has is that many objects will be enqueued with the same timeout 
 * values. When an enqueue is invoked the <code>Registry</code> is looked 
 * up to see if there is a thread dequeuing that has a larger timeout, if 
 * the timeouts are rounded then the <code>Registry</code> will see that
 * there are few threads that are dequeuing with larger timeout values an
 * interrupt will not be issued. 
 * <p>
 * Care should be taken when choosing this type of <code>Scheduler</code>,
 * large performance decreases can be incurred if the problem does not fit 
 * with the <code>PulseScheduler</code>. For most problems the default
 * <code>Scheduler</code> is a better choice. This has a default maximum 
 * timeout of one minute and a default pulse of one hundred ms.
 *
 * @author Niall Gallagher
 */ 
public class PulseScheduler extends Scheduler {

   /**
    * The default frequency with which times are rounded.
    */
   protected static final long DEFAULT_FREQ = 100;

   /**
    * The frequency that this <code>Scheduler</code> uses.
    */
   protected long pulse;

   /**
    * This is just a simple operation saving variable.
    */
   protected long half;

   /**
    * This creates a <code>PulseScheduler</code> to schedule objects. 
    * This type of <code>Scheduler</code> is used to keep the interrupt 
    * rate low for threads. The <code>PulseScheduler</code> keeps the 
    * interrupt rate low by rounding the timeouts that objects are 
    * scheduled for to the next pulse timeout.
    */ 
   public PulseScheduler() {
      this(DEFAULT_MAX, DEFAULT_FREQ);      
   }

   /**
    * This creates a <code>PulseScheduler</code> to schedule objects. 
    * This type of <code>Scheduler</code> is used to keep the interrupt
    * rate low for threads. The <code>PulseScheduler</code> keeps the 
    * interrupt rate low by rounding the timeouts that objects are 
    * scheduled for to the next pulse timeout.
    *
    * @param max this is the maximum timeout that an object can have
    */ 
   public PulseScheduler(long max) {
      this(max, DEFAULT_FREQ);
   }

   /**
    * This creates a <code>PulseScheduler</code> to schedule objects. 
    * This type of <code>Scheduler</code> is used to keep the interrupt
    * rate low for threads. The <code>PulseScheduler</code> keeps the 
    * interrupt rate low by rounding the timeouts that objects are 
    * scheduled for to the next pulse timeout.
    *
    * @param freq this is the frequency of the pulses for this
    * <code>Scheduler</code>
    * @param max this is the maximum timeout that an object can have
    */ 
   public PulseScheduler(long max, long freq) {
      super(max);
      pulse = freq > 0L ? freq : 1L;
      half = pulse >> 1L;
   }   

   /**
    * This just adds some changes to the superclasses version of the
    * enqueue method. This will ensure that the timeout of the entry
    * is rounded to the nearest pulse. This keeps the interrupts low.
    *
    * @param entry this is the entry object that is being scheduled
    */ 
   protected void enqueue(Entry entry) {
      long over = entry.timeout % pulse;
      entry.timeout -= over;

      if(over > half){
         entry.timeout += pulse;
      }      
      super.enqueue(entry);      
   }
}

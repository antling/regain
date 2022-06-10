/*
 * Registry.java February 2001
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
 * This is used by the <code>Scheduler</code> to manage the threads
 * that are dequeuing from the <code>Scheduler</code>. This allows
 * threads to be added to an dequeuers list. When a thread is waiting
 * to dequeue an object from the <code>Scheduler</code> it registers
 * itself with the <code>Registry</code> by calling the sleeping
 * method and specifing when the object will wake.
 * <p>
 * This <code>Registry</code> allows threads to be interrupted from a
 * sleeping state. When a thread is waiting on an objects timeout to
 * expire it may be interrupted. The interrupt method is there to allow
 * an enqueuer to say that there is a new object being enqueued which
 * has a timeout that may be less than one of the sleeping threads. If
 * this is the case then the thread will be interrupted. This ensures
 * that an object will allways exit at its timeout and wont be blocked
 * sleeping for another object with a higher timeout.
 *
 * @author Niall Gallagher
 */
final class Registry {

   /**
    * A linked list of the dequeuing threads.
    */
   private Entry root;

   /**
    * This creates a new <code>Registry</code> to manage the threads
    * that are dequeuing from the <code>Scheduler</code>. This is
    * used to interrupt any thread which may be blocked sleeping for
    * an object whose timeout is larger than the timeout of a newly
    * enqueued object, i.e. the <code>Scheduler</code> is an
    * interactive concurrent <code>PriorityQueue</code>.
    */
   public Registry() {
      init();
   }

   /**
    * This creates the root entry. The root entry is an entry that
    * has a wait that is the largest possible wait. This ensures
    * that if this is at the top of the list then there are no more
    * entrys in the list.
    */
   private void init() {
      root = new Entry();
      root.wait = Long.MAX_VALUE;
      root.prev = root;
      root.next = null;
   }

   /**
    * This will register a thread with the <code>Registry</code>.
    * This indicates that the current thread is the one that will
    * be dequeuing.
    *
    * @param timeout time the current thread will be sleeping for
    */
   public synchronized void sleeping(long timeout) {
      Thread thread = Thread.currentThread();
      Entry entry = new Entry();
      entry.thread = thread;
      entry.wait = timeout;
      link(entry, root);

   }

   /**
    * This is used to add an entry into a list that begins with the
    * entry head. This is iterative, the base case(s) is when the entry
    * being added is greater than the head of the list, also when it
    * encounters the end of the list. There is a trick used by the root
    * entry to allow it to be used as head i.e. root.prev = root.
    *
    * @param entry the entry to be added to the ordered linked list
    * @param head the head of the linked list to add the enbry to
    */
    private void link(Entry entry, Entry head) {
      while(head.next != entry && head.prev != entry) {
         if(head.wait <= entry.wait) {
            head.prev.next = entry;
            entry.prev = head.prev;
            head.prev = entry;
            entry.next = head;
         } else if(head.next == null) {
            head.next = entry;
            entry.prev = head;
         } else {
            head = head.next;
         }
      }
   }

   /**
    * This is used to remove an element from the list. This
    * assumes that it is called from a synchronized method.
    *
    * @param entry the entry to remove from the linked list
    */
   private void unlink(Entry entry){
      entry.prev.next = entry.next;
      if(entry.next != null) {
          entry.next.prev = entry.prev;
      }
   }

   /**
    * This will remove a dequeuer from the <code>Registry</code>.
    * This ensures that once a thread has dequeued from the
    * <code>Scheduler</code> that it does not recive any further
    * interrupts from the <code>Registry</code>.
    */
   public synchronized void remove() {
      Thread thread = Thread.currentThread();
      for(Entry entry = root.next; entry != null;){
         if(thread == entry.thread) {
            thread.interrupted(); /* N.B. */
            unlink(entry);
            entry = null;
         } else {
            entry = entry.next;
         }
      }
   }

   /**
    * This will interrupt the thread that is sleeping longer than
    * any other thread if the timeout of that thread is greater than
    * the timeout specified, ie the firts thread in the list has the
    * longest timeout, this is because the list is ordered on timeout.
    *
    * @param wait this is the timeout of an newly scheduled object
    */
   public synchronized void interrupt(long wait) {
      Entry entry = root.next;
      if(entry != null && entry.wait > wait) {
         entry.thread.interrupt();
      }
   }

   /**
    * This is a simple object that keeps
    * dequeuing threads with there timeouts
    * and facilitates a doubly linked list.
    */
   private class Entry {
      public Thread thread;
      public long wait;
      public Entry next;
      public Entry prev;
   }
}

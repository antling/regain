/*
 * Processor.java September 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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
 
package simple.http.load;

import java.rmi.RemoteException;
import simple.util.ObjectQueue;
import java.util.Hashtable;
import java.util.Collection;
import simple.util.Match;

/**
 * The <code>Processor</code> object notifies the <code>Loader</code>
 * objects that have registered when the <code>LoaderManager</code>
 * has changed. This provides the ability for asynchronous updates
 * to be given to the <code>Loader</code> objects. The manager issues
 * the internal <code>Layout</code> that represents the manager.
 * <p>
 * The transmits the <code>Profile</code> issued which contains the
 * configuration of the links within the manager and the services
 * that have been loaded by the manager. This transfers the details
 * to another thread where the update is made asynchronously 
 * avoiding any deadlock possiblities with <code>Loader</code>s.
 * 
 * @author Niall Gallagher
 */
final class Processor extends Thread {

   /**
    * This is the <code>LoaderManager</code> is being updated.
    */
   private LoaderManager manager;

   /**
    * For asynchronous updates to the <code>Loader</code>s.
    */
   private ObjectQueue queue;

   /**
    * Contains the registered <code>Loader</code> objects.
    */
   private Hashtable registry;      

   /**
    * Constructor for the <code>Processor</code> is used to create
    * instance the starts an asynchronous thread of execution. This
    * requires the manager that this instance is using. This uses 
    * an asynchronous thread of execution which will enable the
    * <code>Loader</code> objects to register without deadlock.
    *
    * @param manager this is the <code>LoaderManager</code> that
    * is to be given with <code>Loader.update</code> invocations
    */
   public Processor(LoaderManager manager){
      this.registry = new Hashtable();
      this.queue = new ObjectQueue();
      this.manager = manager;
      this.start();
   }
   
   /**
    * This is used to insert a <code>Loader</code> object which is
    * used to recieve updates on the current layout. The monitor 
    * can be a remote object which enables this to communicate the
    * state of the <code>LoaderManager</code> to a remote process.
    *
    * @param loader this is the <code>Loader</code> that is to 
    * be registered for updates
    */
   public synchronized void add(String name, Loader loader){
      registry.put(name, loader);
   }
   
   /**
    * This is used to terminate updates on a <code>Loader</code> 
    * object which has previously registered for updates. If the
    * <code>Loader</code> wishes to resume updates it must 
    * register again with the <code>LoaderManager</code> again.
    *
    * @param name this is the <code>Loader</code> that is to
    * have registration terminated
    */
   public synchronized void remove(String name){
      registry.remove(name);
   }

   /**
    * This method will pass the <code>Profile</code> issued to the
    * <code>ObjectQueue</code> for execution. This create an 
    * instance of the <code>PlainLayout</code> and passes it to 
    * the queue so that the <code>run</code> method can dequeue it
    * and update the <code>Loader</code> objects asynchronously.
    *
    * @param profile the <code>Profile</code> that contains the
    * configuration of the loader manager
    */
   public void update(Profile profile){
      try {         
         queue.enqueue(new PlainLayout(profile));
      }catch(InterruptedException e){
         return;
      }
   }   
   
   /**
    * The <code>update</code> method performs the update on all of
    * the registered <code>Loader</code> objects. If at and stage 
    * a loader throws a <code>RemoteException</code> it is removed
    * from the <code>Hashtable</code> of loaders.
    *
    * @param layout this contains the layout of the manager
    */  
   private synchronized void update(Layout layout){
      Collection values  = registry.values();
      Object[] list = values.toArray();
      
      for(int i = 0; i < list.length; i++){  
         try{
            Loader next = (Loader)list[i];
            next.update(manager, layout);
         }catch(RemoteException e){
            values.remove(list[i]);
         }
      }      
   }
   
   /**
    * This is executed by the thread to dequeue the objects
    * from the <code>ObjectQueue</code> and invoke the methods
    * of registered <code>Loader</code> objects. This method 
    * will dequeue parameters for the life of the thread.
    */
   public void run(){
      while(true){
         try{
            Object top = queue.dequeue();
            update((Layout)top);
         }catch(InterruptedException e){
            continue;
         }
      }
   }
}



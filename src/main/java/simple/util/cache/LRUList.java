/*
 * LRUList.java February 2001
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
 
package simple.util.cache;

import java.util.HashMap;

/**
 * This is a LRU, Least Recently Used, list that will store a
 * limited number of objects. When there is an attempt to store
 * an object into this list when the list is full then the Least
 * Recently Used objects are removed from the list, In fact this
 * will remove twenty percent of the Least Recently Used objects,
 * this will ensure that there is not a removal of an object for
 * each insert into the list, this may or may not increase the
 * performance.
 *
 * @author Niall Gallagher
 */ 
public class LRUList {

   /**
    * This is the lists default initial size.
    */
   private static final int DEFAULT_SIZE = 16;

   /**
    * This is used for quicker access of objects.
    */
   private HashMap map;

   /**
    * This specifies the front of the list.
    */
   private Entry root;

   /**
    * This specifies the end of the list.
    */
   private Entry tail;

   /**
    * The number of items allowed in the list.
    */
   private int maxSize;

   /**
    * This is the number of items in the list.
    */
   private int items;

   /**
    * This will create an list with a maximum allowed number of
    * objects to be inserted into the list. If there are further
    * inserts after the list fills, then the least hit objects
    * will be removed from the list, the lookup method is a hit.
    */ 
   public LRUList(){
      this(DEFAULT_SIZE);
   }

   /**
    * This will create an list with a maximum allowed number of
    * objects to be inserted into the list. If there are further
    * inserts after the list fills, then the least hit objects
    * will be removed from the list, the lookup method is a hit.
    *
    * @param maxSize the maximum allowed number of objects
    */ 
   public LRUList(int maxSize){           
      this.map = new HashMap((maxSize+1)*2, 0.2f); 
      this.maxSize = maxSize;
   }

   /**
    * This uses a doubly linked list to store the object within
    * this list. This uses the key specified to store the object
    * in the <code>LRUList</code>. Should not use a duplicate key 
    * in the list.
    *
    * @param key a unique key, that is not used by any other object
    * @param obj the object that is being stored in the list
    */  
   public synchronized void insert(Object key, Object obj) {
      Entry entry = new Entry();
      entry.data = obj;
      entry.key = key;
      remove(key);  
      map.put(key,entry); 
      insert(entry);     
      clean();
   }

   /**
    * This is used to remove some of the items in the list. This
    * will remove items from the tail of the list. This means that
    * only items with the lowest hit rate will be purged.
    */ 
   private void clean() {
      if(items <= maxSize){
         return;
      }
      while(tail != null) {
         if(items<=(maxSize * 0.8f))
            break;         
         remove(tail.key);                        
      }      
   }

   /**
    * This uses a doubly linked list to store the entry within
    * this list. This will simply link the entry in at the top.
    *
    * @param entry the entry being linked into the list
    */  
   private void insert(Entry entry) {
      if(items == 0) {
         Entry end = new Entry();
         Entry start = entry;
         
         start.prev = null;   
         end.next = null;   
         end.prev = start;   
         start.next = end;   
         root = start;      
         tail = end;                  
      } else if(items == 1) {
         Entry end = root;          
         Entry start = entry;
         
         start.prev = null;  
         end.next = null;   
         end.prev = start;  
         start.next = end;  
         root = start;    
         tail = end;      
      }else {
         Entry next = root;
         Entry start = entry;         
          
         start.prev = null;  
         start.next = next;  
         next.prev = start;  
         root = start;              
      }
      items++;
   }

   /**
    * This method will search the list to see if there
    * is an object stored in the list under that name.
    * If there is an object stored within the list using
    * the key specified this returns true otherwise false.
    *
    * @param key the reference to the list object
    *
    * @return true only if the object is in the list
    */  
   public synchronized boolean contains(Object key) {   
      return map.containsKey(key);
   }

   /**
    * This method will search to see if it can find the
    * object stored under the key specified and return it.
    *
    * @param key the reference to the stored object
    *
    * @return the object if it is stored otherwise null
    */   
   public synchronized Object lookup(Object key) {
      Object entry = map.get(key);
      if(entry == null) {
         return null;
      }
      top((Entry)entry);  
      return root.data;   
   }

   /**
    * This is used to move an entry object to the top of the doubly
    * linked list. This is a feature of this list to allow the most
    * frequently hit entrys in the list to stay at the top of the
    * linked list. This means that most hit items stay in the list.
    *
    * @param entry the entry to move to the top of the list
    */  
   private void top(Entry entry) {
      if(entry != null && entry != root && items > 0) {
         if(entry == tail) {
            Entry end = tail.prev;
            Entry start = entry;
            Entry next = root;                                     

            end.next = null;
            start.prev = null;
            start.next = next;
            next.prev = start;
            root = start;
            tail = end;             
         } else {                                    
            Entry prev = entry.prev;
            Entry next = entry.next;
            Entry start = entry;

            next.prev = prev;
            prev.next = next;
            start.prev = null;
            start.next = null;
            start.next = root;
            root.prev = start;
            root = start;            
         }
      }
   }

   /**
    * This method will search the list to see if there
    * is an object stored in the list under that name.
    * If there is an object stored within the list using
    * the key specified this removes that object.
    *
    * @param key the reference to the list object
    */  
   public synchronized void remove(Object key) {
      remove((Entry)map.remove(key));
   }

   /**
    * This method removes an object from the list. This will
    * unlink the entry object that is stored in the list using
    * the reference given. This updates the item count.
    *
    * @param entry the object that is being unlinked
    */  
   private void remove(Entry entry) {
      if(entry != null && items > 0) {
         entry.data = null;  
         if(root == tail) {
            root = null;
         } else if(entry == tail) {   
            tail = tail.prev;            
            tail.next = null;
         }else if(entry == root) {
            root = root.next;
            root.prev = null;            
         }else {
            entry.prev.next = entry.next;
            entry.next.prev = entry.prev;
         }
         items--;         
      }
   }

   /**
    * This simply specifies the maximum size that this
    * list can grow and then purges the Least Recently
    * Used items in the list, that is items at the tail.
    *
    * @param maxSize the max size allowed for the list
    */ 
   public synchronized void resize(int maxSize){
      this.maxSize = maxSize;
      clean();
   }

   /**
    * This will simply return the number of items in the list.
    * Because this is a Least Recently Used list this should
    * never return a length greater that the maximum size.
    *
    * @return the number of items that are in this list.
    */ 
   public synchronized int length(){
      return items;
   }

   /** 
    * This is used to that the capacity of the list can be 
    * determined. The capacity indicates at what point entrys
    * are pushed off the list. Only capacity entrys can fit
    * into the list before the least recently used is removed.
    *
    * @return this returns the number of possible entrys
    */    
   public synchronized int capacity(){
      return maxSize;
   }

   /**
    * This is a simple method that will purge all entrys from
    * this list. This basically deletes the linked list and
    * emptys the <code>HashMap</code> and sets the item count 
    * to zero. The garbage collector can collect all objects
    * that were previously referenced by this list.
    */ 
   public synchronized void clear() {
      if(items >0){
         map.clear();
      }
      root = null;
      tail = null;
      items = 0;
   }

   /**
    * This data structure is used to create a doubly 
    * linked list within the <code>LRUList</code>. This 
    * also remembers specifics about the item being 
    * stored. This allows meta-data to be accessed 
    * quickly, and also enables the list to be traversed.
    */ 
   private class Entry {
      public Object data;
      public Object key;      
      public Entry next;
      public Entry prev;
   }
}



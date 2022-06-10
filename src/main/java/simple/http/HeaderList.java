/*
 * HeaderList.java February 2001
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

/**
 * This is used to provide a quick store for <code>Header</code>
 * objects. It provides a feature that allows <code>Header</code>
 * objects to be searched for and retrived quickly. This also 
 * includes a remove feature so that <code>Header</code> objects
 * can be managed.
 *
 * @author Niall Gallagher
 */ 
final class HeaderList {

   /**
    * Used to store the <code>Header</code>'s for this.
    */
   private Header[] list;
   
   /**
    * The number of <code>Header</code>'s stored in this.
    */
   private int count;

   /**
    * This created a <code>HeaderList</code> object to provide 
    * a quick search an retrive object for <code>Header</code>
    * objects. This will automatically increase its capacity as
    * needed for large amounts of <code>Header</code>'s.
    */ 
   public HeaderList() {
      list = new Header[16];
      count = 0;
   }

   /**
    * This can be used to determine how many <code>Header</code>'s
    * this object contains. The <code>headerCount</code> represents 
    * the number of individual <code>Header</code>'s that this 
    * contains.
    *
    * @return the number of <code>Header</code>'s this contains
    */ 
   public int headerCount(){
      return count;
   }

   /**
    * This can be used to add a <code>Header</code> object to the
    * list of <code>Header</code>'s that this contains. This can 
    * be retrived by using the <code>indexOf</code> methods.
    *
    * @param head the <code>Header</code> being added to this
    */ 
   public void add(Header head) {
      if(count >= list.length) {
         expandCapacity();
      }
      list[count++] = head;    
   }

   /**
    * Used to remove the <code>Header</code> at the specified index. 
    * This will invalidate any value recived by an <code>indexOf</code> 
    * method previous to this. If the index specified is not valid then 
    * an <code>IndexOutOfBoundsException</code> is thrown.
    *
    * @param off the index of the <code>Header</code> to be removed
    *
    * @return the <code>Header</code> that was removed from this
    */    
   public Header remove(int off) {
      if(off >= count || off < 0){        
         throw new IndexOutOfBoundsException();                  
      }
      Header temp = list[off];
      list[off] = null; // help gc      

      if(off == count - 1) {
         count--;
      } else {
         list[off] = list[--count];
      }
      return temp;
   }

   /**
    * This can be used to remove all <code>Header</code>'s with 
    * the specified name. This will search through the list
    * of <code>Header</code> an remove the <code>Header</code>'s
    * from the list. This will invalidate any previous indexes
    * recived.
    *
    * @param name the <code>Header</code>'s to be removed
    */ 
   public void removeAll(String name) {     
      for(int i = 0; i < count; i++) {
         int index = indexOf(name, i); 
         if(index >= 0){ 
            remove(index);
            i = index - 1;  /* account for i++ */
         }
      }
   }

   /**
    * This can be used to get the value of the <code>Header</code> 
    * at the specified index. This is a convinence method that 
    * avoids having to deal with a <code>Header</code> object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the <code>Header</code> value to be
    * returned
    *
    * @return this returns the value that the <code>Header</code> 
    * or null if the index is out of bounds
    */         
   public String getValue(int off){
      if(off < 0 || off >= count) {
         return null;
      }
      return list[off].getValue();
   }

   /**
    * This can be used to get the name of the <code>Header</code> 
    * at the specified index. This is a convinence method that 
    * avoids having to deal with a <code>Header</code> object. If 
    * the offset used specified is invalid then an exception may be 
    * thrown.
    *
    * @param off the offset of the <code>Header</code> name to be
    * returned
    *
    * @return this returns the name of the <code>Header</code> or
    * null if the index is out of bounds
    */         
   public String getName(int off){
      if(off < 0 || off >= count) {
         return null;
      }
      return list[off].getName();
   }

   /**
    * This can be used to get the value of the first <code>Header</code>
    * that has the specified name. This is a convinence method that 
    * avoids having to deal with a <code>Header</code> object and
    * the <code>indexOf</code> methods. This returns null if theres
    * not <code>Header</code>.
    *
    * @param name the <code>Header</code> to get the value from
    *
    * @return this returns the value that the <code>Header</code>
    */    
   public String getValue(String name) {
      return getValue(indexOf(name));
   }

   /**
    * This can be used to get the values of <code>Header</code>'s
    * that have the specified name. This is a convinence method that 
    * avoids having to deal with the <code>indexOf</code> methods.
    * This will return an empty array if there are no headers of
    * the specified name within this list.
    *
    * @param name this is the name of the headers that are to be
    * retreived
    *
    * @return this returns and array of values that corrospond to
    * the headers in the list
    */ 
   public String[] getValues(String name){
      int size = 0;
      for(int i = 0; i < count; i++){
         if(list[i].nameMatches(name))
            size++;         
      }
      String[] value = new String[size];
      for(int i = 0, n = 0; i < count; i++){                  
         if(list[i].nameMatches(name)){
            value[n++] = list[i].getValue();
         }
      }
      return value;
   }


   /**
    * This can be used to retrive <code>Header</code>s from this
    * object. The <code>Header</code>s are returned in the form of 
    * a <code>Header</code> object. If the index specified is not 
    * valid theres an exception.
    *
    * @param off the offset of the <code>Header</code> within this
    * object
    *
    * @return the <code>Header</code> that was found at the specified
    * offset
    */ 
   public Header get(int off) {
      if(off < 0 || off >= count) {
         return null;
      }
      return list[off];
   }
   
   /**
    * This can be used to find the first occurence of the specified 
    * <code>Header</code>. This will search through the list of 
    * <code>Header</code>'s that this contains and when it encounters 
    * a <code>Header</code> with the name specified it returns the 
    * index of that <code>Header</code>. The index will change when a 
    * remove is used. So the index is valid only for the until the next 
    * remove method or possible the next add method.
    *
    * @param name the <code>Header</code> being searched for
    *
    * @return returns the position of the first <code>Header</code>
    */  
   public int indexOf(String name) {
      for(int i = 0; i < count; i++) {
         if(list[i].nameMatches(name))
            return i;    
      }
      return -1;            
   }
   
   /**
    * This can be used to find the first occurence of the specified 
    * <code>Header</code> from a given index. This will search through 
    * the list of <code>Header</code>'s that occur after the index. 
    * When it encounters a <code>Header</code> with the name specified 
    * it returns the index of that <code>Header</code>. The index will 
    * change when a remove is used. So the index is valid only until a 
    * remove or add method is used.
    *
    * @param name the <code>Header</code> being searched for
    * @param from the index from which the search will start
    *
    * @return this returns the position of the <code>Header</code>
    */ 
   public int indexOf(String name, int from) {      
      if(from >= count || from < 0){   
         throw new IndexOutOfBoundsException();
      }
      for(int i = from; i < count; i++) {
         if(list[i].nameMatches(name))
            return i;             
      }
      return -1;
   }   
   
   /**
    * Used to expand the capacity of the <code>HeaderList</code>. If 
    * there is a large number of <code>Header</code>s to be stored 
    * then this ensure that the list can accomadate them.
    */ 
   private void expandCapacity() {
      int size = count * 2;
      Header[] temp = new Header[size];
      System.arraycopy(list, 0, temp, 0, count);
      list = temp;
   }  
}

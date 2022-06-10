/*
 * Resolver.java February 2001
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
 
package simple.util;

import simple.util.cache.LRUList;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;

/**
 * This is used to match <code>String</code>'s with the first pattern
 * that <code>String</code> matches. A pattern consists of characters
 * with either the '*' or '?' characters as wild characters. The '*'
 * character is completely wild meaning that is will match nothing or
 * a long sequence of characters. The '?' character matches a single
 * character.
 * <p>
 * It is a bad idea to follow a '*' wildcard with a '?' wild card as
 * '*' is equivelant to *? anyway. This will attempt to match a given
 * <code>String</code> with the patterns that this contains i.e. a
 * <code>String</code> "hello" will match the pattern "?ell*".
 * <p>
 * A design goal of the <code>Resolver</code> was to make it capable
 * of  high performance in a multithreaded environment. In order to
 * achieve a high performance the <code>Resolver</code> can cache the
 * resolutions it makes so that if the same text is given to the
 * <code>Resolver.resolve</code> method a cached result can be retrived
 * quickly which will decrease the length of time a thread occupies the
 * synchronized method. The cache used is a <code>LRUList</code>.
 * <p>
 * The semantics of the resolver are such that the last pattern inserted
 * with a wild string is the first one checked for a match. This means
 * that if a sequence of insertions like <code>insert(a,b)</code>
 * <code>insert(x,y)</code> is made and then a <code>resolve(z)</code>
 * is attemped then z will be compared to x first and then a, if z 
 * matches x then y is given as the result and if z matches a then b 
 * will be returned, remember if z matches both a and x then y will be
 * the result due to the fact that is was the last pattern inserted.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.cache.LRUList
 */
public class Resolver implements Serializable {

   /**
    * This is the default size of the resolve cache.
    */
   private static final int DEFAULT_CACHE = 512;

   /**
    * The is used to store recent resolves for speed.
    */
   private transient LRUList cache;

   /**
    * This is used to store the matches and patterns.
    */
   private Vector matches;
   
   /**
    * This is the size of the cache for this instance.
    */
   private int size; 
   
   /**
    * The default constructor will create a <code>Resolver</code>
    * without a large cache size. This is intended for use when 
    * the requests for <code>resolve</code> tend to use strings
    * that are reasonably similar. If the strings issued to this
    * instance are dramatically different then the cache tends 
    * to be an overhead rather than a bonus.
    */
   public Resolver(){
      this(DEFAULT_CACHE);
   }

   /**
    * This constructor allows resolves to be cached for increased
    * performance. When strings tend to be reused for pattern
    * matching then this constructor should be used. The cache is
    * a simple Least Recently Used List. So only patterns that
    * have a high 'hit' rate will remain in the cache.
    * <p>
    * If a caching is done then an <code>LRUList</code> is used.
    * This is a transient object so that when this instance of the
    * <code>Resolver</code> is serialized list is cleared.
    *
    * @param size if this is true the caching of resolves is done
    * using a <code>LRUList</code>
    */
   public Resolver(int size){
      this.cache = new LRUList(size); 
      this.matches = new Vector();
      this.size = size;
   }

   /**
    * This will search the patterns in this <code>Resolver</code> to
    * see if there is a pattern in it that matches the string given.
    * This will search the patterns from the last entered pattern to
    * the first entered. So that the last entered patterns are the
    * most searched patterns and will resolve it first if it matches.
    * <p>
    * Although it is criticial that this perform well this method
    * is synchronized. The reasnon for this is that if there was
    * several threads modifing the <code>Resolver</code> at the same
    * time <code>ConcurrentModificationException</code> exceptions
    * would be thrown this would reduce the usefulness of this object.
    *
    * @param text this is the <code>String</code> to be resolved
    *
    * @return will return the <code>String</code> that pattern
    * resolves to
    */
   public synchronized String resolve(String text){
      if(cache.contains(text)){
         Entry entry = (Entry)cache.lookup(text);
         if(entry != null) return entry.match;
      }
      char[] array = text.toCharArray();
      
      for(int pos = matches.size()-1; pos >= 0; pos--) {
         Entry entry = (Entry)matches.elementAt(pos);
         if(match(array, entry.array)){
            cache.insert(text, entry);
            return entry.match;
         }
      }
      return null;
   }

   /**
    * This will add a new pattern with its resolution. This will
    * be added to the end of the list and will be the first pattern
    * checked for a match when the <code>resolve</code> method is
    * invoked. This inserts the pair at the end of the list and so
    * it will be the first pattern checked. If the match is an 
    * empty string then there is not insertion made.
    *
    * @param pattern this is the pattern that will resolve this
    * @param match the <code>String</code> returned when a correct
    * match is made
    *
    * @throws NullPointerException if either string is null
    */
    public synchronized void insert(String pattern, String match) {
      if(match.length() > 0) {
         Entry entry = new Entry(pattern,match);
         matches.addElement(entry);
         cache.clear();
      }
    }

   /**
    * This will add a new pattern with its resolution. This will
    * be added to the end of the list and will be the first pattern
    * checked for a match when the <code>resolve</code> method is
    * invoked. This will insert the pair into the position specified.
    * This behaves like the <code>Vector.insertElementAt</code>
    * method, by bumping all entrys up one position. If the match is
    * an empty string then there is not insertion made.
    *
    * @param pattern this is the pattern that will resolve this
    * @param match the <code>String</code> returned when a correct
    * match is made
    * @param pos this is the position to insert the pair into
    *
    * @throws NullPointerException if either string is null    
    */
    public synchronized void insert(String pattern, String match, int pos){
      if(match.length() > 0){
         Entry entry = new Entry(pattern,match);
         matches.insertElementAt(entry, pos);
         cache.clear();
      }
    }

   /**
    * This will remove the entry in this <code>Resolver</code> so that
    * the pattern will not be used to resolve <code>String</code>'s any
    * more. This behaves like the <code>Vector.removeElementAt</code>.
    *
    * @param pos this is the position that is removed from this
    */
   public synchronized void remove(int pos){
      matches.removeElementAt(pos);
      cache.clear();
   }

   /**
    * This will remove the entry in this <code>Resolver</code> so that
    * the pattern will not be used to resolve <code>String</code>'s any
    * more. If the wild string is parameter is <code>null</code> then
    * there is a <code>NullPointerException</code>. This behaves like
    * the <code>Vector.removeElementAt</code> method.
    *
    * @param pattern this is the pattern that is removed from this
    */
   public synchronized void remove(String pattern){
      matches.remove(new Entry(pattern,""));
      cache.clear();
    }

   /**
    * This will remove the entry in this <code>Resolver</code> so that
    * the pattern will not be used to resolve <code>String</code>'s any
    * more. If the wild string is parameter is <code>null</code> then
    * there is a <code>NullPointerException</code>. This will only use
    * the <code>Match.getPattern</code> to remove the match. This is a
    * convienence method that it can be used with <code>getMatches</code>.
    *
    * @param match this is the match that is removed from this
    */
   public synchronized void remove(Match match){
      matches.remove(match);
      cache.clear();
   }

   /**
    * Retrives an array of <code>Match</code>'s of each pair that 
    * was entered into this <code>Resolver</code>. The elements in
    * the array are ordered to represent the order they were made.
    *
    * @return the array of <code>Match</code> objects that exist
    */
    public synchronized Match[] getMatches(){
      Match[] list = new Match[matches.size()];      
      return (Match[])matches.toArray(list);
    }

   /**
    * This will return the corrosponding <code>Match</code> at
    * the specified position. This can be used in conjunction
    * with the <code>indexOf</code> method to remove matches.
    *
    * @param pos the position <code>Match</code> to retrive
    *
    * @return this will retrun the match object at that position
    */
    public synchronized Match getMatch(int pos){
      return (Match)matches.elementAt(pos);
    }
    
   /**
    * This will return the corrosponding <code>Match</code> at
    * the specified position. This can be used in conjunction
    * with the <code>indexOf</code> method to remove matches.
    * This is a convienence method that avoids having to use
    * the <code>indexOf</code> method.
    *
    * @param pattern the position <code>Match</code> to retrive
    *
    * @return returns the match object at that position
    */
    public synchronized Match getMatch(String pattern){
      int index = indexOf(pattern);
      return index >= 0 ? getMatch(index) : null;
    }

   /**
    * Used to find the position of the <code>Match</code>
    * stored using the specified pattern. This operates in a
    * similar way to the <code>Vector.indexOf</code> method.
    *
    * @param pattern the pattern that the <code>Match</code>
    * is stored using
    *
    * @return this will return the position of that pattern
    */
   public synchronized int indexOf(String pattern){
      return matches.indexOf(new Entry(pattern,""));
   }
   
   /**
    * Used to find the position of the <code>Match</code>
    * stored using the specified pattern. This operates in a
    * similar way to the <code>Vector.indexOf</code> method.
    *
    * @param pattern the pattern that the <code>Match</code>
    * is stored using
    * @param from this starts checking from this position
    *
    * @return this will return the position of that pattern
    */
   public synchronized int indexOf(String pattern, int from){
      return matches.indexOf(new Entry(pattern,""));
   }

   /**
    * This will check this <code>Resolver</code> to see if the
    * pattern given is used by this <code>Resolver</code>, if
    * it is this will return <code>true</code>.
    *
    * @param pattern the pattern that is used resolving
    * <code>String</code>'s
    *
    * @return this will return <code>true</code> if this pattern
    * exists in this <code>Resolver</code>
    */
   public synchronized boolean contains(String pattern){
      return matches.contains(new Entry(pattern,""));
   }
    
   /**
    * This will check this <code>Resolver</code> to see if the
    * match given is used by this <code>Resolver</code>, if it 
    * is this will return <code>true</code>.
    *
    * @param match this is a specific match and pattern pair
    *
    * @return this will return <code>true</code> if this match
    * exists in this <code>Resolver</code>
    */
   public synchronized boolean contains(Match match){
      return matches.contains(match);
   }

   /**
    * This can be used to determine wheather or not there is any
    * entrys in the <code>Resolver</code>. If this <code>Resolver</code>
    * is empty this will return </code>true</code>, there must be at
    * least one entry in the <code>Resolver</code>.
    *
    * @return this returns <code>true</code> is there are no elements
    */
   public synchronized boolean isEmpty(){
      return matches.isEmpty();
   }

   /**
    * This will return the number of entrys that have been inserted into
    * this <code>Resolver</code>. This uses the <code>Vector</code>'s
    * <code>size</code> implementation to determine the number of entrys.
    *
    * @return this returns the number of pattern/match pairs
    */
   public synchronized int size(){
      return matches.size();
   }

   /**
    * The <code>Resolver</code> object is <code>Serializable</code>
    * so this method is used to recover the contents of the object.
    * Also because the <code>Resolver</code> uses a transient cache 
    * this enables the cache to be re-established if the instance 
    * is set to enable caching. The cache is instantiated regardless
    * of wheather this uses caching or not.
    *
    * @param in this is the <code>ObjectInputStream</code> the
    * <code>Resolver</code> is re-constituted from
    */
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      cache = new LRUList(size);
   }

   /**
    * This acts as a driver to the <code>match</code> method so
    * that the offsets can be used as zeros for the start of
    * matching for the <code>match(char[],int,char[],int)</code>.
    * method.
    *
    * @param str this is the buffer that is to be resolved
    * @param wild this is the pattern that will be used
    */
   private boolean match(char[] str, char[] wild){
      return match(str, 0, wild, 0);
   }

   /**
    * This will be used to check to see if a certain buffer
    * matches the pattern if it does then it returns
    * <code>true</code>. This is a recursive method that will
    * attempt to match the buffers based on the wild characters
    * '?' and '*'. This returns <code>true</code> if they match.
    *
    * @param str this is the buffer that is to be resolved
    * @param off this is the read offset for the str buffer
    * @param wild this is the pattern that will be used
    * @param pos this is the read offset for the wild buffer
    */
   private boolean match(char[] str, int off, char[] wild, int pos){
      while(pos < wild.length && off < str.length){ /* examine chars */
         if(wild[pos] == '*'){
            while(wild[pos] == '*'){ /* totally wild */
               if(++pos >= wild.length) /* if finished */
                  return true;
            }
            for(; off < str.length; off++){ /* find next matching char */
               if(wild[pos] == '?'){ /* ? is a char match */
                  if(match(str, off, wild, pos))
                     return true;
               }else if(str[off] == wild[pos]){ /* match */
                  if(match(str, off, wild, pos))
                     return true;
               }
            }
            if(str.length == off)
               return false;
         }
         if(str[off++] != wild[pos++]){
            if(wild[pos-1] != '?')
               return false; /* if not equal */
         }
      }
      if(wild.length == pos){ /* if wild is finished */
          return str.length == off; /* is str finished */
      }
      while(wild[pos] == '*'){ /* ends in all stars */
         if(++pos >= wild.length) /* if finished */
            return true;
      }
      return false;
   }

   /**
    * The <code>Entry</code> is used to keep the pattern and the
    * match togeather. It implementations the <code>Match</code>
    * interface so that the pattern match pair can be retrived
    * using the <code>getMatch</code> methods. This also has an
    * <code>equals</code> method that allows it to be searched
    * for in a list like, a <code>Vector</code>.
    */
   private class Entry implements Match {

      /**
       * This is the pattern string as an array.
       */
      public char[] array;

      /**
       * This is the pattern for this pair.
       */
      public String pattern;

      /**
       * This is the match for the pattern.
       */
      public String match;

      /**
       * This creates an <code>Entry</code> object using the given
       * pattern and match pair. This will create a character array
       * from the pattern to help the <code>Resolver</code> with
       * performance in the <code>resolve</code> method.
       *
       * @param pattern this is the pattern string for this match
       * @param match this is returned from a resolution
       *
       * @throws NullPointerException if either string is null
       */
      public Entry(String pattern, String match){
         this.array = pattern.toCharArray();
         this.match = match.toString();
         this.pattern = pattern;
      }

      /**
       * This is the pattern that this match was stored under
       * in the <code>Resolver</code>. The pattern is typically
       * a string that contains wild characters so that it can
       * be matched with certain strings.
       *
       * @return this returns the wild pattern for the match
       */
      public String getPattern(){
         return pattern;
      }

      /**
       * This is match that the <code>getPattern</code> string
       * resolves for. The <code>Resolver</code> returns this
       * string when the <code>resolve</code> method is given
       * a string that can be matched using the pattern.
       *
       * @return this is the matched string for the pattern
       */
      public String getMatch(){
         return match;
      }

      /**
       * This is used when two matches are compared for equality.
       * This will compare the <code>Match</code> implementations
       * by retriving the pattern and match and comparing both
       * for equality.
       *
       * @param item this is a <code>Match</code> being compared
       * for equality
       */
      public boolean equals(Match item){
         if(match.equals("") || item.getMatch().equals("")){            
            return item.getPattern().equals(pattern);
         }
         return item.getMatch().equals(match)&&
            item.getPattern().equals(pattern);         
      }

      /**
       * This implementation of the <code>equals</code> method will
       * allow <code>Match</code> implementations to be compared.
       * This enables the <code>Match.getMatch</code> to be an empty
       * string and act as a wildcard that matches every thing. If
       * the wildcard match is used a pattern comparison happens.
       *
       * @param item a <code>Match</code> that is to be compared
       */
      public boolean equals(Object item){
         if(item instanceof Match){
            return equals((Match)item);
         }
         return false;
      }

      /**
       * This returns the <code>String</code> representation of
       * the <code>Match</code>. This is useful when the contents
       * of the <code>Resolver</code> is examined using the
       * <code>getMatch</code> method.
       *
       * @return this returns the representation of the pair
       */
      public String toString(){
         return pattern+"="+match;
      }
   }
}


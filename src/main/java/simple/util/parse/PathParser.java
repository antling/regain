/*
 * PathParser.java February 2001
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
 
package simple.util.parse;

import simple.util.net.Path;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/**
 * This is used to parse a path given as part of a URI. This will  read the
 * path, normalize it, and break it up into its components. The normalization
 * of the path is the conversion of the path given into it's actual path by
 * removing the references to the parent directorys and to the current dir.
 * <p>
 * So if the path that was given was <code>/usr/bin/../etc/./README</code>
 * then the actual path, normalized, is <code>/usr/etc/README</code>. This
 * will also extract a localization from the path given. A localization is
 * extracted if there is a second extension given in the path name, for
 * example <code>file.ext.ext</code>. The second extension will be parsed so
 * that all the characters before the first underscore character, '_', will
 * define the language and all the characters after the first underscore will
 * define the country, for example <code>index.en_US.html</code> will have
 * the language en and the country would be US. Currently this only supports
 * two character codes for both language and country. These are defined from
 * the <code>java.util.Locale</code> using the <code>getISOCountries</code>
 * and <code>getISOLanguages</code> methods.
 * <p>
 * Although RFC 2396 defines the path within a URI to have parameters this
 * does not extract those parameters this will simply normalize the path and
 * include the path parameters in the path. If the path is to be converted
 * into a OS specific file system path that has the parameters extracted 
 * then the <code>URIParser</code> should be used. Also the inclusion of the
 * locale attributes in this parser is not defined in RFC 2396. This is 
 * because this is a feature of the parser to extract a locale from a file
 * if you choose to do so, if however you do not this does not effect the 
 * parsing or results of this parser.
 *
 * @author Niall Gallagher
 */
public class PathParser extends Parser implements Path{

   /**
    * Used to check for valid locale extensions in the path.
    */
   private static String[] langs = Locale.getISOLanguages();

   /**
    * Used to check for valid locale extensions in the path.
    */
   private static String[] countrys =  Locale.getISOCountries();

   /**
    * The arrays are sorted so a binary search can be done.
    */
   static {
      Arrays.sort(countrys); 
      Arrays.sort(langs);
   }

   /**
    * Used to store the individual path segments.
    */
   private TokenList list;

   /**
    * Used to store consumed country characters.
    */
   private Token country;
   
   /**
    * Used to store consumed language characters.
    */
   private Token lang;

   /**
    * Used to store consumed name characters.
    */
   private Token name;

   /**
    * Used to store consumed file extension.
    */
   private Token ext;

   /**
    * Used to store the highest directory path.
    */
   private Token dir;

   /**
    * Used to store consumed normalized path name.
    */
   private Token path;

   /**
    * The default constructor will create a <code>PathParser</code> that
    * contains no specifics. The instance will return <code>null</code>
    * for all the get methods. The <code>PathParser</code>'s get methods
    * may be populated by using the parse method.
    */
   public PathParser() {
      list = new TokenList();
      country = new Token();
      lang = new Token();
      ext = new Token();
      dir = new Token();
      path = new Token();
      name = new Token();   
   }

   /**
    * This is primarily a convineance constructor. This will parse the
    * <code>String</code> given to extract the specifics. This could be
    * achived by calling the default no-arg constructor and then using
    * the instance to invoke the <code>parse</code> method on that
    * <code>String</code> to extract the parts.
    *
    * @param path a <code>String</code> containing a path value
    */
   public PathParser(String path){
      this();
      parse(path);
   }

   /**
    * This will parse the path in such a way that it ensures that at no
    * stage there are trailing back references, using path normalization.  
    * The need to remove the back references is so that this
    * <code>PathParser</code> will create the same <code>String</code>
    * path given a set of paths that have different back references. For
    * example the paths <code>/path/../path</code> and <code>/path</code>
    * are the same path but different <code>String</code>'s.
    * <p>
    * This will NOT parse an immediate back reference as this signifies
    * a path that cannot exist. So a path such as <code>/../</code> will
    * result in a null for all methods. Paths such as <code>../bin</code>
    * will not be allowed.
    */
   protected void parse() {
      normalize();
      path();
      segments();
      name();
      extension();
      locale();
   }

   /**
    * This will initialize the parser so that it is in a ready state.
    * This allows the parser to be used to parse many paths. This will
    * clear the parse buffer objects and reset the offset to point to
    * the start of the char buffer. The count variable is reset by the
    * <code>Parser.parse</code> method.
    */
   protected void init() {
      list.count = 0;; 
      country.len = 0;
      lang.len = 0;
      ext.len = 0;
      dir.len = 0;    
      name.len = 0;
      path.len = 0;
      off = 0;
   }

   /**
    * This will return the language that this path has taken 
    * from the locale of the path. For example a file name of
    * <code>file.en_US.extension</code> produces a language
    * of <code>en</code>. This will return null if there was 
    * no language information within the path.     
    *
    * @return returns the locale language this path contains
    */
   public String getLanguage() {
      return lang.toString();
   }

   /**
    * This will return the country that this path has taken 
    * from the locale of the path. For example a file name of
    * <code>file.en_US.extension</code> produces a country
    * of <code>US</code>. This will return null if there was 
    * no country information within the path.     
    *
    * @return returns the locale country this path contains
    */
   public String getCountry(){   
      return country.toString();
   }

   /**
    * This will return the extension that the file name contains.
    * For example a file name <code>file.en_US.extension</code>
    * will produce an extension of <code>extension</code>. This 
    * will return null if the path contains no file extension.
    *
    * @return this will return the extension this path contains
    */
   public String getExtension() {
      return ext.toString();
   }

   /**
    * This will return the full name of the file without the path.
    * As regargs the definition of the path in RFC 2396 the name
    * would be considered the last path segment. So if the path 
    * was <code>/usr/README</code> the name is <code>README</code>.
    * Also for directorys the name of the directory in the last
    * path segment is returned. This returns the name without any
    * of the path parameters. As RFC 2396 defines the path to have
    * path parameters after the path segments.
    *
    * @return this will return the name of the file in the path
    */ 
   public String getName(){
      return name.toString();
   }

   /**
    * This will return the normalized path. The normalized path is
    * the path without any references to its parent or itself. So
    * if the path to be parsed is <code>/usr/../etc/./</code> the
    * path is <code>/etc/</code>. If the path that this represents
    * is a path with an immediate back reference then this will
    * return null. This is the path with all its information even
    * the parameter information if it was defined in the path.
    *
    * @return this returns the normalize path without
    *    <code>../</code> or <code>./</code>
    */
   public String getPath() {
      return path.toString();
   }

   /**
    * This will return the highest directory that exists within 
    * the path. This is used to that files within the same path
    * can be acquired. An example of that this would do given
    * the path <code>/pub/./bin/README</code> would be to return
    * the highest directory path <code>/pub/bin/</code>. The "/"
    * character will allways be the last character in the path.
    *
    * @return this method will return the highest directory
    */
   public String getDirectory(){
      return dir.toString();
   }

   /**
    * This method is used to break the path into individual parts
    * called segments, see RFC 2396. This can be used as an easy
    * way to compare paths and to examine the directory tree that
    * the path points to. For example, if an path was broken from
    * the string <code>/usr/bin/../etc</code> then the segments
    * returned would be <code>usr</code> and <code>etc</code> as
    * the path is normalized before the segments are extracted.
    *
    * @return return all the path segments within the directory
    */
   public String[] getSegments(){
      return list.list();
   }

   /**
    * This will return the path as it is relative to the issued
    * path. This in effect will chop the start of this path if
    * it's start matches the highest directory of the given path
    * as of <code>getDirectory</code>. This is useful if paths 
    * that are relative to a specific location are required. To
    * illustrate what this method will do the following example
    * is provided. If this object represented the path string
    * <code>/usr/share/rfc/rfc2396.txt</code> and the issued
    * path was <code>/usr/share/text.txt</code> then this will
    * return the path string <code>/rfc/rfc2396.txt</code>.
    *
    * @param path the path prefix to acquire a relative path
    *
    * @return returns a path relative to the one it is given
    * otherwize this method will return null 
    */
   public String getRelative(String path){
      return getRelative(new PathParser(path));
   }

   /**
    * This is used by the <code>getRelative(String)</code> to
    * normalize the path string and determine if it contains a
    * highest directory which is shared with the path that is
    * represented by this object. If the path has leading back
    * references, such as <code>../</code>, then the result of
    * this is null. The returned path begins with a '/'.
    *
    * @param path the path prefix to acquire a relative path
    *
    * @return returns a path relative to the one it is given
    * otherwize this method will return null 
    */
   private String getRelative(PathParser path){
      char[] text = path.buf;
      int off = path.dir.off;
      int len = path.dir.len;

      return getRelative(text, off, len);
   }

   /**
    * This will return the path as it is relative to the issued
    * path. This in effect will chop the start of this path if
    * it's start matches the highest directory of the given path
    * as of <code>getDirectory</code>. This is useful if paths 
    * that are relative to a specific location are required. To
    * illustrate what this method will do the following example
    * is provided. If this object represented the path string
    * <code>/usr/share/rfc/rfc2396.txt</code> and the issued
    * path was <code>/usr/share/text.txt</code> then this will
    * return the path string <code>/rfc/rfc2396.txt</code>.
    *
    * @param text the path prefix to acquire a relative path   
    * @param off this is the offset within the text to read
    * @param len this is the number of characters in the path
    *
    * @return returns a path relative to the one it is given
    * otherwize this method will return null 
    */
   private String getRelative(char[] text, int off, int len){
      int size = path.len - len + 1; /* '/' */
      int pos = path.off + len - 1;

      for(int i = 0; i < len; i++){
         if(text[off++] != buf[path.off+i]){
            return null;
         }
      }
      if(pos < 0) { /* ../ */
         return null;
      } 
      return new String(buf,pos,size);
   }

   /**
    * This will extract the path of the given <code>String</code>
    * after it has been normalized. If the path can not be normalized
    * then the count is set to -1 and the path cannot be extracted.
    * When this happens then the path parameter is <code>null</code>.
    */
   private void path() {
      if(count > 0){
         path.len = count;      
         path.off = 0;
      }
   }

   /**
    * This will simply read the characters from the end of the
    * buffer until it encounters the first peroid character. When
    * this is read it will store the file extension and remove the
    * characters from the buffer.
    */
   private void extension() {
      int pos = off + count; /* index.html[]*/
      int len = 0;

      while(pos-1 >= off) { /* index.htm[l]*/
         if(buf[--pos]=='.'){ /* index[.]html*/
            ext.off = pos+1;
            ext.len = len;
            count = pos;
            break;
         }
         len++;
      }
   }

   /**
    * This will remove the locale from the file. This assumes
    * that the file extension has been removed from the file before
    * this is invoked. This will remove all characters upto the
    * first peroid character if these characters are of the form
    * ll_CC where ll is a valid language token, CC is a valid
    * country token. If a valid locale cannot be extracted this
    * does nothing.
    */
   private void locale(){
      int pos = count;

      while(pos-1 >= off) {
         if(buf[--pos]=='.'){  /* index[.]en_US */
            if(isLanguage(++pos)) /* index.[e]n_US */
               switch(count-pos){  /* en_US or en */
               case 5:  /* en_IE */
                  if(buf[pos+2] =='_' &&
                     isCountry(pos+3)){
                     country();
                     count--;
                  }else {
                     break; /* ? */
                  }
               case 2: /* en */
                  language();
                  count--; /* unread '.'*/
               }
            return;
         }
      }
   }

   /**
    * This will remove the characters following the first
    * underscore encountered after the peroid. So if the locale
    * was <code>en_US</code> the the country would be
    * <code>US</code>. This is called from locale and as such
    * should only be called when ther is a valid country code
    * within the buffer as determined from the method
    * <code>isCountry</code>
    */
   private void country() {
      if(off < count-2) { /* robust check*/
         country.off = count-2;
         country.len = 2;
         count -= 2; /* unread US*/
      }
   }

   /**
    * This will extract the language from the file name. It is
    * assumed that the country has already been removed from the
    * locale before this is invoked and so this will store the
    * characters that come before the first peroid character.
    * This is called from locale and as such should only be called
    * when ther is a valid language code within the buffer as
    * determined from the method <code>isLanguage</code>
    */
   private void language() {
      if(off < count-2) { /* robust check*/
         lang.off = count-2;
         lang.len = 2;
         count -= 2; /* unread en*/
      }
   }  

   /**
    * This wil extract each individual segment from the path and
    * also extract the highest directory. The path segments are
    * basically the strings delimited by the '/' character of a
    * normalized path. As well as extracting the path segments
    * this will also extract the directory of path, that is, the
    * the path up to the last occurance of the '/' character. 
    */
   private void segments() {
      int pos = count - 1;
      int len = 1;

      if(count > 0){
         if(buf[pos] == '/'){ /* /pub/bin[/] */
            dir.len = pos+1;
            dir.off = 0;
            pos--; /* /pub/bi[n]/ */
         }
         while(pos >= off){
            if(buf[pos] == '/'){ /* /pub[/]bin/*/
               if(dir.len == 0){
                  dir.len = pos+1; /* [/] is 0*/
                  dir.off = 0;
               }
               list.add(pos+1,len-1); 
               len = 0;
            }
            len++;
            pos--;
         }
      }
   }

   /**
    * The normalization of the path is the conversion of the path
    * given into it's actual path by removing the references to
    * the parent directorys and to the current dir. So if the path
    * given was <code>/usr/bin/../etc/./README</code> then the actual
    * path, the normalized path, is <code>/usr/etc/README</code>.
    * <p>
    * This method ensures the if there are an illegal number of back
    * references that the path will be evaluated as empty. This can
    * evaluate any path configuration, this includes any references
    * like <code>../</code> or <code>/..</code> within the path.
    * This will also remove empty segments like <code>//</code>.
    */
   private void normalize(){
      int size = count + off;
      int mark = off;
      int pos = off;
  
      for(off = count = 0; pos < size; pos++) {
         buf[count++] = buf[pos];

         if(buf[pos] == '/') {
            if(count -1 > 0){               
               if(buf[count -2] == '/') /* [/]/./path/ */
                  count--; /* /[/]./path/ */
            }
         } else if(buf[pos] == '.') { /* //[.]/path/ */
            if(count -1 > 0) { /* /[/]./path/ */
               if(buf[count - 2] !='/') /* /[/]./path./ */
                  continue; /* /path.[/] */
            }     
            if(pos + 2 > size){ /* /path/[.] */
               count--; 
            } else {
               if(buf[pos + 1] =='/'){ /* /.[/]path */ 
                  pos++;/* /[/]. */
                  count--; /* /.[/]path */ 
               }
               if(buf[pos] !='.'){ /* /.[/]path */
                  continue;            
               } 
               if(pos + 2< size){
                  if(buf[pos + 2]!='/') /* /..[p]ath */
                     continue; /* /[.].path */
               }  
               if(count - 2 > 0) {
                  for(count -= 2; count - 1 > 0;){ /* /path[/]..*/
                     if(buf[count - 1]=='/') { /* [/]path/..*/
                        break;           
                     }
                     count--; 
                  }
               }else { /* /../ */
                  count = 0;
                  off = 0;
                  break;
               }
               pos += 2; /* /path/.[.]/ */
            }
         }
      }
   }
   
   /**
    * This will extract the full name of the file without the path.
    * As regards the definition of the path in RFC 2396 the name
    * would be considered the last path segment. So if the path 
    * was <code>/usr/README</code> the name is <code>README</code>.
    * Also for directorys the name of the directory in the last
    * path segment is returned. This returns the name without any
    * of the path parameters. As RFC 2396 defines the path to have
    * path parameters after the path segments. So the path for the
    * directory "/usr/bin;param=value/;param=value" would result 
    * in the name "bin". If the path given was "/" then there will
    * be nothing in the buffer because <code>extract</code> will
    * have removed it.
    */    
   private void name(){
      int pos = count;
      int len = 0;
      
      while(pos-- > off) { /* /usr/bin/;para[m] */
         if(buf[pos]==';'){ /* /usr/bin/[;]param */
            if(buf[pos-1]=='/'){ /* /usr/bin[/];param */
               pos--;   /* /usr/bin[/];param */
            }
            len = 0;  /* /usr/bin[/]*/
         }else if(buf[pos]=='/'){ /* /usr[/]bin*/
            off = pos + 1; /* /usr/[b]in*/
            count = len; /* [b]in */
            break;
         }else{
            len++;
         }
      }
      name.len = count;
      name.off = off;
   }

   /**
    * This is used to check if the next two characters in the
    * buffer constitute a language. This assumes that there are
    * two characters left in the buffer. This should not be
    * called if pos +1 is equal to or greater that count. This
    * will check all the language tokens specified by the
    * <code>Locale.getISOLanguages</code>.
    *
    * @param pos this is the read offset to check for the
    * language token
    *
    * @return this will return true if the to characters make
    * a language token
    */
   private boolean isLanguage(int pos){
      if(pos+1 >= count) return false;
      
      return Arrays.binarySearch(langs, 
         new String(buf, pos, 2)) > 0;
   }

   /**
    * This is used to check if the next two characters in the
    * buffer constitute a country. This assumes that there are
    * two characters left in the buffer. This should not be
    * called if pos +1 is equal to or greater that count. This
    * will check all the language tokens specified by the
    * <code>Locale.getISOLanguages</code>.
    *
    * @param pos this is the read offset to check for the
    * country token
    *
    * @return this will return true if the to characters make
    * a country token
    */
   private boolean isCountry(int pos){  
      if(pos+1 >= count) return false;
      
      return Arrays.binarySearch(countrys, 
         new String(buf, pos, 2)) > 0;
   }

   /**
    * This will return the normalized path. The normalized path is
    * the path without any references to its parent or itself. So
    * if the path to be parsed is <code>/usr/../etc/./</code> the
    * path is <code>/etc/</code>. If the path that this represents
    * is a path with an immediate back reference then this will
    * return null. This is the path with all its information even
    * the parameter information if it was defined in the path.
    *
    * @return this returns the normalize path without
    *    <code>../</code> or <code>./</code>
    */
   public String toString(){
      return getPath();
   }

   /**
    * This is used so that the <code>PathParser</code> can speed
    * up the parsing of the data. Rather than using a buffer like
    * a <code>ParseBuffer</code> or worse a <code>StringBuffer</code>
    * this just keeps an index into the character array from the
    * start and end of the token. Also this enables a cache to be
    * kept so that a <code>String</code> does not need to be made
    * again after the first time it is created.
    */ 
   private class Token implements Serializable {

      /**
       * Provides a quick retrival of the token value. 
       */
      public String value;

      /**
       * Offset within the buffer that the token starts.
       */
      public int off;

      /**
       * Length of the region that the token consumes.
       */
      public int len;

      /**
       * This method will convert the <code>Token</code> into it's
       * <code>String</code> equivelant. This will firstly check
       * to see if there is a value, for the string representation,
       * if there is the value is returned, otherwise the region
       * is converted into a <code>String</code> and returned.
       *
       * @return this returns a value representing the token
       */
      public String toString() {
         if(value != null) {
            return value;
         }
         if(len > 0) {
            value = new String(buf,off,len);
         }
         return value;
      }
   }

   /**
    * The <code>TokenList</code> class is used to store a list of
    * tokens. This provides an <code>add</code> method which can
    * be used to store an offset and length of a token within 
    * the buffer. Once the tokens have been added to they can be
    * examined, in the order they were added, using the provided
    * <code>list</code> method. This has a scalable capacity.
    */    
   private class TokenList implements Serializable {
   
      /** 
       * Contains the offsets and lengths of the tokens.
       */
      private int[] list;

      /**
       * Determines the write offset into the array.
       */
      private int count;

      /**
       * Constructor for the <code>TokenList</code> is used to
       * create a scalable list to store tokens. The initial
       * list is created with an array of sixteen ints, which 
       * is enough to store eight tokens. 
       */
      private TokenList(){
         list = new int[16];
      }
      
      /**
       * This is used to add a new token to the list. Tokens
       * will be available from the <code>list</code> method in
       * the order it was added, so the first to be added will
       * at index zero and the last with be in the last index.
       *
       * @param off this is the read offset within the buffer
       * @param len the number of characters within the token
       */
      public void add(int off, int len){
         if(count+1 > list.length) {
            resize(count *2);
         }
         list[count++] = off;
         list[count++] = len;
      }

      /**
       * This is used to retrieve the list of tokens inserted
       * to this list using the <code>add</code> method. The
       * indexes of the tokens represents the order that the
       * tokens were added to the list.
       *
       * @return returns an ordered list of token strings 
       */
      public String[] list(){
         String[] value= new String[count/2];
         
         for(int i =0; i< count; i+=2){
            value[i/2] = new String(buf,
               list[i],list[i+1]);
         } 
         return value;
      }

      /**
       * This is used to clear all tokens previously stored
       * in the list. This is required so that initialization
       * of the parser with the <code>init</code> method can 
       * ensure that there are no tokens from previous data.
       */
      public void clear(){
         count =0;
      }

      /**
       * Scales the internal array used should the number of
       * tokens exceed the initial capacity. This will just
       * copy across the ints used to represent the token. 
       *
       * @param size length the capacity is to increase to 
       */     
      private void resize(int size){
         int[] copy = new int[size];
         System.arraycopy(list,0,copy,0,count);
         list = copy; 
      }
   }
}

/*
 * ClassParser.java May 2003
 *
 * Copyright (C) 2003, Niall Gallagher <niallg@users.sf.net>
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

import simple.util.parse.ParseBuffer;
import simple.util.parse.URIParser;
import simple.util.parse.PathParser;
import simple.util.parse.Parser;
import simple.util.net.Path;
import simple.util.net.URI;

/**
 * The <code>ClassParser</code> parses a specific URI format for
 * for the <code>DefaultMapper</code>. This parser parses a path 
 * using a specific path structure. The format uses an optional
 * class file name, followed by an optional path or query part.
 * <code><pre>
 *
 *    http://hostname/demo/DemoService.class/index.html
 *    http://hostname/demo/DemoService.class
 *    http://hostname/pub/bin/index.html
 *
 * </pre></code>
 * In the URI examples given the first two will produce a class
 * name of <code>demo.DemoService</code>, however the class name
 * is optional and does not nessecarily need to be included.
 *
 * @author Niall Gallagher
 */
final class ClassParser extends Parser {

   /**
    * This is the number of characters in the class name.
    */ 
   private ParseBuffer name;

   /**
    * Used to determine the number of characters examined.
    */
   private ParseBuffer path;

   /**
    * Constructor for the <code>ClassParser</code>. This is used
    * so that a <code>ClassParser</code> can be created without
    * any initial input. Parsing can then be done using the 
    * <code>parse(String)</code> method with some information.
    */
   public ClassParser() {
      path = new ParseBuffer();
      name = new ParseBuffer();
   }
   
   /**
    * Constructor for the <code>ClassParser</code>. This is used
    * to create and instance of the <code>ClassParser</code> to
    * parse the given path information. This ensures that the
    * <code>getName</code> method produces the Java class name.
    *
    * @param target this is the path information to be parsed
    */
   public ClassParser(String target) {
      this();
      parse(target);
   }

   /**
    * This will parse a full HTTP URI for the class name within
    * the path part. This ensures any URI given to the parser
    * can be handled. The class name is extracted from the path
    * and is available through <code>getName</code> method.
    *
    * @param target the HTTP URI string that is to be parsed
    */
   public void parse(String target) {
      parse(new URIParser(target));
   }

   /**
    * This will parse a <code>URI</code> object for the class 
    * name within the path part. The <code>URI</code> is used
    * to extract and normalize the path part of the URI string.
    *
    * @param target the <code>URI</code> that is to be parsed
    */
   private void parse(URI target) {
      parse(target.getPath());
   }

   /**
    * This uses the <code>Path</code> object to extract the path
    * in its normalized form. This will ensure that it contains
    * a strict format and can be parsed easily. This parser will
    * not accomodate path parameters or query parameters.
    *
    * @param path contains the normalized path to be parsed
    */
   private void parse(Path path) {
      super.parse(path.getPath());
   }

   /** 
    * This performs an initialisation for the parser so that it
    * can begin parsing the path. This is used on any subsequent
    * parsing using this instance. This just resets a counter.
    */
   protected void init() {
      name.clear();
      path.clear();
      off = 0;
   }

   /**
    * This performs a quick scan of the path issued and ensures
    * that the class path given is converted into a class name.
    * This replaces all '/' characters with '.' characters and
    * ensures that the initial forward slash, '/', is removed.
    * If no class name is found, as determined by the ".class" 
    * ending of the path, then this ensures the token is empty.
    */
   protected void parse(){
      name();
      path();
   }

   /**
    * This will attempt to extract a class name from the issued
    * URI path. The class name is determined by the ".class"
    * ending. This will also ensure that all forward slash,
    * '/', characters are replaced by peroid, '.', characters.
    * If no class name is resolved the name token is empty.
    */
   private void name(){
      if(skip("/")){
         while(off < count){
            if(buf[off] == '.'){
               if(skip(".class"))
                  return;
            }
            if(buf[off] != '/'){
               name.append(buf[off]);
            }else {
               name.append('.');
            }
            off++;
         }
         name.clear();
         off = 0;
      }
   }

   /**
    * This will resolve a normalized path from the issued URI.
    * If there is no path information following the URI path the
    * root path, '/', is returned. If a class name is resolved
    * then all path information following the name is returned.
    */
   private void path(){
      int size = count - off;

      if(size > 0){
         path.append(buf,off,size);
      }else{
         path.append('/');
      }
   }    

   /**
    * This returns the Java class name for the path reference. If
    * the path reference did not contain a reference to a class
    * then this will return null to indicate that, otherwise the
    * fully qualified class name of the reference is returned.
    *
    * @return returns the name of the class object referenced
    */
   public String getName() {
      return name.toString();
   }
  
   /**
    * This returns a normalized path resolved from the issued URI.
    * This will return all the path information after the ending
    * of the class name. The embedded class name ends with the
    * ".class" token, however if there is no class name within
    * the URI then the this will return the entire URI path.
    *
    * @return returns the resolved path of the URI path given
    */
   public String getPath(){
      return path.toString();   
   }
} 

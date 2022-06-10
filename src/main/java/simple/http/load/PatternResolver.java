/*
 * PatternResolver.java February 2004
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

import simple.http.serve.Locator;
import simple.util.parse.ParseBuffer;
import simple.util.parse.Parser;
import simple.util.Resolver;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.File;

/**
 * The <code>PatternResolver</code> is used to match a URI path to
 * a service class name using a pattern. The mappings used are
 * acquired from the <code>mapper.properties</code> file, which is
 * located using an instance of the <code>Locator</code> interface.
 * <p> 
 * This is used to implement a scheme similar to the Java Servlet
 * wild card path mapping scheme. In this scheme a wild card text
 * string such as <code>*.html</code> are used to resolve targets.
 *
 * @author Niall Gallagher
 *
 * @see simple.util.Resolver
 */
final class PatternResolver extends Parser {

   /**
    * Used to store the contents of the Java properties file.
    */
   private ParseBuffer text;

   /**
    * Used to consume the characters for the name token.
    */
   private ParseBuffer name;

   /**
    * Used to consume the characters for the value token.
    */
   private ParseBuffer value;

   /**
    * This performs all the resolution for the URI paths.
    */
   private Resolver list;

   /**
    * Constructor for the <code>PatternResolver</code>. This uses
    * a Java properties file located with the <code>Locator</code>
    * object supplied. Once the properties file is located it is 
    * parsed and the patterns extracted are used to resolve the 
    * URI paths to service class names.
    *
    * @param lookup the locator used to discover the properties
    */
   public PatternResolver(Locator lookup){
      this.name = new ParseBuffer();
      this.value = new ParseBuffer();
      this.text = new ParseBuffer();
      this.list = new Resolver();
      this.init(lookup);
   }

   /** 
    * This will attempt to acquire a Java properties file that is
    * used to resolve relative URI paths to class names. The Java
    * properties file is located using the <code>Locator</code>
    * supplied. This will search for the properties using the file
    * names "Mapper.properties" and "mapper.properties".
    *
    * @param lookup the locator used to discover the properties
    */
   private void init(Locator lookup) {
      try {
         load(lookup);
      }catch(IOException e){
         return;
      }
   }

   /** 
    * This <code>load</code> method attempts to load the properties
    * file <code>Mapper.properties</code> using the given locator.
    * If the properties file exists then it is used to resolve 
    * the class name and prefix path to various relative URI paths.
    * <p>
    * This will attempt to load the file using the UTF-8 charset
    * so that international characters can be used for patterns 
    * that can be used. This is compatible with the traditional
    * Java properties format which used the ISO-8859-1 charset.
    *
    * @param lookup this is the locator used to discover the file
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void load(Locator lookup) throws IOException {
      try {
         load(lookup,"Mapper.properties");
      }catch(IOException e) {
         load(lookup,"mapper.properties");
      }
   }

   /**
    * This will load the named file from within the given path. This
    * is used so that a properties file can be loaded from a locator
    * using the specified file name. If the Java properties file 
    * cannot be loaded this will throw an <code>IOException</code>. 
    *
    * @param lookup this is the locator used to discover the file
    * @param name this is the name of the properties file loaded
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void load(Locator lookup, String name) throws IOException{
      parse(lookup.getFile(name), "utf-8");
   }
   
   /**
    * This will parse the named file from the current path. This
    * is used so that a properties file can be parsed from the 
    * specified file using the issued charset. If the properties 
    * file cannot be parsed an <code>IOException</code> occurs. 
    *
    * @param file this is file where the properties are located
    * @param charset this enables the charset to be specified
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void parse(File file, String charset) throws IOException{
      parse(new FileInputStream(file), charset);
   }

   /**
    * This will parse the named file from the given stream. This
    * is used so that a properties file can be parsed from the 
    * stream using the charset specified. Typiclly the charset
    * will be UTF-8 as it is compatable with ISO-8859-1.
    *
    * @param file this is the file that contains the properties
    * @param charset this enables the charset to be specified
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void parse(InputStream file, String charset) throws IOException{
      parse(new InputStreamReader(file, charset));
   }

   /**
    * This will parse the named file from the given reader. This
    * is used so that a properties file can be parsed from the 
    * reader and buffered so that it can be parsed as a single
    * sequence of characters.
    *
    * @param file this is the file that contains the properties
    *
    * @exception IOException thrown if there is an I/O problem
    */
   private void parse(Reader file) throws IOException{
      char[] buf = new char[64];

      while(true){
         int num = file.read(buf);
         if(num < 0){
            break;
         }
         text.append(buf,0,num);
      }
      parse(text);
   }
   
   /**
    * This is a simple convinience method that delegates to
    * the <code>parse(String)</code> method. This is used 
    * because the file is read in chunks from the properties
    * file using the UTF-8 charset and the length is unknown.
    *
    * @param text contains the Java properties file read
    */
   private void parse(ParseBuffer text){
      parse(text.toString());
   }

   /**
    * This performs the resolution using the loaded properties
    * file. This uses the <code>simple.util.Resolver</code> to
    * determine whether a wild card pattern matches a specified
    * URI path. If a match is found the class name is returned.
    * <p>
    * For example, if a pattern such as <code>*.vm</code> was
    * loaded with the value <code>TemplateService</code> then
    * URI paths such as <code>/path/file.vm</code> would be
    * resolved to that service name. For details of how the
    * resolving is performed see the <code>Resolver</code>.
    * 
    * @param path this is the URI path that is to be resolved
    *
    * @return the class name that is resolved from the path
    */
   public String getClass(String path) {
      return list.resolve(path);
   }

   /**
    * This method does nothing, it is simply implemented so that
    * the implementation is not abstract. If the is to be reused
    * this is implemented to clear the both name and value 
    * <code>ParseBuffer</code> objects.
    */
   protected void init(){
      name.clear();
      value.clear();
   }

   /**
    * This will parse the Java properties file line by line. This
    * ensures that all comments are ignored. A comment is any 
    * line that begins with a '!' or '#' character. These comment
    * characters may appear in a name value as long as it is not
    * the first character. Values are seperated from names with
    * an '=' or ':' character.
    */
   protected void parse(){
      while(off < count){
         char ch = buf[off];
         
         if(comment(ch)){
            line();
         }else {
            entry();
         }
      }
   }

   /**
    * This will simply skip a line from the Java properties file
    * checking for a carrage return or line feed character. The
    * use of either to terminate a line is supported, this will
    * enable *NIX and Windows platforms to work equally well.
    */
   private void line(){
      while(off < count){
         char ch = buf[off++];

         if(terminal(ch)){
            break;
         }
      } 
   }

   /** 
    * This is an expression that is used by a Java properties file
    * to specify a property name and value. This will enable the
    * name and value to be extracted as a one line expression that
    * is seperated by an equal character, which is '=' or ':'.
    * <p>
    * This will only consider parameters that have values greater
    * than zero, this means that null values will never be used
    * to define a patten to be inserted in to the resolver.
    */
   private void entry(){
      name();
      if(skip("=")){
         value();
      }else if(skip(":")){
         value();
      }
      insert();
   }

   /**
    * This method adds the name and value in to the resolver so 
    * that the next name and value can be collected. The name and
    * value are added to the map as string objects. Once inserted
    * in to the resolver <code>ParseBuffer.clear</code> is used
    * so the they can be reused to collect further values. 
    */
   private void insert(){
      if(value.length() > 0){
         insert(name,value);
      }
      name.clear();
      value.clear();
   }

   /**
    * This will insert the given name and value to the resolver.
    * The value is stored in the resolver as a string that is
    * trimmed to have no trailing or leading whitespace data as
    * is specified by the Java properties file description.
    *
    * @param name this is the name of the value to be inserted
    * @param value this is the value of a that is to be inserted
    */
   private void insert(ParseBuffer name, ParseBuffer value){
      insert(name.toString(), value.toString());
   }

   /**
    * This will insert the given name and value to the resolver.
    * The value is stored in the resolver as a string that is
    * trimmed to have no trailing or leading whitespace data as
    * is specified by the Java properties file description.
    *
    * @param name this is the name of the value to be inserted
    * @param value this is the value of a that is to be inserted
    */
   private void insert(String name, String value) {
      list.insert(name.trim(), value.trim());
   }

   /**
    * This extracts the name from the Java properties file. This
    * will basically ready any text up to the first occurance of
    * an equal character or a terminal. If an equal character is
    * extracted is is put back so that it is the next one read.
    */
   private void name(){
      while(off < count){
         char ch = buf[off++];
         
         if(terminal(ch)){
            break;
         }else if(equal(ch)){
            off--;
            break;
         }
         name.append(ch);
      }
   }

   /**
    * This extracts the value from the Java properties file. This
    * will basically ready any text up to the first occurance of
    * an equal of a terminal. If a terminal character is read
    * this returns without adding the terminal to the value.
    */
   private void value(){
      while(off < count){
         char ch = buf[off++];

         if(terminal(ch)){
            break;
         }
         value.append(ch);
      }
   }

   /**
    * This is used to determine the line terminal for the Java
    * properties file. Terminal are either the carrage return or
    * line feed characters, '\r' and '\n' respectively.
    *
    * @param ch this is the character that is to be examined
    *
    * @return true if the character is Java properties terminal    
    */
   private boolean terminal(char ch){
      return ch =='\r' || ch == '\n';  
   }

   /**
    * This is used to determine the line comments for the Java
    * properties file. Comments are lines that begin with either
    * the hash or exclamation characters, which are '#' or '!'.
    *
    * @param ch this is the character that is to be examined
    *
    * @return true if the character is Java properties comment
    */
   private boolean comment(char ch){
      return ch =='#' || ch == '!';
   }

   /**
    * This is used to determine the line comments for the Java
    * properties file. Comments are lines that begin with either
    * the equal or colon characters, which are '=' or ':'.
    *
    * @param ch this is the character that is to be examined
    *
    * @return true if the character is Java properties equal
    */
   private boolean equal(char ch){
      return ch =='=' || ch == ':';
   }
}

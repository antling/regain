/*
 * PageParser.java May 2003
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
 
package simple.page;

import simple.util.parse.ParseBuffer;
import simple.util.parse.Parser;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.URLEncoder;
import java.io.IOException;

/**
 * The <code>PageParser</code> is used to parse an SSI expression to
 * create a <code>Reference</code> object. The SSI expression defines
 * a set of parameters and a path parameter using HTML tags. This 
 * is used by the <code>ContentHandler</code> object to pass page
 * parameters to referenced <code>Content</code> objects. The HTML
 * expression that is parsed by this is shown below.
 * <code><pre>
 *
 *    text  = start *(param SP) close
 *    start = "&lt;" "include" SP ("source" | "src") "=" token "&gt;"
 *    param = "&lt;" "param" SP name SP value "&gt;"
 *    name  = "name" "=" token
 *    value = "value" "=" token 
 *    close = "&lt;/include&gt;"
 *    token = text | literal
 *    SP    = *("\r" | "\n" | "\t" | " ") 
 *
 * </pre></code>
 * The above expression can be parsed by this object, and the name 
 * value parameters are exposed via the <code>getParameter</code>
 * method. This parses in a case insensitive manner and it can
 * accomodate intermittent whitespace. If the expression is not
 * will formed parsing stops and the various methods be empty.
 * <p>
 * This cannot accomodate HTML comments, or any syntax that does
 * no comply strictly to the above BNF expression. If there is any
 * information that does not belong in the statement parsing stops
 * and remain parameters will not be examined.
 *
 * @author Niall Gallagher
 */  
final class PageParser extends Parser implements Reference {

   /**
    * This contains the source information within the HTML tag.
    */
   private ParseBuffer source;
  
   /**
    * Used to accumulate the characters for the parameter name.
    */    
   private ParseBuffer name;
   
   /**
    * Used to accumulate the characters for the parameter value.
    */   
   private ParseBuffer value;

   /**
    * Contains the name and value pairs for each parameter.
    */
   private Hashtable map;

   /**
    * Constructor for the <code>PageParser</code>. This creates 
    * an object that can be use to parse an SSI include statement.
    * The SSI statement parsed contains parameters and a source
    * which are exposed using the <code>Reference</code> methods.    
    */
   public PageParser() {
      this.source = new ParseBuffer();
      this.name = new ParseBuffer();
      this.value = new ParseBuffer();
      this.map = new Hashtable(); 
   }

   /**
    * Constructor for the <code>PageParser</code>. This creates 
    * an object that can be use to parse an SSI include statement.
    * The SSI statement parsed contains parameters and a source
    * which are exposed using the <code>Reference</code> methods.    
    *
    * @param text contains the HTML text for the SSI statement
    */
   public PageParser(String text) {
      this();
      parse(text);
   }
   
   /**
    * Constructor for the <code>PageParser</code>. This creates 
    * an object that can be use to parse an SSI include statement.
    * The SSI statement parsed contains parameters and a source
    * which are exposed using the <code>Reference</code> methods.    
    *
    * @param text contains the HTML text for the SSI statement
    * @param size this is the number of valid characters
    */
   public PageParser(char[] text, int size) {
      this(new String(text, 0, size));
   }
   
   /** 
    * This is used to remove all whitespace characters from the
    * <code>String</code> excluding the whitespace within literals
    * and between tokens. In effect this will convert all leading
    * whitespace to a single space, except whitespace within a
    * quoted string, which remains unaffected.
    * <p>
    * This converts a liberal HTML expression what contains space
    * characters to a well formed expression that can be parsed 
    * simply by extracting known tokens in sequence, from HTML.
    */
   private void pack() {
      int len = count;
      int seek = 0;
      int pos = 0;
      char old = buf[0];

      while(seek < len){
         char ch = buf[seek++];
         
         if(quote(ch) && old != '\\'){  
            buf[pos++] = ch;
            
            while(seek < len){
               old = buf[seek - 1];
               ch = buf[seek++];  
               buf[pos++] = ch;             
               
               if(quote(ch) && old!='\\'){  
                  break;
               }
            }
         }else if(space(ch)){ 
            if(!special(old)){
               buf[pos++] = ' '; 
               old = ' ';
            }  
         }else { 
            old = buf[seek - 1]; 
            buf[pos++] = old;   
         }
      }
      count = pos;
   }

   /**
    * This enumerates the names of every parameter. This enables
    * the parameter values to be extracted by providing the name
    * to the <code>getParameters</code> method. The resulting
    * <code>Enumeration</code> contains string objects.
    *
    * @return this returns an <code>Enumeration</code> of names
    */
   public Enumeration getParameterNames(){
      return map.keys();
   }

   /**
    * This extracts a value for the given name. The name issued
    * to this method must be from the <code>Enumeration</code>
    * issued. If there is no parameter of this name this will
    * return a null value. If there are multiple values this
    * will return the first value.
    *
    * @param name the name of the parameter value to retrieve
    *
    * @return this returns the first value for the given name
    */
   public String getParameter(String name){      
      String[] list = getParameters(name);
      return list == null ? null : list[0];
   }

   /**
    * This extracts an integer parameter for the named value.
    * If the named parameter does not exist this will return
    * a zero value. If however the parameter exists but is 
    * not in the format of a decimal integer value then this
    * will throw a <code>NumberFormatException</code>.
    *
    * @param name the name of the parameter value to retrieve
    *
    * @return this returns the parameter value as an integer
    *
    * @throws NumberFormatException if the value is not valid     
    */
   public int getInteger(String name) {
      String[] list = getParameters(name);

      if(list != null) {
         return Integer.parseInt(list[0]);      
      }
      return 0;
   }

   /**
    * This extracts a float parameter for the named value.
    * If the named parameter does not exist this will return
    * a zero value. If however the parameter exists but is 
    * not in the format of a floating point number then this
    * will throw a <code>NumberFormatException</code>.
    *
    * @param name the name of the parameter value to retrieve
    *
    * @return this returns the parameter value as a float
    *
    * @throws NumberFormatException if the value is not valid     
    */
   public float getFloat(String name) {
      String[] list = getParameters(name);

      if(list != null) {
         return Float.parseFloat(list[0]);      
      }
      return 0.0f;
   }

   /**
    * This extracts a boolean parameter for the named value.
    * If the named parameter does not exist this will return
    * false otherwize the value is evaluated. If it is either
    * <code>true</code> or <code>false</code> then those
    * boolean values are returned, otherwize it is false.
    *
    * @param name the name of the parameter value to retrieve
    *
    * @return this returns the parameter value as an float
    */
   public boolean getBoolean(String name) {
      String[] list = getParameters(name);
      Boolean value = Boolean.FALSE;

      if(list != null) {         
         value = Boolean.valueOf(list[0]);
      }
      return value.booleanValue();
   }

   /**
    * This extracts all the values for a given name. The name
    * used must be from the <code>Enumeration</code> issued.
    * If there is not parameter of this name this returns null.
    *
    * @param name the name of the parameter value to retrieve
    *
    * @return returns an array of values for the given name
    */
   public String[] getParameters(String name) {
      return (String[])map.get(name);
   }
   
   /**
    * This returns the content path that is referenced within 
    * the &lt;include&gt; statement. The content path is named
    * as the "source" or "src" parameter within the initial tag
    * for the include statement.
    *
    * @return this returns the source path that is referenced
    */
   public String getContentPath() {
      return source.toString();
   }

   /**
    * This method adds the name and value to a map so that the next
    * name and value can be collected. The name and value are added
    * to the map as string objects. Once added to the map the
    * <code>ParseBuffer.clear</code> method is invoked so the they
    * can be reused to collect further values. This will add the 
    * values to the map as an array of type string. This is done so
    * that if there are multiple values that they can be stored. 
    */
   private void insert(){
      if(name.length() > 0){
         insert(name,value);
      }
      name.clear();
      value.clear();
   }

   /**
    * This will add the given name and value to the parameters map.
    * The value is stored in the map using an array of type string.
    * This is done so that multiple values for a single name can
    * be remembered. Once all the parameters have been inserted 
    * the <code>getParameters</code> method can be used to collect
    * the array of values using the parameter name.
    *
    * @param name this is the name of the value to be inserted
    * @param value this is the value of a that is to be inserted
    */
   private void insert(ParseBuffer name, ParseBuffer value){
      insert(name.toString(), value.toString());
   }
   
   /**
    * This will add the given name and value to the parameters map.
    * The value is stored in the map using an array of type string.
    * This is done so that multiple values for a single name can
    * be remembered. Once all the parameters have been inserted 
    * the <code>getParameters</code> method can be used to collect
    * the array of values using the parameter name.
    *
    * @param name this is the name of the value to be inserted
    * @param value this is the value of a that is to be inserted
    */
   private void insert(String name, String value) {
      String[] values = new String[]{}; 
      
      if(map.containsKey(name)){
         values = (String[])map.get(name);
      }
      int size = values.length + 1;
      String[] list = new String[size];
      
      System.arraycopy(values,0,list,0,size-1);
      list[values.length] = value;
      map.put(name, list);      
   }
   
   /**
    * This performs the actual parsing of the HTML text. This will
    * initially pack the HTML text so that a well formed expression
    * can be formed. Once this is done the source and parameters 
    * are taken from the HTML expression. This will insert all the
    * parameters extracted into a hashtable.
    */
   protected void parse() {
      pack();
      include(); 
      params(); 
   }
   
   /**
    * This initializes the parser so that it can be used several
    * times. This clears any previous parameters extracted. This
    * ensures that when the next <code>parse(String)</code> is
    * invoked the status of the <code>Parameters</code> is empty.
    */   
   protected void init(){
      source.clear();
      name.clear();
      value.clear();
      map.clear();
      off = 0;
   }

   /**
    * This will parse the initial include tag. The include tag is
    * a HTML tag with a source parameter. The source parameter is
    * used to reference a resuorce in the form of a path. For
    * example a source could be <code>/path/bin/index.html</code>.
    * The syntax for the expression parsed is shown below.
    * <pre>
    *
    *  "&lt;" "include" SP ("source" | "src") "=" token "&gt;"
    *
    * </pre>
    * The <code>getContentPath</code> method is used to expose 
    * the source path from the include tag, which is identified
    * by either a "source" or "src" parameter name.
    */
   private void include() {
      if(skip("<include ")) {
         if(skip("source=")) {  
            token(source);
         } else if(skip("src=")){
            token(source);
         }
         skip(">");
      }
   }

   /**
    * This method will extract the list of parameter tags with the
    * HTML text. This will continue to extract the parameter tags
    * until it encounters a malformed tag or until it encounters the
    * terminating &lt;/include&gt; token for the expression. The
    * syntax for a single parameter tag is shown below.    
    * <pre>
    * 
    * "&lt;" "param" SP "name" "=" token SP "value" "=" token "&gt;"
    *
    * </pre>
    * Each of the name and value pairs that are extracted from the
    * parameter tags will be stored withe the internal map so that
    * the <code>getParameter</code> method can expose them. 
    */
   private void params() { 
      while(off < count) {
         if(skip("</include>")){
            break;
         }else if(skip("<")){
            param();            
         } else if(skip(">")){
            continue;
         } else {
            break;
         }
      }
   }

   /**
    * This will extract a subset of the parameter tag expression 
    * and store the name value pairs in the internal hashtable. 
    * The expression parsed by this method is shown below.
    * <pre>
    *
    *  "param" SP "name" "=" token SP "value" "=" token   
    *
    * </pre>
    * If the name of the tag is not "param" this will not parse
    * the expression and will simply return, which will result
    * in a termination of parsing for the entire expression.
    */
   private void param() { 
      if(skip("param ")) {
         name();         
         off++; /* SP */
         value();
         insert();
      }
   }


   /**
    * This will extract the name attribute from the parameter
    * tag. The names extracted are used to store values in the
    * hashtable. The expression <pre>"name" "=" token</code>
    * is taken from the HTML text once this has finished.    
    */
   private void name() {
      if(skip("name=")) {
         token(name);         
      }
   }

   /**
    * This will extract the name attribute from the parameter
    * tag. The names extracted are used to store values in the
    * hashtable. The expression <pre>"value" "=" token</code>
    * is taken from the HTML text once this has finished.    
    */
   private void value() {
      if(skip("value=")) {
         token(value);
      }
   }
   

   /**
    * This is used to extract a token from the HTML text. A token is
    * a string literal, that is, a string within quotes, or a series
    * of text characters. The HTML 4.0 specification defines text
    * characters for an attribute to be a a sequence of letters, 
    * digits, periods, or hyphens. 
    * <p>
    * If a token is within quotes an escaped quote character within
    * the primary quotations is allowed. An escaped quote is a 
    * quote character that is prefixed with the backslash character.
    * So for example the string "The \"Yellow\" Submarine" has two
    * escaped quotation characters within it.
    *
    * @param data this collects the token characters for the value
    */
   private void token(ParseBuffer data) {
      if(off < count && quote(buf[off])){ /* quoted */
         for(off++; off < count; off++){               
            if(buf[off] != '\\') {
               if(quote(buf[off])){
                  off++;
                  break;
               }
               data.append(buf[off]);               
            }else if(off + 1 < count){
               data.append(buf[++off]);
            }
         }
      }else {   
         for(; off < count; off++){ /* plain */
            if(text(buf[off])){ 
               data.append(buf[off]);
            }else {
               break;
            }
         }                      
      }
   }  
   
   
   /** 
    * This is a character set that defines special characters in
    * the HTML expression. The special characters within the HTML 
    * are "&lt;", "&gt;", "&nbsp;", "\n", "\r", "\t", "=", "/".
    * This method is optimized to check for space characters first 
    * as it will avoid having to do multiple checks.
    *
    * @param ch the character value that is being checked
    *
    * @return true if the character is a special character    
    */
   private boolean special(char ch) {      
      switch(ch) {
      case ' ': case '\n':
      case '\r': case '<':
      case '>': case '=':
      case '/': case '\t':      
         return true;
      }
      return false;
   }

   /**
    * The determines whether the given character is a quotation 
    * character or not. A quotation is either the double quote or
    * single quote character, <code>"</code> or <code>'</code>.
    *   
    * @param ch the character value that is being checked
    *
    * @return true if the character is a quotation character    
    */
   private boolean quote(char ch) {
      return ch == '"' || ch == '\'';
   }

   /**
    * This defines text characters that are legal witin HTML 4.0
    * attribute values. The HTML 4.0 specification defines legal
    * text characters as any sequence of letters, digits, periods, 
    * or hyphens. 
    *
    * @param ch the character value that is being checked
    *
    * @return true if the character is a legal text character       
    */
   private boolean text(char ch) {
      return alphanum(ch) || hyphen(ch);
   }

   /**
    * The determines whether the given character is a quotation 
    * character or not. A quotation is either the peroid or the
    * hyphen character, <code>-</code> or <code>.</code>.
    *   
    * @param ch the character value that is being checked
    *
    * @return true if the character is a quotation character    
    */
   private boolean hyphen(char ch) {
      return ch == '-' || ch == '.';
   }
   
   /** 
    * This is used to determine wheather or not a given unicode 
    * character is an alphabetic character or a digit character.
    * That is withing the range <code>0 - 9</code> and between
    * <code>a - z</code> it uses <code>iso-8859-1</code> to 
    * compare the character.
    *
    * @param ch the character value that is being checked
    *
    * @return true if the character has an alphanumeric value
    */ 
   private boolean alphanum(char ch){
      if(ch <= 'z' && 'a' <= ch) {
         return true;
      }else if(ch <= 'Z' && 'A' <= ch) {
         return true;
      }
      return ch <= '9' && '0' <= ch;
   }

   /**
    * This is used to convert and encode a name and value pair to
    * an <code>application/x-www-form-urlencoded</code> parameter.
    * This encodes the string values to ensure that all special
    * parameters are escaped, see RFC 2396.
    *
    * @param name this is the name that is to be URL encoded
    * @param value this is the value that is to be URL encoded
    *
    * @return name and value encoded with escaped characters
    */
   private String encode(String name, String value) {
      return encode(name)+"=" + encode(value);
   }

   /**
    * This will encode the string so that it can be used within 
    * an <code>application/x-www-form-urlencoded</code> parameter
    * string. This ensures that special characters are escaped. 
    *
    * @param text the text string that is to be URL encoded
    * 
    * @return returns the text string with escaped characters
    */
   private String encode(String text) {
      try {
         return URLEncoder.encode(text, "utf-8");
      }catch(IOException e) {
         return text;
      }
   }
   
   /**
    * This will return all parameters represented using the HTTP
    * URL query format. The <code>x-www-form-urlencoded</code>
    * format is used to encode the attributes, see RFC 2616. 
    * <p>
    * This will also encode any special characters that appear
    * within the name and value pairs as an escaped sequence.
    * If there are no parameters an empty string is returned.
    *
    * @return returns an empty string if the is no parameters
    */    
   public String toString() {
      Enumeration names = map.keys();            
      String text = "";

      while(names.hasMoreElements()) {
         String name = ""+names.nextElement();
         String[] list = getParameters(name);
         
         for(int i = 0; i < list.length; i++) {
            text += encode(name, list[i]) + "&";         
         }         
      }      
      int size = Math.max(text.length()-1, 0);
      return text.substring(0, size);
   }
}

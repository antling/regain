/*
 * MimeParser.java February 2001
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

import simple.util.net.MimeType;

/** 
 * <code>MimeParser</code> provides access to the mime type's parts, 
 * that is the type subtype and an optional <code>charset</code> 
 * parameter. The <code>charset</code> parameter is one of many 
 * parameters that can be assiciated with a mime type. This however 
 * only provides access to the <code>charset</code> value.
 * <p>
 * The <code>getCharset</code> will return <code>val</code> if the 
 * mime type represented is type/subtype; charset=val. The type and 
 * subtype are set to the <code>String</code> value <code>null</code>
 * if the <code>setType</code> or <code>setSubtype</code> are given 
 * a <code>null</code><code>String</code>. If the <code>String</code>
 * that is being parsed does not contain a type or subtype then the 
 * <code>toString</code> will return the value null/null otherwise 
 * it will recreate the mime type.
 *
 * @author Niall Gallagher
 */
public class MimeParser extends Parser implements MimeType {

   /** 
    * Used to store the characters consumed for the subtype.
    */
   private ParseBuffer subtype;

   /** 
    * Used to store the characters for the <code>charset</code>.
    */
   private ParseBuffer charset;

   /** 
    * Used to store the characters consumed for the type.
    */
   private ParseBuffer type;
   
   /** 
    * The default constructor will create a <code>MimeParser</code>
    * that contains no charset, type or subtype. This can be used to 
    * extract the type, subtype and the optional <code>charset</code> 
    * parameter by using the parser's <code>parse(String)</code> 
    * method.
    */   
   public MimeParser(){
      subtype = new ParseBuffer();
      charset = new ParseBuffer();
      type = new ParseBuffer();      
   }

   /** 
    * This is primarily a convineance constructor. This will parse 
    * the <code>String</code> given to extract the mime-type. This 
    * could be achived by calling the default no-arg constructor 
    * and then using the instance to invoke the <code>parse</code> 
    * method on that <code>String</code>.
    *
    * @param header <code>String</code> containing a mime-type value
    */
   public MimeParser(String header){
      this();
      parse(header);
   }      

   /** 
    * Sets the type to whatever value is in the <code>String</code> 
    * object. If the <code>String</code> object is <code>null</code> 
    * the this object's <code>toString</code> method will contain 
    * the value <code>null</code>.
    * <p>
    * If type is <code>null</code> then the <code>toString</code> 
    * method will be null/subtype;param=value. If the type is 
    * non-null this will contain the value of the <code>String</code>.
    * 
    * @param primary the type to add to the MIME type
    */ 
   public void setType(String primary) {      
      type.clear();
      type.append(primary==null?"":primary);
   }
   
   /** 
    * This is used to retrive the type of this MIME type. The type 
    * part within the MIME type defines the generic type. For example 
    * <code>type/subtype;param1=value1</code>. This will return the 
    * value of the type part. If there is no type part then this will 
    * return <code>null</code> otherwise the type <code>String</code>.
    *
    * @return the type part of the MIME type
    */  
   public String getType() {
      if(type.length() == 0) {
         return null;
      }
      return type.toString();
   }   
   
   /** 
    * Sets the subtype to whatever value is in the <code>String</code> 
    * object. If the <code>String</code> object is <code>null</code> 
    * the this object's <code>toString</code> method will contain the 
    * value <code>null</code>.
    * <p>
    * If subtype is <code>null</code> then the <code>toString</code> 
    * method will be <code>type/null;param=value</code>. If the type 
    * is non-null this will contain the value of the <code>String</code>.
    *
    * @param type the type to add to the MIME type
    */ 
   public void setSubtype(String type) {
      subtype.clear();
      subtype.append(type==null?"":type);
   }   

   /** 
    * This is used to retrive the subtype of this MIME type. The subtype 
    * part within the MIME type defines the specific type. For example 
    * <code>type/subtype;param1=value1</code>. This will return the value 
    * of the subtype part. If there is no subtype part then this will 
    * return <code>null</code> otherwise the type <code>String</code>.
    *
    * @return the subtype part of the MIME type
    */  
   public String getSubtype(){
      if(subtype.length() == 0) {
         return null;
      }
      return subtype.toString();
   }   

   /** 
    * This will set the <code>charset</code> to whatever value is in the 
    * <code>String</code> object. If the <code>String</code> object is 
    * <code>null</code> the this object's <code>toString</code> method 
    * will not contain the <code>charset</code>.
    * <p>
    * If <code>charset</code> is null then the <code>toString</code> 
    * method will be type/subtype. If the <code>charset</code> value 
    * is non-null this will contain the <code>charset</code> parameter 
    * with that value.
    *
    * @param enc the <code>charset</code> value to add to the MIME type
    */ 
   public void setCharset(String enc) {
      charset.clear();
      charset.append(enc==null?"":enc);   
   }   

   /** 
    * This is used to retrive the <code>charset</code> of this MIME type. 
    * The <code>charset</code> part within the MIME type is an optional 
    * parameter. For example <code>type/subtype;charset=value</code>. This 
    * will return the value of the <code>charset</code> value. If there is 
    * no <code>charset</code> param then this will return <code>null</code> 
    * otherwise the type <code>String</code>.
    *
    * @return the <code>charset</code> value for the MIME type
    */  
   public String getCharset() {
      if(charset.length() == 0) {
         return null;
      }
      return charset.toString();   
   }  

   /** 
    * This is used to remove all whitespace characters from the 
    * <code>String</code> excluding the whitespace within literals. 
    * The definition of a literal can be found in RFC 2616. 
    * <p>
    * The definition of a literal for RFC 2616 is anything between 2 
    * quotes but excuding quotes that are prefixed with the backward 
    * slash character.
    */
   private void pack() {
      int len = count;
      int seek = 0;
      int pos = 0;
      char old = buf[0];

      while(seek < len){
         char ch = buf[seek++];
         if(ch == '"' && old != '\\'){  /* qd-text*/
            buf[pos++] = ch;
            while(seek < len){
               old = buf[seek-1];
               ch = buf[seek++];  
               buf[pos++] = ch;             
               if(ch =='"'&& old!='\\'){  /*qd-text*/
                  break;
               }
            }
         }else if(!space(ch)){            
            old = buf[seek - 1];  
            buf[pos++] = old;                   
         }         
      }
      count = pos;
   }

   /** 
    * This will initialize the parser when it is ready to parse 
    * a new <code>String</code>. This will reset the parser to a 
    * ready state. The init method is invoked by the parser when 
    * the <code>Parser.parse</code> method is invoked.
    */
   protected void init(){
      pack();
      type.clear();
      subtype.clear();
      charset.clear();
      off = 0;
   }
   
   /** 
    * Reads and parses the mime-type from the given <code>String</code> 
    * object. This uses the syntax defined by RFC 2616 for the media-type 
    * syntax. This parser is only concerned with one parameter, the 
    * <code>charset</code> parameter. The syntax for thhe media type is 
    * <pre>
    * media-type = token "/" token *( ";" parameter )
    * parameter = token | literal 
    * </pre>
    */
   protected void parse(){
      type();
      off++;
      subtype();
      parameters();
   }
   
   /** 
    * This reads the type from the mime-type. This will fill the 
    * type <code>ParseBuffer</code>. This will read all chars 
    * upto but not including the first instance of a '/'. The type 
    * of a media-type as defined by RFC 2616 is
    * <code>type/subtype;param=val;param2=val</code>.
    */  
   private void type(){
      while(off < count){
         if(buf[off] =='/'){
            break;
         }
         type.append(buf[off]);
         off++;
      }
   }
  
   /** 
    * This reads the subtype from the mime-type. This will fill the 
    * subtype <code>ParseBuffer</code>. This will read all chars 
    * upto but not including the first instance of a ';'. The subtype 
    * of a media-type as defined by RFC 2616 is
    * <code>type/subtype;param=val;param2=val</code>.
    */
   private void subtype(){
      while(off < count){
         if(buf[off] ==';'){
            break;
         }
         subtype.append(buf[off]);
         off++;
      }      
   }
   
   /** 
    * This will read the parameters from the mime-type. This will search 
    * for the <code>charset</code> parameter within the set of parameters 
    * which are given to the type. The <code>charset</code> param is the 
    * only parameter that this parser will tokenize. 
    * <p>
    * This will remove any parameters that preceed the charset parameter. 
    * Once the <code>charset</code> is retrived the mime-type is considered 
    * to be parsed.
    */
   private void parameters(){   
      while(skip(";")){
         if(skip("charset=")){
            charset();
            break;
         }else{
            parameter();
         }
      }
   }
   
   /** 
    * This is a parameter as defined by RFC 2616. The parameter is added to a 
    * mime-type e.g. <code>type/subtype;param=val</code> etc.  The parameter 
    * name and value are not stored. This is used to simply update the read 
    * offset past the parameter. The reason for reading the parameters is to 
    * search for the <code>charset</code> parameter which will indicate the 
    * encoding.
    */
   private void parameter(){
      name();
      off++; /* = */
      value();
   }
   
   /** 
    * This will simply read all characters from the buffer before the first '=' 
    * character. This represents a parameter name (see RFC 2616 for token). The 
    * parameter name is not buffered it is simply read from the buffer. This will
    * not cause an <code>IndexOutOfBoundsException</code> as each offset
    * is checked before it is acccessed.
    */
   private void name(){
      while(off < count){
         if(buf[off] =='='){
            break;
         }
         off++;
      }   
   }
   
   /** 
    * This is used to read a parameters value from the buf. This will read all 
    * <code>char</code>'s upto but excluding the first terminal <code>char</code> 
    * encountered from the off within the buf, or if the value is a literal 
    * it will read a literal from the buffer (literal is any data between 
    * quotes except if the quote is prefixed with a backward slash character).    
    */
   private void value(){
      if(buf[off] == '"'){         
         for(off++; off < count;){
            if(buf[off++]=='"'){
               if(buf[off-2]!='\\'){
                  break;
               }
            }
         }
      }else{   
         while(off < count){
            if(buf[off] ==';')
               break;            
            off++;
         }
      }
   }
   
   /** 
    * This is used to read the value from the <code>charset</code> param.
    * This will fill the <code>charset</code> <code>ParseBuffer</code> and with 
    * the <code>charset</code> value. This will read a literal or a token as 
    * the <code>charset</code> value. If the <code>charset</code> is a literal 
    * then the quotes will be read as part of the charset.
    */ 
   private void charset(){
      if(buf[off] == '"'){         
         charset.append('"');
         for(off++; off < count;){
            charset.append(buf[off]);
            if(buf[off++]=='"')
               if(buf[off-2]!='\\'){
                  break;
               }            
         }
      }else{   
         while(off < count){
            if(buf[off]==';')
               break;            
            charset.append(buf[off]);
            off++;
         }
      }   
   }

   /** 
    * This will return the <code>String</code> value of the MIME type. This
    * will return the MIME type with the type, subtype and if there is a 
    * <code>charset</code> value specified then a <code>charset</code> parameter.
    * <p>
    * The <code>charset</code> parameter is an optional parameter to the MIME
    * type. An example a MIME type is <code>type/subtype; charset=value</code>.
    * If the type or subtype is <code>null</code> then the MIME type will be
    * wither null/subtype, type/null or if both are <code>null</code> null/null.
    *
    * @return the <code>String</code> representation of the MIME type
    */
   public String toString() {
      return ""+ type + "/" + subtype +
       (charset.length()>0 ?"; charset="+charset:"");
   }
}

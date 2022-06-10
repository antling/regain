/*
 * MimeType.java February 2001
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
 
package simple.util.net;

/** 
 * This provides access to the MIME type parts, that is the type 
 * subtype and an optional <code>charset</code> parameter. The 
 * <code>charset</code> parameter is one of many parameters that 
 * can be assiciated with a MIME type. This however only provides 
 * access to the <code>charset</code> value.
 * <p>
 * The <code>getCharset</code> will return <code>val</code> if the 
 * MIME type represented is type/subtype; charset=val. The type and 
 * subtype are set to the <code>String</code> value <code>null</code> 
 * if the <code>setType</code> or <code>setSubtype</code> are given 
 * a <code>null</code> <code>String</code>.
 *
 * @author Niall Gallagher
 */
public interface MimeType {

   /** 
    * This sets the type to whatever value is in the <code>String</code> 
    * object. If the <code>String</code> object is <code>null</code> the 
    * this object's <code>toString</code> method will contain the value 
    * <code>null</code>.
    * <p>
    * If type is <code>null</code> then the <code>toString</code> method 
    * will be null/subtype;param=value. If the type is non-null this will 
    * contain the value of the <code>String</code>.
    * 
    * @param type the type to add to the MIME type
    */ 
   public void setType(String type);
   
   /** 
    * This is used to retrive the type of this MIME type. The type 
    * part within the MIME type defines the generic type. For example 
    * <code>type/subtype;param1=value1</code>. This will return the 
    * value of the type part. If there is no type part then this will 
    * return <code>null</code> otherwise the type <code>String</code>.
    *
    * @return the type part of the MIME type
    */   
   public String getType();  
   
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
   public void setSubtype(String type);

   /** 
    * This is used to retrive the subtype of this MIME type. The 
    * subtype part within the MIME type defines the specific type. 
    * For example <code>type/subtype;param1=value1</code>. This will 
    * return the value of the subtype part. If there is no subtype 
    * part then this will return <code>null</code> otherwise the type 
    * <code>String</code>.
    *
    * @return the subtype part of the MIME type
    */   
   public String getSubtype();

   /** 
    * This will set the <code>charset</code> to whatever value is 
    * in the <code>String</code> object. If the <code>String</code> 
    * object is <code>null</code> then this <code>toString</code> 
    * method will not contain the <code>charset</code>.
    * <p>
    * If <code>charset</code> is <code>null</code> then the 
    * <code>toString</code> method will be type/subtype. If the 
    * <code>charset</code> value is non-null this will contain 
    * the <code>charset</code> parameter with that value.
    *
    * @param charset the value to add to the MIME type
    */ 
   public void setCharset(String charset);

   /** 
    * This is used to retrive the <code>charset</code> of this MIME 
    * type. The <code>charset</code> part within the MIME type is an 
    * optional parameter. For example <code>type/subtype;charset=value
    * </code>. This will return the value of the <code>charset</code> 
    * value. If there is no <code>charset</code> param then this will 
    * return <code>null</code> otherwise the type <code>String</code>.  
    *
    * @return the <code>charset</code> value for the MIME type
    */     
   public String getCharset();

   /** 
    * This will return the <code>String</code> value of the MIME 
    * type. This will return the MIME type with the type, subtype 
    * and if there is a <code>charset</code> value specified then 
    * a <code>charset</code> parameter.
    * <p>
    * The <code>charset</code> parameter is an optional parameter 
    * to the MIME type. An example a MIME type is <code>type/subtype; 
    * charset=value<code>. If the type or subtype is <code>null</code>
    * then the MIME type will be wither null/subtype, type/null or if 
    * both are <code>null</code> null/null.
    *
    * @return the <code>String</code> representation of the MIME type
    */
   public String toString();
}

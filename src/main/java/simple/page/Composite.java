/*
 * Composite.java May 2003
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

import simple.util.net.Parameters;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.URLEncoder;
import java.io.IOException;
   
/**
 * The <code>Composite</code> object creates a single interface for
 * several <code>Parameters</code> objects. This is used so that SSI 
 * parameters, in the form of a <code>Reference</code>, can be used
 * with another parameter source. This allows a single object to be
 * given to the <code>Content.write</code> method.
 * <p>
 * This ensures that the SSI parameters are given low priority so
 * that they appear in the higher indexes of the string array 
 * returned by the <code>getParameters</code> method. This enables
 * parameter values returned from <code>getParameter</code> method
 * to be dominated by a primary parameters source. 
 * 
 * @author Niall Gallagher
 *
 * @see simple.util.net.Parameters
 */
class Composite implements Reference {

   /**
    * Contains all of the parameters from both source objects.
    */
   private Hashtable map;

   /**
    * Represents the content object referenced within a page.
    */
   private String source;

   /**
    * Constructor for the <code>Composite</code> object. This is
    * used to create a single <code>Parameters</code> object from
    * two parameters sources. This will ensure that the parameters
    * object within the <code>Reference</code> are given a lower
    * priority that those in the <code>Parameters</code> object.
    *
    * @param data contains the primary source of parameters
    * @param page contains the parameters from within the page    
    */
   public Composite(Parameters data, Reference page) {
      this(new Parameters[]{data, page}, page);            
   }
   
   /**
    * Constructor for the <code>Composite</code> object. This is
    * used to create a single <code>Parameters</code> object from
    * several parameters sources. The order of the parameters will
    * be the order the <code>getParameters</code> returns them.
    *
    * @param data the list of <code>Parameters</code> to be used
    * @param page contains the parameters from within the page
    */
   private Composite(Parameters[] data, Reference page) {
      this.source = page.getContentPath();
      this.map = new Hashtable();
      this.insert(data);
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
    * the &lt;include&gt; statement. The content path can be
    * used to acquire a <code>Content</code> object.
    *
    * @return this returns the path that is referenced
    */
   public String getContentPath() {
      return source;
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
    * This inserts the <code>Parameters</code> into the hashable.
    * Copying the parameters to an internal <code>Hashtable</code>
    * ensures that there is a coherent ordering to the parameters
    * and so that the enumeration returned will contain unique
    * names for the parameters, also access will be faster.    
    *
    * @param data the collection of parameters to be inserted
    */
   private void insert(Parameters data[]) {
      for(int i = 0; i < data.length; i++) {
         if(data[i] != null)
            insert(data[i]);
      }
   }
   
   /**
    * This inserts the <code>Parameters</code> into the hashable. 
    * Copying the parameters to an internal <code>Hashtable</code>
    * ensures that there is a coherent ordering to the parameters
    * and so that the enumeration returned will contain unique
    * names for the parameters, also access will be faster.    
    *
    * @param data the collection of parameters to be inserted    
    */
   private void insert(Parameters data) {
      Enumeration list = data.getParameterNames();           

      while(list.hasMoreElements()){
         String name = (String)list.nextElement();
         insert(name, data.getParameters(name));
      }
   }

   /**
    * This adds the list of name and value parameters to the map.
    * Copying the parameters to an internal <code>Hashtable</code>
    * ensures that there is a coherent ordering to the parameters
    * and so that the enumeration returned will contain unique
    * names for the parameters, also access will be faster.    
    *
    * @param name this is the name of the value to be inserted
    * @param list this is the list of values to be inserted
    */
   private void insert(String name, String[] list) {
      for(int i = 0; i < list.length; i++) {
         insert(name, list[i]);
      }
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

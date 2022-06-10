/*
 * Parameters.java February 2001
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

import java.util.Enumeration;

/**
 * The <code>Parameters</code> object is used to represent immutable
 * parameters. Parameters are acquired by name and multiple values
 * can be acquired using <code>getParameters</code>. This follows
 * the parameter methods found in the Java Servlet API Specification.
 * The parameter values found in a parameter set can appear multiple
 * times, for this reason this enables the parameter values to be
 * extracted as an array of string values.
 *
 * @author Niall Gallagher
 */
public interface Parameters {

   /**
    * This enumerates the names of every parameter. This enables
    * the parameter values to be extracted by providing the name
    * to the <code>getParameters</code> method. The resulting
    * <code>Enumeration</code> contains string objects.
    *
    * @return this returns an <code>Enumeration</code> of names
    */
   public Enumeration getParameterNames();

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
   public String getParameter(String name);

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
   public int getInteger(String name);

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
   public float getFloat(String name);

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
   public boolean getBoolean(String name);

   /**
    * This extracts all the values for a given name. The name
    * used must be from the <code>Enumeration</code> issued.
    * If there is not parameter of this name this returns null.
    *
    * @param name the name of the parameter value to retrieve
    *
    * @return returns an array of values for the given name
    */
   public String[] getParameters(String name);

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
   public String toString();
}

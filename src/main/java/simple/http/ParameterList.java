/*
 * ParameterList.java May 2003
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
  
package simple.http;

import simple.util.parse.ParameterParser;

/**
 * The <code>ParameterParser</code> is used to parse several text
 * strings as a complete URL encoded parameter string. This does
 * the following concatatnations.
 *
 * <pre>
 * null + "a=b&amp;c=d&amp;e=f" = "a=b&amp;c=d&amp;e=f"
 * "a=b" + "e=f&amp;g=h" = "a=b&amp;e=f&amp;g=h";
 * "a=b&amp;c=d&amp;e=f" + "" = "a=b&amp;c=d&amp;e=f"
 * </pre>
 *
 * This ensures that the <code>ParameterParser</code> can parse
 * the list of strings as a single URL encoded parameter string.
 * This can parse any mumber of parameter strings.
 *
 * @author Niall Gallagher
 */
class ParameterList extends ParameterParser {

   /**
    * Constructor that allows an array of string objects to 
    * be parsed as a single parameter string. This will check
    * each string to see if it is empty, that is, is either
    * null or the zero length string.
    * 
    * @param list this is the list of strings to be parsed
    */
   public ParameterList(String[] list) {
      parse(list);
   }
   
   /**
    * Constructor that allows a pair of string objects to be
    * parsed as a single parameter string. This will check
    * each string to see if it is empty, that is, is either
    * null or the zero length string.
    * 
    * @param one this is the first parameter string parsed
    * @param two this is the second parameter string parsed
    */
   public ParameterList(String one, String two) {      
      parse(new String[]{one, two});
   }

   /**
    * This will concatanate the list of parameter strings as a 
    * single parameter string, before handing it to be parsed
    * by the <code>parse(String)</code> method. This method 
    * will ignore any null or zero length strings in the array.    
    *
    * @param list this is the list of strings to be parsed
    */
   public void parse(String[] list) {
      String text = "";
      for(int i = 0; i < list.length; i++) {
         if(list[i] == null) {
            continue;
         } else if(list[i].length()==0){
            continue;
         } else if(text.length()==0){
            text = list[i];
         } else{         
            text += "&"+list[i];
         }
      }
      parse(text);
   }
}

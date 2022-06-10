/*
 * FormatFactory.java March 2002
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
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
 
package simple.http.serve;

/**
 * The <code>FormatFactory</code> retrives the <code>Format</code>
 * implementation for the system. Because an implementor of a web 
 * server using the <code>FileEngine</code> may not implement a
 * <code>Format</code> object a default system wide implementation 
 * is needed to keep the instances consistant. This object will
 * produce implementations that are consistant within the system.
 * <p>
 * In order to define a system wide implementation a property is
 * needed to define the object. This uses the <code>System</code>
 * properties to define the class name for the default instance.
 * The property is the <code>simple.http.serve.format</code>
 * property that can be set using an argument to the VM.
 * <pre>
 * java -Dsimple.http.serve.format=demo.example.ExampleFormat
 * </pre>
 * This will set the <code>System</code> property to the class 
 * name <code>demo.example.ExampleFormat</code>. When the factory
 * method <code>getInstance</code> is invoked it will return an
 * implementation of this object or if the implementation cannot
 * be loaded by this classes class loader a default implementation
 * <code>DefaultFormat</code> is returned instead.
 *
 * @author Niall Gallagher
 */
public final class FormatFactory {

   /**
    * This is used to produce the system wide <code>Format</code>
    * implementation so that the <code>FileEngine</code> objects
    * can remain consistant. This will use a system property 
    * <code>simple.http.serve.format</code> to define the
    * class name of the implementation that will be used for the
    * system wide <code>Format</code>. The property should contain
    * the fully qualified class name of the object and should be
    * loadable by this classes class loader. If the specified class
    * cannot be loaded the <code>DefaultFormat</code> is used.
    *
    * @return an implementation of the <code>Format</code> 
    * interface
    */
   public static Format getInstance() {
      String property = "simple.http.serve.format";
      String formatName = System.getProperty(property);
      
      if(formatName == null){ 
         return new DefaultFormat();
      }
      try {    
         Class formatClass = Class.forName(formatName,
            false, FormatFactory.class.getClassLoader());         
         return (Format)formatClass.newInstance();         
      }catch(Exception e){
         return new DefaultFormat();
      }
   }
}


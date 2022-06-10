/*
 * IdentifierFactory.java June 2004
 *
 * Copyright (C) 2004, Niall Gallagher <niallg@users.sf.net>
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

package simple.http.session;

/**
 * The <code>IdentifierFactory</code> retrives the identifier 
 * implementation for the system. This is used to generate session
 * references for the session management system. An implementation
 * is invoked by the session management system each time a session
 * is referenced, the instance is then responsible for either
 * creating a new reference or extracting an existing one.
 * <p>
 * In order to define a system wide implementation a property is
 * needed to define the object. This uses the <code>System</code>
 * properties to define the class name for the default instance.
 * The property is the <code>simple.http.session.identifier</code>
 * property that can be set using an argument to the VM.
 * <pre>
 * java -Dsimple.http.session.identifier=demo.DemoIdentifier
 * </pre>
 * This will set the <code>System</code> property to the class 
 * name <code>demo.DemoIdentifier</code>. When the factory method
 * <code>getInstance</code> is invoked it will return an
 * implementation of this object or if the implementation cannot
 * be loaded by this classes class loader a default implementation
 * <code>DefaultIdentifier</code> is returned instead.
 *
 * @author Niall Gallagher
 */
final class IdentifierFactory {

   /**
    * This is used to produce the <code>Identifier</code> for the
    * session management system. This will use a system property 
    * <code>simple.http.session.identifier</code> to define the
    * class name of the implementation that will be used for the
    * <code>Identifier</code> instance. The property must contain
    * the fully qualified class name of the object and should be
    * loadable by this classes class loader. If the class cannot
    * be loaded the <code>DefaultIdentifier</code> is used.
    *
    * @return the identifier object used for session management
    */
   public static Identifier getInstance() {
      String property = "simple.http.session.identifier";
      String className = System.getProperty(property);
      
      if(className == null){ 
         return new DefaultIdentifier();
      }
      try {    
         Class type = Class.forName(className,
            false, IdentifierFactory.class.getClassLoader());         
         return (Identifier)type.newInstance();         
      }catch(Exception e){
         return new DefaultIdentifier();
      }
   }
}

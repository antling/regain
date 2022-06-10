/*
 * Locator.java February 2004
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

package simple.http.serve;

import java.util.Properties;
import java.io.File;

/**
 * The <code>Locator</code> interface is used to locate resources
 * outside the scope of a <code>Context</code>. This is required 
 * so that various configuration files and other resources can be
 * located by the system. Resources such as XML files and Java
 * properties files required to configure various objects can be
 * discovered using an implementation of this interface.
 * <p> 
 * This provides a set of methods similar to the methods provided
 * by the <code>Context</code> object. However unlike that object
 * resources are located with more flexibility as it does not have 
 * restricted scope. Also, this does not accept a URI target name 
 * to locate the files, instead this will typically be given the 
 * name of a resource. For example if an XML configuration file 
 * named <code>config.xml</code> was to be located then it could
 * be located using an alias like <code>config</code> or possibly
 * with the file name itself, without any path information.
 * <p>
 * For simplicity and portability an implementation of this should
 * be able to cope with path information in the event that it is
 * supplied. Paths in URI format like <code>/bin/config.xml</code>
 * and also system dependant format (although not advisable) like 
 * <code>.\bin\config.xml</code> for a Windows system should be
 * acceptable to an implementation.
 *
 * @author Niall Gallagher
 */ 
public interface Locator {

   /**
    * This is used to discover the location of a resource using
    * the name of the resource. This will return the location of
    * the resource in a system specific path format. This mirrors
    * the <code>Context.getRealPath</code> method, however it
    * should be supplied with only the name of the resource with
    * no path information, which keeps any code interacting with 
    * this portable across systems using different path formats.
    *
    * @param name the name of the resource that is to be located
    *
    * @return the location of the resource in the system format
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   public String getLocation(String name) throws LocateException;

   /**
    * This is used to produce a <code>File</code> object pointing
    * to the location of the named resource. This method mirrors
    * the <code>Context.getFile</code> method, however it should 
    * be supplied with only the name of the resource with no path
    * information, which keeps any code interacting with this
    * portable across systems using different path formats.
    *
    * @param name the name of the resource that is to be located
    *
    * @return a <code>File</code> referencing the name resource
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   public File getFile(String name) throws LocateException;

   /**
    * This is used to produce a <code>Properties</code> object 
    * that contains the contents of the named Java properties file. 
    * This mirrors the <code>Context.getProperties</code> method, 
    * however it should be supplied with only the name of the 
    * resource with no path information, which keeps any code 
    * interacting with this portable across different platforms.
    *
    * @param name the name of the resource that is to be located
    *
    * @return a <code>Properties</code> object for the resource
    *
    * @exception LocateException thrown if the named resource
    * could not be found after exhausting all lookup means
    */
   public Properties getProperties(String name) throws LocateException;
}

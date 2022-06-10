/*
 * LoaderManager.java February 2001
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
 
package simple.http.load;

import java.rmi.RemoteException;
import java.rmi.Remote;
import simple.util.Match;

/**
 * The <code>LoaderManager</code> is used to load <code>Service</code>
 * implementation classes. This can be used from a remote network host
 * using RMI as it extends the <code>Remote</code> interface. Each
 * <code>Service</code> that is loaded can be purged from the system
 * using the <code>unload</code> method.
 * <p>
 * Once an object has been loaded into the system it needs to be looked
 * up in terms of a URI path. This enables links between the service
 * name and a wildcard pattern to be made. When the pattern is matched 
 * by a URI the resource can be isolated. A service instance can be 
 * linked to several patterns. If the service is to be unlinked the 
 * <code>unlink</code> method can remove the pattern from the system.
 * <p>
 * Configuring the links made with the <code>LoaderManager</code> can
 * be done using the <code>Loader</code>. This will ensure that the
 * changes in the configuration of links with the manager will be 
 * transmitted to the <code>Loader</code>. This can thus be used as 
 * a means to provide graphical administration of the manager.
 *
 * @author Niall Gallagher
 */
public interface LoaderManager extends Remote {

   /**
    * This is used to insert a <code>Loader</code> object which 
    * is used to recieve updates on the configuration. The loader
    * can be a remote object which enables the local instance to
    * to communicate its state to a remote administration utility.
    * Once registered the <code>Loader</code> will recieve an
    * immedidate update of the managers layout.
    * <p>
    * This throws a <code>SecurityException</code> if the caller
    * does not have the <code>LoaderPermission</code> with the 
    * "update" token. This ensures that any <code>Loader</code>        
    * instances from an untrusted codebase cannot register.
    *    
    * @param loader this is the <code>Loader</code> object that 
    * will recieve updates on state changes
    *
    * @exception SecurityException if the caller does not have 
    * the permission to register the <code>Loader</code>
    * @exception RemoteException this is thrown by the RMI system     
    */
   public void update(String name, Loader loader)
      throws RemoteException;   

   /**
    * This is used to terminate updates on a <code>Loader</code> 
    * object which has previously registered for updates. If the
    * <code>Loader</code> wishes to resume updates it must 
    * register again using the <code>update</code> method.
    * <p>
    * This throws a <code>SecurityException</code> if the caller
    * does not have the <code>LoaderPermission</code> with the 
    * "update" token. This ensures that any <code>Loader</code>        
    * instances from an untrusted codebase cannot remove objects
    * that have registered.
    *    
    * @param name this is the <code>Loader</code> object that 
    * is terminating updates
    *
    * @exception SecurityException if the caller does not have 
    * the permission to register the <code>Loader</code>
    * @exception RemoteException this is thrown by the RMI system     
    */  
   public void remove(String name) throws RemoteException;   

   /**
    * This loads the class into the system. This will attempt to
    * locate and load the byte codes for a <code>Resource</code>
    * implementation identified by the class name. If the class
    * can not be loaded <code>ClassNotFoundException</code> is
    * thrown. The fully qualified package name must be given.
    * <p>
    * Once the <code>Service</code> class has been loaded it is
    * used to create an instance. This instance can then have 
    * links established to it. The link is created using the 
    * unique name of the instance specified and a wild pattern.
    * <p>
    * This method requires <code>LoaderPermission</code> with 
    * the "load" action. If the client does not have permission
    * to load the service instance this throws an exception.
    *
    * @param name this is the unique name given to the instance
    * @param className this is the fully qualified class name
    *
    * @exception LoadingException is thrown if the class
    * cannot be located or loaded
    * @exception RemoteException thrown by the RMI system
    * @exception SecurityException if the caller does not have
    * permission to load the service
    */
   public void load(String name, String className)
      throws RemoteException, LoadingException;   

   /**
    * This loads the class into the system. This will attempt to
    * locate and load the byte codes for a <code>Resource</code>
    * implementation identified by the class name. If the class
    * can not be loaded <code>ClassNotFoundException</code> is
    * thrown. The fully qualified package name must be given.
    * <p>
    * Once the <code>Service</code> class has been loaded it is
    * used to create an instance. This instance can then have 
    * links established to it. The link is created using the 
    * unique name of the instance specified and a wild pattern.
    * This method also allows an object to be issued to the new
    * service instance for configuration purposes.
    * <p>
    * This method requires <code>LoaderPermission</code> with 
    * the "load" action. If the client does not have permission
    * to load the service instance this throws an exception.    
    *
    * @param name this is the unique name given to the instance
    * @param className this is the fully qualified class name
    * @param data the configuration object used by the service
    *
    * @exception LoadingException is thrown if the class
    * cannot be located or loaded
    * @exception RemoteException thrown by the RMI system
    * @exception SecurityException if the caller does not have
    * permission to load the service
    */  
   public void load(String name, String className, Object data)
      throws RemoteException, LoadingException;   
      
   /**
    * When an instance has been loaded by the <code>load</code>
    * method this can be used to purge it from the system and
    * subsequently remove all links to it. If the class name
    * specified does not correspond to a <code>Service</code>
    * that had be previously loaded this will return quietly.
    * The fully qualified package name must be given.
    * <p>
    * This method like the <code>load</code> methods requires
    * the <code>LoaderPermission</code> with the "load" action
    * to unload a named service instance.
    *
    * @param name this is the name of the service object
    *
    * @exception RemoteException thrown by the RMI system
    * @exception SecurityException if the caller does not have
    * permission to unload the service
    */
   public void unload(String name) throws RemoteException;     
   
   /** 
    * This is used to link a <code>Service</code> to a wild card 
    * pattern. The <code>Service</code> can be linked using the 
    * patterns '*' and '?'. This will enable the resource to be 
    * isolated using a string that matches the suggested pattern.
    * <p>
    * Patterns can take the form of a string with wild characters 
    * embedded in it, for instance "*.html". Also if the class 
    * name does not belong to a previously loaded
    * <code>Service</code> this should return quietly.
    * <p>
    * This throws a <code>SecurityException</code> if the caller
    * does not have the <code>LoaderPermission</code> with the 
    * "link" action. This ensures that any <code>Service</code>        
    * instances loaded from an untrusted codebase cannot change
    * the configuration settings.
    *
    * @param pattern this is a wild string used for matching
    * @param name this is the <code>Service</code> that will be
    * identified by the pattern
    *
    * @exception SecurityException if the caller does not have 
    * the permission to link the pattern   
    * @exception RemoteException this is thrown by the RMI system    
    */   
   public void link(String pattern, String name)
      throws RemoteException;   
      
   /** 
    * This is used to link a <code>Service</code> to a wild card 
    * pattern. The <code>Service</code> can be linked using the 
    * patterns '*' and '?'. This will enable the resource to be 
    * isolated using a string that matches the suggested pattern.
    * <p>
    * Patterns can take the form of a string with wild characters 
    * embedded in it, for instance "*.html". Also if the class 
    * name does not belong to a previously loaded
    * <code>Service</code> this should return quietly.
    * <p>
    * This throws a <code>SecurityException</code> if the caller
    * does not have the <code>LoaderPermission</code> with the 
    * "link" action. This ensures that any <code>Service</code>        
    * instances loaded from an untrusted codebase cannot change
    * the configuration settings.
    *
    * @param pattern this is a wild string used for matching
    * @param name this is the <code>Service</code> that will be 
    * identified by the pattern
    * @param pos the position within the list of patterns to add
    * the new match    
    *
    * @exception SecurityException if the caller does not have 
    * the permission to link the pattern    
    * @exception RemoteException this is thrown by the RMI system    
    */
   public void link(String pattern, String name, int pos)
      throws RemoteException;
      
   /**
    * This is used to unlink a loaded <code>Service</code> that
    * was linked to the specified pattern. If that pattern was 
    * not used to match a <code>Service</code> then this returns
    * quietly.
    * <p>
    * This throws a <code>SecurityException</code> if the caller
    * does not have the <code>LoaderPermission</code> with the 
    * "link" action. This ensures that any <code>Service</code>        
    * instances loaded from an untrusted codebase cannot change 
    * the configuration settings.    
    *
    * @param pattern this is a wild string used for matching
    *
    * @exception SecurityException if the caller does not have 
    * the permission to unlink the pattern
    * @exception RemoteException this is thrown by the RMI system    
    */
   public void unlink(String pattern) throws RemoteException;   

   /**
    * This is used to unlink a loaded <code>Service</code> that
    * was linked to the specified match. If that pattern was 
    * not used to match a <code>Service</code> then this returns
    * quietly.
    * <p>
    * This throws a <code>SecurityException</code> if the caller
    * does not have the <code>LoaderPermission</code> with the 
    * "link" action. This ensures that any <code>Service</code>        
    * instances loaded from an untrusted codebase cannot change 
    * the configuration settings.    
    *
    * @param match this is the pattern match to remove from this
    *
    * @exception SecurityException if the caller does not have 
    * the permission to unlink the match
    * @exception RemoteException this is thrown by the RMI system
    */
   public void unlink(Match match) throws RemoteException;
}

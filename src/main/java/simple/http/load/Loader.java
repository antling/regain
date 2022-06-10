/*
 * Loader.java September 2002
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
  
package simple.http.load;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * The <code>Loader</code> object represents an object that uses 
 * the <code>LoaderManager</code> to load and link resources. The
 * loader will recieve updates regarding changes with the manager.
 * This is done so the implementation can take action based on the
 * most up to date information. The implementation can also make
 * make changes to links using the <code>LoaderManager</code> or
 * (un)load resources. This is useful if there is an administration 
 * application that is used to manage resource configurations with 
 * the <code>LoaderManager</code>. 
 * <p>
 * The intended use of the <code>Loader</code> is that it be used
 * with some sort of graphical administration utility. The update
 * messages can then be used to refresh or repaint the graphics
 * which provides the user with a consistent view of the remote
 * <code>LoaderManager</code> configuration.
 *
 * @author Niall Gallagher
 */
public interface Loader extends Remote {

   /**
    * The <code>update</code> method is invoked when there is a
    * change in the layout. The change can be a link, load, 
    * unlink, or unload action. The intention is not that the
    * implementation interpret the <code>Layout</code> and
    * take action but that the implementation refresh a display
    * for a graphical administration utility that interacts 
    * with the <code>LoaderManager</code>.
    *
    * @param manager this is the manager that has been updated
    * @param layout this contains the current configuration of
    * the <code>LoaderManager</code>
    *
    * @throws RemoteException this is thrown by the RMI system 
    * if there is a problem with the invocation
    */
   public void update(LoaderManager manager, Layout layout)     
      throws RemoteException;
}

/*
 * LoaderStub.java November 2002
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

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

/**
 * This enables the <code>LoaderManager</code> implementation to
 * be exported to the RMI runtime. This is used for convienience 
 * so that the implementation only needs to provide the methods.
 *
 * @author Niall Gallagher
 */
abstract class LoaderStub extends UnicastRemoteObject implements LoaderManager{

   /**
    * Constructor exports the <code>LoaderManager</code> object
    * to the RMI runtime on an anonymous port number.
    *
    * @exception RemoteException if this could not be exported
    */
   protected LoaderStub() throws RemoteException{}         
}


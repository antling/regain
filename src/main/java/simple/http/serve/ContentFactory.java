/*
 * ContentFactory.java December 2002
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
 * The <code>ContentFactory</code> is used to create instances of 
 * the <code>Content</code> object. This is used in conjunction
 * with the <code>FactoryContext</code> to create content objects
 * from issued request URI strings. Because the instances are 
 * created by the <code>FactoryContext</code> a lifecycle must be
 * obeyed by the produced implementations. This lifecycle states
 * that the instance is loaded and used for a limited time.
 * <p>
 * This is required to return a <code>Content</code> instance if
 * there is a suitable match for the issued request URI. If the
 * issued request URI cannot be resolved <code>getContent</code>
 * must throw a <code>ContentException</code>. The request URI
 * issued to the <code>getContent</code> is the same as the URI
 * issued to any of the <code>Context</code> methods.
 *
 * @author Niall Gallagher
 *
 * @see simple.http.serve.Context#getContent
 */
public interface ContentFactory {

   /**
    * This will return a <code>Content</code> instance for the
    * request URI issued. If there is no match for the issued
    * URI then this will throw a <code>ContentException</code>.
    *
    * @param target the request URI for the content required
    * @param context this is the <code>Context</code> used
    *
    * @return this returns a <code>Content</code> instance
    *
    * @exception ContentException thrown if there is no match
    */
   public Content getContent(Context context, String target)
      throws ContentException;
}

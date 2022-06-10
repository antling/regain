/*
 * Path.java February 2001
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

/**
 * The <code>Path</code> represents the path part of a URI. This provides 
 * the various components of the URI path to the user. The normalization
 * of the path is the conversion of the path given into it's actual path by
 * removing the references to the parent directorys and to the current dir.
 * <p>
 * If the path that this represents is <code>/usr/bin/../etc/./README</code>
 * then the actual path, normalized, is <code>/usr/etc/README</code>. This
 * will also provide a localization from the path. The localization is there 
 * if a second extension exists within the path, for example if the path was
 * <code>/usr/bin/file.ext.ext</code>. The second extension will be parsed so
 * that all the characters before the first underscore character, '_', will
 * define the language and all the characters after the first underscore will
 * define the country, for example <code>index.en_US.html</code> will have
 * the language en and the country would be US. This should only support two 
 * character codes for both language and country. 
 *
 * @author Niall Gallagher
 *
 * @see simple.util.parse.PathParser
 */
public interface Path {

   /**
    * This will return the language that this path has taken 
    * from the locale of the path. For example a file name of
    * <code>file.en_US.extension</code> produces a language
    * of <code>en</code>. This will return null if there was 
    * no language information within the path.     
    *
    * @return returns the locale language this path contains
    */
   public String getLanguage();
    
   /**
    * This will return the country that this path has taken 
    * from the locale of the path. For example a file name of
    * <code>file.en_US.extension</code> produces a country
    * of <code>US</code>. This will return null if there was 
    * no country information within the path.     
    *
    * @return returns the locale country this path contains
    */
   public String getCountry();
    
   /**
    * This will return the extension that the file name contains.
    * For example a file name <code>file.en_US.extension</code>
    * will produce an extension of <code>extension</code>. This 
    * will return null if the path contains no file extension.
    *
    * @return this will return the extension this path contains
    */   
   public String getExtension();
    
   /**
    * This will return the full name of the file without the path.
    * As regargs the definition of the path in RFC 2396 the name
    * would be considered the last path segment. So if the path 
    * was <code>/usr/README</code> the name is <code>README</code>.
    * Also for directorys the name of the directory in the last
    * path segment is returned. This returns the name without any
    * of the path parameters. As RFC 2396 defines the path to have
    * path parameters after the path segments.
    *
    * @return this will return the name of the file in the path
    */    
   public String getName();

   /**
    * This will return the normalized path. The normalized path is
    * the path without any references to its parent or itself. So
    * if the path to be parsed is <code>/usr/../etc/./</code> the
    * path is <code>/etc/</code>. If the path that this represents
    * is a path with an immediate back reference then this will
    * return null. This is the path with all its information even
    * the parameter information if it was defined in the path.
    *
    * @return this returns the normalize path without
    *    <code>../</code> or <code>./</code>
    */
   public String getPath();

   /**
    * This method is used to break the path into individual parts
    * called segments, see RFC 2396. This can be used as an easy
    * way to compare paths and to examine the directory tree that
    * the path points to. For example, if an path was broken from
    * the string <code>/usr/bin/../etc</code> then the segments
    * returned would be <code>usr</code> and <code>etc</code> as
    * the path is normalized before the segments are extracted.
    *
    * @return return all the path segments within the directory
    */
   public String[] getSegments();

   /**
    * This will return the highest directory that exists within 
    * the path. This is used to that files within the same path
    * can be acquired. An example of that this would do given
    * the path <code>/pub/./bin/README</code> would be to return
    * the highest directory path <code>/pub/bin/</code>. The "/"
    * character will allways be the last character in the path.
    *
    * @return this method will return the highest directory
    */
   public String getDirectory();

   /**
    * This will return the path as it is relative to the issued
    * path. This in effect will chop the start of this path if
    * it's start matches the highest directory of the given path
    * as of <code>getDirectory</code>. This is useful if paths 
    * that are relative to a specific location are required. To
    * illustrate what this method will do the following example
    * is provided. If this object represented the path string
    * <code>/usr/share/rfc/rfc2396.txt</code> and the issued
    * path was <code>/usr/share/text.txt</code> then this will
    * return the path string <code>/rfc/rfc2396.txt</code>.
    *
    * @param path the path prefix to acquire a relative path
    *
    * @return returns a path relative to the one it is given
    * otherwize this method will return null 
    */
   public String getRelative(String path);

   /**
    * This will return the normalized path. The normalized path is
    * the path without any references to its parent or itself. So
    * if the path to be parsed is <code>/usr/../etc/./</code> the
    * path is <code>/etc/</code>. If the path that this represents
    * is a path with an immediate back reference then this will
    * return null. This is the path with all its information even
    * the parameter information if it was defined in the path.
    *
    * @return this returns the normalize path without
    *    <code>../</code> or <code>./</code>
    */
   public String toString();
}    

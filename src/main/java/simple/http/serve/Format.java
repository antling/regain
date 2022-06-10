/*
 * Format.java March 2002
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
 
package simple.http.serve;

/**
 * The <code>Format</code> class is used to provide a specified format to 
 * pages retrived by the <code>ResourceEngine</code>. Every instance of 
 * the <code>ResourceEngine</code> operates using a <code>Context</code>
 * object. The <code>Context</code> object provides a view of the 
 * underlying file system. Each <code>Context</code> contains a
 * <code>Format</code> object. This is used to generate HTML messages
 * for the specific resource.
 * <p>
 * The <code>Format</code> object is responsible for generating pages 
 * for specific resources and error messages. So when a web browser
 * contacts the web server that is using a <code>ResourceEngine</code> 
 * the resource engine can defer to the <code>Format</code> object. 
 * <pre>
 *    format.getContents(context, "/example/");
 * </pre>
 * This method invocation will allow the <code>Format</code> object to
 * create a directory listing for the target "/example/" relative
 * to the context path which for example on DOS could be "c:\winnt".
 *
 * @author Niall Gallagher
 */
public interface Format {

   /**
    * This is used to produce the contents of the specified resource.
    * If the path does not represent a resource then this may throw
    * a <code>FormatException</code>. If however this does represent 
    * a resource the <code>Context</code> object is used to aquire the
    * <code>File</code> object that represents the request URI path
    * on the system. This can then be used to generate a page the 
    * provides a view of the contents of the resource.
    * <p>
    * The path given is the request URI path that normally comes with
    * a HTTP/1.x request. The request URI is the string that identifies
    * the resource on the host that is required. The context is used
    * to convert that request URI into the system dependant name. The
    * request URI is similar to a UNIX path like /usr/bin/README. For
    * example if the browser requests http://some.host/pub/index.html
    * then the request URI could be /pub/index.html it also could be
    * the full URI as http://some.host/pub/index.html required by the
    * specification RFC 2616.
    *
    * @param context provides the format with a view of the system
    * @param path the request URI that came with the HTTP request
    *
    * @return this returns a byte array containing the contents of    
    * the generated page
    *
    * @exception FormatException if this cannot produce a page that
    * represents the required resource
    */
   public byte[] getContents(Context context, String path)
      throws FormatException;

   /**      
    * The HTTP protocol defines certain status codes that are to be sent
    * with descriptive message bodys, this method is used to create the
    * message body for that status code. This method will generate a
    * message body that describes the error defined by the status code.
    * See the HTTP/1.1 specification for a description of the status
    * codes, RFC 2616.
    * <p>
    * The path given is the request URI path that normally comes with
    * a HTTP/1.x request. The request URI is the string that identifies
    * the resource on the host that is required. The context is used
    * to convert that request URI into the system dependant name. The
    * request URI is similar to a UNIX path like /usr/bin/README. For
    * example if the browser requests http://some.host/pub/index.html
    * then the request URI could be /pub/index.html it also could be
    * the full URI as http://some.host/pub/index.html required by the
    * specification RFC 2616.
    * <p>
    * The implementation of the <code>getMessage</code> method should
    * recieve a report describing the error that has occured. If there
    * was no direct cause for the error then the report should not
    * provide a detailed <code>getCause</code> message, instead it 
    * can simply return an empty string. However, in the event that
    * an exception caused the error the stack track could be provided.
    *
    * @param context provides the format with a view of the system
    * @param path the request URI that came with the HTTP request
    * @param report this provides information that can be used by the
    * specific implementation to present the page    
    *
    * @return this returns a byte array containing the contents of
    * the generated page
    *
    * @exception FormatException if this cannot produce a page that
    * represents the required message
    */
   public byte[] getMessage(Context context, String path, Report report)
      throws FormatException;

   /**
    * The contents generated by this object may not be in HTML format,
    * this is used to retrive the content type. This is nessecary so
    * that if the contents generated by this <code>Format</code> is
    * not HTML that the correct MIME type is returned. This will
    * typically return strings like "text/html; charset=UTF-8".
    *
    * @return this returns the MIME type of the resulting contents
    */
   public String getMimeType();      
}


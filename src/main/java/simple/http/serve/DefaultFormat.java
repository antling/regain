/*
 * DefaultFormat.java March 2002
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

import java.io.IOException;
import java.util.Locale;
import java.io.File;

/**
 * The <code>DefaultFormat</code> object is used to provide pages
 * for the <code>FileEngine</code> that are consistant and have
 * a user specified format. This is used as the default system 
 * format when there is no explicit specification of a default
 * using the system property "simple.http.serve.format". If
 * this property is specified a class loader will attempt to 
 * load and instantiate a format of the specified type.
 * <p>
 * The <code>Format</code> of the <code>FileEngine</code> can
 * be changed from the <code>DefaultFormat</code> by simply 
 * giving the VM the class name of the desired format like
 *
 * <pre>
 * java -Dsimple.http.serve.format=demo.example.FancyFormat
 * </pre>
 *
 * The <code>FormatFactory</code> produces the system default
 * <code>Format</code> implementaion. This is used with the
 * <code>FileEngine.getInstance</code> when the context object
 * is not explicitly used. This implementation only supports 
 * the contents of directory resources. If the resource is
 * requested is not a directorys a <code>FormatException</code>
 * is thrown.
 *
 * @author Niall Gallagher
 */
final class DefaultFormat implements Format {

   /**
    * This is used to produce the contents of the specified resource.
    * The <code>Context</code> is used to aquire a <code>File</code> 
    * object that represents the request URI path on the system. This 
    * is then be used to generate a page the provides a view of the 
    * contents of the resource.   
    * <p>
    * The path given is the request URI path that normally comes with
    * a HTTP/1.x request. The request URI is the string that identifies
    * the resource on the host that is required. The context is used
    * to convert that request URI into the system dependant name. The
    * request URI is similar to a UNIX path like /usr/bin/README. For
    * example if the browser requests http://some.host/pub/index.html
    * then the request URI is /pub/index.html.
    *
    * @param context provides the format with a view of the system
    * @param target the request URI that came with the HTTP request
    *
    * @exception FormatException if there is not format possible for 
    * the requested resource
    */
   public byte[] getContents(Context context, String target)
      throws FormatException{
      if(!context.getFile(target).isDirectory()){
         throw new FormatException("No format available");
      }         
      String fixed = target;
      
      if(fixed.indexOf('?')>0){ /* remove query*/
         fixed = fixed.substring(0,
            fixed.indexOf('/')+1);
      }
      if(fixed.lastIndexOf(';')>0){ /* remove params*/
         fixed = fixed.substring(0,
            fixed.indexOf(';')+1);
      }
      return getContents(context, target, 
         !fixed.endsWith("/") && !fixed.endsWith("/."));
   }

   /**
    * This is used to produce the contents of the specified directory.
    * The <code>Context</code> is used to aquire a <code>File</code> 
    * object that represents the request URI path on the system. This 
    * is then be used to generate a page the provides a listing of the 
    * contents of the directory.
    * <p>
    * The path given is the request URI path that normally comes with
    * a HTTP/1.x request. The request URI is the string that identifies
    * the resource on the host that is required. The context is used
    * to convert that request URI into the system dependant name. The
    * request URI is similar to a UNIX path like /usr/bin/README. For
    * example if the browser requests http://some.host/pub/index.html
    * then the request URI is /pub/index.html.
    * <p>
    * Often times a web browser will request a directory resource not
    * knowing that it is a directory. This causes a problem when the
    * page that is generated contains HTTP hyperlinks to each resource
    * in that directory, much like Apache does. The problem arises 
    * when the user clicks on the resource hyperlink. Since the client
    * browser thinks it requested a HTML file it simply requests that
    * resource with the same directory root. For example say the client
    * requests the resource http://some.host/usr/bin. The web server
    * using a <code>FileEngine</code> object will aquire a listing for
    * the directory /usr/bin. All references to files in this HTML list
    * must be given the prefix bin/. So that when the browser clicks on 
    * a file, say index.html, its get http://some.host/usr/bin/index.html 
    * and not http://some.host/usr/index.html.
    *
    * @param context provides the format with a view of the system
    * @param target the request URI that came with the HTTP request
    * @param isRelative provides information as to how the directory
    * was requested by the client browser
    */
   private byte[] getContents(Context context, String target, boolean isRelative){
      String path = isRelative ? (context.getName(target)+"/"):"";
      File directory = context.getFile(target);
      String index = context.getRequestPath(target);
      String[] names = directory.list();
      
      String text = 
      "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n"+
      "<HTML><HEAD>"+
      "<TITLE>Index of "+index+"</TITLE>\n"+
      "</HEAD><BODY>" +
      "<H1>Index of "+index+"</H1>\n" +
      "<HR><TABLE>" +
      "<TR><TD><B>Name</B></TD>"+
      "<TD><B>Size</B></TD>"+
      "<TD><B>Type</B></TD></TR>\n";

      for(int i = 0; i < names.length; i++) {
         File file = new File(directory,names[i]);
         boolean isDirectory = file.isDirectory();
         String name = names[i] + (isDirectory ?"/":"");
         String size = isDirectory ? "-" : ""+file.length();
         String mime = isDirectory ? "text/html" :
            context.getMimeType("/" +name); /* needs URL */

         text += "<TR><TD><TT><A HREF=\""+path+name+"\">"+name+"</A></TT></TD>"+
            "<TD><TT>"+size+"</TT></TD><TD><TT>"+mime+"</TT></TD></TR>\n";       
      }       
      return  getBytes(text +"</TABLE><HR>" +      
       "</BODY></HTML>");
   }

   /**      
    * The HTTP protocol defines certain status codes the are to be sent
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
    * then the request URI is /pub/index.html.
    *
    * @param context provides the format with a view of the system
    * @param target the request URI that came with the HTTP request
    * @param report this provides information that can be used by the
    * specific implementation to present the page    
    *
    * @return this returns a HTML description of the error message
    * that caused the 
    */
   public byte[] getMessage(Context context, String target, Report report){
      return  getBytes("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n"+
         "<HTML><HEAD><TITLE>"+report.getCode() + " "+report.getText()+"</TITLE>\n"+
         "</HEAD><BODY><H1>"+report.getText()+"</H1>"+
         "<PRE>The requested URL <i>"+target+"</i></PRE>"+
        "<HR><PRE>"+report.getCause()+"</PRE></BODY></HTML>");   
   }

   /**
    * This will convert the given string object in to UTF-8 format so
    * that it can be returned as an array of bytes. This will always
    * work, as the Java Language Specification ensures that UTF-8 is
    * always supported. See <code>java.nio.charset.Charset</code>
    * for details on the charset encodings supported.
    *
    * @param text the string object that is converted into UTF-8
    * 
    * @return this will always return the string in the UTF-8 format
    */
   private byte[] getBytes(String text){
      try {
         return text.getBytes("utf-8");
      }catch(IOException never) {
         return null;
      }
   }


   /**
    * The contents generated by this object may not be in HTML format,
    * this is used to retrive the content type. This is nessecary so
    * that if the contents generated by this <code>Format</code> is
    * not HTML that the correct MIME type is returned. 
    *
    * @return this returns the MIME type of the resulting contents
    */
   public String getMimeType(){      
      return "text/html; charset=utf-8";
   }
}

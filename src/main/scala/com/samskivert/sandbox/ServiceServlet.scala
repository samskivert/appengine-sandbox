//
// Dictionopolis - words, wizards and whimsy
// Copyright © 2011-2012 Three Rings Design, Inc.

package com.samskivert.sandbox

import javax.servlet.http.{HttpServletRequest => HSRequest, HttpServletResponse => HSResponse}

/**
 * Implements a rudimentary service which provides a simple string response.
 */
abstract class ServiceServlet (logName :String) extends AbstractServlet(logName)
{
  override type RES = String

  override def writeOutput (rsp :HSResponse, result :String) = {
    rsp.setContentType("text/plain; charset=UTF-8");
    rsp.getOutputStream.write(result.getBytes("UTF8"))
    // rsp.getWriter.print(result)
  }
}

//
// $Id$

package com.samskivert.sandbox

/**
 * Echos the post contents back to the requester.
 */
class EchoServlet extends ServiceServlet("echo")
{
  override def process (ctx :Context) = {
    ctx.args match {
      case Seq("path", _*) => logret(ctx.args.mkString("/"))
      case Seq("body") => {
        _log.info("REQ", "text", ctx.body, "utf8hex", Digester.tohex(ctx.bodyBytes))
        logret(ctx.body)
      }
      case _ => logret("Must request /path or /body, got " + ctx.args.mkString("/"))
    }
  }

  private def logret (res :String) = {
    _log.info("RSP", "text", res, "utf8hex", Digester.tohex(res.getBytes("UTF8")))
    res
  }
}

//
// Dictionopolis - words, wizards and whimsy
// Copyright © 2011-2012 Three Rings Design, Inc.

package com.samskivert.sandbox

import javax.servlet.http.HttpServlet
import javax.servlet.http.{HttpServletRequest => HSRequest, HttpServletResponse => HSResponse}
import scala.io.{Codec, Source}

import com.google.appengine.api.users.UserServiceFactory

/**
 * Defines the basic framework in which our service and HTML servlets are processed.
 */
abstract class AbstractServlet (logName :String) extends HttpServlet
{
  import HSResponse._

  /** An exception that can be thrown to abort processing and return an error. */
  case class HttpException (code :Int, message :String) extends Exception(message)

  /** Encapsulates the metadata with a service request. */
  trait Context {
    /** Any arguments supplied via path info. */
    def args :Seq[String]
    /** The http request. */
    def req :HSRequest
    /** The http response. */
    def rsp :HSResponse
    /** The body of the request, converted to a string. */
    def body :String
    /** The raw bytes of the body of the request. */
    def bodyBytes :Array[Byte]
  }

  /** Defines the output type generated by this servlet. */
  type RES

  /** Performs the actual work of this servlet. */
  def process (ctx :Context) :RES

  /** Writes the output of `process` (if any) to the servlet response. */
  def writeOutput (rsp :HSResponse, result :RES) :Unit

  /** Returns true if access to this servlet is restricted to admins. Defaults to false. */
  def requireAdmin = false

  // various error throwing convenience methods
  protected def errBadRequest (errmsg :String) =    error(SC_BAD_REQUEST, errmsg)
  protected def errInternalError (errmsg :String) = error(SC_INTERNAL_SERVER_ERROR, errmsg)
  protected def errForbidden (errmsg :String) =     error(SC_FORBIDDEN, errmsg)
  protected def errNotFound (errmsg :String) =      error(SC_NOT_FOUND, errmsg)
  protected def error (code :Int, errmsg :String) = throw new HttpException(code, errmsg)

  // handles the processing of the servlet and access control checks
  override protected def doGet (req :HSRequest, rsp :HSResponse) = doProcess(req, rsp)
  override protected def doPost (req :HSRequest, rsp :HSResponse) = doProcess(req, rsp)

  protected def doProcess (req :HSRequest, rsp :HSResponse) {
    if (!requireAdmin || _usvc.isUserAdmin) process(req, rsp)
    else rsp.sendRedirect(_usvc.createLoginURL(req.getRequestURI))
  }

  protected def process (req :HSRequest, rsp :HSResponse) {
    var ctx :Context = null
    try {
      ctx = mkContext(req, rsp)
      writeOutput(rsp, process(ctx))
    } catch {
      case he :HttpException => rsp.sendError(he.code, he.getMessage)
      case e => {
        _log.warning("Request failure", "url", req.getRequestURI, e)
        rsp.sendError(SC_INTERNAL_SERVER_ERROR);
      }
    }
  }

  protected class ContextImpl (
    val args :Seq[String],
    val req  :HSRequest,
    val rsp  :HSResponse) extends Context {
    lazy val body = {
      _log.info("Decoding request " + reqCodec.name)
      // Source.fromInputStream(req.getInputStream)(reqCodec).mkString
      new String(bodyBytes, reqCodec.name)
    }
    lazy val bodyBytes = req.getIntHeader("Content-Length") match {
      case length if (length > 0) =>
        val data = new Array[Byte](length)
        val in = req.getInputStream
        try {
          val read = in.read(data)
          if (read != data.length) _log.warning("Failed to read entire request",
                                                "length", data.length, "got", read)
        } finally in.close
        data
      case _ => new Array[Byte](0)
    }

    private def reqCodec = req.getCharacterEncoding match {
      case null => Codec(Codec.UTF8)
      case enc => Codec(enc)
    }
  }

  protected def mkContext (req :HSRequest, rsp :HSResponse) :Context =
    new ContextImpl(parseArgs(req), req, rsp)

  protected def parseArgs (req :HSRequest) = req.getPathInfo match {
    case null | "" => Array[String]()
    case info => info.substring(1).split("/")
  }

  protected val _log = new Logger(logName)
  protected val _usvc = UserServiceFactory.getUserService
}

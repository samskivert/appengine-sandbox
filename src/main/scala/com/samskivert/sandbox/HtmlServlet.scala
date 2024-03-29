//
// Dictionopolis - words, wizards and whimsy
// Copyright © 2011-2012 Three Rings Design, Inc.

package com.samskivert.sandbox

import javax.servlet.http.HttpServletResponse
import scala.xml.{Node, Text, XML}
import scala.xml.dtd.{DocType, SystemID}

/**
 * Handles shared bits for servlets that process request args and generate an HTML response.
 */
abstract class HtmlServlet (logName :String) extends AbstractServlet(logName)
{
  override type RES = Node

  /** Returns the title of the HTML page generated by this servlet. */
  def title = logName

  /** Performs the primary work of this service.
   * @return an XML tree wrapped in a `<body>` tag. */
  def process (ctx :Context) :Node

  override def writeOutput (rsp :HttpServletResponse, result :Node) = {
    val page = (<html><head>
                <title>{title}</title>
                <link rel="stylesheet" type="text/css" href="/styles.css"/>
                </head>{result}</html>)
    rsp.setContentType("text/html; charset=UTF-8");
    XML.write(rsp.getWriter, page,
              "utf-8", false, DocType("html", SystemID("about:legacy-compat"), Nil))
  }

  // various HTML generating helper methods
  protected def url (text :Any, root :String, args :Any*) = {
    val href = root + args.mkString("/")
    <a href={href}>{text.toString}</a>
  }

  protected def div (clazz :String, body :Traversable[Node]) = <div class={clazz}>{body}</div>

  protected def header (text :String) = div("header", new Text(text))

  protected def table (rows :Traversable[Node]) = <table>{rows}</table>
  protected def toRow (cols :Seq[Any]) :Node = <tr>{cols.map(c => <td>{c.toString}</td>)}</tr>
  protected def toRow (tup :(String,String)) :Node = toRow(Seq(tup._1, tup._2))
  protected def toRow (tup :(String,String,String)) :Node = toRow(Seq(tup._1, tup._2, tup._3))
}

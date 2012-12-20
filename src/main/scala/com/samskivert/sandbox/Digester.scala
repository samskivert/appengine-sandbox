//
// Dictionopolis - words, wizards and whimsy
// Copyright © 2011-2012 Three Rings Design, Inc.

package com.samskivert.sandbox

import java.security.MessageDigest

/**
 * Hash digest related utilities.
 */
object Digester
{
  /** Returns the SHA1 digest of the supplied text as a hex-encoded string. */
  def sha1hex (text :String) = tohex(hashhex(_sha1, text))

  /** Returns the supplied bytes as a hex encoded string. */
  def tohex (data :Array[Byte]) = data.flatMap(b => {
    val v = if (b >= 0) b else b + 256
    Array(HexDigs.charAt(v/16), HexDigs.charAt(v%16))
  }).mkString("")

  private def hashhex (digest :MessageDigest, text :String) =
    digest.clone.asInstanceOf[MessageDigest].digest(text.getBytes("UTF-8"))

  private val _sha1 = MessageDigest.getInstance("SHA-1")
  private final val HexDigs = "0123456789abcdef"
}

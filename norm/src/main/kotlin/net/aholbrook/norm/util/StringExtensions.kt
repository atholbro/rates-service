/* ktlint-disable filename */
package net.aholbrook.norm.util

fun String.prependIfNotBlank(prepend: String?, delim: String = ""): String =
    if (prepend.isNullOrBlank()) { this } else { "$prepend$delim$this" }

package com.bulkread

/**
 * Well this is a simple map which maintains the meta information about
 * what is there in the chunks of blocks.
 * More specifically fileName as key and value as list of id's it contains
 */
import scala.collection.immutable.TreeMap

case class FileSystem(dictionary:Map[String,List[String]])


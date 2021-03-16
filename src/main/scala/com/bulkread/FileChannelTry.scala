package com.bulkread

import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.file.Paths
import java.nio.file.Path
import java.nio.charset.StandardCharsets

@main def readFileChannel = 
  val path = Paths.get("test_small.csv")
  val fileChannel = FileChannel.open(path)
  //9721,"PK" 10chars per line for 20 lines it'll be 200Bytes
  val buf =  ByteBuffer.allocate(20)
  fileChannel.read(buf)
  println(s"Before flip.. ${buf.hasRemaining} position ${buf.position}")
  buf.flip
  println(s"after flip.. ${buf.hasRemaining} position ${buf.position}")
  val arr = new Array[Byte](10)
  buf.get(arr)
  println(arr.map(_.toChar).mkString)
  buf.get(arr)
  println(arr.map(_.toChar).mkString)
  println(s"buf.hasRemaining ${buf.hasRemaining} ")
  buf.clear
  val buf2 =  ByteBuffer.allocate(20)
  val x = fileChannel.read(buf2)
  buf2.flip
  println(s"return is $x - ${StandardCharsets.UTF_8.decode(buf2).toString()}")
  println("Done!!")
  buf2.clear
  fileChannel.close


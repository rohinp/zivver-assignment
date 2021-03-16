package com.bulkread

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.io.FileOutputStream;

import scala.util.chaining._
import SeqFileChannel._


def createFilesv1:String => Operation => Seq[TmpFile] = 
  file => level => 
    val path = Paths.get(file)
    val fileChannel = FileChannel.open(path)
    val fileSize = fileChannel.size
    val buffer = ByteBuffer.allocate(CHUNK_SIZE)
    val lineBuffer = ByteBuffer.allocate(RECORD_SIZE)
    val seed = FileChannelFlow.Continue(fileChannel,Map())
    program(
      _.groupBy(_.productId)
      .view
      .mapValues(_.map(_.countryCode).flatten.mkString(","))
      .toVector
      .sortBy(_._1)
      .map(t => s"${t._1},${t._2}")    
    ).pipe(p => repeat(seed ,p)).result.run(seed)

    Seq()


@main def fileBreaker1 =
    createFilesv1("test_small.csv")(Operation.Split_Map)
    //cleanUpTempDirectory

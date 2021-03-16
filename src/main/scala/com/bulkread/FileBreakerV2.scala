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
    program(r => {
      r.groupBy(_.productId).view
      .mapValues(_.map(_.countryCode))
      .toVector
      .sortBy(_._1)
    }).pipe(p => repeat1(seed ,p)).run(seed).tap(md => println(md._1.size))
    Seq()


@main def fileBreaker1 =
    createFilesv1("test_small.csv")(Operation.Split_Map)
    //cleanUpTempDirectory

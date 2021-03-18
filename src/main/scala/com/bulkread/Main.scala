package com.bulkread

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.io.FileOutputStream;

import scala.util.chaining._
import SeqFlow._

//import what configuration to use here
import SmallFileConfiguration.conf
//import LargeFileConfiguration.conf

def start:Unit = 
    val path = Paths.get(conf.inputFile)
    val fileChannel = FileChannel.open(path)
    val fileSize = fileChannel.size
    val buffer = ByteBuffer.allocate(conf.chunkSize)
    val lineBuffer = ByteBuffer.allocate(conf.recordSize)
    val seed = FlowControl.Continue(fileChannel,MetaData(Vector(),Map(), Set()))
    (for {
      md <- readChunkGroupSortAndWrite(
              _.groupBy(_.productId)
              .view
              .mapValues(_.map(_.countryCode).flatten.mkString(","))
              .toVector
              .sortBy(_._1)
              .map(t => s"${t._1},${t._2}\n")
            ).pipe(p => repeat(seed ,p)).result
      _ = println(md.operationsDone)
      _ <- shuffleAndReduce(md, "result.txt")
    } yield ()).run(seed)


@main def run =
    start

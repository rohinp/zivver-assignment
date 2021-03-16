package com.bulkread

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.io.FileOutputStream;

import scala.util.chaining._

val _1KB = 1024
// 96 MB which is approx 10Million records, given the record size
val DEFAULT_FILE_CHUNK = 96

val ROOT_DIRECTORY = "."
val OUTPUT_DIRECTORY = s"$ROOT_DIRECTORY/output"
val TMP_DIRECTORY = s"$OUTPUT_DIRECTORY/tmp"
val RECORD_SIZE1 = 10 //Chars/Bytes this can change if we mess with the id :-)
val CHUNK_SIZE1 = RECORD_SIZE1 * 100000 //RECORD_SIZE1 * 10000000 //size of 10 million records, it goes roughly little less then 100MB

enum Operation:
  case Split_Map
  case Sorter
  case Combine
  case Reduce

case class TmpFile(path:String, fileToCreate:String, level:Int)

def tmpFilePrefix:String => Operation => Int => String = fileName => level => counter => 
  s"$fileName-$level-$counter.csv"

def createFiles:String => Operation => Seq[TmpFile] = 
  file => level => 
    val path = Paths.get(file)
    val fileChannel = FileChannel.open(path)
    val fileSize = fileChannel.size
    val buffer = ByteBuffer.allocate(CHUNK_SIZE1)
    val lineBuffer = ByteBuffer.allocate(RECORD_SIZE1)
    var counter  = 0
    while(fileChannel.read(buffer) != -1){
      buffer.flip
      val fc = new FileOutputStream(new File(s"$TMP_DIRECTORY/${tmpFilePrefix(file)(level)(counter)}")).getChannel();
      fc.write(buffer);
      fc.close();
      buffer.clear
      counter = counter + 1
    }
    Seq()

def cleanUpTempDirectory =
    java.util.Arrays.stream(new File(TMP_DIRECTORY).listFiles())
    .forEach(_.delete)


@main def fileBreaker =
    //createFiles("test_small.csv")(Operation.Split_Map)
    cleanUpTempDirectory

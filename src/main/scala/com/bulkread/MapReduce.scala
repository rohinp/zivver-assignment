package com.bulkread

import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.io.FileOutputStream
import java.util.UUID
import java.io.FileOutputStream;
import java.io.File
import scala.util.chaining._

//Model
case class ProductRecord(productId:Int, countryCode:Vector[String]):
  def format:String = s"$productId -> [${countryCode.mkString(",")}]"

//Operations of the the Map redice implimenation
enum Operation:
  case SplitAndGroup
  case ShuffelAndReduce

/**
  * Metadata is like a file system info, answers quations like 
  * 1. list of files and 
  * 2. id X lies in Y file. 
  * 3. Operation stage
  **/
case class MetaData(listOfFiles:Vector[String], fileMap:Map[Int,Vector[String]], operationsDone:Set[Operation]):
  def entry(id:Int,fileName:String):MetaData = 
    this.copy(
      fileMap = this.fileMap.get(id).fold(fileMap + (id -> Vector(fileName)))(v => fileMap + (id -> v.appended(fileName).distinct))
    )
  def file(name:String):MetaData =
    this.copy(listOfFiles = this.listOfFiles.appended(name).distinct)

enum FlowControl:
  case Continue[A](file:A, metaData:MetaData)
  case Stop(metaData:MetaData)

case class SeqFlow[A](run:FlowControl => (A,FlowControl)):
  import SeqFlow._
  import FlowControl._
  //primitive combinators
  def flatMap[B](f:A => SeqFlow[B]):SeqFlow[B] = SeqFlow{
    _.fold(stop => {
      val (a, flow) = run(stop)
      f(a).run(flow)}){
        continue => {
        val (a, flow) = run(continue)
        f(a).run(flow)
      }}
    }
  def map[B](f:A => B):SeqFlow[B] = this.flatMap(a => pure(f(a)))

object SeqFlow:
  import FlowControl._
  //Contextual function to pass configuration implicitly
  type FlowWithConf[A] = Configuration ?=> SeqFlow[A]

  //more primitive combinators
  def pure[A](a:A):SeqFlow[A] = SeqFlow(s => (a,s))
  def getMeta[MetaData] = SeqFlow{_.fold(s => (s.metaData,s))(c => (c.metaData,c))}

  extension (flow:FlowControl) def fold[B,F](stop:Stop => B)(f:Continue[F] => B):B = 
    flow match
      case s:Stop => stop(s)
      case c => f(c.asInstanceOf[Continue[F]])

  //derived combinartors
  def next:FlowWithConf[ByteBuffer] = SeqFlow{
    _.fold[(ByteBuffer, FlowControl),FileChannel](s => (ByteBuffer.allocate(0), s)){
      c => 
        val conf = summon[Configuration]
        val buf = ByteBuffer.allocate(conf.chunkSize)
        if(c.file.isOpen)
          if(c.file.read(buf) == -1)
            buf.clear
            (buf,Stop(c.metaData)).tap(_ => c.file.close) 
          else 
            buf.flip
            (buf, c).tap(_ => buf.clear)
        else (buf, Stop(c.metaData))
    }
  }

  def toRecords:ByteBuffer => FlowWithConf[Vector[ProductRecord]] = 
    buf => SeqFlow{
      case Stop(md) => (Vector.empty[ProductRecord], Stop(md))
      case c@Continue(fc, md) => 
        val conf = summon[Configuration]
        val arr = new Array[Byte](conf.recordSize)
        val fileName = UUID.randomUUID.toString
        def loop(acc:Vector[ProductRecord],metaData:MetaData):(MetaData,Vector[ProductRecord]) = 
          if(!buf.hasRemaining)
            (metaData,acc)
          else 
            buf.get(arr)
            arr.map(_.toChar).mkString.split(',').toList match
              case id::country::Nil => 
                loop(acc.appended(ProductRecord(id.toInt, Vector(country.trim))), metaData.entry(id.toInt, fileName))
              case _ => 
                (metaData,acc)
        val (newMetaData,records) = loop(Vector(), md.file(fileName))

        (records, c.copy(metaData = newMetaData))
    }

  import scala.util.control.TailCalls._
  def repeat(flow:FlowControl , program:SeqFlow[MetaData]):TailRec[SeqFlow[MetaData]] = 
    flow.fold(s => done(pure(s.metaData)))(c => {
      val r = program.run(c)
      tailcall(repeat(r._2,program))
    })

  /*
  not safe as recursion happens on stack, unlike the above implementation where trempolining comes into picture 
  def repeat1(flow:FlowControl , program:SeqFlow[MetaData]):SeqFlow[MetaData] = 
    flow.fold(s => pure(s.metaData))(c => {
      val r = program.run(c)
      repeat1(r._2,program)
    }) 
    */
  
    
  def writeRecords:Vector[String] => MetaData => FlowWithConf[MetaData] = 
    records => metaData => SeqFlow(
      _.fold(s => (s.metaData, s))(c => {
        val conf = summon[Configuration]
        val file = metaData.listOfFiles.last
        val f = s"${conf.tmpPath}/$file.csv"
        val channel = new FileOutputStream(new File(f)).getChannel();
        val strBytes = records.mkString.getBytes()
        val buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();
        channel.write(buffer);
        channel.close();
        buffer.clear
        println(s"file written $f")
        (c.metaData, c)
      })
    )
  
  def shuffleAndReduce(outputFile:String):SeqFlow[Unit] =
      SeqFlow {
        ???
      }
  def readChunkGroupSortAndWrite(f:Vector[ProductRecord] => Vector[String]):FlowWithConf[MetaData] = 
    for
      buf <- next
      records <- toRecords(buf)
      md <- getMeta 
      _ <- writeRecords(f(records))(md)
    yield md
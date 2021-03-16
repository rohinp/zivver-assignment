package com.bulkread

import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.io.FileOutputStream
import java.util.UUID

import scala.util.chaining._

case class InitRecord(productId:Int, countryCode:String)

val RECORD_SIZE = 9 //Chars/Bytes this can change if we mess with the id :-)
val CHUNK_SIZE = RECORD_SIZE * 1000000 //RECORD_SIZE * 10000000 //size of 10 million records, it goes roughly little less then 100MB
type MetaData = Map[String, Vector[Int]]

enum FileChannelFlow:
  case Continue(file:FileChannel, metaData:MetaData)
  case Stop(metaData:MetaData)

//case class ChannelRecord(file:FileChannel, metaData:MetaData)

case class SeqFileChannel[A](run:FileChannelFlow => (A,FileChannelFlow)):
  import SeqFileChannel._
  import FileChannelFlow._

  def flatMap[B](f:A => SeqFileChannel[B]):SeqFileChannel[B] = SeqFileChannel{
    _.fold(stop => {
      val (a, flow) = run(stop)
      f(a).run(flow)}){
        continue => {
        val (a, flow) = run(continue)
        f(a).run(flow)
      }}
    }
  def map[B](f:A => B):SeqFileChannel[B] = this.flatMap(a => pure(f(a)))

object SeqFileChannel:
  import FileChannelFlow._
  def pure[A](a:A):SeqFileChannel[A] = SeqFileChannel(s => (a,s))
  def get[FileChannelFlow] = SeqFileChannel{_.fold(s => (s,s))(c => (c,c))}

  extension (flow:FileChannelFlow) def fold[B](stop:Stop => B)(f:Continue => B):B = 
    flow match
      case s:Stop => stop(s)
      case c:Continue => f(c)
  
  //need to move the values to configuration later
  def next:SeqFileChannel[ByteBuffer] = SeqFileChannel{
    case Stop(md) => (ByteBuffer.allocate(0), Stop(md))
    case c@Continue(fc, md) => 
      val buf = ByteBuffer.allocate(CHUNK_SIZE)
      if(fc.isOpen)
        if(fc.read(buf) == -1)
          buf.clear
          (buf,Stop(md)).tap(_ => fc.close) 
        else 
          buf.flip
          (buf, c).tap(_ => buf.clear)
      else (buf, Stop(md))
  }

  def toRecords:ByteBuffer => SeqFileChannel[Vector[InitRecord]] = 
    buf => SeqFileChannel{
      case Stop(md) => (Vector.empty[InitRecord], Stop(md))
      case c@Continue(fc, md) => 
        val arr = new Array[Byte](RECORD_SIZE)
        def loop(acc:Vector[InitRecord],ids:Vector[Int]):(Vector[Int],Vector[InitRecord]) = 
          if(!buf.hasRemaining)
            (ids,acc)
          else 
            buf.get(arr)
            arr.map(_.toChar).mkString.split(',').toList match
              case id::country::Nil => 
                loop(acc.appended(InitRecord(id.toInt, country)), ids.appended(id.toInt))
              case _ => 
                (ids,acc)
        val (ids,records) = loop(Vector(), Vector())
        val fileName = UUID.randomUUID.toString
        (records, c.copy(metaData = c.metaData + (fileName -> ids.distinct.sorted)))
    }

  import scala.util.control.TailCalls._
  def repeat(flow:FileChannelFlow , program:SeqFileChannel[MetaData]):TailRec[SeqFileChannel[MetaData]] = 
    flow.fold(s => done(pure(s.metaData)))(c => {
      val r = program.run(c)
      tailcall(repeat(r._2,program))
    })

  /*
  not safe as recursion happens on stack, unlike the above implementation where trempolining comes into picture 
  def repeat1(flow:FileChannelFlow , program:SeqFileChannel[MetaData]):SeqFileChannel[MetaData] = 
    flow.fold(s => pure(s.metaData))(c => {
      val r = program.run(c)
      repeat1(r._2,program)
    }) 
    */
  
    
  def writeRecords[A]:A => SeqFileChannel[MetaData] = 
    records => SeqFileChannel(
      _.fold(s => (s.metaData, s))(c => (c.metaData, c))
    )
  

  def program(f:Vector[InitRecord] => Vector[(Int, Vector[String])]):SeqFileChannel[MetaData] = 
    for
      buf <- next
      records <- toRecords(buf)
      md <- writeRecords(f(records))
    yield md
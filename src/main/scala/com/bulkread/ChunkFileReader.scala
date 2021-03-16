/* package com.bulkread

import scala.util.chaining._
import scala.io.Source.fromFile
import java.io.File

enum Record:
  case ValidRecord(prodictId:Int, availableIn:String)
  case InvalidRecord(msg:String)

object Record:
  import Record._

  def makeRecord(line:String):Record =
    line.split(",").toList match
      case (x::y::Nil) if x.toIntOption.nonEmpty => ValidRecord(x.toInt, y)
      case x => InvalidRecord(s"Invalid record: $x")

def readFile(fileName:String) = 
  val file = new File(fileName)
  println(s"file.getTotalSpace ${file.getTotalSpace}")
  println(s"file.getParent ${file.getParent}")
  val source = fromFile(file)

  source.getLines
    .map(Record.makeRecord)
    .map{
      case r@Record.ValidRecord(id, value) => (id, r)
      case in => (-1, in)
    }.toList
    .groupBy(_._1)
    .view.mapValues(_.map(_._2))
    .foreach(println)

@main def fileRead =
  readFile("test_small.csv") */
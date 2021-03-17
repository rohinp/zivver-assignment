package com.bulkread

case class Configuration(
  recordSize:Int,
  chunkSize:Int,
  randomIDMax:Int,
  inputFile:String,
  outputPath:String = "./output",
  tmpPath:String = "./output/tmp",
)

val smallFieConf = Configuration(
  recordSize = 9,
  chunkSize = 9 * 1000000,
  randomIDMax = 1000,
  inputFile = "test_small.csv"
)

val bigFileConf = Configuration(
  recordSize = 10,
  chunkSize = 10 * 10000000, //around 100MB chunk
  randomIDMax = 10000,
  inputFile = "test.csv"
)
package com.bulkread

enum Record:
  case SingleRecord(id:Int, countryCodes:String)
  case GroupRecord(id:Int, countryCodes:Seq[String])
  case EOF
  case InvalidRecord(msg:String)

  def show = this match
    case GroupRecord(i,cs) => s"$i,${cs.mkString(",")}"
    case SingleRecord(i,c) => s"$i,$c"
    case _ => ""
  

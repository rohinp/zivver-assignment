package com.bulkread
import java.io._ 

val RANDOM_ID_MAX = 1000 //999

val countryCode = Vector(
  "AF","AX","AL","DZ","AS","AD","AO","AI","AQ","AG","AR","AM","AW","AU","AT","AZ","BS","BH","BD","BB","BY","BE","BZ","BJ","BM","BT","BO","BQ","BA","BW","BV","BR","IO","BN","BG","BF","BI","KH","CM","CA","CV","KY","CF","TD","CL","CN","CX","CC","CO","KM","CG","CD","CK","CR","CI","HR","CU","CW","CY","CZ","DK","DJ","DM","DO","EC","EG","SV","GQ","ER","EE","ET","FK","FO","FJ","FI","FR","GF","PF","TF","GA","GM","GE","DE","GH","GI","GR","GL","GD","GP","GU","GT","GG","GN","GW","GY","HT","HM","VA","HN","HK","HU","IS","IN","ID","IR","IQ","IE","IM","IL","IT","JM","JP","JE","JO","KZ","KE","KI","KP","KR","KW","KG","LA","LV","LB","LS","LR","LY","LI","LT","LU","MO","MK","MG","MW","MY","MV","ML","MT","MH","MQ","MR","MU","YT","MX","FM","MD","MC","MN","ME","MS","MA","MZ","MM","NA","NR","NP","NL","NC","NZ","NI","NE","NG","NU","NF","MP","NO","OM","PK","PW","PS","PA","PG","PY","PE","PH","PN","PL","PT","PR","QA","RE","RO","RU","RW","BL","SH","KN","LC","MF","PM","VC","WS","SM","ST","SA","SN","RS","SC","SL","SG","SX","SK","SI","SB","SO","ZA","GS","SS","ES","LK","SD","SR","SJ","SZ","SE","CH","SY","TW","TJ","TZ","TH","TL","TG","TK","TO","TT","TN","TR","TM","TC","TV","UG","UA","AE","GB","US","UM","UY","UZ","VU","VE","VN","VG","VI","WF","EH","YE","ZM","ZW"
)

def createRandomRecord:(Int, String) =
  val id = scala.util.Random.nextInt(RANDOM_ID_MAX)
  val countryCodeIndex = scala.util.Random.nextInt(countryCode.length)
  (id, countryCode(countryCodeIndex))

//To make sure all records are of consistent length
//In case we decide to read in blocks and want to calculate the block size
def padding:Int => String = i => 
  List.fill(RANDOM_ID_MAX.toString.length - 1 - i.toString.length)(0).mkString ++ i.toString

def writeToFile(filename: String, numberOfRecords:Int): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    for (_ <- 0 until numberOfRecords)
        val (id, value) = createRandomRecord
        bw.write(s"""${padding(id)},"$value"\n""")
    bw.close()
}

@main def randomData =
  //This generates a little less then 10GB file
  //writeToFile("test.csv", 1000000000)
  //94MB file
  writeToFile("test_small.csv", 10000000)
  println("Done!")

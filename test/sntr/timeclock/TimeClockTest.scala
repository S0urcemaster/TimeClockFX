package sntr.timeclock

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


object TimeClockTest {
	
	val dtf = DateTimeFormat.forPattern("dd.MM.yy HH:mm")
	
	def testUnserialize {
		
		val list = TimeClock.unserialize
		
		list.foreach(f => {println(f)})
		
	}
	
	def testSerialize {
		val cal1 = DateTime.parse("30.11.13 11:00", dtf)
		
		val cal2 = DateTime.parse("30.11.13 13:14", dtf)
		
		val list = List(
				new DataSet {come = cal1; go = Some(cal2)}
				)
				
		TimeClock.serialize(list)
	}
	
	def testLine2DataSet {
		val dataSet = TimeClock.line2DataSet("30.13:4 - 30.13:9", 11, 2013)
		println(dataSet)
	}
	
	def testDataSet2Line {
		
//		val cal = DateTime.parse("301113 1314", dtf)
//		
//		val dataSet = new DataSet
//		dataSet.come = cal.getTime()
//		cal.add(Calendar.MINUTE, 5)
//		dataSet.go = Option(cal.getTime())
//		val line = TimeClock.dataSet2Line(dataSet)
//		println(line)
//		assert(line == "30 13 4 - 30 13 9")
	}
	
	def testGenerateFilename {
		val filename = TimeClock.generateFilename
		println("generateFilename: " +filename)
		assert(filename != "1312.times")
	}
	
	def main(args: Array[String]): Unit = {
		
		
		testSerialize
		testUnserialize
		//testLine2DataSet
		//testDataSet2Line
		//testGenerateFilename
		
	}

}
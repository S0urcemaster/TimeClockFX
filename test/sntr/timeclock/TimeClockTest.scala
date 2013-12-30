package sntr.timeclock

import java.util.Date
import java.text.DateFormat
import java.util.Calendar

object TimeClockTest {
	
	def testUnserialize {
		
		val list = TimeClock.unserialize
		
		list.foreach(f => {println(f)})
		
	}
	
	def testSerialize {
		
		val cal1 = Calendar.getInstance()
		cal1.set(13, 11, 30, 11, 0)
		
		val cal2 = Calendar.getInstance()
		cal2.set(13, 11 ,30 ,14 ,14)
		
		val list = List(
				new DataSet {come = new Date(cal1.getTimeInMillis()); go = Option(new Date(cal2.getTimeInMillis()))}
				)
				
		TimeClock.serialize(list)
	}
	
	def testLine2DataSet {
		val dataSet = TimeClock.line2DataSet("30.13:4 - 30.13:9", 11, 2013)
		println(dataSet)
	}
	
	def testDataSet2Line {
		
		val cal = Calendar.getInstance
		cal.set(2013, 11, 30, 13, 4)
		
		val dataSet = new DataSet
		dataSet.come = cal.getTime()
		cal.add(Calendar.MINUTE, 5)
		dataSet.go = Option(cal.getTime())
		val line = TimeClock.dataSet2Line(dataSet)
		println(line)
		assert(line == "30 13 4 - 30 13 9")
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
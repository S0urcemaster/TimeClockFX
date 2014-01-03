package sntr.timeclock

import java.text.SimpleDateFormat
import java.util.Date

object GenerateData {

	def main(args: Array[String]): Unit = {
		
		val df = new SimpleDateFormat("dd.MM.yyyy-hh:mm")
		
		val list = List(
				new DataSet {come = df.parse("01.01.2014-16:54"); go = Some(df.parse("01.01.2014-16:59"))},
				new DataSet {come = df.parse("02.01.2014-14:54"); go = Some(df.parse("02.01.2014-15:59"))},
				new DataSet {come = df.parse("02.01.2014-16:54"); go = Some(df.parse("02.01.2014-17:59"))}
				)
				
		TimeClock.serialize(list)
		
	}

}
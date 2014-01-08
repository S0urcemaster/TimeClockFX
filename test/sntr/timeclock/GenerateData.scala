package sntr.timeclock

import java.text.SimpleDateFormat
import java.util.Date
import org.joda.time.format.DateTimeFormat

object GenerateData {

	def main(args: Array[String]): Unit = {
		
		val dtf = DateTimeFormat.forPattern("dd.MM.yy HH:mm")
		
		val list = List(
				new DataSet {come = dtf.parseDateTime("01.01.14 16:54"); go = Some(dtf.parseDateTime("01.01.14 16:59"))},
				new DataSet {come = dtf.parseDateTime("02.01.14 14:54"); go = Some(dtf.parseDateTime("02.01.14 15:59"))},
				new DataSet {come = dtf.parseDateTime("02.01.14 16:54"); go = Some(dtf.parseDateTime("02.01.14 17:59"))},
				new DataSet {come = dtf.parseDateTime("02.01.14 19:22"); go = Some(dtf.parseDateTime("03.01.14 00:15"))},
				new DataSet {come = dtf.parseDateTime("03.01.14 00:45"); go = Some(dtf.parseDateTime("03.01.14 03:40"))},
				new DataSet {come = dtf.parseDateTime("04.01.14 16:14"); go = Some(dtf.parseDateTime("04.01.14 19:55"))},
				new DataSet {come = dtf.parseDateTime("04.01.14 22:20"); go = Some(dtf.parseDateTime("05.01.14 01:10"))},
				new DataSet {come = dtf.parseDateTime("05.01.14 02:01"); go = Some(dtf.parseDateTime("05.01.14 02:48"))},
				new DataSet {come = dtf.parseDateTime("05.01.14 14:57"); go = Some(dtf.parseDateTime("05.01.14 18:00"))},
				new DataSet {come = dtf.parseDateTime("05.01.14 23:19"); go = Some(dtf.parseDateTime("05.01.14 23:40"))},
				new DataSet {come = dtf.parseDateTime("06.01.14 15:21"); go = Some(dtf.parseDateTime("06.01.14 17:53"))},
				new DataSet {come = dtf.parseDateTime("06.01.14 19:15"); go = Some(dtf.parseDateTime("06.01.14 23:11"))},
				new DataSet {come = dtf.parseDateTime("07.01.14 00:31"); go = Some(dtf.parseDateTime("07.01.14 04:15"))},
				new DataSet {come = dtf.parseDateTime("07.01.14 17:01"); go = Some(dtf.parseDateTime("07.01.14 18:25"))},
				new DataSet {come = dtf.parseDateTime("07.01.14 20:24"); go = Some(dtf.parseDateTime("08.01.14 01:34"))},
				new DataSet {come = dtf.parseDateTime("08.01.14 01:46"); go = Some(dtf.parseDateTime("08.01.14 03:56"))}
				)
				
		TimeClock.serialize(list)
		
	}

}
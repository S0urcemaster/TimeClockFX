package sntr.timeclock

import org.joda.time.format.DateTimeFormat
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Duration
import scalafx.application.Platform
import java.io.File

object TimingTest extends App {
	
	val longFormat = DateTimeFormat.forPattern("dd.MM.yy HH:mm.ss.SSS")
	val shortFormat = DateTimeFormat.forPattern("dd.MM.yy HH:mm")
	val localFormat = DateTimeFormat.forPattern("HH:mm.ss.SSS")

	testMonthChange
	//testDurationToPeriod
	//reproduceDurationMinuteExceedance
	
	
	/**
	 * Tests a month change while app is running.
	 * Month change within a period
	 */
	def testMonthChange {
		
		GenerateData.empty
		assert(new File(TimeClock.filenameFor(1, 2014)).exists)
		
		val lastMonthCome = shortFormat.parseDateTime("31.01.14 23:00")
		val newMonthGo = shortFormat.parseDateTime("01.02.14 01:00")
		
		val newMonthCome = shortFormat.parseDateTime("01.02.14 01:30")
		val newMonthGo2 = shortFormat.parseDateTime("01.02.14 01:50")
		
		new Thread(new Runnable {
			def run {
				TimeClock.main(null)
			}
		}).start
		Thread.sleep(2000)
		Platform.runLater(new Runnable {
			def run {
				TimeClock.doCome(lastMonthCome)
				TimeClock.doGo(newMonthGo)
				assert(!new File(TimeClock.filenameFor(2, 2014)).exists)
				TimeClock.doCome(newMonthCome)
				assert(new File(TimeClock.filenameFor(2, 2014)).exists)
			}
		})
		
		
		
	}
	
	
	/**
	 * Tests a month change while app is running.
	 * Month change at a new period.
	 */
	def testMonthChangeNewPeriod {
		
		val lastMonthCome = shortFormat.parseDateTime("31.01.14 22:00")
		val lastMonthGo = shortFormat.parseDateTime("31.01.14 23:55")
		
		val newMonthCome = shortFormat.parseDateTime("01.02.14 00:30")
		
	}
	
	
	/**
	 * Tests the Joda Time Duration and Period functionality
	 * and the "loss of precision" when converting from
	 * Duration to Period.
	 */
	def testDurationToPeriod {
		
		println("testDurationToPeriod")
		val today = LocalDate.now
		
		val dayTimes = List(
			localFormat.parseLocalTime("12:00.00.000"),
			localFormat.parseLocalTime("12:59.59.999"),
			localFormat.parseLocalTime("13:00.00.000"),
			localFormat.parseLocalTime("13:00.00.001")
		)
		
		val data = List(
			new DataSet {come = today.toDateTime(dayTimes(0)); go = Some(today.toDateTime(dayTimes(1)))},
			new DataSet {come = today.toDateTime(dayTimes(1)); go = Some(today.toDateTime(dayTimes(3)))}
		)
		
		println("<0 Duration/Period from 12:0,00 to 12:59.59.999>")
		println(longFormat.print(data(0).come) +" - " +longFormat.print(data(0).go.get))
		val duration0 = new Duration(data(0).come, data(0).go.get)
		val period0 = duration0.toPeriod()
		println("minutes: " +period0.getMinutes +" (should be 59)")
		println("seconds: " +period0.getSeconds +" (59)")
		println("millis:  " +period0.getMillis +" (999)")
		println("</0>")
		
		println("<1 Duration/Period from 12:59.59.999 to 13:00.00.001>")
		println(longFormat.print(data(1).come) +" - " +longFormat.print(data(1).go.get))
		val duration1 = new Duration(data(1).come, data(1).go.get)
		val period1 = duration1.toPeriod()
		println("minutes: " +period1.getMinutes +" (should be 0)")
		println("seconds: " +period1.getSeconds +" (0)")
		println("millis:  " +period1.getMillis +" (2)")
		println("</1>")
		
		println("<2 Adding Duration 0 and 1>")
		val duration3 = duration0.plus(duration1)
		val period3 = duration3.toPeriod()
		println("hours: " +period3.getHours +" (should be 1)")
		println("minutes: " +period3.getMinutes +" (should be 0)")
		println("seconds: " +period3.getSeconds +" (0)")
		println("millis:  " +period3.getMillis +" (1)")
		println("</2>")
		
	}
	
	/**
	 * Produces Durations/Periods like
	 * 5:78 and 1:65
	 * FIXED
	 */
	def reproduceDurationMinuteExceedance {
		
		println("reproduceDurationMinuteExceedance")
		
		val data = List(
			new DataSet {come = shortFormat.parseDateTime("06.01.14 20:06"); go = Some(shortFormat.parseDateTime("06.01.14 22:11"))},
			new DataSet {come = shortFormat.parseDateTime("06.01.14 22:12"); go = Some(shortFormat.parseDateTime("07.01.14 00:20"))}
		)
		
		val duration0 = TimeClock.getDataSetDuration(data(0))
		val duration1 = TimeClock.getDataSetDuration(data(1))
		
		var dayDuration = Duration.ZERO
		
		dayDuration = dayDuration plus duration0
		
		println("<0>")
		println(longFormat.print(data(0).come) +" - " +longFormat.print(data(0).go.get))
		val period0 = dayDuration.toPeriod()
		println("hours: " +period0.getHours +" (should be 2)")
		println("minutes: " +period0.getMinutes +" (5)")
		println("</0>")
		
		dayDuration = dayDuration plus duration1
		
		println("<1 Adding Durations>")
		println(longFormat.print(data(1).come) +" - " +longFormat.print(data(1).go.get))
		val period1 = dayDuration.toPeriod()
		println("hours: " +period1.getHours +" (should be 4)")
		println("minutes: " +period1.getMinutes +" (13)")
		println("</1>")
	}	

}
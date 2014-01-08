package sntr.timeclock

import scala.Some
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scala.collection.mutable.ListBuffer
import javafx.scene.text.Text
import javafx.geometry.Pos
import javafx.scene.layout.RowConstraints
import scalafx.geometry.VPos
import scalafx.geometry.HPos
import scalafx.geometry.Insets
import scala.io.Source
import scala.util.Marshal
import java.io.FileWriter
import java.io.File
import scalafx.event.EventHandler
import scala.collection.Iterator
import javafx.scene.layout.Priority
import scalafx.stage.Stage
import scalafx.stage.Modality
import scala.collection.SortedMap
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import org.joda.time.DateTimeFieldType
import org.joda.time.LocalTime
import org.joda.time.LocalDate
import org.joda.time.Duration
import org.joda.time.Period
import org.joda.time.format.PeriodFormat
import org.joda.time.format.PeriodFormatterBuilder
import scala.collection.immutable.TreeMap
import scala.math.Ordering
import scala.collection.SortedSet

class DataSet {

	var come: DateTime = _
	var go: Option[DateTime] = _

	override def toString: String = {
		val dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
		val sCome = dtf.print(come)
		val sGo = {
			if (!go.isEmpty)
				dtf.print(go.get)
			else "missing"
		}
		sCome + " - " + sGo
	}
}


class Style (style: String) {
	
	def addStyle(style: String): String = {
		this.style +style
	}
	
}


object Constants {
	
	val rem = Math.rint(new Text("").getLayoutBounds.getHeight)
	//println(rem) -> 24
	
	val appTitle = "Time Clock"
}


object TimeClock extends JFXApp {
	
	var data = TimeClock.unserialize

	var monthlyDuration = Duration.ZERO

	val WeekDays = List(

		"", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

	val forgotTextField = new TextField {
		text = "hh:mm"
		onKeyPressed = {e: KeyEvent =>
			if(e.code == KeyCode.ENTER) clicked("dOk")
		}
	}
	
	val forgotLabel = new Label("Type in time:")
	val forgotErrorLabel = new Label("")
	forgotErrorLabel.style = "-fx-text-fill: red"
	
	val dialog = new Stage {
		title = "Type Time"
		initModality(Modality.WINDOW_MODAL)
		scene = new Scene(15 * Constants.rem, 8 * Constants.rem) {
			root = new BorderPane {
				top = new VBox {
					content = List(
						forgotLabel, forgotErrorLabel
					)
				}
				center = forgotTextField
				bottom = new HBox {
					content = List(
						new Button("Cancel") {
							onMouseClicked = clicked("dCancel")
						},
						new Button("Ok") {
							onMouseClicked = clicked("dOk")
						}
					)
				}
			}
		}
	}
	
	def getCurrentYear: Int = {
		DateTime.now.getYear
	}

	def getCurrentMonth: Int = {
		DateTime.now.getMonthOfYear
	}

	def generateFilename(): String = {

		val year = getCurrentYear.toString
		val month = getCurrentMonth
		year + String.format("%02d", Int.box(month)) + ".times"

	}

	
	def dataSet2Line(dataSet: DataSet): String = {
		val dtf = DateTimeFormat.forPattern("dd.HH:mm")
		val come = dtf.print(dataSet.come)
		val go = {
			if (!dataSet.go.isEmpty) {
				dtf.print(dataSet.go.get)
			}
			else ""
		}
		come + " - " + go
	}
	

	def line2DataSet(line: String, month: Int, year: Int): DataSet = {

		val dtf = DateTimeFormat.forPattern("dd.HH:mm")

		val split = line.split("""( - )""")
		val partial = dtf.parseDateTime(split(0))
		val comeDateTime = new DateTime(year, month, partial.getDayOfMonth, partial.getHourOfDay, partial.getMinuteOfHour)
		
		val dataSet = new DataSet { come = comeDateTime; go = None }
		
		dataSet.go = {
			if (split.length > 1) {
				val partial = dtf.parseDateTime(split(1))
				Some(new DateTime(year, month, partial.getDayOfMonth, partial.getHourOfDay, partial.getMinuteOfHour))
			}
			else None
		}

		dataSet
	}

	def writeToFile(p: String, s: String): Unit = {
		val pw = new java.io.PrintWriter(new java.io.File(p))
		try pw.write(s) finally pw.close()
	}

	def serialize(list: List[DataSet]) {

		val fw = new FileWriter(generateFilename, false)
		try {
			list.foreach(dataSet => {
				fw.write(dataSet2Line(dataSet) + "\n")
			})
		}
		finally fw.close()

	}

	def unserialize(): List[DataSet] = {

		val filename = generateFilename
		if (!new File(filename).exists) {
			serialize(List[DataSet]())
		}
		val source = Source.fromFile(filename)

		val list = (for (line <- source.getLines) yield line2DataSet(line, getCurrentMonth, getCurrentYear)).toList
		
		source.close
		
		list

	}
	
	def getSmallestForgotComeDate: DateTime = {
		data(0).go.get
	}
	
	def getSmallestForgotGoDate: DateTime = {
		data(0).come
	}
	
	def isForgotGo: Boolean = data(0).go.isEmpty
	
	def clicked(id: String) {

		id match {

			case "come" => {

				val ds = new DataSet { come = DateTime.now; go = None }
				data = ds :: data
				serialize(data)

				dayPanes = makeDayPanes
				dayPanesBox.content = dayPanes
				toggleButtons
				adjustTitle

			}
			case "go" => {
				data(0).go = Some(DateTime.now)
				serialize(data)
				dayPanes = makeDayPanes
				dayPanesBox.content = dayPanes
				toggleButtons
				adjustTitle
			}
			case "forgotCome" => {
				forgotLabel.text = "Type in date and time:"
				forgotErrorLabel.text = ""
				val dtf = DateTimeFormat.forPattern("HH:mm")
				forgotTextField.text = dtf.print(getSmallestForgotComeDate)
				dialog.show
			}
			case "forgotGo" => {
				forgotLabel.text = "Type in time:"
				forgotErrorLabel.text = ""
				val dtf = DateTimeFormat.forPattern("dd.MM.yy HH:mm")
				forgotTextField.text = dtf.print(getSmallestForgotGoDate)
				dialog.show
			}
			case "dOk" => {
				val now = DateTime.now
				if (isForgotGo) {
					val dtf = DateTimeFormat.forPattern("dd.MM.yy HH:mm")
					val inputDateTime = {
						try {
							dtf.parseDateTime(forgotTextField.text.value)
						} catch {
							case e: Exception => {
								forgotErrorLabel.text = "Invalid input"
								return
							}
						}
					}
					val smallestDate = getSmallestForgotGoDate
//					def cal2format(cal: Calendar): String = {
//						val df = new SimpleDateFormat("dd.MM.yy HH:mm")
//						df.format(cal.getTime)
//					}
//					println(cal2format(cal), cal2format(smallestDate))
					if (inputDateTime.isBefore(smallestDate)) {
						forgotErrorLabel.text = "Time must be past the last entry's time"
						return
					}
					if (inputDateTime.isAfter(now)) {
						forgotErrorLabel.text = "Time must be before or equal present"
						return
					}
					data(0).go = Some(inputDateTime)
					serialize(data)
					dayPanes = makeDayPanes
					dayPanesBox.content = dayPanes
					toggleButtons
				}
				else {
					
					val dtf = DateTimeFormat.forPattern("HH:mm")
					val inputTime = {
						try {
							dtf.parseLocalTime(forgotTextField.text.value)
						} catch {
							case e: Exception => {
								forgotErrorLabel.text = "Invalid input"
								return
							}
						}
					}
					val inputDateTime = {
						val input = inputTime.toDateTimeToday
						if (input.isAfter(now)){
							input.minusDays(1)
						}
						else input
						
					}
					val smallestDate = getSmallestForgotComeDate
					if (inputDateTime.isBefore(smallestDate)) {
						forgotErrorLabel.text = "Time must be past the last entry's time"
						return
					}
					val ds = new DataSet { come = inputDateTime; go = None }
					data = ds :: data
					serialize(data)
	
					dayPanes = makeDayPanes
					dayPanesBox.content = dayPanes
					toggleButtons
					
				}
				dialog.hide
			}
			case "dCancel" => {
				dialog.hide
				
			}

		}

	}


	def makeDayLabel(come: DateTime, period: Period): Label = {
		
		val durationText = " (" +String.format("%02d", Long.box(period.getHours)) +":" +String.format("%02d", Long.box(period.getMinutes)) +")"
		val dayLabel = new Label(come.getDayOfMonth + ". " + come.dayOfWeek.getAsText +" " +durationText)

		dayLabel.style = {
			val size = "-fx-font-size:1.5em"
			val color = if (come.getDayOfWeek == 7)	"-fx-text-fill:red"
				else if (come.getDayOfWeek == 6) "-fx-text-fill:blue"
				else "-fx-text-fill:black"
			size +";" +color
		}
		dayLabel
	}

	
	def makeTimeText(hour: Int, minute: Int): String = {

		String.format("%02d", Int.box(hour)) + ":" +
			String.format("%02d", Int.box(minute))
	}
	
	def getDataSetDuration(ds: DataSet): Duration = {
		if (!ds.go.isEmpty) return new Duration(ds.come, ds.go.get)
		Duration.ZERO
	}
	
	object ReverseOrdering extends Ordering[Int] {
		def compare(a:Int, b:Int) = {
			//println("compare " +a +", " +b)
			b compare a
		}
	}
	
	object DataSetOrdering extends Ordering[DataSet] {
		def compare(a:DataSet, b:DataSet) = {
			if (a.come.isBefore(b.come)) 1
			else -1
		}
	}
	
	def makeDayPanes: Iterable[VBox] = {

		val days = data.groupBy(_.come.getDayOfMonth)
		
		val daysOrdered = {
			for ((k, v) <- days) yield {
				k -> v.sorted(DataSetOrdering)
			}
		}
		
		val sorted = SortedMap[Int, List[DataSet]]()(ReverseOrdering) ++ daysOrdered
		
		monthlyDuration = Duration.ZERO
		
		for ((k, v) <- sorted) yield {

			var dayDuration = Duration.ZERO
			
			val come = v(0).come

			val timeLabels = for (ds <- v) yield {

				val comeText = makeTimeText(ds.come.getHourOfDay, ds.come.getMinuteOfHour)

				def goText: String = {
					if (!ds.go.isEmpty) {
						return makeTimeText(ds.go.get.getHourOfDay, ds.go.get.getMinuteOfHour)
					}
					""
				}
				val duration = getDataSetDuration(ds)
				dayDuration = dayDuration.plus(duration)
				val pf = PeriodFormat.getDefault
				val periodText = "(" +String.format("%02d", Long.box(duration.toPeriod().getHours)) +":" +String.format("%02d", Long.box(duration.toPeriod().getMinutes)) +")"
				val label = new Label(comeText + " - " + goText +" " +periodText)
				label.style = {
					"-fx-translate-x: 1em"
				}
				label
			}

			monthlyDuration = monthlyDuration.plus(dayDuration)
			
			val dayLabel = makeDayLabel(come, dayDuration.toPeriod)

			new VBox {

				content = List.concat(List(dayLabel), timeLabels)
			}
		}
	}
	
	def adjustTitle = {
		val now = DateTime.now
		val pf = new PeriodFormatterBuilder()
			.printZeroAlways
			.minimumPrintedDigits(2)
			.appendHours
			.appendSeparator(":")
			.minimumPrintedDigits(2)
			.appendMinutes()
			.toFormatter
		stage.title = Constants.appTitle +" : " +now.monthOfYear.getAsText + " " +now.getYear +" (" +pf.print(monthlyDuration.toPeriod) +")"
	}

	
	def toggleButtons {
		if (!data.isEmpty && data(0).go.isEmpty) {
			comeButton.disable = true
			goButton.disable = false
			forgotComeButton.disable = true
			forgotGoButton.disable = false
		} else {
			comeButton.disable = false
			goButton.disable = true
			forgotComeButton.disable = false
			forgotGoButton.disable = true
		}
	}

	val comeButton = new Button {
		id = "come"
		maxWidth = Double.MaxValue
		maxHeight = Double.MaxValue
		text = "Come"
		onMouseClicked = clicked(id.value)
	}
	val forgotComeButton = new Button {
		id = "forgotCome"
		maxWidth = Double.MaxValue
		maxHeight = Double.MaxValue
		text = "Forgot"
		onMouseClicked = clicked(id.value)
	}
	val goButton = new Button {
		id = "go"
		maxWidth = Double.MaxValue
		maxHeight = Double.MaxValue
		text = "Go"
		onMouseClicked = clicked(id.value)
	}
	val forgotGoButton = new Button {
		id = "forgotGo"
		maxWidth = Double.MaxValue
		maxHeight = Double.MaxValue
		text = "Forgot"
		onMouseClicked = clicked(id.value)
	}

	var dayPanes = makeDayPanes
	var dayPanesBox = new VBox {
		content = dayPanes
	}
	
	toggleButtons
	
	stage = new PrimaryStage {
		title = "Time Clock"
		scene = new Scene(25 * Constants.rem, 15 * Constants.rem) {

			root = new BorderPane {

				padding = Insets.apply(3, 3, 3, 3)

				center = new ScrollPane {
					
					style = "-fx-focus-color:transparent"
					padding = Insets(
							top = 5,
							right = 5,
							bottom = 5,
							left = 5
					)
					content = dayPanesBox

				}
				right = new GridPane {

					hgap = 3
					vgap = 3

					GridPane.setConstraints(comeButton, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.SOMETIMES, Priority.ALWAYS)

					GridPane.setConstraints(forgotComeButton, 0, 1, 1, 1, HPos.CENTER, VPos.BOTTOM)

					addColumn(0, comeButton, forgotComeButton)

					GridPane.setConstraints(comeButton, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.SOMETIMES, Priority.ALWAYS)

					GridPane.setConstraints(forgotComeButton, 0, 1, 1, 1, HPos.CENTER, VPos.BOTTOM)

					addColumn(1, goButton, forgotGoButton)

				}
			}
		}
	}

	adjustTitle


}
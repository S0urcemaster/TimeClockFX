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
import java.util.Date
import java.text.DateFormat
import scala.io.Source
import scala.util.Marshal
import java.util.Calendar
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.io.File
import scalafx.event.EventHandler
import scala.collection.Iterator
import javafx.scene.layout.Priority
import scalafx.stage.Stage
import scalafx.stage.Modality
import scala.collection.SortedMap
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode

class DataSet {

	var come: Date = _
	var go: Option[Date] = _

	override def toString: String = {
		val comeDf = DateFormat.getDateTimeInstance()
		val sCome = comeDf.format(come)
		val sGo = {
			if (!go.isEmpty)
				comeDf.format(go.get)
			else "missing"
		}
		sCome + " - " + sGo
	}

}


object Constants {
	val rem = Math.rint(new Text("").getLayoutBounds.getHeight)
	//println(rem) -> 24
}

object TimeClock extends JFXApp {
	
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
	
	val WeekDays = List(

		"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

	def getCurrentYear: Int = {
		val cal = Calendar.getInstance
		cal.setTime(new Date)
		cal.get(Calendar.YEAR)
	}

	def getCurrentMonth: Int = {
		val cal = Calendar.getInstance
		cal.setTime(new Date)
		cal.get(Calendar.MONTH)
	}

	def generateFilename(): String = {

		val year = getCurrentYear.toString
		val month = getCurrentMonth + 1
		year + String.format("%02d", Int.box(month)) + ".times"

	}

	def dataSet2Line(dataSet: DataSet): String = {
		val cal = Calendar.getInstance()
		cal.setTime(dataSet.come)
		val come = String.format("%02d", Int.box(cal.get(Calendar.DAY_OF_MONTH))) + "." +
			String.format("%02d", Int.box(cal.get(Calendar.HOUR_OF_DAY))) + ":" +
			String.format("%02d", Int.box(cal.get(Calendar.MINUTE)))
		def go: String = {
			if (!dataSet.go.isEmpty) {
				cal.setTime(dataSet.go.get)
				return String.format("%02d", Int.box(cal.get(Calendar.DAY_OF_MONTH))) + "." +
					String.format("%02d", Int.box(cal.get(Calendar.HOUR_OF_DAY))) + ":" +
					String.format("%02d", Int.box(cal.get(Calendar.MINUTE)))
			}
			""
		}

		come + " - " + go

	}

	def line2DataSet(line: String, month: Int, year: Int): DataSet = {

		val cal = Calendar.getInstance

		val split = line.split("""\.|:|( - )""")

		cal.set(year, month, Integer.parseInt(split(0)), Integer.parseInt(split(1)), Integer.parseInt(split(2)))

		val dataSet = new DataSet { come = cal.getTime; go = None }

		dataSet.come = cal.getTime

		if (split.length > 3) {
			cal.set(year, month, Integer.parseInt(split(3)), Integer.parseInt(split(4)), Integer.parseInt(split(5)))
			dataSet.go = Some(cal.getTime)
		} else dataSet.go = None

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
		} finally fw.close()

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
	
	def getSmallestForgotComeDate: Date = {
		data(0).go.get
	}
	
	def getSmallestForgotGoDate: Date = {
		data(0).come
	}
	
	def forgotGo: Boolean = data(0).go.isEmpty
	
	def clicked(id: String) {

		id match {

			case "come" => {

				val ds = new DataSet { come = new Date; go = None }
				data = ds :: data
				serialize(data)

				dayPanes = makeDayPanes
				dayPanesBox.content = dayPanes
				toggleButtons

			}
			case "go" => {
				data(0).go = Some(new Date)
				serialize(data)
				dayPanes = makeDayPanes
				dayPanesBox.content = dayPanes
				toggleButtons
			}
			case "forgotCome" => {
				forgotLabel.text = "Type in date and time:"
				forgotErrorLabel.text = ""
				val df = new SimpleDateFormat("dd.MM.yy HH:mm")
				forgotTextField.text = df.format(getSmallestForgotComeDate)
				dialog.show
			}
			case "forgotGo" => {
				forgotLabel.text = "Type in time:"
				forgotErrorLabel.text = ""
				val df = new SimpleDateFormat("HH:mm")
				forgotTextField.text = df.format(getSmallestForgotGoDate)
				dialog.show
			}
			case "dOk" => {
				val cal = Calendar.getInstance
				val calNow = Calendar.getInstance()
				if (forgotGo) {
					calNow.setTime(new Date)
					cal.setTime(data(0).come)
					try {
						cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(forgotTextField.text.value.substring(0, 2)))
						cal.set(Calendar.MINUTE, Integer.parseInt(forgotTextField.text.value.substring(3, 5)))
					} catch {
						case e: Exception => {
							forgotErrorLabel.text = "Invalid input"
							return
						}
					}
					val smallestDate = Calendar.getInstance()
					smallestDate.setTime(getSmallestForgotGoDate)
//					def cal2format(cal: Calendar): String = {
//						val df = new SimpleDateFormat("dd.MM.yy HH:mm")
//						df.format(cal.getTime)
//					}
//					println(cal2format(cal), cal2format(smallestDate))
					if (cal.before(smallestDate)) {
						forgotErrorLabel.text = "Time must be past the last entry's time"
						return
					}
					if (cal.after(calNow)) {
						forgotErrorLabel.text = "Time must be before or equal present"
						return
					}
					data(0).go = Some(cal.getTime())
					serialize(data)
					dayPanes = makeDayPanes
					dayPanesBox.content = dayPanes
					toggleButtons
				}
				else {
					val df = new SimpleDateFormat("dd.MM.yy HH:mm")
					try {
						cal.setTime(df.parse(forgotTextField.text.value))
					} catch {
						case e:Exception => {
							forgotErrorLabel.text = "Invalid input"
							return
						}
					}
					val smallestDate = Calendar.getInstance()
					smallestDate.setTime(getSmallestForgotComeDate)
					if (cal.before(smallestDate)) {
						forgotErrorLabel.text = "Time must be past the last entry's time"
						return
					}					
					if (cal.after(calNow)) {
						forgotErrorLabel.text = "Time must be before or equal present"
						return
					}
					val ds = new DataSet { come = cal.getTime(); go = None }
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

	var data = TimeClock.unserialize

	//for ((k, v) <- days) {
	//	val cal = Calendar.getInstance
	//	cal.setTime(v(0).come)
	//	println(k, WeekDays(cal.get(Calendar.DAY_OF_WEEK)), v)
	//}

	val df = new SimpleDateFormat("dd.MM.yyyy")

	def makeDayLabel(dayOfMonth: Int, dayOfWeek: Int): Label = {

		val dayLabel = new Label(dayOfMonth + ". " + WeekDays(dayOfWeek))

		dayLabel.style = {
			if (dayOfWeek == 6 || dayOfWeek == 0)
				"color:red" else "color:black"
		}
		dayLabel
	}

	def makeTimeText(hour: Int, minute: Int): String = {

		String.format("%02d", Int.box(hour)) + ":" +
			String.format("%02d", Int.box(minute))
	}

	def makeDayPanes: Iterable[VBox] = {

		val days = data.groupBy(ds => {
			val cal = Calendar.getInstance
			cal.setTime(ds.come)
			cal.get(Calendar.DAY_OF_MONTH)
		})
		
		val sorted = days.toSeq.sortWith(_._1 > _._1).toMap
		//val sorted = SortedMap(days.toSeq:_*)
		
		for ((k, v) <- sorted) yield {

			val cal = Calendar.getInstance
			cal.setTime(v(0).come)

			val dayLabel = makeDayLabel(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_WEEK))

			val timeLabels = for (ds <- v) yield {

				cal.setTime(ds.come)
				val come = makeTimeText(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

				def go: String = {
					if (!ds.go.isEmpty) {
						cal.setTime(ds.go.get)
						return makeTimeText(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
					}
					""
				}

				new Label(come + " - " + go)
			}

			new VBox {

				content = List.concat(List(dayLabel), timeLabels)
			}
		}
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

	var dayPanes = makeDayPanes
	var dayPanesBox = new VBox {
		content = dayPanes
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

	toggleButtons

	stage = new PrimaryStage {
		title = "Time Clock"
		scene = new Scene(25 * Constants.rem, 15 * Constants.rem) {

			root = new BorderPane {

				padding = Insets.apply(3, 3, 3, 3)

				center = new ScrollPane {

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

}
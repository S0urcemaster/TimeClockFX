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
import scala.pickling._
import json._
import java.util.Calendar
import java.io.FileWriter
import java.text.SimpleDateFormat


class DataSet() {
	
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

object TimeClock extends JFXApp {
	
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
		val month = getCurrentMonth.toString
		year +month +".times"
		
	}
	
	def dataSet2Line(dataSet: DataSet): String = {
		
		val come = dataSet.come.getDate() +"." +dataSet.come.getHours() +":" +dataSet.come.getMinutes()
		val go = dataSet.go.get.getDate() +"." +dataSet.go.get.getHours() +":" +dataSet.go.get.getMinutes()
		come +" - " +go
		
	}
	
	def line2DataSet(line: String, month: Int, year: Int): DataSet = {
		
		val cal = Calendar.getInstance
		
		val split = line.split("""\.|:|( - )""")
		
		cal.set(year, month, Integer.parseInt(split(0)), Integer.parseInt(split(1)), Integer.parseInt(split(2)))
		
		val dataSet = new DataSet
		dataSet.come = cal.getTime
		
		cal.set(year, month, Integer.parseInt(split(3)), Integer.parseInt(split(4)), Integer.parseInt(split(5)))
		
		dataSet.go = Option(cal.getTime)
		
		dataSet
	}
	
	def writeToFile(p: String, s: String): Unit = {
	    val pw = new java.io.PrintWriter(new java.io.File(p))
	    try pw.write(s) finally pw.close()
	}
	
	def serialize(list: List[DataSet]) {
		
		val fw = new FileWriter(generateFilename, true)
		try {
			list.foreach(dataSet => {
				fw.write(dataSet2Line(dataSet) +"\n")
			})
		}
		finally fw.close()
		
	}
	
	def unserialize(): List[DataSet] = {
		val source = Source.fromFile(generateFilename)
		
		List.fromIterator(for(line <- source.getLines) yield line2DataSet(line, getCurrentMonth, getCurrentYear))
		
	}
	
	val rem = Math.rint(new Text("").getLayoutBounds().getHeight())
	//println(rem) -> 24

	val data = TimeClock.unserialize
	
	val labels = for (dataSet <- data) yield {
		val cal = Calendar.getInstance
		cal.setTime(dataSet.come)
		val from = cal.get(Calendar.DAY_OF_MONTH) +". " +cal.get(Calendar.MONTH) +". " +
			cal.get(Calendar.YEAR) +" - " +cal.get(Calendar.HOUR) +":" +cal.get(Calendar.MINUTE) +" => "
		cal.setTime(dataSet.go.get)
		val to = cal.get(Calendar.DAY_OF_MONTH) +". " +cal.get(Calendar.MONTH) +". " +
			cal.get(Calendar.YEAR) +" - " +cal.get(Calendar.HOUR) +":" +cal.get(Calendar.MINUTE)
		new Label(from +to)
	}

	
	stage = new PrimaryStage {
		title = "Time Clock"
		scene = new Scene(25 * rem, 15 * rem) {
			
			root = new BorderPane {
				
				padding = Insets.apply(3, 3, 3, 3)
				
				left = new VBox {

					content = labels

				}
				right = new GridPane {
					
					hgap = 3
					vgap = 3
					
					val comeButton = new Button {
						maxWidth = Double.MaxValue
						maxHeight = Double.MaxValue
						text = "Come"
					}
					GridPane.setConstraints(comeButton, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.SOMETIMES, Priority.ALWAYS)
					
					val forgotComeButton = new Button {
						maxWidth = Double.MaxValue
						maxHeight = Double.MaxValue
						text = "Forgot"
					}
					GridPane.setConstraints(forgotComeButton, 0, 1, 1, 1, HPos.CENTER, VPos.BOTTOM)
					

					addColumn(0, comeButton, forgotComeButton)
					
					val goButton = new Button {
						maxWidth = Double.MaxValue
						maxHeight = Double.MaxValue
						text = "Go"
					}
					GridPane.setConstraints(comeButton, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.SOMETIMES, Priority.ALWAYS)
					
					val forgotGoButton = new Button {
						maxWidth = Double.MaxValue
						maxHeight = Double.MaxValue
						text = "Forgot"
					}
					GridPane.setConstraints(forgotComeButton, 0, 1, 1, 1, HPos.CENTER, VPos.BOTTOM)
					

					addColumn(1, goButton, forgotGoButton)
					

				}
			}
		}
	}

}
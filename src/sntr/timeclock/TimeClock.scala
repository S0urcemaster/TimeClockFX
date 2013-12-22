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

object TimeClock extends JFXApp {

	val rem = Math.rint(new Text("").getLayoutBounds().getHeight())
	//println(rem) -> 24

	val lastEntries = new ListBuffer[String]

	lastEntries += "Test"

	stage = new PrimaryStage {
		title = "Time Clock"
		scene = new Scene(25 * rem, 15 * rem) {
			
			root = new BorderPane {
				
				padding = Insets.apply(3, 3, 3, 3)
				
				left = new VBox {
					val labels = new ListBuffer[Label]
					for (item <- lastEntries) labels += new Label(item)
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
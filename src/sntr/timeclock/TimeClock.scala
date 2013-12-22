package sntr.timeclock

import scala.Some
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scala.collection.mutable.ListBuffer

object TimeClock extends JFXApp {
	
	val lastEntries = new ListBuffer[String]
	
	lastEntries += "Test"
	
	stage = new PrimaryStage{
		title = "Time Clock"
		scene = new Scene(600, 400) {
			root = new BorderPane {
				left = new VBox {
					val labels = new ListBuffer[Label]
					for(item <- lastEntries) labels += new Label(item)
					content = labels
					
				}
				right = new HBox {
					content = List(
						new VBox {
							style = "-fx-background-color: #336699;"
							content = List(
								new Button {
									maxWidth = 100
									minHeight = 50
									text = "Come"
									
								},
								new Button {
									maxWidth = 100									
									text = "Forgot"
								}
							)
						},
						new VBox {
							content = List(
								new Button {
									maxWidth = 100
									text = "Go"
									
								},
								new Button {
									maxWidth = 100									
									text = "Forgot"
								}
							)
						}
					)
				}
			}
		}
	}

}
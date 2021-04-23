package ru.pshiblo.gui.views

import com.jfoenix.controls.JFXProgressBar
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.HBox
import tornadofx.*

class Browser: Fragment("Браузер") {

    val url: String by param()

    override val root = vbox(alignment = Pos.CENTER) {
        hbox(alignment = Pos.CENTER) {
             webview {
                 engine.load(url)
            }
        }
    }

    override fun onBeforeShow() {
        currentStage?.width = 450.0
        currentStage?.height = 550.0
        super.onBeforeShow()
    }
}
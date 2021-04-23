package ru.pshiblo.gui.views

import com.jfoenix.controls.JFXTabPane
import javafx.scene.text.FontWeight
import tornadofx.View
import tornadofx.c
import tornadofx.stylesheet


class TabView : View("My View") {

    override val root = JFXTabPane().apply {
        tab<MainFragment>()
        tab<Console>()
        this.stylesheet {
            select(".jfx-tab-pane") {
                select(".tab-header-background") {
                    backgroundColor += c("white")

                }

                select(".tab-header-area .tab-selected-line") {
                    backgroundColor += c("blue")
                }

                select(".tab .tab-label") {
                    textFill = c("black")
                    fontWeight = FontWeight.LIGHT
                }

                select(".tab-header-area .jfx-rippler") {
                    raw("-jfx-rippler-fill :RED;")
                }
            }
        }
    }
}

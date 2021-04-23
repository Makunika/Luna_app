package ru.pshiblo.gui.fragments

import com.jfoenix.controls.JFXTabPane
import javafx.scene.text.FontWeight
import tornadofx.*


class TabFragment : Fragment() {

    override val root = JFXTabPane().apply {
        maxWidth = 1000.0
        prefWidth = 820.0

        tab<MainTabFragment>()
        tab<ConsoleFragment>()
        this.stylesheet {
            select(".jfx-tab-pane") {
                select(".tab-header-background") {
                    backgroundColor += c("transparent")
                }

                select(".tab-header-area .tab-selected-line") {
                    backgroundColor += c("#4da5f6")
                }

                select(".tab .tab-label") {
                    textFill = c("black")
                    fontWeight = FontWeight.THIN
                }

                select(".tab-header-area .jfx-rippler") {
                    raw("-jfx-rippler-fill: #4da5f6;")
                }
            }
        }
        style {
            paddingRight = 20.0
            paddingBottom = 20.0
        }
    }
}

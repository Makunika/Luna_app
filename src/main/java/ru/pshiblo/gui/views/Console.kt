package ru.pshiblo.gui.views

import ru.pshiblo.gui.log.ConsoleOut
import tornadofx.*

class Console: Fragment("Консоль") {

    override val root = borderpane {
        style {
            paddingAll(20.0)
        }
        center {
            ConsoleOut.getTextArea().isEditable = false
            add(ConsoleOut.getTextArea())
        }
    }
}
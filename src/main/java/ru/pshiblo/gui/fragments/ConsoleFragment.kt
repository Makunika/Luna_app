package ru.pshiblo.gui.fragments

import ru.pshiblo.gui.log.ConsoleOut
import tornadofx.*

class ConsoleFragment: Fragment("Консоль") {

    override val root = borderpane {
        style {
            paddingTop = 20.0
        }
        center {
            ConsoleOut.getTextArea().isEditable = false
            add(ConsoleOut.getTextArea())
        }
    }
}
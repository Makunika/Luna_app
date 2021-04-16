package ru.pshiblo.gui.views

import ru.pshiblo.gui.log.ConsoleOut
import tornadofx.*

class Console: Fragment("Вывод консоли") {

    override val root = borderpane {
        style {
            paddingAll(20.0)
        }
        top {
            label("Консоль вывода")
        }
        center {
            ConsoleOut.getTextArea().isEditable = false
            add(ConsoleOut.getTextArea())
        }
    }
}
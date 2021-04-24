package ru.pshiblo.gui.factory

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.scene.Cursor
import javafx.scene.control.Button
import tornadofx.c
import tornadofx.px
import tornadofx.style

object Buttons {
    fun createButton(text: String, icon: FontAwesomeIcon? = null, size: Double = 25.0): Button {
        if (icon != null) {
            val iconView = FontAwesomeIconView(icon)
            iconView.fill = c("white")

            val bt = JFXButton(text, iconView)
            bt.buttonType = JFXButton.ButtonType.RAISED;
            bt.style {
                backgroundColor += c("#0A75AD")
                textFill = c("white")
                fontSize = size.px
                cursor = Cursor.HAND
            }
            bt.graphicTextGap = 15.0

            return bt
        } else {
            val bt = JFXButton(text)
            bt.buttonType = JFXButton.ButtonType.RAISED;
            bt.style {
                backgroundColor += c("#0A75AD")
                textFill = c("white")
                fontSize = size.px
                cursor = Cursor.HAND
            }

            return bt
        }

    }
}
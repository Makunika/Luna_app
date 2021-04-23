package ru.pshiblo.gui.factory

import tornadofx.*
import com.jfoenix.animation.alert.JFXAlertAnimation
import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialogLayout
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.stage.Modality
import javafx.stage.Stage


object Alerts {
    fun createAlert(text: String, stage: Stage): JFXAlert<Void> {
        val layout = JFXDialogLayout()
        val body = Label(text)
        body.style {
            textFill = c("red")
            fontSize = 20.px
        }
        layout.setBody(body)

        val btnOk = JFXButton("Ок")
        btnOk.buttonType = JFXButton.ButtonType.FLAT
        btnOk.style {
            textFill = c("red")
            fontSize = 20.px
            backgroundColor += c("white")
        }

        layout.setActions(btnOk)
        val alert = JFXAlert<Void>(stage)
        alert.isOverlayClose = true
        alert.animation = JFXAlertAnimation.CENTER_ANIMATION
        alert.setContent(layout)
        alert.initModality(Modality.NONE)
        btnOk.action {
            alert.close()
        }
        return alert
    }
}
package ru.pshiblo.gui.factory

import tornadofx.*
import com.jfoenix.animation.alert.JFXAlertAnimation
import com.jfoenix.controls.*
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.stage.Modality
import javafx.stage.Stage


object Dialogs {
    fun createAlert(text: String, stage: Stage): JFXAlert<Void> {
        val layout = JFXDialogLayout()
        val body = Label(text)
        body.style {
            textFill = c("red")
            fontSize = 15.px
        }
        layout.setBody(body)

        val btnOk = JFXButton("Ок")
        btnOk.buttonType = JFXButton.ButtonType.FLAT
        btnOk.style {
            textFill = c("red")
            fontSize = 15.px
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

    fun createSpinner(stage: Stage): JFXAlert<Void> {
        val layout = JFXDialogLayout()
        layout.setBody(JFXSpinner().apply {
            progress = -1.0
        })
        val alert = JFXAlert<Void>(stage)
        alert.setContent(layout)
        alert.animation = JFXAlertAnimation.BOTTOM_ANIMATION
        alert.initModality(Modality.NONE)
        alert.isHideOnEscape = true
        alert.isOverlayClose = false
        return alert
    }
}
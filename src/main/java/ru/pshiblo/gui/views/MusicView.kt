package ru.pshiblo.gui.views

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXPasswordField
import com.jfoenix.controls.JFXProgressBar
import com.jfoenix.controls.JFXTextField
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import ru.pshiblo.Config
import ru.pshiblo.gui.ConfigGUI
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.services.Context
import ru.pshiblo.services.audio.discord.DiscordMusicService
import ru.pshiblo.services.audio.local.LocalMusicService
import ru.pshiblo.services.http.HttpService
import tornadofx.*
import javax.security.auth.login.LoginException

class MusicView : View("Luna") {
    override val root = hbox(alignment = Pos.CENTER) {
        vbox(20, Pos.CENTER) {
            val pb = JFXProgressBar().apply {
                progress = -1.0
                maxWidth = 300.0
            }
            pb.isVisible = false

            label("Luna") {
                style {
                    fontSize = 50.px
                    textFill = c("#4da5f6")
                }
            }
            separator {
                style {
                    paddingBottom = 20.0
                }
            }

            add(JFXPasswordField().apply {
                isLabelFloat = true
                promptText = "Токен Discord бота"
                text = Config.getInstance().tokenDiscord
                textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                    Config.getInstance().tokenDiscord = newValue
                })
            })

            separator {
                style {
                    paddingBottom = 20.0
                }
            }

            Buttons.createButton("Использовать локальную музыку", size = 20.0).also {
                add(it)
                it.action {
                    ConfigGUI.isDiscord = false

                    this.isDisable = true

                    pb.isVisible = true
                    runAsync {
                        Context.addServiceAndStart(LocalMusicService())
                        Context.addServiceAndStart(HttpService())
                    } ui {
                        this.isDisable = false
                        pb.isVisible = false
                        replaceWith<MainView>()
                    }
                }
            }
            Buttons.createButton("Использовать дискорд бота для музыки", size = 20.0).also {
                add(it)
                it.action {

                    ConfigGUI.isDiscord = true

                    this.isDisable = true
                    pb.isVisible = true
                    runAsync {
                        Context.addServiceAndStart(DiscordMusicService().apply {
                            subscribeException { e ->
                                if (e is LoginException) {
                                    Platform.runLater {
                                        alert(Alert.AlertType.ERROR, "Токен", "Токен дискорда неверный! Введите его снова, нажав кнопку \"назад\"",
                                        ButtonType.OK);
                                    }
                                } else {
                                    Platform.runLater {
                                        alert(Alert.AlertType.ERROR, "Ошибка", e.message ?: "Возникла исключительная ситуация, помогите",
                                            ButtonType.OK);
                                    }
                                }
                                e.printStackTrace()
                            }
                        })
                        Context.addServiceAndStart(HttpService())
                    } ui {
                        this.isDisable = false
                        pb.isVisible = false
                        replaceWith<MainView>()
                    }
                }
            }
            separator {
                style {
                    paddingTop = 20.0
                    paddingBottom = 20.0
                }
            }
            add(JFXButton("Назад").apply {
                val iconView = MaterialIconView(MaterialIcon.ARROW_BACK)
                iconView.fill = c("#4da5f6")
                graphic = iconView
                style {
                    fontSize = 18.px
                    paddingAll = 1.0
                    textFill = c("#4da5f6")
                    cursor = Cursor.HAND
                }
                buttonType = JFXButton.ButtonType.FLAT

                action {
                    replaceWith<StartView>()
                }
            })

            add(pb)
        }
    }
}

package ru.pshiblo.gui.views

import com.jfoenix.animation.alert.JFXAlertAnimation
import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.controls.JFXPasswordField
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.stage.Modality
import ru.pshiblo.Config
import ru.pshiblo.github.updating.UpdateApplication
import ru.pshiblo.gui.ConfigGUI
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.gui.fragments.BrowserFragment
import ru.pshiblo.gui.fragments.UpdateFragment
import ru.pshiblo.services.broadcast.twitch.TwitchAuth
import ru.pshiblo.services.broadcast.youtube.YouTubeAuth
import tornadofx.*

class StartView: View("Luna") {

    private fun checkUpdate() {
        runAsync {
            val updater = UpdateApplication()
            val release = updater.checkUpdate()
            if (release != null) {
                Platform.runLater {
                    val layout = JFXDialogLayout()

                    layout.setBody(vbox(5) {
                        scrollpane(fitToWidth = true) {
                            minHeight = 300.0

                            style {
                                backgroundColor += c("transparent")
                                border = null
                            }
                            stylesheet {
                                select(".scroll-pane .viewport") {
                                    backgroundColor += c("transparent")
                                }
                            }

                            label(release.body) {
                                style {
                                    fontSize = 14.px
                                }
                            }
                        }
                        separator()
                        hyperlink("Обновление на сайте") {
                            action {
                                hostServices.showDocument(release.htmlUrl.toExternalForm())
                            }
                        }
                    })
                    layout.setHeading(label("Новое обновление ${release.name}"))

                    val btnOk = Buttons.createButton("Обновить", size = 15.0)

                    val btnCancel = Buttons.createButton("Отменить", size = 15.0)

                    layout.setActions(btnOk, btnCancel)
                    val alert = JFXAlert<Void>(currentWindow ?: currentStage ?: primaryStage)
                    alert.isOverlayClose = true
                    alert.animation = JFXAlertAnimation.CENTER_ANIMATION
                    alert.setContent(layout)
                    alert.initModality(Modality.NONE)
                    btnOk.action {
                        alert.close()
                        find<UpdateFragment>(mapOf(UpdateFragment::release to release, UpdateFragment::updater to updater))
                            .openWindow()
                    }
                    btnCancel.action {
                        alert.close()
                    }
                    alert.show()
                }
            }
        }
    }

    override var root = hbox(alignment = Pos.CENTER) {
        vbox(10, alignment = Pos.CENTER) {
            vbox(spacing = 50, alignment = Pos.CENTER) {
                label("Luna") {
                    style {
                        fontSize = 50.px
                        textFill = c("#4da5f6")
                    }
                }
            }
            vbox(10, Pos.CENTER) {
                hbox(100, Pos.CENTER) {
                    /**
                     * Войти через google
                     */
                    vbox(10, Pos.TOP_CENTER) {
                        add(Buttons.createButton("Войти", FontAwesomeIcon.GOOGLE).apply {
                            action {
                                runAsync {
                                    if (!YouTubeAuth.auth { url ->
                                            println(url)
                                            Platform.runLater {
                                                find<BrowserFragment>(mapOf(BrowserFragment::url to url)).openModal()
                                            }
                                        }) {
                                        Platform.runLater {
                                            Dialogs.createAlert("Авторизация не прошла, попробуйте снова", currentStage ?: primaryStage).show()
                                        }
                                    } else {
                                        Platform.runLater {
                                            ConfigGUI.isTwitch = false
                                            replaceWith<MusicView>()
                                        }
                                    }
                                }
                            }
                        })
                        hyperlink("Войти через браузер") {
                            action {
                                if (!YouTubeAuth.auth()) {
                                    Dialogs.createAlert("Авторизация не прошла, попробуйте снова", currentStage ?: primaryStage).show()
                                } else {
                                    ConfigGUI.isTwitch = false
                                    replaceWith<MusicView>()
                                }
                            }
                        }
                    }
                    /**
                     * Войти через twitch
                     */
                    vbox(10, Pos.CENTER) {
                        add(Buttons.createButton("Войти", FontAwesomeIcon.TWITCH).apply {
                            action {
                                val spinner = Dialogs.createSpinner(currentStage ?: primaryStage)
                                spinner.show()
                                runAsync {
                                    try {
                                        if (TwitchAuth.auth()) {
                                            Platform.runLater {
                                                spinner.close()
                                                ConfigGUI.isTwitch = true
                                                replaceWith<MusicView>()
                                            }
                                        } else {
                                            Platform.runLater {
                                                spinner.close()
                                                Dialogs.createAlert("Токен Twitch Chat неверный, проверьте его", currentStage ?: primaryStage).show()
                                            }
                                        }
                                    } catch(e: Throwable) {
                                        e.printStackTrace()
                                        Platform.runLater {
                                            spinner.close()
                                            Dialogs.createAlert("Исключительная ситуация", currentStage ?: primaryStage).show()
                                        }
                                    }
                                }
                            }
                        })
                        add(JFXPasswordField().apply {
                            text = Config.getInstance().tokenTwitch
                            promptText = "Токен Twitch Chat"
                            textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                                Config.getInstance().tokenTwitch = newValue
                            })
                        })
                        hyperlink("Откуда взять токен Twitch Chat") {
                            action {
                                hostServices.showDocument("https://twitchapps.com/tmi/")
                            }
                        }
                    }
                }
                separator()
                hyperlink("Условия использования YouTube") {
                    action {
                        hostServices.showDocument("https://www.youtube.com/t/terms")
                    }
                }
                hyperlink("Политика конфиденциальности Google") {
                    action {
                        hostServices.showDocument("https://policies.google.com/privacy")
                    }
                }
                hyperlink("Страница настроек безопасности Google") {
                    action {
                        hostServices.showDocument("https://myaccount.google.com/permissions?pli=1")
                    }
                }

            }
        }
        checkUpdate()
    }



}




package ru.pshiblo.gui.views

import com.jfoenix.animation.alert.JFXAlertAnimation
import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXDialogLayout
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.stage.Modality
import ru.pshiblo.github.updating.UpdateApplication
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.gui.fragments.BrowserFragment
import ru.pshiblo.gui.fragments.UpdateFragment
import ru.pshiblo.services.youtube.YouTubeAuth
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

                val button = Buttons.createButton("Войти", FontAwesomeIcon.GOOGLE)
                add(button)
                button.action {
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
                                replaceWith<MusicView>()
                            }
                        }
                    }
                }
            }
            vbox(10, Pos.CENTER) {
                hyperlink("Войти через браузер") {
                    action {
                        if (!YouTubeAuth.auth()) {
                            Dialogs.createAlert("Авторизация не прошла, попробуйте снова", currentStage ?: primaryStage).show()
                        } else {
                            replaceWith<MusicView>()
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
                checkUpdate()
            }
        }
    }



}




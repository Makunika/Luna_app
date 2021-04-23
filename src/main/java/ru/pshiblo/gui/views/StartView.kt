package ru.pshiblo.gui.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.application.Platform
import javafx.geometry.Pos
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.gui.fragments.Browser
import ru.pshiblo.services.youtube.YouTubeAuth
import tornadofx.*

class StartView: View("Luna") {

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
                                    find<Browser>(mapOf(Browser::url to url)).openModal()
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
            }
        }
    }



}




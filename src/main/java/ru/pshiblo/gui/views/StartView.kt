package ru.pshiblo.gui.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.geometry.Pos
import ru.pshiblo.gui.factory.Alerts
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.services.youtube.YouTubeAuth
import tornadofx.*

class StartView: View("YouTube Chat") {

    override var root = hbox(alignment = Pos.CENTER) {
        vbox(10, alignment = Pos.CENTER) {
            vbox(spacing = 50, alignment = Pos.CENTER) {

                label("YouTube Chat Application") {
                    style {
                        fontSize = 50.px
                    }
                }

                val button = Buttons.createButton("Войти", FontAwesomeIcon.GOOGLE)
                add(button)
                button.action {
                    if (!YouTubeAuth.auth()) {
                        Alerts.createAlert("Авторизация не прошла, попробуйте снова", currentStage ?: primaryStage).show()
                    } else {
                        replaceWith<MusicView>()
                    }
                }
            }
            vbox(10, Pos.CENTER) {
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




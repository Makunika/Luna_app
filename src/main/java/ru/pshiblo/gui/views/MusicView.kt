package ru.pshiblo.gui.views

import com.jfoenix.controls.JFXProgressBar
import javafx.geometry.Pos
import ru.pshiblo.Config
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.services.Context
import ru.pshiblo.services.audio.discord.DiscordMusicService
import ru.pshiblo.services.audio.local.LocalMusicService
import ru.pshiblo.services.http.HttpService
import tornadofx.*

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

            Buttons.createButton("Использовать локальную музыку", size = 20.0).also {
                add(it)
                it.action {
                    Config.getInstance().isDiscord = false

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
                    Config.getInstance().isDiscord = true

                    this.isDisable = true
                    pb.isVisible = true
                    runAsync {
                        Context.addServiceAndStart(DiscordMusicService())
                        Context.addServiceAndStart(HttpService())
                    } ui {
                        this.isDisable = false
                        pb.isVisible = false
                        replaceWith<MainView>()
                    }
                }
            }

            add(pb)
        }
    }
}
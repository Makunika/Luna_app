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

class MusicView : View("YouTube Chat") {
    override val root = vbox(30, Pos.CENTER) {
        hbox(20, Pos.CENTER) {
            Buttons.createButton("Использовать локальную музыку", size = 20.0).also {
                add(it)
                it.action {
                    Config.getInstance().isDiscord = false

                    this.isDisable = true
                    this@vbox.add(JFXProgressBar().apply {
                        progress = -1.0
                        maxWidth = 300.0
                    })
                    runAsync {
                        Context.addServiceAndStart(LocalMusicService())
                        Context.addServiceAndStart(HttpService())
                    } ui {
                        replaceWith<TabView>()
                    }
                }
            }
            Buttons.createButton("Использовать дискорд бота для музыки", size = 20.0).also {
                add(it)
                it.action {
                    Config.getInstance().isDiscord = true

                    this.isDisable = true
                    this@vbox.add(JFXProgressBar().apply {
                        progress = -1.0
                        maxWidth = 300.0
                    })
                    runAsync {
                        Context.addServiceAndStart(DiscordMusicService())
                        Context.addServiceAndStart(HttpService())
                    } ui {
                        replaceWith<TabView>()
                    }
                }
            }
        }
    }
}

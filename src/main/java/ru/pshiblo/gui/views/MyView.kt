package ru.pshiblo.gui.views

import ru.pshiblo.Config
import ru.pshiblo.services.Context
import ru.pshiblo.services.audio.local.LocalMusicService
import ru.pshiblo.services.audio.discord.DiscordMusicService
import ru.pshiblo.services.http.HttpService
import tornadofx.*

class MyView: View("YouTube Chat") {

    override var root =  tabpane {
         tab("Настройки") {
             borderpane {
                 center {
                     vbox(20) {
                         label("Использовать дискорд бота для музыки?")
                         button("Да") {
                             action {
                                 Config.getInstance().isDiscord = true
                                 this.isDisable = true
                                 this.text = "Загрузка... ботов"
                                 Context.addServiceAndStart(DiscordMusicService())
                                 Context.addServiceAndStart(HttpService())
                                 updateRoot()
                             }
                         }
                         button("Нет") {
                             action {
                                 Config.getInstance().isDiscord = false
                                 this.isDisable = true
                                 this.text = "Загрузка... локальной музыки"
                                 Context.addServiceAndStart(LocalMusicService())
                                 Context.addServiceAndStart(HttpService())
                                 updateRoot()
                             }
                         }
                     }
                 }
             }
         }
    }

    private fun updateRoot() {
        root.tabs.clear()
        root.tab<Init>()
        root.tab<Console>()
        root.tab<Chat>()
    }
}




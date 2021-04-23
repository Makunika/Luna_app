package ru.pshiblo.gui.views

import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import ru.pshiblo.Config
import ru.pshiblo.services.Context
import ru.pshiblo.services.ServiceType
import ru.pshiblo.services.keypress.GlobalKeyListenerService
import ru.pshiblo.services.youtube.ChatListService
import ru.pshiblo.services.youtube.ChatPostService
import ru.pshiblo.services.youtube.YouTubeAuth
import ru.pshiblo.services.youtube.listener.UpdatedCommand
import tornadofx.*

class Init:Fragment("Настройки") {

    private val videoId = TextField()
    private val maxTimeAudio = SimpleLongProperty()
    private val timeInsert = SimpleLongProperty()
    private val timeList = SimpleLongProperty()
    private val globalListener = checkbox {
        isSelected = false
    }
    private val configYouTube = Config.getInstance()

    init {
        maxTimeAudio.set(configYouTube.maxTimeTrack / 1000)
        timeInsert.set(configYouTube.timeInsert / (1000 * 60))
        timeList.set(configYouTube.timeList / 1000)

        maxTimeAudio.addListener(ChangeListener { observable, oldValue, newValue ->
            configYouTube.maxTimeTrack = newValue as Long * 1000
        })
        timeInsert.addListener(ChangeListener { observable, oldValue, newValue ->
            configYouTube.timeInsert = newValue as Long * 60 * 1000
        })
        timeList.addListener(ChangeListener { observable, oldValue, newValue ->
            configYouTube.timeList = newValue as Long * 1000
        })

    }

    override val root = borderpane {
        center {
            form {
                fieldset( if (Config.getInstance().isDiscord) "Для начала введи в дискорде команду !connect <название канала>" else "Настройки") {
                    field("id трансляции (после v=)") {
                        add(videoId)
                    }
                    field("Максимальное время одного трека (в cекундах)") {
                        textfield(maxTimeAudio)
                    }
                    field("Задержка между сообщениями о боте в чат (в минутах)") {
                        textfield(timeInsert)
                    }
                    field("Задержка между проверками сообщений на команду ./track (в секундах)") {
                        textfield(timeList)
                    }
                    field("F12 для стоп музыки включать?") {
                        add(globalListener)
                    }
                    field("Громкость музыки локальной: " ) {
                        slider(0,100, 100) {
                            this.valueProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                                Context.getMusicService().volume(newValue.toInt())
                                this@field.text = "Громкость музыки: ${newValue.toInt()}"
                            })
                        }
                    }
                    button("Начать") {
                        action {
                            if (validate()) {
                                if (globalListener.isSelected) {
                                    Context.addServiceAndStart(GlobalKeyListenerService())
                                }
                                configYouTube.videoId = videoId.text
                                println(Config.getInstance().toString())
                                if (!YouTubeAuth.setLiveChatId()) {
                                    alert(Alert.AlertType.ERROR, "YouTube", "невалидный id трансляции")
                                    return@action
                                }
                                Context.addServiceAndStart(ChatListService())
                                Context.addServiceAndStart(ChatPostService())
                                this@borderpane.add(text("Работает!"));
                                this.isDisable = true
                                globalListener.isDisable = true
                                videoId.isDisable = true
                                alert(Alert.AlertType.INFORMATION, "", "Можно изменять данные, который не стали серыми.")
                            }
                        }
                    }
                    button("Пропустить музыку") {
                        action {
                            Context.getMusicService().skip()
                        }
                    }
                }
                fieldset("Дополнительная команда") {
                    field("Команда (К примеру /command)") {
                        textfield(UpdatedCommand.getInstance().commandProperty())
                    }
                    field("Ответ на эту команду") {
                        textfield(UpdatedCommand.getInstance().answerProperty())
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        if (Config.getInstance().isDiscord && !Context.isInitService(ServiceType.MUSIC)) {
            alert(Alert.AlertType.ERROR, "Discord", "Забыл написать в канале !connect <название канала>")
            return false
        }
        if (videoId.text.isNullOrEmpty()) {
            alert(Alert.AlertType.ERROR, "YouTube", "Забыл написать id канала")
            return false
        }
        return true
    }
}
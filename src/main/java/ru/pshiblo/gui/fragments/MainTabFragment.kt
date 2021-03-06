package ru.pshiblo.gui.fragments

import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXSlider
import com.jfoenix.controls.JFXTextField
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import ru.pshiblo.Config
import ru.pshiblo.gui.ConfigGUI
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.gui.factory.Buttons
import ru.pshiblo.services.Context
import ru.pshiblo.services.ServiceType
import ru.pshiblo.services.broadcast.twitch.TwitchChatListService
import ru.pshiblo.services.broadcast.twitch.TwitchChatPostService
import ru.pshiblo.services.keypress.GlobalKeyListenerService
import ru.pshiblo.services.broadcast.youtube.YTChatListService
import ru.pshiblo.services.broadcast.youtube.YTChatPostService
import ru.pshiblo.services.broadcast.youtube.YouTubeAuth
import tornadofx.*
import kotlin.reflect.KMutableProperty

class MainTabFragment : Fragment("Главная") {

    private var isRun = false

    private val videoIdTextField = JFXTextField().apply {
        promptText = "ID трансляции YouTube / Канал Twitch"
        isLabelFloat = true
        maxWidth = 200.0
        if (ConfigGUI.isTwitch) {
            text = Config.getInstance().twitchChannelName
            Config.getInstance().videoId = Config.getInstance().twitchChannelName
        } else {
            text = ""
            Config.getInstance().videoId = ""
        }
        this.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            Config.getInstance().videoId = newValue
            if (ConfigGUI.isTwitch) {
                Config.getInstance().twitchChannelName = newValue
            }
        })
    }

    val btnStart = Buttons.createButton("Запустить", size = 20.0).also { btn ->
        btn.action {
            if (isRun) {
                val spinner = Dialogs.createSpinner(currentStage ?: primaryStage)
                spinner.show()
                runAsync {
                    Context.removeService(ServiceType.CHAT_POST)
                    Context.removeService(ServiceType.CHAT_LIST)
                } ui {
                    spinner.close()
                    videoIdTextField.isDisable = false
                    btn.text = "Запустить"
                    isRun = false
                }
            } else {
                if (validate()) {
                    if (ConfigGUI.isTwitch) {
                        Dialogs.createAlert("Правильность Twitch канала нельзя, поэтому если что перезапустите сервис!", currentStage ?: primaryStage).show()
                    }
                    if (!ConfigGUI.isTwitch && !YouTubeAuth.setLiveChatId()) {
                        Dialogs.createAlert("Неверный id трансляции", currentStage ?: primaryStage).show()
                        return@action
                    }
                    runAsync {
                        if (ConfigGUI.isTwitch) {
                            Context.addServiceAndStart(TwitchChatListService())
                            Context.addServiceAndStart(TwitchChatPostService())
                        } else {
                            Context.addServiceAndStart(YTChatListService())
                            Context.addServiceAndStart(YTChatPostService())
                        }

                    } ui {
                        videoIdTextField.isDisable = true
                        btn.text = "Остановить"
                        isRun = true
                    }
                }
            }

        }
    }

    override val root = borderpane {
        style {
            paddingTop = 20
        }

        top {
            vbox(20, Pos.TOP_LEFT) {
                vbox(5) {
                    label("Обязательные настройки перед запуском") {
                        style {
                            fontWeight = FontWeight.MEDIUM
                            fontSize = 18.px
                        }
                    }
                    label("Перед запуском необходимо написать в дискорд текстовом канале !connect <название голосового канал>") {
                        visibleWhen(ConfigGUI.isDiscordProperty)
                    }
                    add(videoIdTextField)
                }
                separator(Orientation.HORIZONTAL)
                label("Настройки") {
                    style {
                        fontWeight = FontWeight.MEDIUM
                        fontSize = 18.px
                    }
                }
                vbox(30) {
                    add(JFXCheckBox("Использовать кнопку F12 для пропуска музыки").apply {
                        style {
                            fontSize = 12.px
                        }
                        this.selectedProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                            if (newValue) {
                                Context.addServiceAndStart(GlobalKeyListenerService())
                            } else {
                                Context.removeService(ServiceType.KEYPRESS)
                            }
                        })
                        checkedColor = c("#4da5f6")
                    })
                    add(JFXTextField((Config.getInstance().maxTimeTrack / 1000).toString()).apply {
                        style {
                            fontSize = 14.px
                        }
                        promptText = "Максимальное время одного трека (в cекундах)"
                        isLabelFloat = true
                        maxWidth = 400.0
                        this.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                            val num = newValue.toLongOrNull()
                            if (num != null) {
                                Config.getInstance().maxTimeTrack = num * 1000
                            } else {
                                Dialogs.createAlert("Только числа!", currentStage ?: primaryStage).show()
                                this.text = (Config.getInstance().maxTimeTrack / 1000).toString()
                            }
                        })
                    })
                    add(JFXTextField((Config.getInstance().timeInsert / (1000 * 60)).toString()).apply {
                        style {
                            fontSize = 14.px
                        }
                        promptText = "Задержка между сообщениями о боте в чат (в минутах)"
                        isLabelFloat = true
                        maxWidth = 400.0
                        this.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                            val num = newValue.toLongOrNull()
                            if (num != null) {
                                Config.getInstance().timeInsert = num * 60 * 1000
                            } else {
                                Dialogs.createAlert("Только числа!", currentStage ?: primaryStage).show()
                                this.text = (Config.getInstance().timeInsert / (1000 * 60)).toString()
                            }
                        })
                    })
                    add(JFXTextField((Config.getInstance().timeList / 1000).toString()).apply {
                        style {
                            fontSize = 14.px
                        }
                        promptText = "Задержка между получением сообщений чата (в секундах)"
                        isLabelFloat = true
                        maxWidth = 400.0
                        enableWhen(ConfigGUI.isTwitchProperty.not())
                        this.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                            val num = newValue.toLongOrNull()
                            if (num != null) {
                                Config.getInstance().timeList = num * 1000
                            } else {
                                Dialogs.createAlert("Только числа!", currentStage ?: primaryStage).show()
                                this.text = (Config.getInstance().timeList / 1000).toString()
                            }
                        })
                    })
                }
                separator(Orientation.HORIZONTAL)
                hbox(10, Pos.CENTER_LEFT) {
                    label("Громкость музыки") {
                        style {
                            fontSize = 14.px
                        }
                    }
                    add(JFXSlider().apply {
                        maxWidth = 400.0
                        prefWidth = 400.0
                        max = 100.0
                        min = 0.0
                        value = 100.0
                        valueProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                            Context.getMusicService().volume(newValue.toInt())
                        })
                    })
                }
                separator(Orientation.HORIZONTAL)
                Buttons.createButton("Пропустить трек", size = 14.0).also {
                    add(it)
                    it.action {
                        Context.getMusicService().skip()
                    }
                }

            }
        }

        bottom {
            hbox(20, Pos.BOTTOM_RIGHT) {
                hyperlink("Страница настроек безопасности Google") {
                    alignment = Pos.BOTTOM_LEFT
                    action {
                        hostServices.showDocument("https://myaccount.google.com/permissions?pli=1")
                    }
                }
                style {
                    paddingTop = 20
                }
                add(btnStart)
            }
        }
    }

    private fun validate(): Boolean {
        if (ConfigGUI.isDiscord && !Context.isInitService(ServiceType.MUSIC)) {
            Dialogs.createAlert("Забыл написать в канале !connect <название канала>", currentStage ?: primaryStage).show()
            return false
        }
        if (Config.getInstance().videoId.isNullOrEmpty()) {
            Dialogs.createAlert("Забыл написать id трансляции/название канала", currentStage ?: primaryStage).show()
            return false
        }
        return true
    }
}

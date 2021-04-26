package ru.pshiblo.gui.views

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.image.Image
import javafx.scene.shape.Circle
import javafx.scene.text.FontWeight
import javafx.util.StringConverter
import ru.pshiblo.Config
import ru.pshiblo.gui.ConfigGUI
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.gui.fragments.TabFragment
import ru.pshiblo.services.Context
import ru.pshiblo.services.broadcast.youtube.YouTubeAuth
import tornadofx.*

class MainView: View("Luna") {



    override val root = borderpane {
        left {
            anchorpane {
                vbox(10, Pos.BOTTOM_LEFT) {
                    anchorpaneConstraints {
                        bottomAnchor = 10.0
                        leftAnchor = 10.0
                    }

                    add(JFXButton("Выйти из аккаунта").apply {
                        val iconView = MaterialIconView(MaterialIcon.EXIT_TO_APP)
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
                            val spinner = Dialogs.createSpinner(currentStage ?: primaryStage)
                            spinner.show()
                            runAsync {
                                Context.removeAllService()
                                YouTubeAuth.exit()
                            } ui {
                                spinner.close()
                                replaceWith<StartView>()
                            }
                        }
                        visibleWhen(ConfigGUI.isTwitchProperty.not())

                    })

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
                            val spinner = Dialogs.createSpinner(currentStage ?: primaryStage)
                            spinner.show()
                            runAsync {
                                Context.removeAllService()
                            } ui {
                                spinner.close()
                                replaceWith<MusicView>()
                            }
                        }
                    })
                }
                vbox(5.0) {
                    minWidth = 150.0
                    anchorpaneConstraints {
                        topAnchor = 10.0
                        leftAnchor = 10.0
                    }


                    label {
                        style {
                            fontSize = 18.px
                            fontWeight = FontWeight.LIGHT
                            textFill = c("#4da5f6")
                        }
                        isWrapText = true
                        textProperty().bindBidirectional(ConfigGUI.isTwitchProperty, object: StringConverter<Boolean>() {
                            override fun toString(`object`: Boolean?): String {
                                return Config.getInstance().userinfo.name
                            }

                            override fun fromString(string: String?): Boolean {
                                return ConfigGUI.isTwitch
                            }
                        })
                        prefWidthProperty().bind(this@vbox.widthProperty());
                    }
                    var image: Image? = null

                    runAsync {
                        image = Image(Config.getInstance().userinfo.picture, 48.0, 48.0, false, true)
                    } ui {
                        val imageView = imageview(image!!) {
                            setPrefSize(48.0,48.0)
                        }
                        val circle = Circle(24.0, 24.0, 24.0)
                        imageView.clip = circle
                        add(imageView)
                    }
                }
            }
        }

        center {
            separator(Orientation.VERTICAL) {
                style {
                    paddingAll = 10.0
                }
            }
        }

        right {
            add(find<TabFragment>())
        }

    }
}
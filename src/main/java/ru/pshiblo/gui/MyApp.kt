package ru.pshiblo.gui

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import ru.pshiblo.Config
import ru.pshiblo.gui.factory.Dialogs
import ru.pshiblo.gui.views.StartView
import ru.pshiblo.services.Context
import tornadofx.*
import kotlin.system.exitProcess

class MyApp: App(StartView::class) {

    override fun start(stage: Stage) {
        stage.centerOnScreen()
        stage.icons.add(resources.image("/icon.png"))
        stage.width = 1100.0
        stage.height = 700.0
        stage.setOnCloseRequest {  }
        stage.setOnCloseRequest {
            it.consume()
            Dialogs.createSpinner(stage).show()
            runAsync {
                try {
                    Config.getInstance().saveConfig()
                    Context.shutdownAllService()
                    Platform.exit()
                    exitProcess(0)
                } catch(e: Throwable) {
                    exitProcess(0)
                }
            }
        }
        super.start(stage)
    }



}
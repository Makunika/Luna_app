package ru.pshiblo.gui

import javafx.stage.Stage
import ru.pshiblo.Config
import ru.pshiblo.gui.views.StartView
import ru.pshiblo.services.Context
import tornadofx.*
import kotlin.system.exitProcess

class MyApp: App(StartView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1000.0
        stage.height = 700.0
        stage.setOnCloseRequest {
            Config.getInstance().saveConfig()
            Context.shutdownAllService()
            exitProcess(0);
        }
    }



}
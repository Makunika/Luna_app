package ru.pshiblo.gui

import javafx.stage.Stage
import ru.pshiblo.Config
import ru.pshiblo.gui.views.MyView
import ru.pshiblo.services.Context
import tornadofx.*
import kotlin.system.exitProcess

class MyApp: App(MyView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1000.0
        stage.height = 600.0
        stage.setOnCloseRequest {
            Config.getInstance().saveConfig()
            Context.shutdownAllService()
            exitProcess(0);
        }
    }



}
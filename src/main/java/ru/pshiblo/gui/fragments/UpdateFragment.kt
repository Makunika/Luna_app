package ru.pshiblo.gui.fragments

import com.jfoenix.controls.JFXProgressBar
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import org.kohsuke.github.GHRelease
import ru.pshiblo.github.updating.UpdateApplication
import tornadofx.*

class UpdateFragment: Fragment("Обновление") {

    val release: GHRelease by param()
    val updater: UpdateApplication by param()

    val pb = JFXProgressBar().apply {
        this.progress = 0.0
    }

    override val root = vbox(20) {
        prefWidth = 400.0
        style {
            paddingAll = 20.0
        }
        label("Обновление ${release.name}")
        separator()
        label(release.body) {
            isWrapText = true
            maxWidth = this@vbox.prefWidth - 40
        }
        hyperlink("Обновление на сайте") {
            action {
                hostServices.showDocument(release.htmlUrl.toExternalForm())
            }
        }
        separator()
        add(pb.apply {
            this.prefWidth = this@vbox.prefWidth - 40
        })

        runAsync {
            updater.downloadUpdate { i ->
                pb.progress = i.toDouble() / 100
            }
        } ui {
            alert(Alert.AlertType.INFORMATION, "Обновление", "Обновление завершено, для установки перезапустите приложение", ButtonType.APPLY)
        }
    }
}
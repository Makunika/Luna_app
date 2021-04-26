package ru.pshiblo.gui

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.property
import tornadofx.getValue
import tornadofx.setValue

object ConfigGUI {

    val isDiscordProperty = SimpleBooleanProperty(false)
    var isDiscord by isDiscordProperty

    val isTwitchProperty = SimpleBooleanProperty(false)
    var isTwitch by isTwitchProperty

}
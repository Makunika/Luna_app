package ru.pshiblo.luna.log

import org.apache.log4j.Layout
import org.apache.log4j.WriterAppender
import java.io.StringWriter

class UiLogAppender : WriterAppender {

    companion object {
        val UI_CONSOLE = StringWriter()
    }

    constructor() {
    }

    constructor(layout: Layout?) {
        setLayout(layout)
    }

    override fun activateOptions() {
        setWriter(UI_CONSOLE)
        super.activateOptions()
    }

    override fun closeWriter() {
        UI_CONSOLE.close()
        super.closeWriter()
    }


}

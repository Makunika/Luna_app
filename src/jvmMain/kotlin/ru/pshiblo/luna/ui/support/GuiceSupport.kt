package ru.pshiblo.luna.ui.support

import androidx.compose.runtime.State
import ru.pshiblo.luna.ui.support.DI.getInstance
import kotlin.reflect.KClass

object DI {
    @JvmStatic
    var dicontainer: DIContainer? = null

    interface DIContainer {
        fun <T : Any> getInstance(type: KClass<T>): T
        fun <T : Any> getInstance(type: KClass<T>, name: String): T {
            throw AssertionError("Injector is not configured, so bean of type $type with name $name can not be resolved")
        }
    }

    inline fun <reified T : Any> DIContainer.getInstance() = getInstance(T::class)
    inline fun <reified T : Any> DIContainer.getInstance(name: String) = getInstance(T::class, name)
}

inline fun <reified T : Any> guice(name: String? = null): State<T> = object : State<T> {
    var injected: T

    init {
        val dicontainer = DI.dicontainer ?: throw AssertionError(
            "Injector is not configured, so bean of type ${T::class} cannot be resolved"
        )
        injected = dicontainer.let {
            if (name != null) {
                it.getInstance(name)
            } else {
                it.getInstance()
            }
        }
    }

    override val value: T
        get() = injected
}
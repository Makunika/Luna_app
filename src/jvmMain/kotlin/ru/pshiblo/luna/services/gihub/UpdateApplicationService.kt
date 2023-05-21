package ru.pshiblo.luna.services.gihub

import com.google.inject.ImplementedBy
import org.kohsuke.github.GHRelease
import org.kohsuke.github.GitHub
import ru.pshiblo.luna.services.properties.ApplicationProperties

@ImplementedBy(UpdateApplicationServiceImpl::class)
interface UpdateApplicationService {
    fun getUpdate() : GHRelease?
}

class UpdateApplicationServiceImpl : UpdateApplicationService {

    companion object {
        const val REPOSITORY_ID = 358708505L
    }

    override fun getUpdate(): GHRelease? {
        return runCatching {
            val gitHub = GitHub.connectAnonymously()
            val repository = gitHub.getRepositoryById(REPOSITORY_ID)
            repository.latestRelease
        }.onSuccess {
            val currentVersion = ApplicationProperties.version
            if (it.tagName!! != currentVersion) {
                return it
            }
            return null
        }.getOrNull()
    }

}


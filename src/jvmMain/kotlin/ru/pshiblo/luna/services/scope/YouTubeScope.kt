package ru.pshiblo.luna.services.scope

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponseException
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.oauth2.Oauth2
import com.google.api.services.youtube.YouTube
import com.google.inject.Singleton
import ru.pshiblo.luna.services.properties.ApplicationProperties
import ru.pshiblo.luna.services.properties.UserInfo
import ru.pshiblo.luna.utils.recursiveDelete
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

@Singleton
class YouTubeScope : Scope {

    companion object {
        private const val PATH_STORE_CREDENTIALS = "tokens"
        private const val APPLICATION_NAME = "Luna"
        private val SCOPES = listOf(
            "https://www.googleapis.com/auth/youtube",
            "https://www.googleapis.com/auth/userinfo.profile"
        )
        private const val CLIENT_SECRETS = "/client_secret.json"
    }

    private var _isAuth = false
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private lateinit var _youtubeService: YouTube
    private lateinit var _oauth2: Oauth2

    val youtubeService: YouTube
        get() = if (isAuth) _youtubeService else error("Not auth")

    override suspend fun auth() {
        if (isAuth) {
            return
        }
        try {
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val credential = authorize(httpTransport)

            _youtubeService = YouTube.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build()

            _oauth2 = Oauth2.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build()

            val userinfo = _oauth2.userinfo().get().execute()

            ApplicationProperties.userInfo = UserInfo(
                userinfo.name,
                userinfo.picture
            )

            _isAuth = true
        } catch (e: TokenResponseException) {
            exit()
            auth()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override val isAuth: Boolean
        get() = _isAuth

    fun exit() {
        recursiveDelete(PATH_STORE_CREDENTIALS)
        _isAuth = false
    }

    fun getLiveChatId(videoId: String): String {
        val requestVideo =
            youtubeService.videos().list(listOf("snippet", "contentDetails", "statistics", "liveStreamingDetails"))
                .setId(listOf(videoId))
        val response = requestVideo.execute()
        if (response.items.isNullOrEmpty()) {
            throw IllegalArgumentException("ID трансляции неверный")
        }
        return response.items[0].liveStreamingDetails.activeLiveChatId
    }

    @Throws(IOException::class)
    private fun authorize(httpTransport: NetHttpTransport): Credential {

        val inClientSecret: InputStream = this.javaClass.getResourceAsStream(CLIENT_SECRETS)
            ?: throw IllegalCallerException("Not secrets!")

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(inClientSecret))

        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecrets,
            SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(PATH_STORE_CREDENTIALS)))
            .setAccessType("online")
            .build()

        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver(), AuthorizationCodeInstalledApp::browse)
            .authorize("user")
    }
}
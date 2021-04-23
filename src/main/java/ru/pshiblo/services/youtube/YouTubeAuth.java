package ru.pshiblo.services.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.mtls.MtlsProvider;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class YouTubeAuth {

    private static final String CLIENT_SECRETS = "/client_secret.json";
    private static final String PATH_STORE_CREDENTIALS = "tokens";
    private static final Collection<String> SCOPES =
            List.of("https://www.googleapis.com/auth/youtube",
                    "https://www.googleapis.com/auth/userinfo.profile");

    private static final String APPLICATION_NAME = "Luna";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static YouTube youtubeService;

    private static Credential authorize(final NetHttpTransport httpTransport, Consumer<String> consumer) throws IOException {

        InputStream inClientSecret = YouTubeAuth.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inClientSecret));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new File(PATH_STORE_CREDENTIALS)))
                        .setAccessType("offline")
                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver(), consumer::accept).authorize("user");

        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                APPLICATION_NAME).build();
        Userinfo userinfo = oauth2.userinfo().get().execute();
        System.out.println(userinfo.toPrettyString());
        Config.getInstance().setUserinfo(userinfo);

        return credential;
    }

    public static YouTube getYoutubeService() {
        if (youtubeService == null) {
            throw new IllegalCallerException("not auth!");
        }
        return youtubeService;
    }

    public static boolean auth(Consumer<String> consumer) {
        try {
            if (youtubeService == null) {
                final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                Credential credential = authorize(httpTransport, consumer);
                youtubeService = new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            }
            return true;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean auth() {
        try {
            if (youtubeService == null) {
                final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                Credential credential = authorize(httpTransport, AuthorizationCodeInstalledApp::browse);
                youtubeService = new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            }
            return true;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() {
        youtubeService = null;
        File path = new File(PATH_STORE_CREDENTIALS);
        recursiveDelete(path);
    }

    private static void recursiveDelete(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                // рекурсивный вызов
                recursiveDelete(f);
            }
        }
        file.delete();
    }

    public static boolean setLiveChatId() {
        try {
            YouTube youtubeService = getYoutubeService();
            YouTube.Videos.List requestVideos = youtubeService.videos()
                    .list(List.of("snippet","contentDetails","statistics", "liveStreamingDetails"));
            VideoListResponse response = requestVideos.setId(List.of(Config.getInstance().getVideoId())).execute();

            if (response.getItems().size() == 0) {
                throw new IOException("id youtube not valid");
            }
            Config.getInstance().setLiveChatId(response.getItems().get(0).getLiveStreamingDetails().getActiveLiveChatId());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleOut.alert(e.getMessage());
            return false;
        }
    }
}

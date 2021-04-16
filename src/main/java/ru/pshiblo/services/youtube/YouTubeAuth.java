package ru.pshiblo.services.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;

public class YouTubeAuth {

    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
            List.of("https://www.googleapis.com/auth/youtube");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static YouTube youtubeService;

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    private static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = YouTubeAuth.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static YouTube getYoutubeService() {
        if (youtubeService == null) {
            throw new IllegalCallerException("not auth!");
        }
        return youtubeService;
    }

    public static boolean auth() {
        try {
            youtubeService = getService();
            YouTube.Videos.List requestVideos = youtubeService.videos()
                    .list(List.of("snippet","contentDetails","statistics", "liveStreamingDetails"));
            VideoListResponse response = requestVideos.setId(List.of(Config.getInstance().getVideoId())).execute();

            if (response.getItems().size() == 0) {
                throw new IOException("id youtube not valid");
            }
            Config.getInstance().setLiveChatId(response.getItems().get(0).getLiveStreamingDetails().getActiveLiveChatId());
            return true;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            ConsoleOut.alert(e.getMessage());
            return false;
        }
    }
}

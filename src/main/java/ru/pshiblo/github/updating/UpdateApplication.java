package ru.pshiblo.github.updating;

import org.kohsuke.github.*;
import ru.pshiblo.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.function.Consumer;

public class UpdateApplication {

    private String browserDownloadUrl;

    public GHRelease checkUpdate() throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepositoryById(358708505);
        GHRelease release = repository.getLatestRelease();

        if (release.getTagName().equals(Config.getInstance().getVersion()))
            return null;

        List<GHAsset> assets = release.listAssets().toList();

        for (GHAsset asset : assets) {
            if (asset.getName().equals("Luna.jar")) {
                browserDownloadUrl = asset.getBrowserDownloadUrl();
                return release;
            }
        }
        return null;
    }

    public void downloadUpdate(Consumer<Integer> downloadStatus) {
        if (browserDownloadUrl == null)
            throw new IllegalStateException("please call checkUpdate!");

        File path = new File("update");

        if (!path.exists())
            path.mkdir();

        try {
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

            URL url = new URL(browserDownloadUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int contentLength = connection.getContentLength();

            InputStream is = connection.getInputStream();

            File file = new File("update\\luna.jar");

            if (file.exists())
                file.delete();

            file.createNewFile();

            FileOutputStream os = new FileOutputStream(file);

            byte[] buffer = new byte[2048];

            int length;
            int downloaded = 0;

            while ((length = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, length);
                downloaded += length;
                downloadStatus.accept((int)(((double) downloaded / (double) contentLength) * 100.0));
                //System.out.println((int)(((double) downloaded / (double) contentLength) * 100.0));
            }

            os.close();
            is.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

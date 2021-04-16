package ru.pshiblo;

import net.dv8tion.jda.api.entities.MessageChannel;
import ru.pshiblo.property.ConfigProperties;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Config {

    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    private long maxTimeTrack;
    private long timeInsert;
    private long timeList;
    private String videoId;
    private String liveChatId;
    private String path;
    private boolean isDiscord;
    private String tokenDiscord;
    private ConfigProperties property;


    private Config() {
        try {
            path = new File(".").getCanonicalPath();
            property = new ConfigProperties(path + "\\config.properties");

            tokenDiscord = property.getProperty("tokenDiscord", null);
            if (tokenDiscord == null) {
                System.out.println("token discord is null!");
            }

            timeInsert = property.getLongProperty("timeInsert", 5 * 60 * 1000);
            maxTimeTrack = property.getLongProperty("maxTimeTrack",3 * 60 * 1000);
            timeList = property.getLongProperty("timeList",20 * 1000);
            isDiscord = false;
            System.out.println(this.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            property.setProperty("timeInsert", Long.toString(timeInsert));
            property.setProperty("timeList", Long.toString(timeList));
            property.setProperty("maxTimeTrack", Long.toString(maxTimeTrack));
            property.store(new FileOutputStream(path + "\\config.properties"), "by Pshiblo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getMaxTimeTrack() {
        return maxTimeTrack;
    }

    public void setMaxTimeTrack(long maxTimeTrack) {
        this.maxTimeTrack = maxTimeTrack;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getPath() {
        return path;
    }

    public String getLiveChatId() {
        return liveChatId;
    }

    public void setLiveChatId(String liveChatId) {
        this.liveChatId = liveChatId;
    }

    public long getTimeInsert() {
        return timeInsert;
    }

    public void setTimeInsert(long timeInsert) {
        this.timeInsert = timeInsert;
    }

    public long getTimeList() {
        return timeList;
    }

    public void setTimeList(long timeList) {
        this.timeList = timeList;
    }

    public boolean isDiscord() {
        return isDiscord;
    }

    public void setDiscord(boolean discord) {
        isDiscord = discord;
    }

    public String getTokenDiscord() {
        return tokenDiscord;
    }

    @Override
    public String toString() {
        return "Config{" +
                "maxTimeTrack=" + maxTimeTrack +
                ", timeInsert=" + timeInsert +
                ", timeList=" + timeList +
                ", videoId='" + videoId + '\'' +
                ", liveChatId='" + liveChatId + '\'' +
                ", path='" + path + '\'' +
                ", isDiscord=" + isDiscord +
                ", tokenDiscord='" + tokenDiscord + '\'' +
                ", property=" + property +
                '}';
    }
}

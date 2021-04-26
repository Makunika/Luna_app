package ru.pshiblo;

import com.google.api.services.oauth2.model.Userinfo;
import net.dv8tion.jda.api.entities.MessageChannel;
import ru.pshiblo.property.ConfigProperties;
import ru.pshiblo.property.Property;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Config {

    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    @Property(propertyName = "maxTimeTrack", defaultValue = "180000")
    private long maxTimeTrack;

    @Property(propertyName = "timeInsert", defaultValue = "300000")
    private long timeInsert;

    @Property(propertyName = "timeList", defaultValue = "20000")
    private long timeList;

    @Property(propertyName = "tokenDiscord")
    private String tokenDiscord;

    @Property(propertyName = "tokenTwitch")
    private String tokenTwitch;

    @Property(propertyName = "twitchChannelName")
    private String twitchChannelName;

    private String videoId;
    private String liveChatId;
    private String path;
    private Userinfo userinfo;

    private ConfigProperties property;

    private Config() {
        try {
            path = new File(".").getCanonicalPath();// + "\\bin";
            property = new ConfigProperties(path + "\\config.properties");

            doRefresh();

            if (tokenDiscord == null) {
                System.out.println("token discord is null!");
            }

            //System.out.println(this.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return "0.1.7";
    }

    public void doRefresh() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Property.class)) {
                Property annotation = field.getAnnotation(Property.class);

                String propertyName = annotation.propertyName();
                String defaultValue = annotation.defaultValue();

                String value = property.getProperty(propertyName, defaultValue);
                try {
                    if (field.getType().isPrimitive()) {
                        if (field.getType().getSimpleName().equals("long")) {
                            long defaultLong = Long.parseLong(defaultValue);
                            field.setLong(this, property.getLongProperty(propertyName, defaultLong));
                        }
                    } else if (field.getType().getSimpleName().equals("String")) {
                            field.set(this, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void saveConfig() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Property.class)) {
                    Property annotation = field.getAnnotation(Property.class);
                    String propertyName = annotation.propertyName();

                    try {
                        String s = field.get(this).toString();
                        property.setProperty(propertyName, s);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

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

    public String getTokenDiscord() {
        return tokenDiscord;
    }

    public void setTokenDiscord(String tokenDiscord) {
        this.tokenDiscord = tokenDiscord;
    }

    public Userinfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(Userinfo userinfo) {
        this.userinfo = userinfo;
    }

    public String getTokenTwitch() {
        return tokenTwitch;
    }

    public void setTokenTwitch(String tokenTwitch) {
        this.tokenTwitch = tokenTwitch;
    }

    public String getTwitchChannelName() {
        return twitchChannelName;
    }

    public void setTwitchChannelName(String twitchChannelName) {
        this.twitchChannelName = twitchChannelName;
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
                ", tokenDiscord='" + tokenDiscord + '\'' +
                ", property=" + property +
                '}';
    }
}

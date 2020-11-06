package me.diniamo;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Config implements SettingsHolder {
    private Config() {}

    public static final Property<String> BOT_TOKEN = newProperty("token", "");
    public static final Property<String> BOT_PREFIX = newProperty("prefix", "|");
    public static final Property<String> FFMPEG = newProperty("ffmpeg", "ffmpeg");
    public static final Property<Integer> MAX_CACHE_SIZE_PER_GUILD = newProperty("max-cache-size-per-guild", 5);
}

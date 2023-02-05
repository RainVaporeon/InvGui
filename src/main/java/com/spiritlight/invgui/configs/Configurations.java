package com.spiritlight.invgui.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.spiritlight.invgui.Main;
import com.spiritlight.invgui.exceptions.ProcessException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Configurations {
    private static boolean flag = false;

    // Deprecated: Accounts via tokens are not valid the next day,
    // todo: Create an account manager that allows login via account/pw instead.
    // alternatively a login stuff
    public static void getConfig() throws ProcessException {
        File config = new File("config/InvGui.json");
        try {
            if (config.exists()) {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = (JsonObject)parser.parse(Files.newBufferedReader(config.toPath(), StandardCharsets.UTF_8));
                for (JsonElement element : jsonObject.getAsJsonArray("login")) {
                    Main.accountHash.add(element.getAsString());
                }
            } else {
                writeConfig();
            }
        } catch (Exception e) {
            try {
                writeConfig();
            } catch (IOException ex) {
                throw new ProcessException("Unable to load the configurations!");
            }
            if(flag) {
                throw new ProcessException("Unable to load the configurations!");
            }
            flag = true;
            getConfig();
        }
    }

    public static void writeConfig() throws IOException {
        JsonWriter writer = new JsonWriter(Files.newBufferedWriter(Paths.get("config/InvGui.json"), StandardCharsets.UTF_8));
        writer.beginObject();
        writer.name("login");
        writer.beginArray();
        for (String acc : Main.accountHash) {
            writer.value(acc);
        }
        writer.endArray();
        writer.endObject();
        writer.close();
    }
}

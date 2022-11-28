package com.spiritlight.invgui.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.spiritlight.invgui.exceptions.InvalidCredentialException;
import com.spiritlight.invgui.mixins.IMixinMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.nio.charset.StandardCharsets;
import java.util.*;

// dev note: encryption etc is still a wip.
public class SessionUtils {
    private static final Session session = Minecraft.getMinecraft().getSession();
    /**
     * The default session.
     */
    public static final Session DEFAULT = newInstance(session);

    private static final int INVERT = 0xff;

    public static Session getSession() {
        return Minecraft.getMinecraft().getSession();
    }

    /**
     * @see IMixinMinecraft
     */
    public static IMixinMinecraft getMinecraft() {
        return (IMixinMinecraft) (Minecraft.getMinecraft());
    }

    public static boolean sessionEquals(Session s1, Session s2) {
        if(s1 == null ^ s2 == null) return false; // Either are null
        if(s1 == s2) return true; // Both are null
        return s1.getPlayerID().equals(s2.getPlayerID()) && s1.getUsername().equals(s2.getUsername()) && s1.getToken().equals(s2.getToken());
    }

    /**
     * Encrypts the session for given password
     * @param s The session
     * @param password The password
     * @return An encrypted hash of this session.
     */
    public static String encrypt(Session s, long password) {
        JsonObject object = new JsonObject();
        final Random random = new Random(password);
        object.addProperty("username", s.getUsername());
        object.addProperty("uuid", s.getPlayerID());
        object.addProperty("token", s.getToken());
        String ret = object.toString();
        System.out.println("ret= " + ret);
        byte[] val = Base64.getEncoder().encode(ret.getBytes(StandardCharsets.UTF_8));
        /*
        System.out.println("val=" + new String(val, StandardCharsets.UTF_8));
        for(int i = 0; i < val.length; i++) {
            if (random.nextBoolean())
                val[i] ^= INVERT;
        }
        */
        System.out.println("returned_val=" + new String(val, StandardCharsets.UTF_8));
        return new String(val, StandardCharsets.UTF_8);
    }

    /**
     * Decrypts this supplied hash for supplied password.
     * @param hash The hash to retrieve session from
     * @param password The password used to decrypt it
     * @return The decrypted session, or throw {@link com.spiritlight.invgui.exceptions.InvalidCredentialException} if password is invalid.
     * @throws InvalidCredentialException if the password is incorrect, or the hash supplied is invalid.
     */
    public static Session decrypt(String hash, long password) throws InvalidCredentialException {
        byte[] decode = hash.getBytes(StandardCharsets.UTF_8);
        /*
        System.out.println("decode=" + new String(decode));
        final Random random = new Random(password);
        for(int i=0; i<decode.length; i++) {
            if(random.nextBoolean())
                decode[i] ^= INVERT;
        }*/
        System.out.println("result=" + new String(decode, StandardCharsets.UTF_8));
        String output = new String(decode, StandardCharsets.UTF_8);
        System.out.println("output=" + output);
        String json = new String(Base64.getDecoder().decode(output.getBytes(StandardCharsets.UTF_8)));
        try {
            JsonObject object = new Gson().fromJson(json, JsonObject.class);
            String username = object.get("username").getAsString();
            String uuid = object.get("uuid").getAsString();
            String token = object.get("token").getAsString();
            return new Session(username, uuid, token, "SpiritTree");
        } catch (JsonParseException | NullPointerException ex) {
            throw new InvalidCredentialException("The supplied hash or password is wrong!");
        }
    }

    /**
     * Utility method to mass-decrypt assuming the password is the same.<br>
     * Especially useful if the password is known to be the same, invalid ones will be ignored.
     * @param hash The list of hashes to decrypt
     * @param password The singleton password to decrypt.
     * @return The decrypted hashes, or an exception if any of the hash cannot be decrypted.
     */
    public static List<Session> decrypt(List<String> hash, long password) {
        final List<Session> ret = new ArrayList<>();
        for(String h : hash) {
            try {
                ret.add(decrypt(h, password));
            } catch (InvalidCredentialException e) {
                //
            }
        }
        return ret;
    }

    public static Session newInstance(Session s) {
        return new Session(s.getUsername(), s.getPlayerID(), s.getToken(), "SpiritTree");
    }
}

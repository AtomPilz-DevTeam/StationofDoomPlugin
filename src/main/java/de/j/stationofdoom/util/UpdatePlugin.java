package de.j.stationofdoom.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.j.stationofdoom.cmd.VersionCMD;
import de.j.stationofdoom.main.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class UpdatePlugin {

    private boolean run = false;
    public UpdatePlugin(boolean enabled) throws IOException {
        if (enabled && updateAvailable())
            run = true;
    }

    public void run() {
        if (run) {
            Main.getMainLogger().entering("UpdatePlugin", "update");
            update();
        }
    }

    private boolean updateAvailable() throws IOException {
        Main.getMainLogger().info("[Update] Plugin version: " + Main.version);
        String latest = VersionCMD.getLatestTagName();
        Main.getMainLogger().info("[Update] Latest version: " + latest);
        boolean update = !latest.equalsIgnoreCase("v" + Main.version);
        Main.getMainLogger().info("[Update] " + (update ? "There is an update available!" : "There is no update available!"));
        return update;
    }

    private void update() {
        try {
            URLConnection con = getLatestVersionDownloadURL().openConnection();
            InputStream inputStream = con.getInputStream();//TODO: Delete old file an restart server
            File file = new File("plugins//Stationofdoom-" + VersionCMD.getLatestTagName() + ".jar");
            if (!file.exists())
                if (!file.createNewFile())
                    throw new RuntimeException("[Update] Failed to create file");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int byteRead;
            Main.getMainLogger().log(Level.FINE, "[Update] Starting to write bytes");
            while ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Main.getMainLogger().severe(e.getMessage());
        } finally {
            Main.getMainLogger().info("[Update] Finished plugin update function");
        }
    }

    private URL getLatestVersionDownloadURL() throws IOException {
        final URL releaseURL = new URL("https://api.github.com/repos/AtomPilz-DevTeam/StationofdoomPlugin/releases/latest");
        HttpURLConnection con = (HttpURLConnection) releaseURL.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder responseBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseBuilder.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        JsonObject latestTag = gson.fromJson(responseBuilder.toString(), JsonObject.class);

        String browserDownloadUrl = latestTag.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();

        URL url = new URL(browserDownloadUrl);
        Main.getMainLogger().log(Level.FINE, "[Update] Latest release download jar: " + url);
        return url;
    }
}

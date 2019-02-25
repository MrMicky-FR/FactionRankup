package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Checker {

    private FactionRankup plugin;
    private boolean valid = true;
    private String username = "";

    public Checker(FactionRankup plugin) {
        this.plugin = plugin;
        checkValid();
        loadUsername();

        if (valid) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, this::checkUpdate);
        }
    }

    private void checkValid() {
        String link = String.format("https://mrmicky.fr/verify.php?plugin=%s&uid=%s&nonce=%s&version=%s&faction=%s", plugin.getName(), FactionRankup.USER_ID, FactionRankup.NONCE_ID, plugin.getDescription().getVersion(), plugin.getFactionType());

        try {
            URL url = new URL(link);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                StringBuilder builder = new StringBuilder();
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    builder.append(inputLine).append('$');
                }

                if (builder.toString().contains(FactionRankup.USER_ID) || builder.toString().contains("refused")) {
                    valid = false;
                    plugin.getLogger().severe(" ");
                    plugin.getLogger().severe("*** THIS PLUGIN ID IS BLACKLISTED ! Please contact MrMicky on SpigotMC ! ***");
                    if (builder.toString().contains("id=") && builder.toString().contains("$")) {
                        plugin.getLogger().severe("***  REASON: " + builder.toString().split("\\$")[1] + " ***");
                    }
                    plugin.getLogger().severe("*** THE PLUGIN WILL DISABLE NOW ***");
                    plugin.getLogger().severe(" ");
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private void loadUsername() {
        try {
            URL url = new URL("https://www.spigotmc.org/members/" + FactionRankup.USER_ID);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder builder = new StringBuilder();
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    builder.append(inputLine);
                }

                username = builder.toString().split("<title>")[1].split("</title>")[0].split(" | ")[0] + " ";
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void checkUpdate() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=43316");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String lastVersion = reader.readLine();
                if (!plugin.getDescription().getVersion().equalsIgnoreCase(lastVersion)) {
                    plugin.getLogger().warning("A new version is available ! Last version is " + lastVersion + " and you are on " + plugin.getDescription().getVersion());
                    plugin.getLogger().warning("You can download it on: " + plugin.getDescription().getWebsite());
                }
            }
        } catch (IOException e) {
            // Don't display an error
        }
    }

    public boolean isValid() {
        return valid;
    }

    public String getUsername() {
        return username;
    }
}

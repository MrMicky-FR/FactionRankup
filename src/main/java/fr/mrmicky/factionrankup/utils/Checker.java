package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.FactionRankup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Checker {

    private final FactionRankup plugin;

    public Checker(FactionRankup plugin) {
        this.plugin = plugin;
    }

    public void checkUpdate() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=43316");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String version = plugin.getDescription().getVersion();
                String lastVersion = reader.readLine();
                if (!plugin.getDescription().getVersion().equalsIgnoreCase(lastVersion)) {
                    plugin.getLogger().warning("A new version is available ! Last version is " + lastVersion + " and you are on " + version);
                    plugin.getLogger().warning("You can download it on: " + plugin.getDescription().getWebsite());
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public boolean isValid() {
        String rawUrl = String.format("https://mrmicky.fr/verify.php?plugin=%s&uid=%s&nonce=%s&version=%s&faction=%s", plugin.getName(), FactionRankup.USER_ID, FactionRankup.NONCE_ID, plugin.getDescription().getVersion(), plugin.getFactionType());

        try {
            StringBuilder str = new StringBuilder();

            URL url = new URL(rawUrl);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String inputLine;

                while ((inputLine = br.readLine()) != null) {
                    str.append(inputLine).append('%');
                }
            }

            String result = str.toString();

            if (result.contains(FactionRankup.USER_ID) || result.contains("refused")) {
                plugin.getLogger().severe(" ");
                plugin.getLogger().severe("*** THIS PLUGIN ID IS BLACKLISTED ! Please contact MrMicky on SpigotMC ! ***");
                if (result.contains("id=") && result.contains("%")) {
                    plugin.getLogger().severe("*** " + result.split("%")[1] + " ***");
                }
                plugin.getLogger().severe(" ");

                return false;
            }
        } catch (Exception e) {
            // ignore
        }
        return true;
    }
}

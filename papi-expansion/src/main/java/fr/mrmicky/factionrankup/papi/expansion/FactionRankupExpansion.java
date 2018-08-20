package fr.mrmicky.factionrankup.papi.expansion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.mrmicky.factionrankup.FactionRankup;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * This class will automatically register as a placeholder expansion when a jar
 * including this class is added to the /plugins/placeholderapi/expansions/
 * folder
 *
 */
public class FactionRankupExpansion extends PlaceholderExpansion {

	/**
	 * Since this expansion requires api access to the plugin "SomePlugin" we
	 * must check if "SomePlugin" is on the server in this method
	 */
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin("FactionRankup") != null;
	}

	/**
	 * We can optionally override this method if we need to initialize variables
	 * within this class if we need to or even if we have to do other checks to
	 * ensure the hook is properly setup.
	 */
	@Override
	public boolean register() {
		/*
		 * Make sure "SomePlugin" is on the server
		 */
		if (!canRegister()) {
			return false;
		}

		/*
		 * if for some reason we can not get our variable, we should return
		 * false
		 */
		if (FactionRankup.getInstance() == null) {
			return false;
		}
		/*
		 * Since we override the register method, we need to manually register
		 * this hook
		 */
		return PlaceholderAPI.registerPlaceholderHook(getIdentifier(), this);
	}

	/**
	 * The name of the person who created this expansion should go here
	 */
	@Override
	public String getAuthor() {
		return "MrMicky";
	}

	/**
	 * The placeholder identifier should go here This is what tells
	 * PlaceholderAPI to call our onPlaceholderRequest method to obtain a value
	 * if a placeholder starts with our identifier. This must be unique and can
	 * not contain % or _
	 */
	@Override
	public String getIdentifier() {
		return "factionrankup";
	}

	/**
	 * if an expansion requires another plugin as a dependency, the proper name
	 * of the dependency should go here. Set this to null if your placeholders
	 * do not require another plugin be installed on the server for them to
	 * work. This is extremely important to set if you do have a dependency
	 * because if your dependency is not loaded when this hook is registered, it
	 * will be added to a cache to be registered when plugin: "getPlugin()" is
	 * enabled on the server.
	 */
	@Override
	public String getPlugin() {
		return "FactionRankup";
	}

	/**
	 * This is the version of this expansion
	 */
	@Override
	public String getVersion() {
		return "1.2.0";
	}

	/**
	 * This is the method called when a placeholder with our identifier is found
	 * and needs a value We specify the value identifier in this method
	 */
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) {
			return "";
		}

		if (identifier.equals("level")) {
			return String.valueOf(FactionRankup.getInstance().getFactionLevel(p));
		}
		return null;
	}
}
package ar.ticua;

import ar.ticua.commands.IpWhitelistCommands;
import ar.ticua.mixin.ConnectionAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class Ipwhitelist implements ModInitializer {
	public static final String MOD_ID = "ipwhitelist";
	private static final Path CONFIG_DIR =
			FabricLoader.getInstance().getConfigDir().resolve("ipwhitelist");

	private static final Path WHITELIST_FILE =
			CONFIG_DIR.resolve("ip-whitelist.txt");

	private static final Path CONFIG_FILE =
			CONFIG_DIR.resolve("ip-whitelist-config.properties");
	public static final Set<String> whitelistedIps = new HashSet<>();
	public static boolean enabled = true;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		loadData();
		IpWhitelistCommands.register();

		ServerLoginConnectionEvents.QUERY_START.register(((handler, server, sender, synchronizer) -> {

			Connection conn = ((ConnectionAccessor) handler).ipwhitelist$getConnection();
			// Example ip: /127.0.0.1:62981
			String fullAddr = conn.getLoggableAddress(true);
			String ip = fullAddr.replace("/", "").split(":")[0];

			// means we are on singleplayer
			if (fullAddr.equals("local")) {
				LOGGER.info("Not enabling IP Whitelist, we are on singleplayer.");
				return;
			}

			if (enabled && !whitelistedIps.contains(ip)) {
				LOGGER.info("IP not found in whitelist");
				handler.disconnect(Component.literal(
						"§cYou are not white-listed on this server!\n" +
								"§7Ask an admin to add you using:\n" +
								"§f/ipwhitelist addip §b" + ip
				));
			}

		}));

	}

	public static void loadData() {


		try {
			Files.createDirectories(CONFIG_DIR);

			// Load Whitelist IPs
			if (Files.exists(WHITELIST_FILE)) {
				whitelistedIps.clear();
				whitelistedIps.addAll(Files.readAllLines(WHITELIST_FILE).stream()
						.map(String::trim)
						.filter(line -> !line.isEmpty() && !line.startsWith("#"))
						.collect(Collectors.toSet()));
			}

			// Load ON/OFF State
			if (Files.exists(CONFIG_FILE)) {
				Properties props = new Properties();
				try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
					props.load(in);

					// it should be disabled first cuz otherwise you get locked out
					// and you must add yourself through the console
					enabled = Boolean.parseBoolean(props.getProperty("enabled", "false"));
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load IP whitelist data", e);
		}
	}

	public static void saveAll() {
		try {
			// Save Whitelist
			Files.writeString(WHITELIST_FILE, "# One IP per line\n" + String.join("\n", whitelistedIps));

			// Save Config
			Properties props = new Properties();
			props.setProperty("enabled", String.valueOf(enabled));
			try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
				props.store(out, "IP Whitelist Configuration");
			}
		} catch (IOException e) {
			LOGGER.error("Failed to save IP whitelist data", e);
		}
	}

}
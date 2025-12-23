package ar.ticua;

import ar.ticua.mixin.ConnectionAccessor;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Ipwhitelist implements ModInitializer {
	public static final String MOD_ID = "ipwhitelist";
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ip-whitelist.txt");
	private final Set<String> whitelistedIps = new HashSet<>();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		loadWhitelist();

		LOGGER.info(whitelistedIps.toString());

		ServerLoginConnectionEvents.QUERY_START.register(((handler, server, sender, synchronizer) -> {

			Connection conn = ((ConnectionAccessor) handler).ipwhitelist$getConnection();
			// Example ip: /127.0.0.1:62981
			String fullAddr = conn.getLoggableAddress(true);
			String ip = fullAddr.split("/")[1].split(":")[0];

			// means we are on singleplayer
			if (fullAddr.equals("local")) {
				LOGGER.info("Not enabling IP Whitelist, we are on singleplayer.");
				return;
			}


			if (whitelistedIps.contains(ip)) {
				LOGGER.info("IP found in whitelist");
			} else {
				LOGGER.info("IP not found in whitelist");
				handler.disconnect(Component.literal("You are not white-listed on this server!"));
			}


			LOGGER.info("Someone connected!!: {}", ip);
		}));

		LOGGER.info("Hello Fabric world!");
	}

	private void loadWhitelist() {
		try {
			if (!Files.exists(CONFIG_PATH)) {
				Files.writeString(CONFIG_PATH, "# Add one IP per line\n127.0.0.1\n");
			}
			whitelistedIps.clear();
			whitelistedIps.addAll(Files.readAllLines(CONFIG_PATH).stream()
					.map(String::trim)
					.filter(line -> !line.isEmpty() && !line.startsWith("#"))
					.collect(Collectors.toSet()));
		} catch (IOException e) {
			LOGGER.error("Could not load IP whitelist", e);
		}
	}
}
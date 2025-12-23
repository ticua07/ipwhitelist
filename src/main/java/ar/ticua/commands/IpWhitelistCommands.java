package ar.ticua.commands;

import ar.ticua.Ipwhitelist;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.context.CommandContext;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
public class IpWhitelistCommands {

    // IPs suggestions for removeip
    private static final SuggestionProvider<CommandSourceStack> WHITELISTED_IP_SUGGESTIONS =
            (CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
                for (String ip : Ipwhitelist.whitelistedIps) {
                    builder.suggest(ip);
                }
                return builder.buildFuture();
            };


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("ipwhitelist")
                    .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                    .then(literal("reload").executes(context -> {
                        Ipwhitelist.loadData();
                        context.getSource().sendSuccess(() -> Component.literal("§aWhitelist and Config reloaded from disk."), true);
                        return 1;
                    }))
                    .then(literal("on").executes(context -> {
                        Ipwhitelist.enabled = true;
                        Ipwhitelist.saveAll();
                        context.getSource().sendSuccess(() -> Component.literal("§aIP Whitelist is now ON."), true);
                        return 1;
                    }))
                    .then(literal("off").executes(context -> {
                        Ipwhitelist.enabled = false;
                        Ipwhitelist.saveAll();
                        context.getSource().sendSuccess(() -> Component.literal("§cIP Whitelist is now OFF."), true);
                        return 1;
                    }))
                    .then(literal("add")
                            .then(argument("target", EntityArgument.player()).executes(context -> {
                                ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                String ip = player.getIpAddress();
                                Ipwhitelist.whitelistedIps.add(ip);
                                Ipwhitelist.saveAll();
                                context.getSource().sendSuccess(() -> Component.literal("§aWhitelisted IP: " + ip), true);
                                return 1;
                            }))

                    ).then(literal("addip")
                            .then(argument("manual_ip", StringArgumentType.string()).executes(context -> {
                                String ip = StringArgumentType.getString(context, "manual_ip");
                                Ipwhitelist.whitelistedIps.add(ip);
                                Ipwhitelist.saveAll();
                                context.getSource().sendSuccess(() -> Component.literal("§aWhitelisted IP (Manual): " + ip), true);
                                return 1;
                            })))
                    .then(literal("remove")
                            .then(argument("target", EntityArgument.player()).executes(context -> {
                                ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                String ip = player.getIpAddress();
                                if (Ipwhitelist.whitelistedIps.remove(ip)) {
                                    Ipwhitelist.saveAll();
                                    context.getSource().sendSuccess(() -> Component.literal("§eRemoved IP: " + ip), true);
                                }
                                return 1;
                            })))
                    .then(literal("removeip")
                            .then(argument("manual_ip", StringArgumentType.string()).suggests(WHITELISTED_IP_SUGGESTIONS).executes(context -> {
                                String ip = StringArgumentType.getString(context, "manual_ip");
                                if (Ipwhitelist.whitelistedIps.remove(ip)) {
                                    Ipwhitelist.saveAll();
                                    context.getSource().sendSuccess(() -> Component.literal("§eRemoved IP: " + ip), true);
                                }
                                return 1;
                            }))
                    ).then(literal("list")
                            .executes(context -> {
                                String list = String.join(", ", Ipwhitelist.whitelistedIps);
                                context.getSource().sendSuccess(() -> Component.literal("§aIP List: §r" + (list.isEmpty() ? "Empty" : list)), true);
                                return 1;
                            })
                    )
            );
        });
    }
}
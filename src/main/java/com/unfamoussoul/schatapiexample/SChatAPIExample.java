package com.unfamoussoul.schatapiexample;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.unfamoussoul.schat.api.PseudoPlayer;
import com.unfamoussoul.schat.api.SChatAPI;
import com.unfamoussoul.schatapiexample.listeners.PseudoPlayerListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Тестовый плагин-пример использования SChatAPI.
 * <p>
 * Здесь приведены примеры регистрации псевдо-игрока, а также ответ в личные сообщения в {@code PseudoPlayerListener} и
 * отправка от лица бота в глобальный (/testglobal ТЕКСТ) и локальные чаты (/testlocal ТЕКСТ, бот будет писать на 0 Y 0
 * в мире игрока на Y игрока)
 */
public final class SChatAPIExample extends JavaPlugin {

    public static final String MESSAGE = "message";
    private static final String BOT_NAME = "Havirong";

    private SChatAPI api;
    private PseudoPlayer bot;

    @Override
    public void onEnable() {
        // Здесь получение провайдера моего API плагина SChat
        RegisteredServiceProvider<SChatAPI> registration = getServer().getServicesManager()
                .getRegistration(SChatAPI.class);

        if (registration == null) {
            getLogger().warning("SChat не найден, пример не работает.");
            return;
        }

        // Получение реального класса API
        api = registration.getProvider();

        bot = new PseudoPlayer();
        bot.setName(BOT_NAME);
        api.registerPseudoPlayer(bot);

        getServer().getPluginManager().registerEvents(new PseudoPlayerListener(this), this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        SChatAPI currentApi = api;
        PseudoPlayer currentBot = bot;
        if (currentApi == null || currentBot == null) return;

        currentApi.unregisterPseudoPlayer(currentBot);
    }

    private void registerCommands() {
        LifecycleEventManager<@NotNull Plugin> manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            commands.register(
                    Commands.literal("testglobal")
                            .then(Commands.argument(MESSAGE, StringArgumentType.greedyString())
                                            .executes(c -> {
                                                if (api == null || bot == null) return 0;

                                                String message = c.getArgument(MESSAGE, String.class);
                                                api.sendMessage(bot, message);
                                                return 1;
                                            })
                                    )
                            .build()
            );
            commands.register(
                    Commands.literal("testlocal")
                            .then(Commands.argument(MESSAGE, StringArgumentType.greedyString())
                                    .executes(c -> {
                                        if (api == null || bot == null) return 0;

                                        if (!(c instanceof Player player)) return 0;

                                        Location location = player.getLocation();
                                        bot.setLocation(new Location(location.getWorld(), 0, location.getBlockY(), 0));

                                        String message = c.getArgument(MESSAGE, String.class);
                                        api.sendLocalMessage(bot, message);
                                        return 1;
                                    })
                            )
                            .build()
            );
        });
    }

    public @Nullable PseudoPlayer getBot() {
        return bot;
    }
}
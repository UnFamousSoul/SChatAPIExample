package com.unfamoussoul.schatapiexample.listeners;

import com.unfamoussoul.schat.api.PseudoPlayer;
import com.unfamoussoul.schat.api.event.PseudoPlayerPrivateMessageEvent;
import com.unfamoussoul.schatapiexample.SChatAPIExample;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Отвечает на личные сообщения, адресованные боту {@code Havirong}.
 */
public record PseudoPlayerListener(SChatAPIExample plugin) implements Listener {

    @EventHandler
    private void onPseudoMessage(@NotNull PseudoPlayerPrivateMessageEvent event) {
        PseudoPlayer bot = plugin.getBot();
        if (event.getPseudoPlayer() != bot) return;

        String reply = "Привет, " + event.getSender().getName() + "! Я бот " + bot.getName() + ".";
        event.reply(reply);
    }
}
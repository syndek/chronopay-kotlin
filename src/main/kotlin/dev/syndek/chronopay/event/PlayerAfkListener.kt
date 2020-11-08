package dev.syndek.chronopay.event

import dev.syndek.chronopay.ChronoPayPlugin
import net.ess3.api.events.AfkStatusChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal class PlayerAfkListener(private val plugin: ChronoPayPlugin) : Listener {
    @EventHandler
    private fun onAfkStatusChange(event: AfkStatusChangeEvent) {
        val player = event.affected.base

        plugin.playerTracker.setPlayerAfkStatus(player, event.value)
        plugin.playerTracker.recalculatePlayerValidity(player)

        if (plugin.playerTracker.playerFailsAfkCheck(player)) {
            val goneAfkMessage = plugin.settings.goneAfkMessage
            if (!goneAfkMessage.isNullOrEmpty()) player.sendMessage(goneAfkMessage)
        }
    }
}
package dev.syndek.chronopay.event

import dev.syndek.chronopay.ChronoPayPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

internal class PlayerConnectionListener(private val plugin: ChronoPayPlugin) : Listener {
    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        plugin.playerTracker.addPlayerAddress(player)

        if (plugin.playerTracker.playerFailsAddressCheck(player)) {
            val multipleAccountsMessage = plugin.settings.multipleAccountsMessage
            if (!multipleAccountsMessage.isNullOrEmpty()) player.sendMessage(multipleAccountsMessage)
        }
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.playerTracker.removePlayerAddress(event.player)
    }
}
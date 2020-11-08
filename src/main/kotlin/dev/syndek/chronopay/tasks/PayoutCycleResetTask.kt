package dev.syndek.chronopay.tasks

import dev.syndek.chronopay.ChronoPayPlugin

internal class PayoutCycleResetTask(private val plugin: ChronoPayPlugin) : Runnable {
    override fun run() {
        for ((id, data) in plugin.playerTracker.playerData) {
            data.resetPayedMoney()

            val player = plugin.server.getPlayer(id) ?: continue

            val cycleResetMessage = plugin.settings.cycleResetMessage
            if (!cycleResetMessage.isNullOrEmpty() && plugin.playerTracker.playerFailsCapCheck(player)) {
                player.sendMessage(cycleResetMessage)
            }

            plugin.playerTracker.recalculatePlayerValidity(player)
        }
    }
}
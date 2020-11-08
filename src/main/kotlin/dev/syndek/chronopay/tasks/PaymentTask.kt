package dev.syndek.chronopay.tasks

import dev.syndek.chronopay.ChronoPayPlugin

internal class PaymentTask(private val plugin: ChronoPayPlugin) : Runnable {
    override fun run() {
        for (player in plugin.playerTracker.validPlayers) {
            val data = plugin.playerTracker[player.uniqueId]

            data.incrementOnlineTime()

            // We only want to pay players who have been online long enough.
            if (data.onlineTime < plugin.settings.payoutInterval) continue

            val payoutAmount = plugin.settings.payoutAmount

            // Pay the player the configured amount.
            plugin.economy.depositPlayer(player, payoutAmount)
            data.addPayedMoney(payoutAmount)
            data.resetOnlineTime()

            // Alert the player of the payout.
            val payoutMessage = plugin.settings.payoutMessage
            if (!payoutMessage.isNullOrEmpty()) player.sendMessage(payoutMessage)

            // Recalculate player validity after payout to ensure they haven't exceeded the cap.
            plugin.playerTracker.recalculatePlayerValidity(player)

            // If the player has now exceeded the cap, alert them.
            if (plugin.playerTracker.playerFailsCapCheck(player)) {
                val capReachedMessage = plugin.settings.capReachedMessage
                if (!capReachedMessage.isNullOrEmpty()) player.sendMessage(capReachedMessage)
            }
        }
    }
}
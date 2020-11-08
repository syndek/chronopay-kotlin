package dev.syndek.chronopay

import org.bukkit.ChatColor
import org.bukkit.configuration.Configuration

internal class ChronoPaySettings(private val plugin: ChronoPayPlugin) {
    var payoutInterval = 0
        private set
    var payoutCycleResetInterval = 0
        private set
    var payoutAmount = 0.0
        private set
    var payoutCap = 0.0
        private set
    var checkAddress = false
        private set
    var checkAfk = false
        private set
    var checkCap = false
        private set
    var payoutMessage: String? = null
        private set
    var cycleResetMessage: String? = null
        private set
    var capReachedMessage: String? = null
        private set
    var multipleAccountsMessage: String? = null
        private set
    var goneAfkMessage: String? = null
        private set

    fun load() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()

        with(plugin.config) {
            payoutInterval = getInt("payout-interval", 60)
            payoutCycleResetInterval = getInt("payout-cycle-reset-interval", 60)
            payoutAmount = getDouble("payout-amount", 0.1)
            payoutCap = getDouble("payout-cap", 5.0)
            checkAddress = getBoolean("checks.address", true)
            checkAfk = getBoolean("checks.afk", true)
            checkCap = getBoolean("checks.cap", true)
            payoutMessage = getMessage("payout")
                .replace("{money}", payoutAmount.toString())
                .replace("{minutes}", (payoutInterval.toFloat() / 60).toString())
                .replace("{seconds}", payoutInterval.toString())
            cycleResetMessage = getMessage("cycle-reset")
            capReachedMessage = getMessage("cap-reached")
            multipleAccountsMessage = getMessage("multiple-accounts")
            goneAfkMessage = getMessage("gone-afk")
        }
    }

    private fun Configuration.getMessage(key: String) =
        ChatColor.translateAlternateColorCodes('&', getString("messages.$key", "")!!)
}
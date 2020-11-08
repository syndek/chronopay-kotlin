package dev.syndek.chronopay

import dev.syndek.chronopay.event.PlayerAfkListener
import dev.syndek.chronopay.event.PlayerConnectionListener
import dev.syndek.chronopay.tasks.PaymentTask
import dev.syndek.chronopay.tasks.PayoutCycleResetTask
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

internal class ChronoPayPlugin : JavaPlugin() {
    internal val settings: ChronoPaySettings = ChronoPaySettings(this)
    internal val playerTracker: PlayerTracker = PlayerTracker(this)

    internal lateinit var economy: Economy

    override fun onEnable() {
        val economyProvider = server.servicesManager.getRegistration(Economy::class.java)
        if (economyProvider != null) {
            economy = economyProvider.provider
            logger.info("Successfully hooked Vault.")
        } else {
            logger.severe("Failed to hook Vault. The plugin will now shut down.")
            server.pluginManager.disablePlugin(this)
            return
        }

        getCommand("cpreload")!!.setExecutor(ChronoPayCommandExecutor(this))

        settings.load()

        server.scheduler.runTaskTimer(this, PaymentTask(this), 0, 20)
        server.scheduler.runTaskTimer(this, PayoutCycleResetTask(this), 0, settings.payoutCycleResetInterval * 20L)

        server.pluginManager.registerEvents(PlayerConnectionListener(this), this)

        if (server.pluginManager.getPlugin("Essentials") != null) {
            server.pluginManager.registerEvents(PlayerAfkListener(this), this)
        }
    }
}
package dev.syndek.chronopay

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class PlayerTracker(private val plugin: ChronoPayPlugin) {
    private val playersAtAddress = HashMap<String, MutableSet<UUID>>()
    private val afkPlayers = HashSet<UUID>()

    internal val playerData: Map<UUID, PlayerData> = HashMap()
    internal val validPlayers: Set<Player> = ConcurrentHashMap.newKeySet()

    init {
        plugin.server.onlinePlayers.forEach(this::recalculatePlayerValidity)
    }

    operator fun get(playerId: UUID) = (playerData as MutableMap).getOrPut(playerId) { PlayerData() }

    fun setPlayerAfkStatus(player: Player, isAfk: Boolean) = afkPlayers.apply {
        if (isAfk) add(player.uniqueId) else remove(player.uniqueId)
    }

    fun recalculatePlayerValidity(player: Player) {
        // Trigger smart cast for validPlayers.
        validPlayers as MutableSet

        if (playerFailsAddressCheck(player) || playerFailsAfkCheck(player) || playerFailsCapCheck(player)) {
            validPlayers -= player
        } else {
            validPlayers += player
        }
    }

    fun playerFailsAddressCheck(player: Player): Boolean {
        if (plugin.settings.checkAddress && !player.hasPermission("chronopay.bypass.address")) {
            val playersAtAddress = playersAtAddress[player.address!!.hostString]
            return playersAtAddress != null && playersAtAddress.size > 1
        }

        return false
    }

    fun playerFailsAfkCheck(player: Player) = plugin.settings.checkAfk &&
            !player.hasPermission("chronopay.bypass.afk") &&
            player.uniqueId in afkPlayers

    fun playerFailsCapCheck(player: Player) = plugin.settings.checkCap &&
            !player.hasPermission("chronopay.bypass.cap") &&
            this[player.uniqueId].payedMoney >= plugin.settings.payoutCap

    fun addPlayerAddress(player: Player) {
        val playerId = player.uniqueId
        val playerAddress = player.address!!.hostString
        val playersAtAddress = playersAtAddress.computeIfAbsent(playerAddress) { HashSet() }

        playersAtAddress += playerId

        // Recalculate the validity of all players at the address.
        for (playerIdAtAddress in playersAtAddress) {
            // If the player being tested is the same that's being passed to this method,
            // we can use the faster recalculatePlayerValidity(Player) method.
            if (playerIdAtAddress == playerId) {
                recalculatePlayerValidity(player)
            } else {
                recalculatePlayerValidity(playerIdAtAddress)
            }
        }
    }

    fun removePlayerAddress(player: Player) {
        val playerIdsAtAddress = playersAtAddress[player.address!!.hostString] ?: return
        playerIdsAtAddress -= player.uniqueId
        playerIdsAtAddress.forEach(this::recalculatePlayerValidity)
    }

    private fun recalculatePlayerValidity(playerId: UUID) {
        recalculatePlayerValidity(plugin.server.getPlayer(playerId) ?: return)
    }
}
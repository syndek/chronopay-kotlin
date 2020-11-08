package dev.syndek.chronopay

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor

import org.bukkit.command.CommandSender

internal class ChronoPayCommandExecutor(private val plugin: ChronoPayPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender.hasPermission("chronopay.reload")) {
            plugin.settings.load()
            sender.sendMessage(ChatColor.GREEN.toString() + "Configuration reloaded.")
        } else {
            sender.sendMessage(ChatColor.RED.toString() + "You do not have permission to do this.")
        }

        return true
    }
}
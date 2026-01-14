package net.spacydev.bingo.commands

import net.spacydev.bingo.bingo.BingoGUI
import net.spacydev.bingo.bingo.BingoManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BingoCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!")
            return true
        }

        if (args.isNotEmpty() && args[0].equals("start", ignoreCase = true)) {
            val rid = BingoManager.startRound()
            sender.sendMessage("§aBingo-Runde gestartet (id=$rid)")
            return true
        }

        BingoGUI.open(sender)
        return true
    }
}

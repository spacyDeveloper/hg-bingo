package bingo.commands

import bingo.bingo.BingoGUI
import bingo.bingo.BingoWorldManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BingoCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        if (args.isNotEmpty() && args[0].equals("start", true)) {
            val world = BingoWorldManager.createWorldForPlayer(sender.uniqueId)
            sender.teleport(world.spawnLocation)
            sender.sendMessage("§aDu bist in einer neuen Bingo-Welt!")
            return true
        }

        bingo.bingo.BingoGUI.open(sender)
        return true
    }
}

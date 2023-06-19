package com.kryeit.storage;

import com.kryeit.Teleposte;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandDumpDB implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Teleposte.getDB().dump());
        return true;
    }
}

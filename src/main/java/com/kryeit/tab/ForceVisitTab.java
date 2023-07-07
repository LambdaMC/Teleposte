package com.kryeit.tab;

import com.kryeit.Teleposte;
import com.kryeit.commands.PostAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ForceVisitTab implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        int size = args.length;
        if (size == 0) return new ArrayList<>();

        List<String> completions = new ArrayList<>();

        List<String> allTabs = new ArrayList<>();


        if(size == 1) {
            Bukkit.getOnlinePlayers().forEach(p -> allTabs.add(p.getName()));

            for (String allTab : allTabs) {
                if (allTab.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(allTab);
                }
            }
        }

        Teleposte.getDB().getPosts().forEach(p -> allTabs.add(p.name()));

        // Add to "completions" all words that have letters that are contained on "commands" list
        for (String tab : allTabs) {
            if (tab == null) continue;
            String aux = tab;

            for (int i = 0; i < size - 1; i++) {
                tab = tab.replaceFirst("(?i)" + args[i] + " ", "");
            }

            if (aux.toLowerCase().startsWith(PostAPI.getPostName(args).toLowerCase())) {
                completions.add(tab);
            }
        }
        return completions;

    }
}


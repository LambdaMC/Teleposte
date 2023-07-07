package com.kryeit.commands;

import com.kryeit.Teleposte;
import com.kryeit.storage.bytes.Post;
import com.kryeit.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.kryeit.commands.PostAPI.WORLD;

public class ForceVisitCommand  implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) {
            sender.sendMessage("Usa /forzarvisita <Jugador> <Poste>");
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage("Jugador no encontrado");
            return false;
        }

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        String postID = PostAPI.getPostID(newArgs);

        Optional<Post> post = Teleposte.getDB().getPost(postID);
        if (post.isPresent()) {
            Location pos = post.get().location();
            int height = WORLD.getHighestBlockYAt(pos);
            pos.setY(height + 2);
            player.teleport(pos);

            sender.sendMessage("Jugador teletransportado");
            player.sendMessage("Un administrador te ha teletransportado");
            return true;
        } else {
            sender.sendMessage("El poste no existe");
            return false;
        }
    }
}

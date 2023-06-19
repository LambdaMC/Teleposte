package com.kryeit;

import com.kryeit.commands.*;
import com.kryeit.leash.onLeash;
import com.kryeit.listeners.onFall;
import com.kryeit.listeners.onGlide;
import com.kryeit.listeners.onKick;
import com.kryeit.listeners.onPlayerMove;
import com.kryeit.storage.CommandDumpDB;
import com.kryeit.storage.IDatabase;
import com.kryeit.storage.LevelDBImpl;
import com.kryeit.storage.PlayerNamedPosts;
import com.kryeit.tab.*;
import com.kryeit.util.ArrayListHashMap;
import io.github.thatsmusic99.configurationmaster.CMFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Teleposte extends JavaPlugin {

    // All global lists
    public final ArrayList<UUID> blockFall = new ArrayList<>();
    public final HashMap<UUID, UUID> leashed = new HashMap<>();
    public final List<String> offline = new ArrayList<>();

    PluginDescriptionFile pdffile = getDescription();
    public final String name = ChatColor.YELLOW + "[" + ChatColor.WHITE + pdffile.getName() + ChatColor.YELLOW + "]" + ChatColor.WHITE;
    public final String version = pdffile.getVersion();

    public static Teleposte instance;

    public final ArrayListHashMap<UUID, UUID> invites = new ArrayListHashMap<>();

    public PlayerNamedPosts playerNamedPosts;
    public IDatabase database;

    @Override
    public void onLoad() {
        database = new LevelDBImpl();
        try {
            playerNamedPosts = new PlayerNamedPosts("plugins/Teleposte/PlayerPosts");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public YamlConfiguration messages;

    @Override
    public void onEnable() {

        // Set the config.yml file
        loadConfig();

        // Set the messages.yml file
        loadMessages();

        instance = this;

        // Register all commands and tab completions
        registerCommands();

        // Register events
        registerEvents();

        messages = YamlConfiguration.loadConfiguration(new File(getInstance().getDataFolder(), "messages.yml"));

        // Plugin activated at this point
        Bukkit.getConsoleSender().sendMessage(name + ChatColor.GRAY + " The plugin has been activated. Version: " + ChatColor.GREEN + version);
    }

    @Override
    public void onDisable() {
        database.stop();
        Bukkit.getConsoleSender().sendMessage(name + ChatColor.WHITE + " The plugin has been deactivated.");
    }

    private void loadConfig() {
        CMFile myConfigFile = new CMFile(this, "config") {
            @Override
            public void loadDefaults() {
                addLink("GitHub", "https://github.com/LambdaMC/Teleposte");

                addComment("This number has to be higher than 0. (default = 800 blocks)");
                addDefault("distance-between-posts", 800);

                addComment("First post's X coordinate (default -> x = 0)");
                addDefault("post-x-location", 0);

                addComment("First post's Z coordinate (default -> z = 0)");
                addDefault("post-z-location", 0);

                addComment("The width of the post, with center on /nearestpost. Only odd, don't even. ( default = 5 blocks, 2 to each coordinate + the center )");
                addDefault("post-width", 5);

                addComment("/homepost and /visit have this feature, this launches you to the sky before teleporting. ( default = true )");
                addDefault("launch-feature", true);

                addComment("After using a TP command you get teleported to y = 265 if this is true, if not, it will teleport you to ground level. ( default = true )");
                addDefault("tp-in-the-air", true);

                addComment("Shows the message on the Action Bar. ( doesn't work for all commands ) ( default = true )");
                addDefault("messages-on-action-bar", true);

                addComment("If leashed entities get teteported");
                addDefault("teleport-leashed", true);

                addComment("ClaimBlocks needed to name a post. This needs to have GriefDefender installed on the server.");
                addDefault("needed-blocks", 40000);
            }
        };
        myConfigFile.load();
    }

    private void loadMessages() {
        CMFile myMessagesFile = new CMFile(this, "messages") {
            @Override
            public void loadDefaults() {
                addLink("GitHub", "https://github.com/LambdaMC/Teleposte");

                addComment("Usage:");
                addDefault("namepost-usage", "&fUsa /nombrarposte <Nombre>.");
                addDefault("visit-usage", "&fUsa /visitar <Nombre/Jugador>.");
                addDefault("setpost-usage", "&fUsa /mudarse.");
                addDefault("invite-usage", "&fUsa /invitar <Jugador>.");
                addDefault("homepost-usage", "&fUsa /casa.");

                addComment("Global:");
                addDefault("cant-execute-from-console", "You can't execute this command from console.");
                addDefault("no-permission", "&cNo tienes permiso para usar este comando.");
                addDefault("not-on-overworld", "&cTienes que estar en el Overworld para usar este comando.");
                addDefault("not-inside-post", "&cTienes que estar dentro de un poste, usa /poste.");

                addComment("/PostList:");
                addDefault("named-posts-translation", "&6Postes con nombre");
                addDefault("hover-postlist", "&fHaz clic para teletransportarte a: &7%POST_NAME%&f.\nEste poste se encuentra en &6%POST_LOCATION%&f.");

                addComment("/SetPost:");
                addDefault("set-post-success", "&fHas hecho de tu casa el poste que se encuentra en: &6%POST_LOCATION%&f.");
                addDefault("move-post-success", "&fHas mudado tu casa al poste que se encuentra en: &6%POST_LOCATION%&f.");

                addComment("/HomePost or /Visit:");
                addDefault("already-at-homepost", "&cYa estás en tu casa.");
                addDefault("named-post-arrival", "&fBienvenido a &6%POST_NAME%&f.");
                addDefault("invited-home-arrival", "&Bienvenido a la casa de &6%PLAYER_NAME%&f.");
                addDefault("own-homepost-arrival", "&fBienvenido a tu casa.");
                addDefault("homepost-without-setpost", "&fPor favor, usa /mudarse antes.");
                addDefault("visit-not-invited", "&cNo has sido invitado.");
                addDefault("no-homepost", "&cTodavía no tienes casa, usa /mudarse antes.");
                addDefault("already-invited-post", "&cYa estás en su casa.");
                addDefault("already-at-namedpost", "&cYa estás en &6%POST_NAME%&c.");
                addDefault("unknown-post", "El poste &6%POST_NAME%&f no existe.");
                addDefault("block-above", "&cTienes bloques encima, no puedes teletransportarte.");

                addComment("/NearestPost:");
                addDefault("nearest-message", "&fEl poste más cercano está en: &6%POST_LOCATION%&f. Está a &6%DISTANCE% &fbloques de distancia.");
                addDefault("nearest-message-named", "&fEl poste más cercano está en: &6%POST_LOCATION%&f, es &6%POST_NAME%&f. Está a &6%DISTANCE% &fbloques de distancia.");

                addComment("/Invite:");
                addDefault("own-invite", "&cNo te puedes invitar a ti mismo.");
                addDefault("not-found", "&cJugador no encontrado.");
                addDefault("invite-expire", "&fEl jugador &6%PLAYER_NAME%&f ya no tiene permiso para teletransportarse a tu casa.");
                addDefault("inviting", "&fHas invitado a &6%PLAYER_NAME%&f.");
                addDefault("invited", "&fEl jugador &6%PLAYER_NAME%&f te ha invitado.");

                addComment("/UnnamePost:");
                addDefault("unname-named-post", "&6%POST_NAME% &apost has been unnamed.");
                addDefault("no-such-post", "&cNo posts by that name.");

                addComment("/NamePost:");
                addDefault("not-enough-claimblocks","&cNo tienes suficientes &6CB's&f para nombrar el poste, necesitas haber conseguido al menos %CLAIM_BLOCKS% CB's");
                addDefault("nearest-already-named", "&cEl poste más cercano ya tiene nombre, es &6%POST_NAME%&c.");
                addDefault("no-named-posts", "&cNo hay postes con nombre.");
                addDefault("name-post", "&fLe has dado el nombre de &6%POST_NAME%&f al poste más cercano.");
                addDefault("named-post-already-exists", "&cEl poste &6%POST_NAME%&c ya existe.");

                addComment("Post Building");
                addDefault("unnamed-post","Poste sin nombre");
            }
        };
        myMessagesFile.load();
    }

    public static Teleposte getInstance() {
        return instance;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new onGlide(), this);
        getServer().getPluginManager().registerEvents(new onFall(), this);
        getServer().getPluginManager().registerEvents(new onPlayerMove(), this);
        getServer().getPluginManager().registerEvents(new onKick(), this);
        if (getConfig().getBoolean("teleport-leashed")) {
            getServer().getPluginManager().registerEvents(new onLeash(), this);
        }
    }

    public void registerCommands() {
        // /nearestpost
        registerCommand("postecercano", new ComandoPosteCercano());

        // /setpost
        registerCommand("mudarse", new ComandoMudarse());

        // /homepost
        registerCommand("casa", new ComandoCasa());

        // /invite <Player>
        registerCommand("invitar", new ComandoInvitar(), new InviteTab());

        // /visit <NamedPost/Player>
        registerCommand("visitar", new ComandoVisitar(), new VisitTab());

        // /namepost <Name>
        registerCommand("nombrarposte", new ComandoNombrarPoste());

        // /unnamepost (Name)
        registerCommand("desnombrarposte", new ComandoDesnombrarPoste(), new UnnamePost());

        // /postlist
        registerCommand("listapostes", new ComandoListaPostes(), new PostListTab());

        // /dumpdb
        registerCommand("dumpdb", new CommandDumpDB());

    }

    private void registerCommand(String name, CommandExecutor executor) {
        registerCommand(name, executor, new ReturnEmptyTab());
    }

    private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = getCommand(name);
        if (command == null)
            throw new RuntimeException("Failed to register command \"" + name + "\"! Add it to plugin.yml!");

        command.setExecutor(executor);
        if (tabCompleter != null) command.setTabCompleter(tabCompleter);
    }

    public static IDatabase getDB() {
        return getInstance().database;
    }
}

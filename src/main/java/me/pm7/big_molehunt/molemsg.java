package me.pm7.big_molehunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class molemsg implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if(sender instanceof Player plr) {
            if(!Big_molehunt.moles.contains(plr)) return true;

            String message = String.join(" ", args);

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(Big_molehunt.moles.contains(p)) {
                    p.sendMessage(ChatColor.RED + "[Mole " + plr.getName() + "]: " + ChatColor.WHITE + message);
                }
            }
        }



        return true;
    }
}

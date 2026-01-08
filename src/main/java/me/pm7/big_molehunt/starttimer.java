package me.pm7.big_molehunt;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class starttimer implements CommandExecutor, Listener {
    private static final Big_molehunt plugin = Big_molehunt.getPlugin();
    private static int totalSeconds = plugin.getConfig().getInt("preTime") * 60;
    private static int task;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(!sender.hasPermission("owo")) {
            sender.sendMessage(ChatColor.RED + "OwO, what's this?!?1 You w-want t-to stawt the x3 timew aww by youwsewf!? *sweats* t-t-that's quite a wawge t-task you've got. I'm nyot suwe you c-can handwe i-it UwU");
            return true;
        }

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            totalSeconds--;

            int hours = (int) Math.floor((double) totalSeconds / 3600);
            int minutes = (int) Math.floor((double) (totalSeconds % 3600) / 60);
            int seconds = (totalSeconds % 3600) % 60;

            String strHours;
            String strMinutes;
            String strSeconds;
            if(Math.floor((double) hours/10) == 0) {strHours = "0"+hours;} else {strHours= String.valueOf(hours);}
            if(Math.floor((double) minutes/10) == 0) {strMinutes = "0"+minutes;} else {strMinutes= String.valueOf(minutes);}
            if(Math.floor((double) seconds/10) == 0) {strSeconds = "0"+seconds;} else {strSeconds= String.valueOf(seconds);}

            for(Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "End portal accessible in: " + strHours + ":" + strMinutes + ":" + strSeconds));
            }

            if(totalSeconds <= 0) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ChatColor.GREEN + "End portal accessible!");
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
                }

                Bukkit.getScheduler().cancelTask(task);
            }

        }, 20L, 20L);

        return true;
    }
}

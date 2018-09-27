package me.wertik.milestones.listeners;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wertik.milestones.ConfigLoader;
import me.wertik.milestones.DataHandler;
import me.wertik.milestones.Main;
import me.wertik.milestones.objects.Condition;
import me.wertik.milestones.objects.Milestone;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ConditionHandler {

    public ConditionHandler() {
    }

    // Simplify adding new conditions to the list. :)
    /*
     *
     * Again, it's just easier for me. You'll find it absurd and ignorant.
     *
     * */

    ConfigLoader cload = new ConfigLoader();
    DataHandler dataHandler = new DataHandler();
    Main plugin = Main.getInstance();
    WorldGuardPlugin wg = plugin.getWorldGuard();

    public void process(String type, String targetType, Player p) {

        for (Milestone milestone : cload.getMilestones()) {
            process(milestone, type, targetType, p);
        }
    }

    public void process(Milestone milestone, String type, String targetType, Player p) {

        // Toggle checkers
        // player
        // Too lazy..

        Condition condition = milestone.getCondition();

        // Type of the condition
        if (!condition.getType().equalsIgnoreCase(type))
            return;

        // targetTypes
        if (!condition.getTargetTypes().contains(targetType) && !condition.getTargetTypes().isEmpty())
            return;

        // biomeTypes
        if (!condition.getBiomes().contains(p.getLocation().getBlock().getBiome().toString()) && !condition.getBiomes().isEmpty())
            return;

        // toolTypes
        if (!condition.getToolTypes().contains(p.getInventory().getItemInMainHand().getType().toString()) && !condition.getType().equalsIgnoreCase("blockplace") && !condition.getToolTypes().isEmpty())
            return;

        // regionNames
        LocalPlayer localPlayer = wg.wrapPlayer(p);
        Vector vector = localPlayer.getPosition();
        List<String> regionSet = wg.getRegionManager(p.getWorld()).getApplicableRegionsIDs(vector);

        if (!condition.getRegionNames().isEmpty() && regionSet.isEmpty())
            return;

        for (String region : regionSet) {

            if (condition.getRegionNames().isEmpty())
                break;

            if (condition.getRegionNames().contains(region))
                break;
            else
                return;
        }

        // inventory items
        for (String itemType : condition.getInInventory()) {

            if (condition.getInInventory().isEmpty())
                break;

            if (p.getInventory().contains(Material.valueOf(itemType)))
                continue;
            else
                return;
        }

        // Reward him.
        reward(milestone, p);
    }


    // reward system
    public void reward(Milestone milestone, Player p) {

        if (milestone.isGlobal()) {
            dataHandler.addGlobalScore(milestone.getName());
        } else {
            if (milestone.isOnlyOnce()) {
                if (dataHandler.getScore(p.getName(), milestone.getName()) == 0) {
                    dataHandler.addScore(p.getName(), milestone.getName());
                } else
                    return;
            } else
                dataHandler.addScore(p.getName(), milestone.getName());
        }

        // Messages
        if (milestone.isBroadcast()) {
            for (Player t : plugin.getServer().getOnlinePlayers()) {
                t.sendMessage(cload.getFinalString(milestone.getBroadcastMessage(), p, milestone));
            }
        }

        if (milestone.isInform()) {
            p.sendMessage(cload.getFinalString(milestone.getInformMessage(), p, milestone));
        }

        // Commands
        for (String command : milestone.getCommandsReward()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cload.parseString(command, p, milestone));
        }

    }
}


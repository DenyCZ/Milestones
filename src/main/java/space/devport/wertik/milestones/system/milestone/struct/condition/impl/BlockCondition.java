package space.devport.wertik.milestones.system.milestone.struct.condition.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.utils.configuration.Configuration;
import space.devport.wertik.milestones.system.action.struct.ActionContext;
import space.devport.wertik.milestones.system.milestone.struct.condition.AbstractCondition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockCondition extends AbstractCondition {

    private final Set<Material> allowedMaterials = new HashSet<>();

    @Override
    public boolean onCheck(Player player, ActionContext context) {
        Block block = context.get(Block.class);
        return allowedMaterials.isEmpty() || block != null && allowedMaterials.contains(block.getType());
    }

    @Override
    public void onLoad(Configuration configuration, ConfigurationSection section) {
        List<String> materials = section.getStringList("blocks");
        for (String material : materials) {
            Material mat = Material.matchMaterial(material);
            if (mat == null) continue;
            this.allowedMaterials.add(mat);
        }
    }
}
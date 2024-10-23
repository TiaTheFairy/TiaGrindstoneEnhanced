package org.tiathefairyland.tiagrindstoneenhanced.Hookers;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

public class AuraSkillsHooker {
    public TiaGrindstoneEnhanced plugin;
    private AuraSkillsApi api = null;

    public AuraSkillsHooker(TiaGrindstoneEnhanced plugin) {
        this.plugin = plugin;
    }

    public void hookAuraSkill() {
        api = AuraSkillsApi.get();
    }

    public boolean givePlayerExp(Player player, double exp) {
        SkillsUser user = api.getUser(player.getUniqueId());

        if (!plugin.getConfig().getBoolean("hooks.AuraSkills.enchanting.give-xp")) {
            return false;
        }

        if (plugin.getConfig().getBoolean("hooks.AuraSkills.enchanting.use-multiplier")) {
            user.addSkillXp(Skills.ENCHANTING, exp);
        }
        else {
            user.addSkillXpRaw(Skills.ENCHANTING, exp);
        }
        return true;
    }
}

package org.tiathefairyland.tiagrindstoneenhanced.Hookers;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

public class AuraSkillsHooker {
    public static TiaGrindstoneEnhanced plugin;
    private AuraSkillsApi auraSkills = null;

    public AuraSkillsHooker(TiaGrindstoneEnhanced plugin) {
        AuraSkillsHooker.plugin = plugin;
        auraSkills = AuraSkillsApi.get();
    }

    public boolean givePlayerExp(Player player, Skills skills, double exp){
        SkillsUser user = auraSkills.getUser(player.getUniqueId());

        if(skills.equals(Skills.ENCHANTING)){
            if(plugin.getConfig().getBoolean("hooks.AuraSkills.enchanting.give-exp")){
                if(plugin.getConfig().getBoolean("hook.AuraSkills.enchanting.use-multiplier")){
                    user.addSkillXp(Skills.ENCHANTING, exp);
                }
                else{
                    user.addSkillXpRaw(Skills.ENCHANTING, exp);
                }
                return true;
            }
            return false;
        }
        return false;
    }
}

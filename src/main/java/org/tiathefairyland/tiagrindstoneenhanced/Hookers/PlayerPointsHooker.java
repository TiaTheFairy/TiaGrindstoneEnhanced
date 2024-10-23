package org.tiathefairyland.tiagrindstoneenhanced.Hookers;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

public class PlayerPointsHooker {
    public TiaGrindstoneEnhanced plugin;
    private PlayerPointsAPI api = null;

    public PlayerPointsHooker(TiaGrindstoneEnhanced plugin){
        this.plugin = plugin;
    }

    public void hookPlayerPoints() {
        api = PlayerPoints.getInstance().getAPI();
    }

    public void givePlayerPoints(Player player, int points) {
        if(this.api != null){
            api.give(player.getUniqueId(), points);
        }
    }

    public void takePlayerPoints(Player player, int points){
        if(this.api != null){
            api.take(player.getUniqueId(), points);
        }
    }

    public int getPlayerPoints(Player player){
        if(this.api != null){
            return api.look(player.getUniqueId());
        }
        return -1;
    }
}

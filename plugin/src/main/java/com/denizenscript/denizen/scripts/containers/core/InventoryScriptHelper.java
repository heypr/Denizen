package com.denizenscript.denizen.scripts.containers.core;
import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.abstracts.ImprovedOfflinePlayer;
import com.denizenscript.denizen.nms.interfaces.PlayerHelper;
import com.denizenscript.denizen.objects.InventoryTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryScriptHelper implements Listener {

    public static Map<Inventory, InventoryTag> notedInventories = new HashMap<>();

    public static HashMap<String, InventoryScriptContainer> inventoryScripts = new HashMap<>();

    public InventoryScriptHelper() {
        Denizen.getInstance().getServer().getPluginManager().registerEvents(this, Denizen.getInstance());
    }

    public static void _savePlayerInventories() {
        PlayerHelper playerHelper = NMSHandler.getPlayerHelper();
        for (Map.Entry<UUID, PlayerInventory> offlineInv : ImprovedOfflinePlayer.offlineInventories.entrySet()) {
            playerHelper.getOfflineData(offlineInv.getKey()).setInventory(offlineInv.getValue());
        }
        for (Map.Entry<UUID, Inventory> offlineEnderChest : ImprovedOfflinePlayer.offlineEnderChests.entrySet()) {
            playerHelper.getOfflineData(offlineEnderChest.getKey()).setEnderChest(offlineEnderChest.getValue());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerHelper playerHelper = NMSHandler.getPlayerHelper();
        if (ImprovedOfflinePlayer.offlineInventories.containsKey(uuid)) {
            playerHelper.getOfflineData(uuid).setInventory(ImprovedOfflinePlayer.offlineInventories.get(uuid));
            ImprovedOfflinePlayer.offlineInventories.remove(uuid);
        }
        if (ImprovedOfflinePlayer.offlineEnderChests.containsKey(uuid)) {
            playerHelper.getOfflineData(uuid).setEnderChest(ImprovedOfflinePlayer.offlineEnderChests.get(uuid));
            ImprovedOfflinePlayer.offlineEnderChests.remove(uuid);
        }
    }
}

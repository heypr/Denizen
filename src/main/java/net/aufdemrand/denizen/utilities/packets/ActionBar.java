package net.aufdemrand.denizen.utilities.packets;

import net.aufdemrand.denizen.utilities.debugging.dB;
import net.minecraft.server.v1_9_R1.ChatComponentText;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;

public class ActionBar {

    private static final Field chat_message, chat_type;

    static {
        Map<String, Field> fields = PacketHelper.registerFields(PacketPlayOutChat.class);
        chat_message = fields.get("a");// TODO: 1.9
        chat_type = fields.get("b");// TODO: 1.9
    }

    public static PacketPlayOutChat getActionBarPacket(String message) {
        PacketPlayOutChat actionBarPacket = new PacketPlayOutChat();
        try {
            chat_message.set(actionBarPacket, new ChatComponentText(message));
            chat_type.set(actionBarPacket, (byte) 2);
        }
        catch (Exception e) {
            dB.echoError(e);
        }
        return actionBarPacket;
    }

    public static void sendActionBarMessage(Player player, String message) {
        PacketPlayOutChat actionBarPacket = getActionBarPacket(message);
        PacketHelper.sendPacket(player, actionBarPacket);
    }

}

package me.pjsph.inspectoruhc.tools;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Titles {
    private static Class<?> packetPlayOutTitleClass;
    private static Class<?> chatSerializerClass;
    private static Class<?> iChatBaseComponent;
    private static Class<?> enumTitleActionClass;

    private final static Class<?> packetClass;
    private final static Method sendPacketMethod;
    private final static Class<?> craftPlayerClass;
    private final static Class<?> entityPlayerClass;

    private static Object enumTitleActionTitle;
    private static Object enumTitleActionSubtitle;

    static {
            packetPlayOutTitleClass = Reflection.getNMSClass("PacketPlayOutTitle");
            iChatBaseComponent = Reflection.getNMSClass("IChatBaseComponent");

            try {
                chatSerializerClass = Reflection.getNMSClassWithoutCatch("ChatSerializer");
            } catch(ClassNotFoundException e) {
                chatSerializerClass = Reflection.getNMSClass("IChatBaseComponent$ChatSerializer");
            }

            enumTitleActionClass = Reflection.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            if (enumTitleActionClass == null) enumTitleActionClass = Reflection.getNMSClass("EnumTitleAction");

            if (packetPlayOutTitleClass == null ||
                    iChatBaseComponent == null ||
                    chatSerializerClass == null ||
                    enumTitleActionClass == null) {
                throw new IncompatibleMinecraftVersionException("Unable to find NMS Classes", new Exception());
            }

            try {
                craftPlayerClass = Reflection.getOBCClass("entity.CraftPlayer");
                entityPlayerClass = Reflection.getNMSClass("EntityPlayer");

                packetClass = Reflection.getNMSClass("Packet");
                sendPacketMethod = Reflection.getNMSClass("PlayerConnection").getDeclaredMethod("sendPacket", packetClass);
            } catch(NoSuchMethodException e) {
                throw new IncompatibleMinecraftVersionException("Cannot load classes needed to send network packets.", e);
            }

            for (Object enumConstant : enumTitleActionClass.getEnumConstants()) {
                switch (Enum.class.cast(enumConstant).name()) {
                    case "TITLE":
                        enumTitleActionTitle = enumConstant;
                        break;
                    case "SUBTITLE":
                        enumTitleActionSubtitle = enumConstant;
                        break;
                }
            }
    }

    private Titles() {}

    public static void displayTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        displayRawTitle(
                player, fadeIn, stay, fadeOut,
                "{\"text\": \"" + (title != null ? title.replace("\"", "\\\"") : "") + "\"}",
                "{\"text\": \"" + (subtitle != null ? subtitle.replace("\"", "\\\"") : "") + "\"}"
        );
    }

    public static void displayRawTitle(Player player, int fadeIn, int stay, int fadeOut, String rawTitle, String rawSubtitle) {
        try {
            displayTitle(getPlayerConnection(player), fadeIn, stay, fadeOut, rawTitle, rawSubtitle);
        } catch(InvocationTargetException e) {
            throw new IncompatibleMinecraftVersionException(e);
        }
    }

    public static void broadcastTitle(int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            displayTitle(player, fadeIn, stay, fadeOut, title, subtitle);
        }
    }

    private static void displayTitle(Object connection, int fadeIn, int stay, int fadeOut, String rawTitle, String rawSubtitle) {
        sendTimes(connection, fadeIn, stay, fadeOut);

        if((rawTitle == null || rawTitle.isEmpty()) && rawSubtitle != null && !rawSubtitle.isEmpty()) {
            rawTitle = "{\"text\":\" \"}";
        }

        if(rawTitle != null && !rawTitle.isEmpty()) {
            sendTitleAction(connection, enumTitleActionTitle, rawTitle);
        }

        if(rawSubtitle != null && !rawSubtitle.isEmpty()) {
            sendTitleAction(connection, enumTitleActionSubtitle, rawSubtitle);
        }
    }

    private static void sendTimes(Object connection, int fadeIn, int stay, int fadeOut) {
        try {
            if(fadeIn >= 0 || stay >= 0 || fadeOut >= 0) {
                sendPacket(
                        connection,
                        packetPlayOutTitleClass.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut)
                );
            }
        } catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IncompatibleMinecraftVersionException("Error while sending a TIMES title packer", e instanceof InvocationTargetException ? e.getCause() : e);
        }
    }

    private static void sendTitleAction(Object connection, Object action, String payload) {
        try {
            Object baseComponent = iChatBaseComponent.cast(Reflection.callMethod(chatSerializerClass.getMethod("a", String.class), chatSerializerClass, payload));
            Object titlePacket = packetPlayOutTitleClass.getConstructor(enumTitleActionClass, iChatBaseComponent).newInstance(action, baseComponent);

            sendPacket(connection, titlePacket);
        } catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IncompatibleMinecraftVersionException("Error while sending a " + action + " title packet.", e instanceof InvocationTargetException ? e.getCause() : e);
        }
    }

    private static void sendPacket(Object playerConnection, Object packet) throws InvocationTargetException {
        try {
            if(!packetClass.isAssignableFrom(packet.getClass()))
                throw new ClassCastException("Cannot send a packet object if the object is not a subclass of net.minecraft.server.<version>.Packet (got " + packet.getClass().getName() + ").");

            sendPacketMethod.invoke(playerConnection, packet);
        } catch(IllegalAccessException e) {
            throw new IncompatibleMinecraftVersionException("Error while sending a packet to a player.", e);
        }
    }

    private static Object getPlayerConnection(Object playerHandle) throws InvocationTargetException {
        try {
            if(!entityPlayerClass.isAssignableFrom(playerHandle.getClass()))
                throw new ClassCastException("Cannot retrieve a player connection from another class that net.minecraft.server.<version>.EntityPlayer (got " + playerHandle.getClass().getName() + ").");

            return Reflection.getField(playerHandle.getClass(), "playerConnection").get(playerHandle);
        } catch(IllegalAccessException e) {
            throw new IncompatibleMinecraftVersionException(e);
        }
    }

    private static Object getPlayerConnection(Player player) throws InvocationTargetException {
        return getPlayerConnection(getPlayerHandle(player));
    }

    private static Object getPlayerHandle(Player player) throws InvocationTargetException {
        try {
            Object craftPlayer = craftPlayerClass.cast(player);
            return Reflection.callMethod(craftPlayer.getClass().getDeclaredMethod("getHandle"), craftPlayer);
        } catch(NoSuchMethodException e) {
            throw new IncompatibleMinecraftVersionException("Cannot retrieve standard Bukkit or NBS object while gettting a player's handle.", e);
        }
    }

}

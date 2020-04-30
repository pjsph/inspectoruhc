package me.pjsph.inspectoruhc.game;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.pjsph.com.comphenix.packetwrapper.WrapperPlayServerChat;
import me.pjsph.com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import me.pjsph.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import me.pjsph.com.comphenix.packetwrapper.WrapperPlayServerTitle;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.misc.chat.IUChat;
import me.pjsph.inspectoruhc.misc.chat.IUNoChat;
import me.pjsph.inspectoruhc.teams.Team;
import me.pjsph.inspectoruhc.tools.VariableCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.*;

import java.util.*;

public class IUPlayer {
    private static HashMap<UUID, IUPlayer> cachedPlayer = new HashMap<>();

    public static IUPlayer thePlayer(Player player) {
        if(player == null) return null;
        IUPlayer iup = cachedPlayer.get(player.getUniqueId());
        if(iup == null) {
            iup = new IUPlayer(player);
            iup.setUuid(player.getUniqueId());
            cachedPlayer.put(player.getUniqueId(), iup);
        }
        if(iup.getPlayer() == null)
            iup.setPlayer(player);
        return iup;
    }

    public static IUPlayer removePlayer(Player player) {
        return cachedPlayer.remove(player);
    }

    @Getter @Setter private Player player;
    @Getter @Setter private UUID uuid;
    @Getter private Team team;
    @Getter private VariableCache cache = new VariableCache();

    public IUPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public void sendActionBarMessage(String msg) {
        if(this.player != null) {
            WrapperPlayServerChat chat = new WrapperPlayServerChat();
            chat.setPosition((byte)2);
            chat.setMessage(WrappedChatComponent.fromText(msg));
            chat.sendPacket(getPlayer());
        }
    }

    public void sendMessage(String msg) {
        if(this.player != null)
            getPlayer().sendMessage(msg);
    }

    public void sendTitle(String title, String subtitle, int stay) {
        if(this.player != null) {
            WrapperPlayServerTitle titlePacket = new WrapperPlayServerTitle();
            titlePacket.setAction(EnumWrappers.TitleAction.TIMES);
            titlePacket.setFadeIn(10);
            titlePacket.setStay(stay);
            titlePacket.setFadeOut(10);
            titlePacket.sendPacket(getPlayer());

            titlePacket = new WrapperPlayServerTitle();
            titlePacket.setAction(EnumWrappers.TitleAction.TITLE);
            titlePacket.setTitle(WrappedChatComponent.fromText(title));
            titlePacket.sendPacket(getPlayer());

            titlePacket = new WrapperPlayServerTitle();
            titlePacket.setAction(EnumWrappers.TitleAction.SUBTITLE);
            titlePacket.setTitle(WrappedChatComponent.fromText(subtitle));
            titlePacket.sendPacket(getPlayer());
        }
    }

    public void remove() {
        this.player = null;
    }

    private String name;
    public String getName() {
        return player != null ? getPlayer().getName() : name;
    }

    public void seeRoleOf(IUPlayer toUpdate) {
        if(!isDead() && player != null) {
            if(!toUpdate.isDead()) {
                OfflinePlayer toUpdateOff = Bukkit.getOfflinePlayer(toUpdate.getUuid());
                String displayName = Team.getTeamForPlayer(toUpdate).getColor()+toUpdateOff.getName();

                WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
                ArrayList<PlayerInfoData> infos = new ArrayList<>();
                info.setAction(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
                infos.add(new PlayerInfoData(new WrappedGameProfile(toUpdateOff.getUniqueId(), toUpdateOff.getName()), 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(displayName)));
                info.setData(infos);
                info.sendPacket(getPlayer());

                WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
                team.setMode(0);
                team.setName(toUpdateOff.getName());
                team.setPrefix(Team.getTeamForPlayer(toUpdate).getColor().toString());
                team.setPlayers(Arrays.asList(toUpdateOff.getName()));
                team.sendPacket(getPlayer());
            }
        }
    }

    public Team getTeam() {
        return this.team;
    }

    public boolean isOnline() {
        return this.player != null && this.player.isOnline();
    }

    @Getter private IUChat chat;

    public void joinChat(IUChat chat, IUChat.IUChatCallback callback) {
        joinChat(chat, callback, false);
    }

    public void joinChat(IUChat chat) {
        joinChat(chat, null, false);
    }

    public void joinChat(IUChat chat, boolean muted) {
        joinChat(chat, null, muted);
    }

    public void joinChat(IUChat chat, IUChat.IUChatCallback callback, boolean muted) {
        if(this.chat != null && !muted)
            this.chat.leave(this);

        if(!muted)
            this.chat = chat;

        if(chat != null && player != null)
            chat.join(this, callback == null ? chat.getDefaultCallback() : callback);
    }

    public void leaveChat() {
        joinChat(new IUNoChat(), null);
    }

    public void onChat(String msg) {
        if(chat != null)
            chat.sendMessage(this, msg);
    }

    public boolean isDead() {
        return InspectorUHC.get().getGameManager().isPlayerDead(this);
    }

}

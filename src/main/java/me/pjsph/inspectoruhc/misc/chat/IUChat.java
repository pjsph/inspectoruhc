package me.pjsph.inspectoruhc.misc.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.game.IUPlayer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class IUChat {
    @Getter private final HashMap<IUPlayer, IUChatCallback> viewers = new HashMap<>();
    @Getter private final IUChatCallback defaultCallback;

    public static interface IUChatCallback {
        public String receive(IUPlayer sender, String message);
        public default String send(IUPlayer sender, String message) {return null;}
    }

    public void sendMessage(IUPlayer sender, String message) {
        String sendMessage = getViewers().get(sender).send(sender, message);
        for(Map.Entry<IUPlayer, IUChatCallback> entry : viewers.entrySet())
            entry.getKey().sendMessage(sendMessage != null ? sendMessage : entry.getValue().receive(sender, message));
    }

    public void join(IUPlayer player, IUChatCallback callback) {
        if(getViewers().containsKey(player))
            getViewers().replace(player, callback);
        else
            getViewers().put(player, callback);
    }

    public void leave(IUPlayer player) {
        getViewers().remove(player);
    }
}

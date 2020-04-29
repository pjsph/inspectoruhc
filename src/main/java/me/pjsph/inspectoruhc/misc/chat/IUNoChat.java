package me.pjsph.inspectoruhc.misc.chat;

import me.pjsph.inspectoruhc.game.IUPlayer;

public class IUNoChat extends IUChat {

    public IUNoChat() {
        super(null);
    }

    @Override
    public void sendMessage(IUPlayer sender, String message) {}

    @Override
    public void join(IUPlayer player, IUChatCallback callback) {}

    @Override
    public void leave(IUPlayer player) {}
}

package pt.fcul.lasige.sonaar.api;

import pt.fcul.lasige.sonaar.api.pojo.Message;

public interface IMessageHandler {
    void onSearchResponseMessage(Message message, MessageHandler.SOCIAL_NETWORK socialNetwork);
}

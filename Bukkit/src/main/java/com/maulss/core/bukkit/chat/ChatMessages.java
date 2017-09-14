/*
 * Part of core.
 * 
 * Created on 14 June 2017 at 11:06 PM.
 */

package com.maulss.core.bukkit.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.net.paster.PasteException;
import com.maulss.core.net.paster.Paster;
import org.apache.commons.lang3.Validate;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * The type Chat messages.
 */
public final class ChatMessages {

    private static final ChatMessages INSTANCE = new ChatMessages();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<ChatMessage> chatMessageList = Collections.synchronizedList(Lists.newArrayList());

    // disable initialization
    private ChatMessages() {}

	/**
	 * Paste all chat messages in this session.
	 */
	public void pasteChatMessages() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonElements = new JsonArray();
        for (ChatMessage chatMessage : chatMessageList) {
            jsonElements.add(chatMessage.toJsonObject());
        }

        jsonObject.add("messages", jsonElements);

        try {
            URL paste = Paster.hastebin(gson.toJson(jsonObject)).paste();
            CoreLogger.log("Saved chat messages to %s", paste.toString());
        } catch (PasteException e) {
            CoreLogger.log("Could not paste saved chat messages: " + e);
        }
    }

	/**
	 * Adds a new chat message to the session.
	 *
	 * @param message the message
	 */
	public void add(final ChatMessage message) {
        chatMessageList.add(Validate.notNull(message));
    }

	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	public static ChatMessages getInstance() {
        return INSTANCE;
    }
}
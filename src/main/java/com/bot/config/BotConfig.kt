package com.bot.config

import com.bot.entity.*
import java.util.*

object BotConfig {
	
	var startChatDeterminer: (User) -> MessageChatBuilder = {
		TextChatBuilder(it)
			.then("Hello. Implement this chat first.", {})
			.toMessageDrivenChat()
	}
	var homeMessageFx: (String) -> Boolean = { it == "/home" }
	var backMessageFx: (String) -> Boolean = { it == "/back" }
	
	var specialMessages: Map<(String) -> Boolean, (String, User) -> TextChatBuilder> = mapOf()
	
	fun isSpecialMessage(message: String): Boolean =
		specialMessages.filter { it.key.invoke(message) }.isNotEmpty()
	
	fun getSpecialChat(message: String, user: User): TextChatBuilder? =
		specialMessages.keys.filter { it.invoke(message) }
			.map { specialMessages[it]?.invoke(message, user) }.first()
	
	object ListConfig {
		var nextButton = ">"
		var prevButton = "<"
		var homeButton = "Home"
		var backButton = "Back"
	}
	
	val botToken = Optional.ofNullable(System.getenv("bot-token")).orElse("512984644:AAGqguBiJ-MfCfkeRSpNLN7XwiWzrnQVZok")
	val botHost = Optional.ofNullable(System.getenv("host-server")).orElse("https://68e708d2.ngrok.io")
	
}
package com.bot.entity

import org.springframework.data.annotation.Id
import java.util.*

data class User(@Id var id: String,
                var type: Type = Type.NONAME,
                var fullName: String? = null,
                var username: String? = null,
                var email: String? = null,
                var accessLevel: Int = 0) {
	
	constructor(message: Message) : this(
		id = message.senderId,
		fullName = Optional.ofNullable(message.firstName).orElse("") + " " + Optional.ofNullable(message.lastName).orElse(message.username), //cause it works only this way
		username = message.username
	)
	
	var isSubmitted: Boolean = false
	
	companion object {
		enum class Type {
			BROKER, ADMIN, NONAME, CREDIT
		}
	}
}
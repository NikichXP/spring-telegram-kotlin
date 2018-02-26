package com.bot.api

import com.bot.entity.Message
import com.bot.logic.ChatProcessor
import com.bot.repo.UserFactory
import com.bot.tgapi.Method
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.UnsupportedOperationException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

@RestController
class WebHookHandler {
	
	private var logInput: Boolean = true
	private val chatProcessors = ConcurrentHashMap<String, ChatProcessor>()
	
	@RequestMapping("/")
	fun listen(request: HttpServletRequest) {
		var x = BufferedReader(InputStreamReader(request.inputStream, StandardCharsets.UTF_8))
			.lines().reduce { s1, s2 -> s1 + s2 }.orElse("")
		x = String(x.toByteArray(charset("ISO-8859-1")), Charset.forName("UTF-8"))
		val sb = StringBuilder()
		var i = 0
		while (i < x.length) {
			if (x[i] != '\\') {
				sb.append(x[i])
			} else {
				if (x[i + 1] == 'u') {
					val charCode = Integer.parseInt(x.substring(i + 2, i + 6), 16)
					sb.append(Character.toChars(charCode))
				}
				i += 5
			}
			i++
		}
		val inputJson = sb.toString()
		println("Input JSON:" + inputJson)
		
		try {
			val message: Message
			var jsonObject: JsonObject
			try {
				jsonObject = JsonParser().parse(inputJson).asJsonObject
			} catch (e: JsonSyntaxException) {
				return
			}
			when {
				jsonObject.has("message")        -> message = Message(jsonObject.getAsJsonObject("message"))
				jsonObject.has("callback_query") -> {
					jsonObject = jsonObject.getAsJsonObject("callback_query")
					message = Message(
							id = jsonObject.get("id").asString,
							senderId = jsonObject.getAsJsonObject("from").get("id").asString,
							text = jsonObject["data"].asString,
							textMessageJson = jsonObject
					)
				}
				else                             -> throw UnsupportedOperationException("Need to update Telegram Input Parser, $inputJson")
			}
			chatProcessors.getOrPut(message.senderId, {
				val user = UserFactory[message]
				return@getOrPut ChatProcessor(user)
			}).input(message)
		} catch (e: Exception) {
			e.printStackTrace()
			Method.sendMessage("34080460", "error on parse: $inputJson")
		}
		
	}
}

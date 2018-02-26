//package com.bot.chats
//
//import com.bot.config.BotConfig
//import com.bot.entity.AbstractChatBuilder
//import com.bot.entity.TextChatBuilder
//import com.bot.entity.Response
//import com.bot.entity.User
//import java.util.concurrent.atomic.AtomicInteger
//


// TODO THIS CLASS IS UNDER RECONSTRUCTION. RELEASE IN 1.1 OR 1.2


//class ListChat<T>(user: User) : ChatParent(user) {
//
//	var list: MutableList<T> = mutableListOf()
//	var pageSize: Int = 10
//	var fixedPageSize: Int = 10
//	var printFx: (T) -> String = { it.toString() }
//	var customButtonsMap = HashMap<String, ((ListChat<T>) -> Unit)>()
//	var customChatButtons = HashMap<String, TextChatBuilder>()
//	var backChat: AbstractChatBuilder = BotConfig.startChatDeterminer(user)
//	var selectFunction: (T) -> AbstractChatBuilder = { BotConfig.startChatDeterminer(user) }
//	var headText = ""
//	var tailText = ""
//	var customFlags = HashMap<String, Any>()
//	var elseFunction: (String) -> TextChatBuilder = { getChat() }
//
//	constructor(user: User, list: List<T>) : this(user) {
//		this.list = list.toMutableList()
//	}
//
//	fun add(miniList: List<T>) {
//		this.list.addAll(miniList)
//	}
//
//	fun addCustomButton(name: String, action: (ListChat<T>) -> Unit) = also { this.customButtonsMap[name] = action }
//	fun addCustomChatButton(name: String, chat: TextChatBuilder) = also { this.customChatButtons[name] = chat }
//	fun pageSize(pageSize: Int) = also {
//		this.pageSize = pageSize
//		this.fixedPageSize = pageSize
//	}
//
//	fun printFunction(printFunction: (T) -> String) = also { this.printFx = printFunction }
//	fun selectFunction(chat: (T) -> TextChatBuilder) = also { this.selectFunction = chat }
//	fun backChat(chat: TextChatBuilder) = also { this.backChat = chat }
//	fun elseFunction(inputHandler: (String) -> TextChatBuilder) = also { this.elseFunction = inputHandler }
//	fun reset(list: List<T>) = also {
//		this.list = list.toMutableList()
//		this.pageSize = fixedPageSize
//	}
//
//	fun getChat(skip: Int = 0): TextChatBuilder {
//		val i = AtomicInteger(1)
//		pageSize = Math.min(pageSize, list.size)
//		return TextChatBuilder(user)
//			.setNextChatFunction(
//					Response {
//						"[${skip + 1} - ${skip + pageSize}] / ${list.size}\n" + headText + "\n" +
//								list.stream().skip(skip.toLong()).limit(pageSize.toLong())
//									.map { i.getAndIncrement().toString() + " - " + printFx.invoke(it) }
//									.reduce { a, b -> a + "\n" + b }
//									.orElse("Empty list") + "\n$tailText"
//					}
//						.withCustomKeyboard(arraySelection(list.drop(skip).take(pageSize).count()))
//					, {
//				if (customButtonsMap.containsKey(it)) {
//					customButtonsMap[it]!!.invoke(this)
//					return@setNextChatFunction getChat(0)
//				}
//				return@setNextChatFunction when {
//					it == "◀️"                -> getChat(Math.max(0, skip - pageSize))
//					it == "▶️"                -> getChat(Math.min(skip + pageSize, list.size - pageSize))
//					it.contains("Home", true) -> BaseChats.hello(user)
//					it.contains("Back", true) -> backChat
//					else                      -> when {
//						it.toIntOrNull() != null      -> {
//							if (it.toInt() + skip - 1 < list.size) {
//								selectFunction.invoke(list[it.toInt() + skip - 1])
//							} else {
//								elseFunction.invoke(it)
//							}
//						}
//						customChatButtons[it] != null -> customChatButtons[it]!!
//						else                          -> elseFunction.invoke(it)
//					}
//				}
//			})
//	}
//
//	private fun arraySelection(count: Int): Array<Array<String>> {
//		val line1 = (1..count).map { it.toString() }.toTypedArray()
//		if ((customButtonsMap.size + customChatButtons.size) < 5) {
//			val line2 = arrayOf("◀️", "\uD83C\uDFE0 Home", "\uD83D\uDD19 Back") + customButtonsMap.keys.toTypedArray() +
//					customChatButtons.keys.toTypedArray() + arrayOf("▶️")
//			return arrayOf(line1, line2)
//		} else {
//			val line2 = arrayOf("◀️", "\uD83C\uDFE0 Home", "\uD83D\uDD19 Back", "▶️")
//			val line3 = customButtonsMap.keys.toTypedArray() + customChatButtons.keys.toTypedArray()
//			return arrayOf(line1, line2, line3)
//		}
//	}
//
//}
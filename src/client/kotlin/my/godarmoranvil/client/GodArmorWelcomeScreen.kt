package my.godarmoranvil.client

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class GodArmorWelcomeScreen(private val parent: Screen? = null) : Screen(Component.literal("GodArmorAnvil")) {
    private val startTime = System.currentTimeMillis()
    private lateinit var discordBtn: Button
    private lateinit var doneBtn: Button

    private val lines = listOf(
        "✦ WELCOME TO GOD ARMOR ANVIL ✦",
        "Break the limits of Minecraft armor forging!",
        "",
        "• UNLIMITED ANVIL: The 'Too Expensive!' repair cap is gone forever.",
        "• GOD ARMOR: Protection, Fire, Blast, and Projectile",
        "  Protection can now be combined together on the same armor!",
        "• Weapons & tools retain their original vanilla balance.",
        "",
        "Need help, found an issue, or want to suggest new features?",
        "Feel free to reach out to me directly on Discord:"
    )

    private fun argb(alpha: Int, rgb: Int): Int = (alpha.coerceIn(0, 255) shl 24) or (rgb and 0xFFFFFF)

    override fun onClose() {
        if (parent != null) {
            GodarmoranvilClient.openScreen(parent)
        } else {
            super.onClose()
        }
    }

    override fun init() {
        val w = 240
        val left = width / 2 - w / 2
        val bottomY = height - 34

        discordBtn = Button.builder(Component.literal("📋 Copy Discord: l9mm").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)) { btn ->
            Minecraft.getInstance().keyboardHandler.clipboard = "l9mm"
            btn.message = Component.literal("✔ Copied: l9mm").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
        }.bounds(left, bottomY - 24, w, 20).build()
        addRenderableWidget(discordBtn)

        doneBtn = Button.builder(Component.literal("Back to Menu")) { _ -> onClose() }
            .bounds(left, bottomY, w, 20).build()
        addRenderableWidget(doneBtn)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, delta)

        val elapsedMs = System.currentTimeMillis() - startTime
        val cardW = 340
        val cardH = 175
        val cx = width / 2
        val cy = height / 2 - 24
        val cardX = cx - cardW / 2
        val cardY = maxOf(10, cy - cardH / 2)

        // Fade-in tween ناعم وسلس للوحة
        val progress = (elapsedMs / 400f).coerceIn(0f, 1f)
        val ease = (1f - (1f - progress) * (1f - progress))
        val currentAlpha = (ease * 255).toInt()

        val bgColor = argb((currentAlpha * 0.94f).toInt(), 0x0D1424)
        val borderColor = argb((currentAlpha * 0.85f).toInt(), 0x2A3E66)

        // رسم كارت العرض وحدود النيون الفخمة
        graphics.fill(cardX, cardY, cardX + cardW, cardY + cardH, bgColor)
        graphics.fill(cardX, cardY, cardX + cardW, cardY + 1, borderColor)
        graphics.fill(cardX, cardY + cardH - 1, cardX + cardW, cardY + cardH, borderColor)
        graphics.fill(cardX, cardY, cardX + 1, cardY + cardH, borderColor)
        graphics.fill(cardX + cardW - 1, cardY, cardX + cardW, cardY + cardH, borderColor)

        // أنيميشن الـ Typewriter لكتابة النص كلمة كلمة مع ألوان ARGB صريحة ومضيئة
        val wordSpeedMs = 45L
        var totalWordsAllowed = (elapsedMs / wordSpeedMs).toInt()

        var lineY = cardY + 10
        for (line in lines) {
            if (line.isEmpty()) {
                lineY += 6
                continue
            }

            val words = line.split(" ")
            val visibleWords = ArrayList<String>()
            for (w in words) {
                if (totalWordsAllowed > 0) {
                    visibleWords.add(w)
                    totalWordsAllowed--
                }
            }

            if (visibleWords.isNotEmpty()) {
                val visibleLine = visibleWords.joinToString(" ")
                val rgb = when {
                    "✦" in visibleLine -> 0xFFD700 // ذهبي
                    "UNLIMITED" in visibleLine || "GOD ARMOR" in visibleLine -> 0x00E5FF // نيون أزرق
                    "Discord" in visibleLine -> 0xFFA726 // برتقالي دافئ
                    else -> 0xE0EAF8 // أبيض فضي ناصع
                }
                val textColor = argb(currentAlpha, rgb)
                graphics.centeredText(font, Component.literal(visibleLine), cx, lineY, textColor)
            }

            lineY += 11
        }
    }
}

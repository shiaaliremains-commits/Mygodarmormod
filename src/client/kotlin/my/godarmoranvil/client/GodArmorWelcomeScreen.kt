package my.godarmoranvil.client

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class GodArmorWelcomeScreen(private val parent: Screen? = null) : Screen(Component.literal("GodArmorAnvil")) {
    private val startTime = System.currentTimeMillis()
    private lateinit var discordBtn: Button
    private lateinit var doneBtn: Button

    private val lines = listOf(
        "Welcome to GodArmorAnvil!",
        "",
        "✦ UNLIMITED ANVIL POWER:",
        "• The 'Too Expensive!' repair cap has been completely removed.",
        "• Combine and repair your gear indefinitely without limits.",
        "",
        "✦ DIVINE ARMOR COMBINATIONS:",
        "• Protection, Fire Protection, Blast Protection, and",
        "  Projectile Protection can now be united on the same armor piece!",
        "• Forge your invincible God Armor set.",
        "• Weapon and tool enchantments retain their original balance.",
        "",
        "Have questions, found a bug, or want to suggest new features?",
        "Feel free to reach out to me directly on Discord:"
    )

    override fun onClose() {
        if (parent != null) {
            minecraft?.setScreen(parent)
        } else {
            super.onClose()
        }
    }

    override fun init() {
        val w = 240
        val left = width / 2 - w / 2
        val bottomY = height - 32

        discordBtn = Button.builder(Component.literal("📋 Copy Discord: l9mm").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)) { btn ->
            Minecraft.getInstance().keyboardHandler.clipboard = "l9mm"
            btn.message = Component.literal("✔ Copied: l9mm").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
        }.bounds(left, bottomY - 26, w, 20).build()
        addRenderableWidget(discordBtn)

        doneBtn = Button.builder(Component.literal("Back to Menu")) { _ -> onClose() }
            .bounds(left, bottomY, w, 20).build()
        addRenderableWidget(doneBtn)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val elapsed = (System.currentTimeMillis() - startTime) / 1000f
        val fade = (elapsed * 2f).coerceIn(0f, 1f)
        val alpha = (fade * 180).toInt()
        guiGraphics.fill(0, 0, width, height, (alpha shl 24) or 0x0A0E18)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)

        val elapsedMs = System.currentTimeMillis() - startTime
        val cardW = 340
        val cardH = 210
        val cx = width / 2
        val cy = height / 2 - 20
        val cardX = cx - cardW / 2
        val cardY = maxOf(10, cy - cardH / 2)

        // Smooth panel fade & slide tween
        val progress = (elapsedMs / 450f).coerceIn(0f, 1f)
        val ease = 1f - (1f - progress) * (1f - progress)
        val currentAlpha = (ease * 255).toInt()

        val bgColor = (currentAlpha * 0.9f).toInt().coerceIn(0, 255) shl 24 or 0x101726
        val borderColor = (currentAlpha * 0.8f).toInt().coerceIn(0, 255) shl 24 or 0x2A3E66
        guiGraphics.fill(cardX, cardY, cardX + cardW, cardY + cardH, bgColor)
        guiGraphics.fill(cardX, cardY, cardX + cardW, cardY + 1, borderColor)
        guiGraphics.fill(cardX, cardY + cardH - 1, cardX + cardW, cardY + cardH, borderColor)
        guiGraphics.fill(cardX, cardY, cardX + 1, cardY + cardH, borderColor)
        guiGraphics.fill(cardX + cardW - 1, cardY, cardX + cardW, cardY + cardH, borderColor)

        val title = Component.literal("✦ GOD ARMOR ANVIL ✦").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        guiGraphics.drawCenteredString(font, title, cx, cardY + 8, 0xFFE24D)
        guiGraphics.fill(cardX + 16, cardY + 22, cardX + cardW - 16, cardY + 23, 0x444D94FF)

        // أنيميشن الـ Typewriter لكتابة النص كلمة كلمة
        val wordSpeedMs = 50L
        var totalWordsAllowed = (elapsedMs / wordSpeedMs).toInt()

        var lineY = cardY + 30
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
                val color = when {
                    "✦" in visibleLine -> 0x00E5FF
                    "•" in visibleLine -> 0xE0EAF8
                    "Discord" in visibleLine -> 0xFFA726
                    else -> 0xB0C2DE
                }
                guiGraphics.drawString(font, visibleLine, cardX + 14, lineY, color, true)
            }

            lineY += 11
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }
}

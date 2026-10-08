package my.godarmoranvil.client

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

object GodarmoranvilClient : ClientModInitializer {
    override fun onInitializeClient() {
        // Welcome screen registered via ModMenu
    }

    fun openScreen(screen: Screen?) {
        val mc = Minecraft.getInstance()
        val gui = runCatching { mc.javaClass.getField("gui").get(mc) }.getOrNull()
        if (!invokeSetScreen(gui, screen)) invokeSetScreen(mc, screen)
    }

    private fun invokeSetScreen(target: Any?, screen: Screen?): Boolean {
        if (target == null) return false
        val method = target.javaClass.methods.firstOrNull {
            (it.name == "setScreen" || it.name == "setScreenAndShow") && it.parameterCount == 1
        } ?: return false
        method.invoke(target, screen)
        return true
    }
}

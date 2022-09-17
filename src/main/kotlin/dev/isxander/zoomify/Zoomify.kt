package dev.isxander.zoomify

import dev.isxander.zoomify.config.ZoomKeyBehaviour
import dev.isxander.zoomify.config.ZoomifySettings
import dev.isxander.zoomify.utils.mc
import dev.isxander.zoomify.zoom.SingleZoomHelper
import dev.isxander.zoomify.zoom.TieredZoomHelper
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

object Zoomify : ClientModInitializer {
    val guiKey = KeyBinding("zoomify.key.gui", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F8, "zoomify.key.category")
    val zoomKey = KeyBinding("zoomify.key.zoom", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_C, "zoomify.key.category")

    var zooming = false

    private val normalZoomHelper = SingleZoomHelper({ ZoomifySettings.initialZoom.toDouble() }, { ZoomifySettings.zoomSpeed.toDouble() / 100.0 }, { ZoomifySettings.zoomTransition })
    private val scrollZoomHelper = TieredZoomHelper({ ZoomifySettings.scrollZoomSpeed.toDouble() / 100.0 }, { ZoomifySettings.scrollZoomTransition }, { 6 }, { ZoomifySettings.maxScrollZoom / 100.0 * 5.0 })
    private var scrollSteps = 0

    var previousZoomDivisor = 1.0
        private set

    override fun onInitializeClient() {
        ZoomifySettings

        KeyBindingHelper.registerKeyBinding(zoomKey)
        KeyBindingHelper.registerKeyBinding(guiKey)

        ClientTickEvents.END_CLIENT_TICK.register {
            if (zoomKey.wasPressed() && ZoomifySettings.zoomKeyBehaviour == ZoomKeyBehaviour.TOGGLE) {
                zooming = !zooming
            }

            if (guiKey.wasPressed()) {
                mc.setScreen(ZoomifySettings.gui(mc.currentScreen))
            }
        }
    }

    @JvmStatic
    fun getZoomDivisor(tickDelta: Float = 1f): Double {
        if (ZoomifySettings.zoomKeyBehaviour == ZoomKeyBehaviour.HOLD)
            zooming = zoomKey.isPressed

        if (!zooming) scrollSteps = 0

        previousZoomDivisor = (normalZoomHelper.getZoomDivisor(SingleZoomHelper.SingleZoomParams(zooming, tickDelta)) +
                scrollZoomHelper.getZoomDivisor(TieredZoomHelper.TieredZoomParams(scrollSteps, tickDelta))).coerceAtLeast(1.0)

        return previousZoomDivisor
    }

    @JvmStatic
    fun mouseZoom(mouseDelta: Double) {
        if (mouseDelta > 0) {
            scrollSteps++
        } else if (mouseDelta < 0) {
            scrollSteps--
        }
        scrollSteps = scrollSteps.coerceIn(-2..6)
    }
}

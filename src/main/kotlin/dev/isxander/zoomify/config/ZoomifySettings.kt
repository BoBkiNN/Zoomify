package dev.isxander.zoomify.config

import dev.isxander.settxi.impl.*
import dev.isxander.settxi.serialization.PrimitiveType
import dev.isxander.settxi.serialization.SettxiFileConfig
import dev.isxander.settxi.serialization.kotlinxSerializer
import dev.isxander.zoomify.Zoomify
import dev.isxander.zoomify.utils.TransitionType
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import kotlin.io.path.notExists

object ZoomifySettings : SettxiFileConfig(
    FabricLoader.getInstance().configDir.resolve("zoomify.json"),
    kotlinxSerializer(Json { prettyPrint = true })
) {
    const val BEHAVIOUR = "zoomify.gui.category.behaviour"
    const val SCROLLING = "zoomify.gui.category.scrolling"
    const val CONTROLS = "zoomify.gui.category.controls"
    const val SPYGLASS = "zoomify.gui.category.spyglass"
    const val SECONDARY = "zoomify.gui.category.secondary"
    const val MISC = "zoomify.gui.category.misc"

    private var needsSaving = false

    var initialZoom by int(4) {
        name = "zoomify.gui.initialZoom.name"
        description = "zoomify.gui.initialZoom.description"
        category = BEHAVIOUR
        range = 1..10
    }

    var zoomInTime by double(1.0) {
        name = "zoomify.gui.zoomInTime.name"
        description = "zoomify.gui.zoomInTime.description"
        category = BEHAVIOUR
        range = 0.1..5.0
    }

    var zoomOutTime by double(0.5) {
        name = "zoomify.gui.zoomOutTime.name"
        description = "zoomify.gui.zoomOutTime.description"
        category = BEHAVIOUR
        range = 0.1..5.0
    }

    var zoomInTransition by enum(TransitionType.EASE_OUT_EXP) {
        name = "zoomify.gui.zoomInTransition.name"
        description = "zoomify.gui.zoomInTransition.description"
        category = BEHAVIOUR
        defaultSerializedValue = { _, category ->
            if (category?.containsKey("zoomify_gui_zoomtransition_name") == true) {
                needsSaving = true
                category["zoomify_gui_zoomtransition_name"]!!
            } else {
                PrimitiveType.of(default.ordinal)
            }
        }
        migrator { type ->
            if (type.primitive.isString) {
                Zoomify.LOGGER.info("Migrating transition type from string to int")
                PrimitiveType.of(TransitionType.values().find { transition ->
                    transition.displayName.lowercase().replace(Regex("\\W+"), "_")
                        .trim { it == '_' || it.isWhitespace() } == type.primitive.string
                }!!.ordinal).also { needsSaving = true }
            } else type
        }
    }

    var zoomOutTransition by enum(TransitionType.EASE_OUT_EXP) {
        name = "zoomify.gui.zoomOutTransition.name"
        description = "zoomify.gui.zoomOutTransition.description"
        category = BEHAVIOUR
        defaultSerializedValue = { _, category ->
            if (category?.containsKey("zoomify_gui_zoomtransition_name") == true) {
                needsSaving = true
                category["zoomify_gui_zoomtransition_name"]!!
            } else {
                PrimitiveType.of(default.ordinal)
            }
        }
        migrator { type ->
            if (type.primitive.isString) {
                Zoomify.LOGGER.info("Migrating transition type from string to int")
                PrimitiveType.of(TransitionType.values().find { transition ->
                    transition.displayName.lowercase().replace(Regex("\\W+"), "_")
                        .trim { it == '_' || it.isWhitespace() } == type.primitive.string
                }!!.ordinal).also { needsSaving = true }
            } else type
        }
        modifyGet { it.opposite() }
    }

    var affectHandFov by boolean(true) {
        name = "zoomify.gui.affectHandFov.name"
        description = "zoomify.gui.affectHandFov.description"
        category = BEHAVIOUR
    }

    var retainZoomSteps by boolean(false) {
        name = "zoomify.gui.retainZoomSteps.name"
        description = "zoomify.gui.retainZoomSteps.description"
        category = BEHAVIOUR
    }

    var scrollZoom by boolean(true) {
        name = "zoomify.gui.scrollZoom.name"
        description = "zoomify.gui.scrollZoom.description"
        category = SCROLLING
    }

    var scrollZoomAmount by int(3) {
        name = "zoomify.gui.scrollZoomAmount.name"
        description = "zoomify.gui.scrollZoomAmount.description"
        category = SCROLLING
        range = 1..10
        migrator { type ->
            if (!type.primitive.isInt) {
                PrimitiveType.of(default)
            }
            type
        }
    }

    var scrollZoomSmoothness by int(70) {
        name = "zoomify.gui.scrollZoomSmoothness.name"
        description = "zoomify.gui.scrollZoomSmoothness.description"
        category = SCROLLING
        range = 0..100
    }

    var linearLikeSteps by boolean(true) {
        name = "zoomify.gui.linearLikeSteps.name"
        description = "zoomify.gui.linearLikeSteps.description"
        category = SCROLLING
    }

    var keybindScrolling = false
        private set

    var _keybindScrolling by boolean(false) {
        name = "zoomify.gui.keybindScrolling.name"
        description = "zoomify.gui.keybindScrolling.description"
        category = CONTROLS
    }

    var relativeSensitivity by int(100) {
        name = "zoomify.gui.relativeSensitivity.name"
        description = "zoomify.gui.relativeSensitivity.description"
        category = CONTROLS
        range = 0..150

        migrator { type ->
            if (type.isPrimitive && type.primitive.isBoolean) {
                needsSaving = true
                if (type.primitive.boolean)
                    PrimitiveType.of(100)
                else
                    PrimitiveType.of(0)
            } else {
                type
            }
        }
    }

    var relativeViewBobbing by boolean(true) {
        name = "zoomify.gui.relativeViewBobbing.name"
        description = "zoomify.gui.relativeViewBobbing.description"
        category = CONTROLS
    }

    var cinematicCamera by int(0) {
        name = "zoomify.gui.cinematicCam.name"
        description = "zoomify.gui.cinematicCam.description"
        category = CONTROLS
        range = 0..250

        migrator { type ->
            if (type.isPrimitive && type.primitive.isBoolean) {
                needsSaving = true
                if (type.primitive.boolean)
                    PrimitiveType.of(100)
                else
                    PrimitiveType.of(0)
            } else {
                type
            }
        }
    }

    var spyglassBehaviour by enum(SpyglassBehaviour.COMBINE) {
        name = "zoomify.gui.spyglassBehaviour.name"
        description = "zoomify.gui.spyglassBehaviour.description"
        category = SPYGLASS
    }

    var spyglassOverlayVisibility by enum(OverlayVisibility.HOLDING) {
        name = "zoomify.gui.spyglassOverlayVisibility.name"
        description = "zoomify.gui.spyglassOverlayVisibility.description"
        category = SPYGLASS
    }

    var spyglassSoundBehaviour by enum(SoundBehaviour.WITH_OVERLAY) {
        name = "zoomify.gui.spyglassSoundBehaviour.name"
        description = "zoomify.gui.spyglassSoundBehaviour.description"
        category = SPYGLASS
    }

    var secondaryZoomAmount by int(4) {
        name = "zoomify.gui.secondaryZoomAmount.name"
        description = "zoomify.gui.secondaryZoomAmount.description"
        category = SECONDARY
        range = 2..10
    }

    var secondaryZoomInTime by double(10.0) {
        name = "zoomify.gui.secondaryZoomInTime.name"
        description = "zoomify.gui.secondaryZoomInTime.description"
        category = SECONDARY
        range = 6.0..30.0
    }

    var secondaryZoomOutTime by double(1.0) {
        name = "zoomify.gui.secondaryZoomOutTime.name"
        description = "zoomify.gui.secondaryZoomOutTime.description"
        category = SECONDARY
        range = 0.0..5.0
    }

    var secondaryHideHUDOnZoom by boolean(true) {
        name = "zoomify.gui.secondaryZoomHideHUDOnZoom.name"
        description = "zoomify.gui.secondaryZoomHideHUDOnZoom.description"
        category = SECONDARY
    }

    val firstLaunch = filePath.notExists()

    init {
        import()
        if (needsSaving) {
            export()
            needsSaving = false
        }

        keybindScrolling = _keybindScrolling
    }
}

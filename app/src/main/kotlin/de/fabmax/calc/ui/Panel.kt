package de.fabmax.calc.ui

import android.content.Context
import de.fabmax.lightgl.util.Color
import de.fabmax.lightgl.util.Painter

/**
 * A panel with a single color background.
 */
open class Panel<T: PanelConfig>(config: T, context: Context) : UiElement<T>(config, context) {
    /**
     * Draws a rectangular shape with the current size and color of this panel
     */
    override fun paint(painter: Painter) {
        val color = layoutConfig.color
        painter.setColor(color)
        painter.fillRect(0.0f, 0.0f, width, height)
    }
}

/**
 * Panel specific LayoutConfiguration extended with a orientation dependent color.
 */
open class PanelConfig : LayoutConfig() {
    private val props = Array(Orientation.ALL, { i -> PanelProperties() })

    val color = FloatArray(4)

    /**
     * Interpolates panel size and color.
     */
    override fun mixConfigs(portLandMix: Float) {
        super.mixConfigs(portLandMix)

        val colorP = props[Orientation.PORTRAIT].color
        val colorL = props[Orientation.LANDSCAPE].color
        color[0] = colorP.r * portLandMix + colorL.r * (1f - portLandMix)
        color[1] = colorP.g * portLandMix + colorL.g * (1f - portLandMix)
        color[2] = colorP.b * portLandMix + colorL.b * (1f - portLandMix)
        color[3] = colorP.a * portLandMix + colorL.a * (1f - portLandMix)
    }

    fun getPanelProperties(orientation: Int): PanelProperties {
        return props[orientation]
    }

    /**
     * Sets the color for the given orientation. If orientation is Orientation.ALL, color is set
     * for all orientations.
     */
    fun setColor(orientation: Int, color: Color) {
        if (orientation >= 0 && orientation < props.size) {
            props[orientation].color = color
        } else {
            props.forEach { p -> p.color = color }
        }
    }
}

class PanelProperties {
    var color = Color.LIGHT_GRAY
}

/**
 * Base builder for panels, offers the color property.
 */
abstract class AbstractPanelBuilder<T: Panel<*>>(context: Context) : UiElementBuilder<T>(context) {
    /**
     * Panel color for the current orientation.
     */
    var color: Color
        get() = element.layoutConfig.getPanelProperties(orientation).color
        set(value) {
            element.layoutConfig.setColor(orientation, value)
        }
}

class PanelBuilder(context: Context) : AbstractPanelBuilder<Panel<PanelConfig>>(context) {
    override fun create(): Panel<PanelConfig> {
        return Panel(PanelConfig(), context)
    }
}

package infrastructure

import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.HSV

fun generatePalette(base: Color, colors: Int): List<Color> {
    val mathBase = base.toColormathColor()
    val hsvPrimary = mathBase.toHSV()

    return (0..<colors).map { i ->
        HSV(
            h = (hsvPrimary.h + (360 / colors) * i) % 360,
            s = hsvPrimary.s,
            v = hsvPrimary.v
        ).toComposeColor()
    }
}
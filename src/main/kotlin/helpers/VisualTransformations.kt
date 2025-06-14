package helpers

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Making XXXX-XXXX-XXXX-XXXX string.
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if(i == 3 || i == 5) out += "-"
        }

        /**
         * The offset translator should ignore the hyphen characters, so conversion from original offset
         * to transformed text works like
         * - The 4th char of the original text is 5th char in the transformed text.
         * - The 13th char of the original text is 15th char in the transformed text. Similarly, the
         *   reverse conversion works like
         * - The 5th char of the transformed text is 4th char in the original text.
         * - The 12th char of the transformed text is 10th char in the original text.
         */
        val creditCardOffsetTranslator =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (offset <= 3) return offset
                    if (offset <= 5) return offset + 1
                    if (offset <= 7) return offset + 2
                    return 10
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if(offset <= 4) return offset
                    if(offset <= 7) return offset - 1
                    if(offset <= 9) return offset - 2
                    return 8
                }
            }

        return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)

    }
}
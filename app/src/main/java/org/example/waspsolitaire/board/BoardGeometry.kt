package org.example.waspsolitaire.board

/** Presentation geometry in dp, with no game rules or alternative board state. */
data class BoardGeometry(val cardWidth: Float, val cardHeight: Float, val faceStep: Float, val backStep: Float) {
    fun columnHeight(faceStates: List<Boolean>): Float =
        cardHeight + faceStates.dropLast(1).sumOf { (if (it) faceStep else backStep).toDouble() }.toFloat()

    companion object {
        const val GUTTER = 4f
        fun fit(availableWidth: Float, fontScale: Float, viewportHeight: Float = Float.POSITIVE_INFINITY): BoardGeometry {
            require(availableWidth > GUTTER * 6 && fontScale > 0)
            val width = ((availableWidth - GUTTER * 6) / 7f).coerceAtMost(120f)
                .coerceAtMost(((viewportHeight - 8f) / 1.44f).coerceAtLeast(20f))
            val rankSize = (width * .27f).coerceAtMost(22f)
            val faceStep = rankSize * fontScale * 1.3f + width * .24f + 10f
            return BoardGeometry(width, maxOf(width * 1.44f, faceStep + 12f), faceStep, width * .20f)
        }
    }
}

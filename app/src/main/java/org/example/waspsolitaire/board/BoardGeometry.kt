package org.example.waspsolitaire.board

/** Presentation geometry in dp, with no game rules or alternative board state. */
data class BoardGeometry(val cardWidth: Float, val cardHeight: Float, val faceStep: Float, val backStep: Float,
    val rankSize: Float = (cardWidth * .36f).coerceIn(16.8f, 26.4f)) {
    fun stackedIndices(fontScale: Float) = fontScale >= 1.75f && rankSize * fontScale * 2.1f + 6f > cardWidth
    fun columnHeight(faceStates: List<Boolean>): Float =
        cardHeight + faceStates.dropLast(1).sumOf { (if (it) faceStep else backStep).toDouble() }.toFloat()

    companion object {
        const val GUTTER = 4f
        fun fit(availableWidth: Float, fontScale: Float, maxRankSize: Float = 26.4f): BoardGeometry {
            require(availableWidth > GUTTER * 6 && fontScale > 0)
            val width = ((availableWidth - GUTTER * 6) / 7f).coerceAtMost(104f)
            val rankSize = (width * .36f).coerceIn(16.8f, maxRankSize.coerceAtLeast(16.8f))
            val stacked = fontScale >= 1.75f && rankSize * fontScale * 2.1f + 6f > width
            // Four dp above the indices, two dp below; no empty middle strip.
            val faceStep = rankSize * fontScale * (if (stacked) 2.1f else 1.2f) + 6f
            return BoardGeometry(width, maxOf(width * 1.44f, faceStep + 12f), faceStep, 8f, rankSize)
        }
    }
}

package org.example.waspsolitaire.board

/** Presentation geometry in dp, with no game rules or alternative board state. */
data class BoardGeometry(val cardWidth: Float, val cardHeight: Float, val faceStep: Float, val backStep: Float,
    val rankSize: Float = (cardWidth * .44f).coerceIn(18.4f, 32.3f)) {
    fun stackedIndices(fontScale: Float) = fontScale >= 1.75f && rankSize * fontScale * 1.63f + 4f > cardWidth
    fun columnHeight(faceStates: List<Boolean>): Float =
        cardHeight + faceStates.dropLast(1).sumOf { (if (it) faceStep else backStep).toDouble() }.toFloat()

    companion object {
        const val GUTTER = 5f
        fun fit(availableWidth: Float, fontScale: Float, maxRankSize: Float = 32.3f): BoardGeometry {
            require(availableWidth > GUTTER * 6 && fontScale > 0)
            val width = ((availableWidth - GUTTER * 6) / 7f).coerceAtMost(104f)
            val rankSize = (width * .44f).coerceIn(18.4f, maxRankSize.coerceAtLeast(18.4f))
            val stacked = fontScale >= 1.75f && rankSize * fontScale * 1.63f + 4f > width
            // Tight ink-height header: two dp above and below the visible glyphs.
            val faceStep = rankSize * fontScale * (if (stacked) 1.63f else .85f) + 4f
            return BoardGeometry(width, maxOf(width * 1.44f, faceStep + 12f), faceStep, 8f, rankSize)
        }
    }
}

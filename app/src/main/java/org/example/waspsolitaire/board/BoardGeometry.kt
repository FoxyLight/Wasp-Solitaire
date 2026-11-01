package org.example.waspsolitaire.board

/** Presentation geometry in dp, with no game rules or alternative board state. */
data class BoardGeometry(val cardWidth: Float, val cardHeight: Float, val faceStep: Float, val backStep: Float,
    val rankSize: Float = (cardWidth * .30f).coerceIn(14f, 22f)) {
    fun stackedIndices(fontScale: Float) = rankSize * fontScale * 2.35f + 8f > cardWidth
    fun columnHeight(faceStates: List<Boolean>): Float =
        cardHeight + faceStates.dropLast(1).sumOf { (if (it) faceStep else backStep).toDouble() }.toFloat()

    companion object {
        const val GUTTER = 4f
        fun fit(availableWidth: Float, fontScale: Float, maxRankSize: Float = 22f): BoardGeometry {
            require(availableWidth > GUTTER * 6 && fontScale > 0)
            val width = ((availableWidth - GUTTER * 6) / 7f).coerceAtMost(104f)
            val rankSize = (width * .30f).coerceIn(14f, maxRankSize)
            val stacked = rankSize * fontScale * 2.35f + 8f > width
            val header = rankSize * fontScale * (if (stacked) 2.2f else 1.3f) + 10f
            val faceStep = maxOf(40f, header)
            return BoardGeometry(width, maxOf(width * 1.44f, faceStep + 12f), faceStep, 8f, rankSize)
        }
    }
}

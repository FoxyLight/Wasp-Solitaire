package org.example.waspsolitaire.board

import kotlinx.coroutines.runBlocking
import org.example.waspsolitaire.engine.ClassicWasp
import org.example.waspsolitaire.game.*
import org.junit.Assert.*
import org.junit.Test

class BoardGeometryTest {
    @Test fun sevenColumnsFitPhonesAndTabletsWithoutHorizontalOverflow() {
        for (width in listOf(320f, 360f, 400f, 800f, 1200f)) {
            val geometry = BoardGeometry.fit(width - 16f, 1f)
            assertTrue(geometry.cardWidth * 7 + BoardGeometry.GUTTER * 6 <= width - 16f + .01f)
            assertTrue(geometry.faceStep < geometry.cardHeight)
            assertTrue(geometry.backStep < geometry.faceStep)
        }
    }
    @Test fun largeTextGetsEnoughExposedHeaderSpaceAndLongColumnsRemainMeasurable() {
        val normal = BoardGeometry.fit(304f, 1f)
        val large = BoardGeometry.fit(304f, 2f)
        assertTrue(large.faceStep > normal.faceStep)
        assertTrue(large.cardHeight > large.faceStep)
        assertEquals(large.cardHeight + 51 * large.faceStep, large.columnHeight(List(52) { true }), .05f)
    }
    @Test fun readableIndicesAndLongTailsUseScrollingRatherThanShrinking() {
        for (scale in listOf(1f, 1.3f, 2f)) {
            val geometry = BoardGeometry.fit(304f, scale)
            assertTrue(geometry.rankSize >= 14f)
            val indicesHeight = geometry.rankSize * scale *
                (if (geometry.stackedIndices(scale)) 2.1f else 1.2f) + 4f
            assertTrue("Overlap must leave rank and suit fully exposed", geometry.faceStep >= indicesHeight + 4f)
            assertTrue(geometry.cardHeight > geometry.faceStep)
            assertTrue(geometry.columnHeight(List(52) { true }) > 320f)
        }
        assertTrue(BoardGeometry.fit(304f, 2f).stackedIndices(2f))
    }
    @Test fun seedSupplierRunsOnceAndFactoryKeepsRepositorySnapshot() = runBlocking<Unit> {
        var calls = 0
        val owner = GameSessionOwner { calls++; 123L }
        assertSame(owner.repository, owner.repository)
        assertEquals(1, calls)
        val factory = GameViewModelFactory.open(owner.repository)
        val model = factory.create(GameViewModel::class.java)
        assertEquals(ClassicWasp.newGame(123).state, model.uiState.value.board)
        model.dispatch(GameIntent.DealReserve)
        val reopened = GameViewModelFactory.open(owner.repository).create(GameViewModel::class.java)
        assertSame(model.uiState.value.session, reopened.uiState.value.session)
        assertEquals(1, calls)
    }
}

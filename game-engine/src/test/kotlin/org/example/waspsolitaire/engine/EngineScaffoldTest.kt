package org.example.waspsolitaire.engine

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests JVM test discovery/module wiring, not gameplay correctness. */
class EngineScaffoldTest {
    @Test
    fun engineModuleIsAvailableToJvmTests() {
        assertEquals("Game engine not implemented", EngineScaffold.STATUS)
    }
}

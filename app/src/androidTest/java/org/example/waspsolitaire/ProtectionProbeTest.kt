package org.example.waspsolitaire

import org.junit.Test

/** Deliberate CI enforcement probe. Never merge this branch. */
class ProtectionProbeTest {
    @Test fun requiredEmulatorMustBlockMerge() {
        throw AssertionError("Intentional emulator protection probe; do not merge")
    }
}

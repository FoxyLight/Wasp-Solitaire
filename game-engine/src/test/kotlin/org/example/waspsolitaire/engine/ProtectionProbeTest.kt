package org.example.waspsolitaire.engine

import org.junit.Test

/** Deliberate failure: validates main protection; this branch must never merge. */
class ProtectionProbeTest {
    @Test fun requiredVerifyMustBlockMerge() {
        throw AssertionError("Intentional protection enforcement probe; do not merge")
    }
}

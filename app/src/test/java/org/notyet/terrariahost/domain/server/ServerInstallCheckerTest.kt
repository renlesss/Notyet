package org.notyet.terrariahost.domain.server

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ServerInstallCheckerTest {
    private val checker = ServerInstallChecker()

    @Test
    fun missingFileReportsMissing() {
        val missing = File("build/tmp/notyet/missing/TerrariaServer")

        assertTrue(checker.check(missing) is ServerInstallStatus.Missing)
    }
}

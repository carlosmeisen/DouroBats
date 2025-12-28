package pt.dourobats.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test to verify CI workflow catches failing tests
 *
 * This test is intentionally designed to FAIL to verify that:
 * 1. CI runs on pull requests
 * 2. Failed tests block PR from being merged
 * 3. Branch protection rules are working correctly
 *
 * Once CI is verified to catch this failure, this test should be removed or fixed.
 */
class CiWorkflowTest {

    @Test
    fun `CI should catch this intentional failure`() {
        // This test intentionally fails to verify CI is working
        val expected = "CI is working"
        val actual = "CI is NOT working"

        assertEquals(
            expected = expected,
            actual = actual,
            message = "This test should FAIL to verify CI catches test failures"
        )
    }

    @Test
    fun `CI should also catch this assertion failure`() {
        // Another intentional failure
        assertTrue(
            actual = false,
            message = "CI workflow verification: This test intentionally fails"
        )
    }

    @Test
    fun `This test passes to show CI runs all tests`() {
        // This one should pass
        val result = 2 + 2
        assertEquals(4, result, "Basic math should work")
    }
}

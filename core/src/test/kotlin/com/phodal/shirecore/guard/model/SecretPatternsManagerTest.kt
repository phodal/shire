import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.guard.model.SecretPatternsManager

class SecretPatternsManagerTest: BasePlatformTestCase() {
    fun testShouldLoadResources() {
        val secretPatternsManager = SecretPatternsManager()
        val patterns = try {
            secretPatternsManager.load()
        } catch (e: Exception) {
            emptyList()
        }
        assert(patterns.isNotEmpty())
    }
}

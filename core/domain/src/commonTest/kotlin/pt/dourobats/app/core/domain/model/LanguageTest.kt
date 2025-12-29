package pt.dourobats.app.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LanguageTest {

    @Test
    fun `all languages have valid codes`() {
        Language.entries.forEach { language ->
            assertNotNull(language.code, "Language ${language.name} should have a code")
            assert(language.code.isNotEmpty()) { "Language ${language.name} code should not be empty" }
        }
    }

    @Test
    fun `all languages have valid BCP 47 tags`() {
        Language.entries.forEach { language ->
            assertNotNull(language.bcp47Tag, "Language ${language.name} should have a BCP 47 tag")
            assert(language.bcp47Tag.isNotEmpty()) { "Language ${language.name} BCP 47 tag should not be empty" }
        }
    }

    @Test
    fun `all languages have valid resource qualifiers`() {
        Language.entries.forEach { language ->
            assertNotNull(language.resourceQualifier, "Language ${language.name} should have a resource qualifier")
            assert(language.resourceQualifier.isNotEmpty()) { "Language ${language.name} resource qualifier should not be empty" }
        }
    }

    @Test
    fun `all languages have display names`() {
        Language.entries.forEach { language ->
            assertNotNull(language.displayName, "Language ${language.name} should have a display name")
            assert(language.displayName.isNotEmpty()) { "Language ${language.name} display name should not be empty" }
        }
    }

    @Test
    fun `fromCode returns correct language for valid code`() {
        assertEquals(Language.ENGLISH_US, Language.fromCode("en"))
        assertEquals(Language.ENGLISH_GB, Language.fromCode("en-GB"))
        assertEquals(Language.PORTUGUESE_BR, Language.fromCode("pt-BR"))
        assertEquals(Language.PORTUGUESE_PT, Language.fromCode("pt-PT"))
        assertEquals(Language.SPANISH, Language.fromCode("es"))
    }

    @Test
    fun `fromCode returns ENGLISH_US for invalid code`() {
        assertEquals(Language.ENGLISH_US, Language.fromCode("invalid"))
        assertEquals(Language.ENGLISH_US, Language.fromCode(""))
        assertEquals(Language.ENGLISH_US, Language.fromCode("xyz"))
    }

    @Test
    fun `fromCode is case sensitive`() {
        assertEquals(Language.ENGLISH_US, Language.fromCode("EN"))
        assertEquals(Language.ENGLISH_US, Language.fromCode("En"))
    }

    @Test
    fun `getSystemDefault returns a valid language`() {
        val systemDefault = Language.getSystemDefault()
        assertNotNull(systemDefault)
        assert(Language.entries.contains(systemDefault)) {
            "System default should be one of the supported languages"
        }
    }

    @Test
    fun `ENGLISH_US has correct properties`() {
        assertEquals("en", Language.ENGLISH_US.code)
        assertEquals("en", Language.ENGLISH_US.resourceQualifier)
        assertEquals("en-US", Language.ENGLISH_US.bcp47Tag)
        assertEquals("English (US)", Language.ENGLISH_US.displayName)
    }

    @Test
    fun `ENGLISH_GB has correct properties`() {
        assertEquals("en-GB", Language.ENGLISH_GB.code)
        assertEquals("en-rGB", Language.ENGLISH_GB.resourceQualifier)
        assertEquals("en-GB", Language.ENGLISH_GB.bcp47Tag)
        assertEquals("English (UK)", Language.ENGLISH_GB.displayName)
    }

    @Test
    fun `PORTUGUESE_BR has correct properties`() {
        assertEquals("pt-BR", Language.PORTUGUESE_BR.code)
        assertEquals("pt-rBR", Language.PORTUGUESE_BR.resourceQualifier)
        assertEquals("pt-BR", Language.PORTUGUESE_BR.bcp47Tag)
        assertEquals("Português (Brasil)", Language.PORTUGUESE_BR.displayName)
    }

    @Test
    fun `PORTUGUESE_PT has correct properties`() {
        assertEquals("pt-PT", Language.PORTUGUESE_PT.code)
        assertEquals("pt-rPT", Language.PORTUGUESE_PT.resourceQualifier)
        assertEquals("pt-PT", Language.PORTUGUESE_PT.bcp47Tag)
        assertEquals("Português (Portugal)", Language.PORTUGUESE_PT.displayName)
    }

    @Test
    fun `SPANISH has correct properties`() {
        assertEquals("es", Language.SPANISH.code)
        assertEquals("es-rES", Language.SPANISH.resourceQualifier)
        assertEquals("es-ES", Language.SPANISH.bcp47Tag)
        assertEquals("Español", Language.SPANISH.displayName)
    }

    @Test
    fun `deprecated localeTag returns bcp47Tag`() {
        @Suppress("DEPRECATION")
        assertEquals(Language.ENGLISH_US.bcp47Tag, Language.ENGLISH_US.localeTag)
        @Suppress("DEPRECATION")
        assertEquals(Language.PORTUGUESE_BR.bcp47Tag, Language.PORTUGUESE_BR.localeTag)
    }

    @Test
    fun `all languages have unique codes`() {
        val codes = Language.entries.map { it.code }
        val uniqueCodes = codes.distinct()
        assertEquals(codes.size, uniqueCodes.size, "All language codes should be unique")
    }

    @Test
    fun `all languages have unique BCP 47 tags`() {
        val tags = Language.entries.map { it.bcp47Tag }
        val uniqueTags = tags.distinct()
        assertEquals(tags.size, uniqueTags.size, "All BCP 47 tags should be unique")
    }

    @Test
    fun `total number of supported languages is 5`() {
        assertEquals(5, Language.entries.size)
    }
}

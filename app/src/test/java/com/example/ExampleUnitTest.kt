package com.example

import com.example.data.model.BackupRule
import com.example.ui.components.AppLanguage
import com.example.ui.components.LanguageManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLanguageTranslations() {
        LanguageManager.currentLanguage = AppLanguage.ENGLISH
        assertEquals("Photos", LanguageManager.getString("photos"))
        assertEquals("Vault", LanguageManager.getString("vault"))
        assertEquals("Settings", LanguageManager.getString("settings"))

        LanguageManager.currentLanguage = AppLanguage.SPANISH
        assertEquals("Fotos", LanguageManager.getString("photos"))
        assertEquals("Bóveda", LanguageManager.getString("vault"))
        assertEquals("Ajustes", LanguageManager.getString("settings"))

        LanguageManager.currentLanguage = AppLanguage.JAPANESE
        assertEquals("写真", LanguageManager.getString("photos"))
        assertEquals("保管庫", LanguageManager.getString("vault"))
        assertEquals("設定", LanguageManager.getString("settings"))

        // Reset to English
        LanguageManager.currentLanguage = AppLanguage.ENGLISH
    }

    @Test
    fun testBackupRules() {
        val rule = BackupRule.WIFI_AND_CHARGING
        assertNotNull(rule)
        assertTrue(BackupRule.values().contains(BackupRule.WIFI_ONLY))
    }
}

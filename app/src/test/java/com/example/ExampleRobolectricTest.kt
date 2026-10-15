package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.ConfigParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Netcon Precision VPN", appName)
  }

  @Test
  fun `test config parser default nodes`() {
    val presetNodes = ConfigParser.defaultPresetNodes
    assertTrue(presetNodes.isNotEmpty())
    assertEquals("United States", presetNodes[0].country)
  }
}

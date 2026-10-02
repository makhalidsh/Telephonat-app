package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RepairRecord
import com.example.util.MoroccanPhoneUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Phone Repair Register", appName)
  }

  @Test
  fun `moroccan phone number format`() {
    val formatted = MoroccanPhoneUtils.formatMoroccanPhone("0661234567")
    assertEquals("06 61 23 45 67", formatted)

    val withCountryCode = MoroccanPhoneUtils.formatMoroccanPhone("+212661234567")
    assertEquals("06 61 23 45 67", withCountryCode)
  }

  @Test
  fun `repair record qr payload verification`() {
    val record = RepairRecord(
      code = "001",
      codeNumber = 1,
      brand = "Samsung",
      model = "Galaxy A54",
      color = "Noir",
      problem = "Ecran cassé",
      price = 450.0,
      clientName = "Youssef",
      clientPhone = "0661234567",
      receivedAt = "2026-10-02T12:00:00Z"
    )
    val payload = record.qrPayload()
    assertTrue(payload.startsWith("ID:001|TEL:0661234567|CLIENT:Youssef|DEV:Samsung Galaxy A54|PRICE:450DH"))
  }
}

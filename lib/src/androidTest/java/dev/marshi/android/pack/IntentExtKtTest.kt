package dev.marshi.android.pack

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntentExtKtTest {

  @get:Rule
  val activityTestRule = ActivityTestRule(TestActivity::class.java)

  @Before
  fun setUp() {
  }

  @Test
  fun putPackedExtra() {
    val intent = Intent()
    val activity = activityTestRule.activity
    intent.putPackedExtra(activity, "key", Data(1, "aiueo", "nonvalue"))
    val data = intent.getPackedExtra<TestActivity, Data>(activity, "key")
    println(data)
  }

  @Test
  fun getPackedExtra() {
  }
}

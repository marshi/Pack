package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity2 : AppCompatActivity() {

  companion object {
    const val EXTRA = "EXTRA"

    fun creteIntent(context: Context) =
      Intent(context, MainActivity2::class.java).apply {
        val content = Data(
          10,
          "a".repeat(10),
          1000L,
          SubData("sub".repeat(10), SubDataParcelable("1".repeat(10000000))),
//          Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8),
          "non"
        )
        putPackedExtra(context, EXTRA, content)
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main2)
    val packedExtra = intent.getPackedExtra<AppCompatActivity, Data>(this, EXTRA)
    println(packedExtra)
  }
}

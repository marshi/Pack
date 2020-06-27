package dev.marshi.android.pack

import android.graphics.Bitmap
import android.os.Parcelable
import android.widget.ImageView
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class Data(
  @Pack val int: Int,
  @Pack val content: String,
  @Pack val long: Long,
  @Pack val subData: SubData,
  val non: String
) : Serializable


data class SubData(
  val str: String,
  val sub: SubDataParcelable
) : Serializable

data class SubDataParcelable(
  @Pack val str: String
) : Serializable

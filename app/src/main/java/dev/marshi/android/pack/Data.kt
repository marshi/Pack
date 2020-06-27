package dev.marshi.android.pack

import android.graphics.Bitmap
import android.os.Parcelable
import android.widget.ImageView
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.text.DateFormat

data class Data(
  val int: Int?,
  @Pack val content: String,
  @Pack val long: Long,
  @Pack val subData: SubData,
  @Pack val dateFormat: DateFormat,
  val non: String
) : Serializable


data class SubData(
  val str: String?,
  @Pack val sub: SubDataParcelable
) : Serializable

data class SubDataParcelable(
  val str: String
) : Serializable

package dev.marshi.android.pack

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Data(
  @Pack val int: Int,
  @Pack val content: String,
  @Pack val long: Long,
  val non: String
) : Parcelable

package dev.marshi.android.pack

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Data(
  @Pack val int: Int,
  @Pack val str: String,
  val nonValue: String
): Parcelable

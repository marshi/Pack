package dev.marshi.android.pack

import java.io.Serializable

data class Data(
  @Pack val int: Int,
  @Pack val content: String,
  @Pack val long: Long,
  @Pack val subData: SubData,
  val non: String
) : Serializable

data class SubData(
  @Pack val str: String
): Serializable

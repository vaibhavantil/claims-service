package com.hedvig.homer.handlers.utils

enum class LanguageCode {
  SWEDISH {
    override fun toString(): String {
      return "sv-SE"
    }
  },
  GREEK {
    override fun toString(): String {
      return "el-GR"
    }
  }
}

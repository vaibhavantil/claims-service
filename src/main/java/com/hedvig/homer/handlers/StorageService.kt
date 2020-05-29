package com.hedvig.homer.handlers

import java.nio.file.Path

interface StorageService {
  fun uploadObjectAndGetUri(filePath: Path): String
}

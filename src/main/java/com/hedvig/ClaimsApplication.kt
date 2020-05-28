package com.hedvig

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import java.util.TimeZone


@SpringBootApplication(scanBasePackages = ["com.hedvig"])
@EnableFeignClients(basePackages = ["com.hedvig"])
open class ClaimsApplication {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
      SpringApplication.run(ClaimsApplication::class.java, *args)
    }
  }
}

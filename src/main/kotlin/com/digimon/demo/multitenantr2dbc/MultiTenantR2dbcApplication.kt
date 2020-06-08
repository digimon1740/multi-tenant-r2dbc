package com.digimon.demo.multitenantr2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(basePackages = ["com.digimon.demo.multitenantr2dbc.config.properties"])
@SpringBootApplication
class MultiTenantR2dbcApplication

fun main(args: Array<String>) {
	runApplication<MultiTenantR2dbcApplication>(*args)
}

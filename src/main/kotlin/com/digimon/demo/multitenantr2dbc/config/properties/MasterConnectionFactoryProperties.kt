package com.digimon.demo.multitenantr2dbc.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "datasource.master")
data class MasterConnectionFactoryProperties(val username: String, val password: String, val url: String) {

    companion object {
        const val LOOKUP_KEY = "master"
    }
}

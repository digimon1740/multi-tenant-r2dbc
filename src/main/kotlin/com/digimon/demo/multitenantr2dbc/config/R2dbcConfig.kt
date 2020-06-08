package com.digimon.demo.multitenantr2dbc.config

import com.digimon.demo.multitenantr2dbc.config.properties.MasterConnectionFactoryProperties
import com.digimon.demo.multitenantr2dbc.config.properties.SlaveConnectionFactoryProperties
import com.github.jasync.r2dbc.mysql.JasyncConnectionFactory
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory
import com.github.jasync.sql.db.mysql.util.URLParser
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
class R2dbcConfig(val masterConnectionFactoryProperties: MasterConnectionFactoryProperties,
                  val slaveConnectionFactoryProperties: SlaveConnectionFactoryProperties) : AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val multiTenantRoutingConnectionFactory = MultiTenantRoutingConnectionFactory()

        val factories = HashMap<String, ConnectionFactory>()
        factories[MasterConnectionFactoryProperties.LOOKUP_KEY] = masterConnectionFactory()
        factories[SlaveConnectionFactoryProperties.LOOKUP_KEY] = slaveConnectionFactory()

        multiTenantRoutingConnectionFactory.setDefaultTargetConnectionFactory(masterConnectionFactory())
        multiTenantRoutingConnectionFactory.setTargetConnectionFactories(factories)
        return multiTenantRoutingConnectionFactory
    }

    @Bean
    fun masterConnectionFactory(): ConnectionFactory {
        return parseAndGet(Triple(masterConnectionFactoryProperties.url, masterConnectionFactoryProperties.username, masterConnectionFactoryProperties.password))
    }

    @Bean
    fun slaveConnectionFactory(): ConnectionFactory {
        return parseAndGet(Triple(slaveConnectionFactoryProperties.url, slaveConnectionFactoryProperties.username, slaveConnectionFactoryProperties.password))
    }

    private fun parseAndGet(propertiesAsTriple: Triple<String, String, String>): ConnectionFactory {
        val (url, username, password) = propertiesAsTriple

        val properties = URLParser.parseOrDie(url)
        return JasyncConnectionFactory(MySQLConnectionFactory(
            com.github.jasync.sql.db.Configuration(
                username = username,
                password = password,
                host = properties.host,
                port = properties.port,
                database = properties.database,
                charset = properties.charset,
                ssl = properties.ssl
            )))
    }

    @Bean
    fun masterTransactionManager(@Qualifier("masterConnectionFactory") connectionFactory: ConnectionFactory?) =
        R2dbcTransactionManager(connectionFactory!!)

    @Bean
    fun slaveTransactionManager(@Qualifier("slaveConnectionFactory") connectionFactory: ConnectionFactory?) =
        R2dbcTransactionManager(connectionFactory!!)
}


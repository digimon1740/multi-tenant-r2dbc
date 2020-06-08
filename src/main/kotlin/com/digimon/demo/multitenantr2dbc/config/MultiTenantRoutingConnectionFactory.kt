package com.digimon.demo.multitenantr2dbc.config

import com.digimon.demo.multitenantr2dbc.config.properties.MasterConnectionFactoryProperties
import com.digimon.demo.multitenantr2dbc.config.properties.SlaveConnectionFactoryProperties
import org.springframework.data.r2dbc.connectionfactory.lookup.AbstractRoutingConnectionFactory
import org.springframework.transaction.reactive.TransactionSynchronizationManager
import reactor.core.publisher.Mono

class MultiTenantRoutingConnectionFactory : AbstractRoutingConnectionFactory() {

    override fun determineCurrentLookupKey(): Mono<Any> =
        TransactionSynchronizationManager.forCurrentTransaction().map {
            val key = MasterConnectionFactoryProperties.LOOKUP_KEY
            if (it.isActualTransactionActive) {
                if (it.isCurrentTransactionReadOnly) {
                    SlaveConnectionFactoryProperties.LOOKUP_KEY
                } else key
            } else key
        }
}
package com.example.mirotest

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaRepositories(basePackages = ["com.example.mirotest.domains.*"])
@ComponentScan(basePackages = ["com.example.mirotest.*"])
@EntityScan(basePackages = ["com.example.mirotest.domains.*"])
@EnableTransactionManagement
@EnableZuulProxy
class ApplicationConfig 
package com.logreposit.renogyrover.services.logreposit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.logreposit.renogyrover.communication.renogy.RenogyRamData
import com.logreposit.renogyrover.configuration.LogrepositConfiguration
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.IngressDefinition
import com.logreposit.renogyrover.services.logreposit.mappers.LogrepositIngressDataMapper
import com.logreposit.renogyrover.utils.logger
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class LogrepositApiService(
    restTemplateBuilder: RestTemplateBuilder,
    private val logrepositConfiguration: LogrepositConfiguration
) {
    private val logger = logger()
    private val deviceDefinition = getDefinition()

    private val restTemplate: RestTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.of(10, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(10, ChronoUnit.SECONDS))
        .build()

    @Retryable(
        value = [RestClientException::class],
        exclude = [HttpClientErrorException.UnprocessableEntity::class],
        maxAttempts = 5,
        backoff = Backoff(delay = 500)
    )
    fun pushData(renogyRamData: RenogyRamData) {
        val data = LogrepositIngressDataMapper.toLogrepositIngressDto(
            date = Instant.now(),
            data = renogyRamData,
            address = "1" // Address is currently hardcoded, in future maybe configurable
        )

        val url = logrepositConfiguration.apiBaseUrl + "/v2/ingress/data"

        logger.info("Sending data to Logreposit API ({}): {}", url, data)

        val response = restTemplate.postForObject(url, HttpEntity(data, createHeaders(logrepositConfiguration.deviceToken)), String::class.java)

        logger.info("Response from Logreposit API: {}", response)
    }

    @Recover
    fun recoverUnprocessableEntity(e: HttpClientErrorException.UnprocessableEntity, renogyRamData: RenogyRamData) {
        logger.warn("Error while sending data to Logreposit API. Got unprocessable entity. Most likely a device definition validation error.", renogyRamData, e)
        logger.warn("Updating device ingress definition ...")

        val url = logrepositConfiguration.apiBaseUrl + "/v2/ingress/definition"

        restTemplate.put(url, HttpEntity(deviceDefinition, createHeaders(logrepositConfiguration.deviceToken)))
    }

    @Recover
    fun recoverThrowable(e: Throwable, renogyRamData: RenogyRamData) {
        logger.error("Could not send data to Logreposit API: {}", renogyRamData, e)

        throw e
    }

    private fun getDefinition(): IngressDefinition {
        val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

        val definitionAsString = LogrepositApiService::class.java.getResource("/device-definition.yaml").readText()

        return yamlMapper.readValue(definitionAsString, IngressDefinition::class.java)
    }

    private fun createHeaders(deviceToken: String?): HttpHeaders {
        val httpHeaders = HttpHeaders()

        httpHeaders["x-device-token"] = deviceToken

        return httpHeaders
    }
}

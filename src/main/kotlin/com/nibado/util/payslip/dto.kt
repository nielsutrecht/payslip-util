package com.nibado.util.payslip

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Token(@JsonProperty("access_token") val token: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeResponse(@JsonProperty("content") val employee: Employee)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Employee(
        val id: String,
        @JsonProperty("persoonsgegevens") val info: EmployeeInfo
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmploymentsResponse(@JsonProperty("_embedded") val employments: List<Employment>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Employment(
        val id: String,
        @JsonProperty("indienstDatum") val start: ZonedDateTime)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmployeeInfo(
        @JsonProperty("achternaam") val lastName: String,
        @JsonProperty("voorletters") val initials: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Payslips(
        val totalSize: Int,
        val pageSize: Int,
        val totalPages: Int,
        val currentPage: Int,
        @JsonProperty("_embedded")
        val payslips: List<Payslip>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Payslip(
        @JsonProperty("loonstrookId")
        val id: String,
        @JsonProperty("periodeOmschrijving")
        val periodDescription: String,
        @JsonProperty("periodeId")
        val periodId: Int,
        @JsonProperty("datumAfgehandeld")
        val dateHandled: ZonedDateTime,
        @JsonProperty("aantal")
        val amount: Int,
        @JsonProperty("loonstrookBestandsnaam")
        val fileName: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class YearStatements(@JsonProperty("_embedded") val statements: List<YearStatement>, val totalPages: Int)

@JsonIgnoreProperties(ignoreUnknown = true)
data class YearStatement(
        @JsonProperty("jaaropgaveId") val id: String,
        @JsonProperty("jaar") val year: Int)

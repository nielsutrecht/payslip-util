package com.nibado.util.payslip

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

object PayslipUtil {
    private val baseUrl = "https://api.loket.nl"
    private val pathPayslips = "/providers/werkgevers/werknemers/dienstverbanden/%s/loonstroken"
    private val pathPayslipDownload = "/providers/werkgevers/werknemers/dienstverbanden/loonstroken/%s?bearer=%s&language=nl"
    private val pathEmployees = "/providers/werkgevers/werknemers"
    private val pathEmployer = "/providers/werkgevers/werknemers/%s/werkgever"
    private val pathEmployment = "/providers/werkgevers/werknemers/%s/dienstverbanden?pageSize=2000"
    private val pathYearStatements = "/providers/werkgevers/werknemers/dienstverbanden/%s/jaaropgaven?pageSize=2000"
    private val pathYearStatementDownload = "/providers/werkgevers/werknemers/dienstverbanden/jaaropgaven/%s?bearer=%s&language=nl"
    private val loginUrl = "https://oauth.loket.nl/token"

    private val mapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
    private val client = OkHttpClient()

    fun login(user: String, password: String) : String {
        val formBody = FormBody.Builder()
                .add("grant_type", "password")
                .add("client_id", "werknemerLoketClient")
                .add("username", user)
                .add("password", password)
                .build()

        val request = Request.Builder()
                .url(loginUrl)
                .post(formBody)
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        return mapper.readValue<Token>(response.body()!!.byteStream()).token
    }

    fun getEmployee(token: String) : Employee {
        val request = Request.Builder()
                .url(baseUrl + pathEmployees)
                .header("Authorization", "Bearer $token")
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        val result : EmployeeResponse = mapper.readValue(response.body()!!.byteStream())

        return result.employee
    }

    fun getEmployment(id: String, token: String) : List<Employment> {
        val request = Request.Builder()
                .url(baseUrl + pathEmployment.format(id))
                .header("Authorization", "Bearer $token")
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        val result : EmploymentsResponse = mapper.readValue(response.body()!!.byteStream())

        return result.employments
    }

    fun getPayslips(id: String, token: String) : Payslips {
        val request = Request.Builder()
                .url(baseUrl + pathPayslips.format(id))
                .header("Authorization", "Bearer $token")
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        return mapper.readValue(response.body()!!.byteStream())
    }

    fun getYearStatements(id: String, token: String) : YearStatements {
        val request = Request.Builder()
                .url(baseUrl + pathYearStatements.format(id))
                .header("Authorization", "Bearer $token")
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        return mapper.readValue(response.body()!!.byteStream())
    }

    fun downloadPayslips(payslips: Payslips, token: String, dir: File) {
        if(!dir.isDirectory) {
            throw IllegalArgumentException("File should be a directory")
        }

        payslips.payslips.forEach { downloadPayslip(it, token, dir) }
    }

    private fun downloadPayslip(payslip: Payslip, token: String, dir: File) {
        if(!dir.isDirectory) {
            throw IllegalArgumentException("File should be a directory")
        }
        val request = Request.Builder()
                .url(baseUrl + pathPayslipDownload.format(payslip.id, token))
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        val file = File(dir, payslip.fileName + ".pdf")
        val outs = file.outputStream()

        response.body()!!.byteStream().copyTo(outs)

        outs.close()
    }

    fun downloadYearStatements(yearStatements: YearStatements, token: String, dir: File) {
        if(!dir.isDirectory) {
            throw IllegalArgumentException("File should be a directory")
        }

        yearStatements.statements.forEach { downloadYearStatement(it, token, dir) }
    }

    private fun downloadYearStatement(statement: YearStatement, token: String, dir: File) {
        val request = Request.Builder()
                .url(baseUrl + pathYearStatementDownload.format(statement.id, token))
                .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code()}")
        }

        val file = File(dir,  "Jaaropgaaf-${statement.year}.pdf")
        val outs = file.outputStream()

        response.body()!!.byteStream().copyTo(outs)

        outs.close()
    }
}

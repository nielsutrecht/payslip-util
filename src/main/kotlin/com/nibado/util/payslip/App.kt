package com.nibado.util.payslip

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun main(args: Array<String>) {
    val config = getConfig(args)

    println("Loggin in as ${config.username} / ${"*".repeat(config.password.length)}")
    println("Saving files to ${File(config.dir).absolutePath}")

    val token = PayslipUtil.login(config.username, config.password)
    val employee = PayslipUtil.getEmployee(token)
    val employments = PayslipUtil.getEmployment(employee.id, token)

    println("${employee.info.initials} ${employee.info.lastName} (id: ${employee.id}) has ${employments.size} employments")

    employments.forEach { employment(it, token, config) }

    println("Done")
}

fun employment(employment: Employment, token: String, config: Config) {
    val payslips = PayslipUtil.getPayslips(employment.id, token)
    val statements = PayslipUtil.getYearStatements(employment.id, token)

    val localDate  = LocalDateTime.ofInstant(employment.start.toInstant(), ZoneId.of("Europe/Amsterdam")).toLocalDate()

    println(" - Employment ${employment.id} started on $localDate has ${payslips.payslips.size} payslips and ${statements.statements.size} year statements")

    if(payslips.totalPages > 1) {
        println(" - WARN: ${payslips.totalPages} payslip pages in total, but only downloading first page")
    }
    if(payslips.totalPages > 1) {
        println(" - WARN: ${statements.totalPages} statement pages in total, but only downloading first page")
    }

    val dir = File(File(config.dir), employment.id)
    dir.mkdirs()

    PayslipUtil.downloadPayslips(payslips, token, dir)
    PayslipUtil.downloadYearStatements(statements, token, dir)
}

fun getConfig(args: Array<String>) : Config {
    fun defaultDir(dir: String) = if(dir.isEmpty()) {"."} else dir

    return if(args.size >= 3) {
        Config(args[0], args[1], defaultDir(args[2]))
    } else {
        val scanner = Scanner(System.`in`)

        print("username  > ")
        val username = scanner.nextLine()
        print("password  > ")
        val password = scanner.nextLine()
        print("directory > ")
        val dir = scanner.nextLine()

        Config(username, password, defaultDir(dir))
    }
}

data class Config(val username: String, val password: String, val dir: String)

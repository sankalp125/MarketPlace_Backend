package com.example

import io.ktor.server.application.Application
import java.io.BufferedReader
import java.io.InputStreamReader

fun Application.insertCities() {
    val inputStream = object {}.javaClass.getResourceAsStream("/cities.csv")
        ?: throw IllegalArgumentException("Csv not found in resources folder")
    val reader = BufferedReader(InputStreamReader(inputStream))
    val lines: List<String> = reader.readLines().drop(1)
    print("INSERT INTO cities VALUES")
    lines.forEach { line ->
        val columns = line.split(",")
        if (columns.size < 3) return@forEach
        val cityName = columns[0].trim()
        val stateName = toCamelCase(columns[2].trim())
        print("(gen_random_uuid(), '$cityName', (SELECT state_id FROM states WHERE \"state_name\" = '$stateName')),\n")
    }
}
fun toCamelCase(input: String): String {
    return input.lowercase().split(" ").joinToString(" ") {
        it.replaceFirstChar(Char::uppercaseChar)
    }
}

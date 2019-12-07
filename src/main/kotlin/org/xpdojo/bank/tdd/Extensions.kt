package org.xpdojo.bank.tdd

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


val DEFAULT_ZONE: ZoneId = ZoneId.of("EST5EDT")

fun Instant.dataTimeStrForStatement(locale: Locale, zone: ZoneId): String {
    val dateStr = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            .withLocale(locale)
            .withZone(zone)
            .format(this)

    val timeStr = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withLocale(locale)
            .withZone(zone)
            .format(this)
    return "Date: $dateStr Time: $timeStr ET"

}

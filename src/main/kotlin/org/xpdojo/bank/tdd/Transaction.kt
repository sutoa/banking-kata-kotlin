package org.xpdojo.bank.tdd

import java.time.LocalDateTime
import java.util.Locale.US

data class Transaction(val type: TransactionType, val amount: Money, val datetime: LocalDateTime) {
    override fun toString(): String {

        return """
            Activity: $type $amount
            ${datetime.atZone(DEFAULT_ZONE).toInstant().dataTimeStrForStatement(US, DEFAULT_ZONE)}
        """.trimIndent()
    }
}

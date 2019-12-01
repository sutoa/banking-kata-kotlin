package org.xpdojo.bank.tdd

import java.time.LocalDateTime

data class Transaction(val type: TransactionType, val balance: Money, val datetime: LocalDateTime) {

}

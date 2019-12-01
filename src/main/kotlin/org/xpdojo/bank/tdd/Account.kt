package org.xpdojo.bank.tdd

import org.xpdojo.bank.tdd.TransactionType.*
import java.time.Clock
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Represents a bank account.  You can do things to this class like deposit, withdraw and transfer.
 */
class Account(var balance: Money = Money(0.0),
              var clock: Clock = Clock.systemDefaultZone(),
              private val transactions: MutableList<Transaction> = mutableListOf()) {
    init {
        transactions.add(Transaction(ACCOUNT_OPEN, balance, now(clock)))
    }

    fun deposit(delta: Money) {
        balance = balance add delta
        transactions.add(Transaction(DEPOSIT, balance, now(clock)))
    }

    fun withdraw(delta: Money) {
        if (balance.lessThan(delta))
            throw InsufficientFundsException("")
        balance = balance minus delta
        transactions.add(Transaction(WITHDRAW, balance, now(clock)))
    }

    fun transfer(toAccount: Account, delta: Money) {
        withdraw(delta)
        toAccount.deposit(delta)
    }

    fun balanceSlip(): String {
        val instant = clock.instant()
        val dateStr = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .withLocale(Locale.US)
                .withZone(ZoneId.of("EST5EDT"))
                .format(instant)

        val timeStr = DateTimeFormatter.ofPattern("hh:mm:ss")
                .withLocale(Locale.US)
                .withZone(ZoneId.of("EST5EDT"))
                .format(instant)

        return """
            Balance: $balance
            Date: $dateStr Time: $timeStr ET
        """.trimIndent()
    }

    fun statement(): String {
        return """
            abc
            efg
            ${activityHistory()}
        """.trimIndent()
    }

    private fun activityHistory(): String {
        return "withdraw"
    }

    fun transactions(): List<Transaction> {
        return transactions.toList()
    }
}
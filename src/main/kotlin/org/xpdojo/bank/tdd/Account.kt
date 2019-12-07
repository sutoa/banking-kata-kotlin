package org.xpdojo.bank.tdd

import org.xpdojo.bank.tdd.TransactionType.*
import java.time.Clock
import java.time.LocalDateTime.now
import java.util.Locale.US

/**
 * Represents a bank account.  You can do things to this class like deposit, withdraw and transfer.
 */
class Account(var balance: Money = Money(0.0),
              var clock: Clock = Clock.systemDefaultZone(),
              private val transactions: MutableList<Transaction> = mutableListOf()) {

    init {
        transactions.add(Transaction(OPEN, balance, now(clock)))
    }

    fun deposit(delta: Money) {
        balance = balance add delta
        transactions.add(Transaction(DEPOSIT, delta, now(clock)))
    }

    fun withdraw(delta: Money) {
        if (balance.lessThan(delta))
            throw InsufficientFundsException("")
        balance = balance minus delta
        transactions.add(Transaction(WITHDRAW, delta, now(clock)))
    }

    fun transfer(toAccount: Account, delta: Money) {
        withdraw(delta)
        toAccount.deposit(delta)
    }

    fun balanceSlip(): String {

        return """
            Balance: $balance
            ${clock.instant().dataTimeStrForStatement(US, DEFAULT_ZONE)}
        """.trimIndent()
    }

    private val newLine = System.lineSeparator().repeat(2)

    fun statement(): String {
        return transactions.joinToString(separator = newLine, postfix = "$newLine${balanceSlip()}")
    }

    fun transactions(): List<Transaction> {
        return transactions.toList()
    }
}



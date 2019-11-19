package org.xpdojo.bank.tdd

/**
 * Represents a bank account.  You can do things to this class like deposit, withdraw and transfer.
 */
class Account(var balance: Money = Money(0.0)) {
    fun deposit(delta: Money) {
          balance = balance add delta
    }

    fun withdraw(delta: Money) {
        if(balance.lessThan(delta))
            throw InsufficientFundsException("")
        balance = balance minus delta
    }
}
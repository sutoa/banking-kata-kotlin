package org.xpdojo.bank.tdd

/**
 * Class to represent a monetary amount.  Should treat this an immutable class.
 * Hint: should this be a data class.
 */
data class Money(val amount: Double) {
    infix fun add(delta: Money): Money {
        return Money(amount + delta.amount)
    }

    infix fun minus(delta: Money): Money {
        return Money(amount - delta.amount)
    }

    fun lessThan(anAmount: Money): Boolean {
        return amount < anAmount.amount
    }
}
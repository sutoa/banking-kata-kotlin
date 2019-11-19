package org.xpdojo.bank.tdd

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Requirements:
 *  I can Deposit money to accounts
 *  I can Withdraw money from accounts
 *  I can Transfer amounts between accounts (if I have the funds)
 *  I can print out an Account balance slip (date, time, balance)
 *  I can print a statement of account activity (statement)
 *  I can apply Statement filters (include just deposits, withdrawal, date)
 */
@DisplayName("With an account we can ...")
class AccountTest {

    private val account: Account = Account()

    @Test
    fun `deposit an amount to increase the balance`() {
        account.deposit(Money(100.0))

        assertThat(account.balance).isEqualTo(Money(100.0))
    }

    @Test
    internal fun `withdraw an amount to decrease the balance`() {
        account.deposit(Money(10.0))

        account.withdraw(Money(6.0))

        assertThat(account.balance).isEqualTo(Money(4.0))
    }

    @Test
    internal fun `throws exception when withdraw without sufficient fund`() {
        assertThatExceptionOfType(InsufficientFundsException::class.java)
                .isThrownBy { account.withdraw(Money(5.0)) }
    }

    @Test
    internal fun `transfer an amount when there's sufficient fund`() {
        val fromAccount = Account(Money(100.0))
        val toAccount = Account(Money(20.0))

        fromAccount.transfer(toAccount, Money(33.0))

        assertThat(toAccount.balance).isEqualTo(Money(53.0))
        assertThat(fromAccount.balance).isEqualTo(Money(67.0))
    }
}
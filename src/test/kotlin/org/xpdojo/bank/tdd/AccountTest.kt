package org.xpdojo.bank.tdd

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.xpdojo.bank.tdd.TransactionType.*
import java.time.Clock.fixed
import java.time.Clock.offset
import java.time.Duration
import java.time.Duration.ofDays
import java.time.Duration.ofHours
import java.time.Instant.parse
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.of
import java.util.stream.Stream

private val etZone = of("EST5EDT")

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

    private val accountOpenTimeInUtc = parse("2020-02-10T17:03:03.00Z")
    private val clock = fixed(accountOpenTimeInUtc, etZone)
    private val anAccountWithBalance = Account(Money(100.0), clock)


    @BeforeEach
    internal fun setUp() {

    }

    @Test
    fun `deposit an amount to increase the balance`() {
        anAccountWithBalance.deposit(Money(100.0))

        assertThat(anAccountWithBalance.balance).isEqualTo(Money(200.0))
    }

    @Test
    internal fun `withdraw an amount to decrease the balance`() {
        anAccountWithBalance.withdraw(Money(6.0))

        assertThat(anAccountWithBalance.balance).isEqualTo(Money(94.0))
    }

    @Test
    internal fun `throws exception when withdraw without sufficient fund`() {
        assertThatExceptionOfType(InsufficientFundsException::class.java)
                .isThrownBy { anAccountWithBalance.withdraw(Money(500.0)) }
    }

    @Test
    internal fun `transfer an amount when there's sufficient fund`() {
        val fromAccount = Account(Money(100.0))
        val toAccount = Account(Money(20.0))

        fromAccount.transfer(toAccount, Money(33.0))

        assertThat(toAccount.balance).isEqualTo(Money(53.0))
        assertThat(fromAccount.balance).isEqualTo(Money(67.0))
    }

    @Test
    internal fun `produce a balance slip with date, time and balance`() {
        anAccountWithBalance.deposit(Money(99.9))

        assertThat(anAccountWithBalance.balanceSlip()).isEqualTo(
                """
                    Balance: $199.90
                    Date: Feb 10, 2020 Time: 12:03:03 ET
                """.trimIndent()
        )
    }

    @Test
    internal fun `produce statement with account activities`() {

        offset(clock, ofDays(1))
        anAccountWithBalance.withdraw(Money(50.5))
        offset(clock, ofDays(2))
        anAccountWithBalance.deposit(Money(100.3))
        offset(clock, ofHours(6))

        val statement = anAccountWithBalance.statement()

        assertThat(statement).isEqualTo("""
            Activity: Open; Balance: $100.00
            Date: Feb 10, 2020 Time: 12:03:03 ET
            
            Activity: Withdraw $50.50; 
            Date: Feb 11, 2020 Time: 12:03:03 ET
            
            Activity: Deposit $100.00; 
            Date: Feb 13, 2020 Time: 12:03:03 ET
            
            Balance: $149.50
            Date: Feb 13, 2020 Time: 18:03:03 ET
        """.trimIndent())
    }

    @Test
    internal fun `record account open transaction`() {
        assertThat(anAccountWithBalance.transactions()).containsExactly(
                Transaction(ACCOUNT_OPEN, Money(100.0), ofInstant(accountOpenTimeInUtc, etZone)))
    }

    @Test
    internal fun `record deposit transaction`() {
        anAccountWithBalance.clock = offset(anAccountWithBalance.clock, ofHours(6L))
        anAccountWithBalance.deposit(Money(40.0))

        assertThat(anAccountWithBalance.transactions()).containsExactly(
                Transaction(ACCOUNT_OPEN, Money(100.0), ofInstant(accountOpenTimeInUtc, etZone)),
                Transaction(DEPOSIT, Money(140.0), ofInstant(parse("2020-02-10T23:03:03.00Z"), etZone))
        )
    }

    @Test
    internal fun `record withdraw transaction`() {
        anAccountWithBalance.clock = offset(anAccountWithBalance.clock, ofHours(6L))
        anAccountWithBalance.withdraw(Money(40.0))

        assertThat(anAccountWithBalance.transactions()).containsExactly(
                Transaction(ACCOUNT_OPEN, Money(100.0), ofInstant(accountOpenTimeInUtc, etZone)),
                Transaction(WITHDRAW, Money(60.0), ofInstant(parse("2020-02-10T23:03:03.00Z"), etZone))
        )
    }

    @ParameterizedTest
    @MethodSource("testData")
    internal fun `records transactions`(dur: Duration, transact: (acc: Account) -> Unit, transaction: Transaction) {
        anAccountWithBalance.clock = offset(anAccountWithBalance.clock, dur)
        transact(anAccountWithBalance)

        assertThat(anAccountWithBalance.transactions()).contains(transaction)
    }

    companion object {
        @JvmStatic
        fun testData(): Stream<Arguments> =
                Stream.of(
                        Arguments.of(ofHours(6L), { acc: Account -> acc.withdraw(Money(39.0)) },
                                Transaction(WITHDRAW, Money(61.0),
                                        ofInstant(parse("2020-02-10T23:03:03.00Z"), etZone))),
                        Arguments.of(ofHours(5L), { acc: Account -> acc.deposit(Money(39.0)) },
                                Transaction(DEPOSIT, Money(139.0),
                                        ofInstant(parse("2020-02-10T22:03:03.00Z"), etZone)))
                )
    }
}
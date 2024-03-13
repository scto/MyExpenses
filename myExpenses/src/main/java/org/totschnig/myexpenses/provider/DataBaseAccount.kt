package org.totschnig.myexpenses.provider

import android.net.Uri
import org.totschnig.myexpenses.model.Grouping
import org.totschnig.myexpenses.model.SortDirection
import org.totschnig.myexpenses.model2.AccountInfoWithGrouping
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_ACCOUNTID
import org.totschnig.myexpenses.provider.DatabaseConstants.KEY_CURRENCY

/**
 * groups databaseSpecific information
 */
abstract class DataBaseAccount : AccountInfoWithGrouping {
    abstract val id: Long
    abstract override val currency: String
    abstract override val grouping: Grouping
    abstract val sortDirection: SortDirection

    override val accountId: Long
        get() = id

    val isHomeAggregate get() = isHomeAggregate(id)

    val isAggregate get() = isAggregate(id)

    fun uriForTransactionList(
        mergeTransfers: Boolean = true,
        shortenComment: Boolean = false,
        extended: Boolean = true
    ): Uri = uriBuilderForTransactionList(mergeTransfers, shortenComment, extended).build()

    fun uriBuilderForTransactionList(
        mergeTransfers: Boolean = true,
        shortenComment: Boolean = false,
        extended: Boolean = true
    ) = uriBuilderForTransactionList(id, currency, mergeTransfers, shortenComment, extended)

    companion object {

        const val AGGREGATE_HOME_CURRENCY_CODE = "___"

        const val HOME_AGGREGATE_ID = Int.MIN_VALUE.toLong()

        const val GROUPING_AGGREGATE = "AGGREGATE_GROUPING____"
        const val SORT_DIRECTION_AGGREGATE = "AGGREGATE_SORT_DIRECTION____"
        const val SORT_BY_AGGREGATE = "AGGREGATE_SORT_BY____"

        fun isHomeAggregate(id: Long) = id == HOME_AGGREGATE_ID

        fun isAggregate(id: Long) = id < 0

        fun uriBuilderForTransactionList(
            id: Long,
            currency: String,
            mergeTransfers: Boolean = false,
            shortenComment: Boolean = false,
            extended: Boolean = true
        ): Uri.Builder = when {
            !isAggregate(id) -> uriBuilderForTransactionList(shortenComment, extended).apply {
                appendQueryParameter(KEY_ACCOUNTID, id.toString())
            }

            isHomeAggregate(id) -> uriForTransactionListHome(mergeTransfers, shortenComment, extended)
            else -> uriForTransactionListAggregate(mergeTransfers, shortenComment, extended).apply {
                appendQueryParameter(KEY_CURRENCY, currency)
            }
        }

        fun uriForTransactionList(shortenComment: Boolean, extended: Boolean = true): Uri =
            uriBuilderForTransactionList(shortenComment, extended).build()

        fun uriBuilderForTransactionList(
            shortenComment: Boolean,
            extended: Boolean = true
        ): Uri.Builder =
            (if (extended) TransactionProvider.EXTENDED_URI else TransactionProvider.TRANSACTIONS_URI)
                .buildUpon().apply {
                    if (shortenComment) {
                        appendQueryParameter(
                            TransactionProvider.QUERY_PARAMETER_SHORTEN_COMMENT,
                            "1"
                        )
                    }
                }

        private fun uriForTransactionListHome(
            mergeTransfers: Boolean,
            shortenComment: Boolean,
            extended: Boolean
        ): Uri.Builder =
            uriWithMergeTransfers(mergeTransfers, true, shortenComment, extended)

        private fun uriForTransactionListAggregate(
            mergeTransfers: Boolean,
            shortenComment: Boolean,
            extended: Boolean
        ): Uri.Builder =
            uriWithMergeTransfers(mergeTransfers, false, shortenComment, extended)

        private fun uriWithMergeTransfers(
            mergeTransfers: Boolean,
            forHome: Boolean,
            shortenComment: Boolean,
            extended: Boolean
        ) =
            uriBuilderForTransactionList(shortenComment, extended).apply {
                if (mergeTransfers)
                    appendQueryParameter(
                        TransactionProvider.QUERY_PARAMETER_MERGE_TRANSFERS,
                        if (forHome) "2" else "1"
                    )
            }
    }
}
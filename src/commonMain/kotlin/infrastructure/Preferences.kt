package infrastructure

import data_objects.LoginResult
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

expect object Preferences {
    var instanceUrl: String?

    object Authorization {
        val token: String?
        @OptIn(ExperimentalTime::class)
        val expiryDate: Instant?

        val admin: Boolean

        fun assign(login: LoginResult)

        fun clear()
    }
}
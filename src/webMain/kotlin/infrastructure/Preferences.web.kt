package infrastructure

import data_objects.LoginResult
import kotlinx.browser.localStorage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

actual object Preferences {
    actual var instanceUrl: String?
        get() = Configuration.config.instanceUrl
        set(_) {
            throw Error("Instance URL should not be set on web")
        }

    actual object Authorization {
        @OptIn(ExperimentalTime::class)
        actual val token: String?
            get() = expiryDate?.let {
                if (it.toLocalDateTime(TimeZone.UTC) <= Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
                    clear()
                    null
                } else {
                    localStorage.getItem(Authorization::token.name)
                }
            }

        @OptIn(markerClass = [ExperimentalTime::class])
        actual val expiryDate: Instant?
            get() = localStorage.getItem(Authorization::expiryDate.name)?.let {
                Instant.parseOrNull(it)
            }

        actual val admin: Boolean
            get() = localStorage.getItem(Authorization::admin.name).toBoolean()

        @OptIn(ExperimentalTime::class)
        actual fun assign(login: LoginResult) {
            localStorage.setItem(Authorization::token.name, login.token)
            localStorage.setItem(Authorization::expiryDate.name, login.expiryDate.toString())
            localStorage.setItem(Authorization::admin.name, login.admin.toString())
        }

        @OptIn(ExperimentalTime::class)
        actual fun clear() {
            localStorage.removeItem(Authorization::token.name)
            localStorage.removeItem(Authorization::expiryDate.name)
            localStorage.removeItem(Authorization::admin.name)
        }
    }
}
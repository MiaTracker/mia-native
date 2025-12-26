package infrastructure

import data_objects.LoginResult
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

actual object Preferences {
    private val preferences = java.util.prefs.Preferences.userRoot().node("org.nara.mia")

    actual var instanceUrl: String?
        get() = preferences.get(Preferences::instanceUrl.name, null)
        set(value) =
            if(value != null) preferences.put(Preferences::instanceUrl.name, value)
            else preferences.remove(Preferences::instanceUrl.name)


    actual object Authorization {
        private val node = preferences.node("authorization")

        @OptIn(ExperimentalTime::class)
        actual val token: String?
            get() = expiryDate?.let {
                if(it.toLocalDateTime(TimeZone.UTC) <= Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
                    clear()
                    null
                }
                else node.get(Authorization::token.name, null)
            }

        @OptIn(ExperimentalTime::class)
        actual val expiryDate: Instant?
            get() = node.getLong(Authorization::expiryDate.name, -1).let {
                if(it < 0) null
                else Instant.fromEpochMilliseconds(it)
            }

        actual val admin: Boolean
            get() = node.getBoolean(Authorization::admin.name, false)

        @OptIn(ExperimentalTime::class)
        actual fun assign(login: LoginResult) {
            node.put(Authorization::token.name, login.token)
            node.putLong(Authorization::expiryDate.name, login.expiryDate.toEpochMilliseconds())
            node.putBoolean(Authorization::admin.name, login.admin)
        }

        @OptIn(ExperimentalTime::class)
        actual fun clear() {
            node.remove(Authorization::token.name)
            node.remove(Authorization::expiryDate.name)
            node.remove(Authorization::admin.name)
        }
    }
}
package infrastructure

import data_objects.LoginResult
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object Preferences {
    private val preferences = java.util.prefs.Preferences.userRoot().node("org.nara.mia")

    var instanceUrl: String?
        get() = preferences.get("instanceUrl", null)
        set(value) =
            if(value != null) preferences.put("instanceUrl", value)
            else preferences.remove("instanceUrl")


    object Authorization {
        private val node = preferences.node("authorization")

        val token: String?
            get() = expiryDate?.let {
                if(it.toLocalDateTime(TimeZone.UTC) <= Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
                    clear()
                    null
                }
                else node.get("token", null)
            }

        val expiryDate: Instant?
            get() = node.getLong("expiryDate", -1).let {
                if(it < 0) null
                else Instant.fromEpochMilliseconds(it)
            }

        val admin: Boolean
            get() = node.getBoolean("admin", false)

        fun assign(login: LoginResult) {
            login.expiryDate.toEpochMilliseconds()
            node.put("token", login.token)
            node.putLong("expiryDate", login.expiryDate.toEpochMilliseconds())
            node.putBoolean("admin", login.admin)
        }

        fun clear() {
            node.remove("token")
            node.remove("expiryDate")
            node.remove("admin")
        }
    }
}
package infrastructure

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import data_objects.LoginResult
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

actual object Preferences {
    private const val PREFERENCES_FILE_NAME = "org.nara.mia.preferences_file"

    private var _sharedPreferences: SharedPreferences? = null
    private val sharedPreferences: SharedPreferences
        get() = _sharedPreferences ?: throw Exception("Preferences not initialized")

    actual var instanceUrl: String?
        get() = sharedPreferences.getString(Preferences::instanceUrl.name, null)
        set(value) = sharedPreferences.edit {
            if(value == null) remove(Preferences::instanceUrl.name)
            else putString(Preferences::instanceUrl.name, value)
        }

    fun initialize(activity: Activity) {
        _sharedPreferences = activity.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    actual object Authorization {
        private const val PREFIX = "authorization."

        @OptIn(ExperimentalTime::class)
        actual val token: String?
            get() = expiryDate?.let {
                if (it.toLocalDateTime(TimeZone.UTC) <= Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
                    clear()
                    null
                } else {
                    sharedPreferences.getString(PREFIX + Authorization::token.name, null)
                }
            }

        @OptIn(markerClass = [ExperimentalTime::class])
        actual val expiryDate: Instant?
            get() = sharedPreferences.getString(PREFIX + Authorization::expiryDate.name, null)?.let {
                Instant.parseOrNull(it)
            }

        actual val admin: Boolean
            get() = sharedPreferences.getBoolean(PREFIX + Authorization::admin.name, false)

        @OptIn(ExperimentalTime::class)
        actual fun assign(login: LoginResult) {
            sharedPreferences.edit {
                putString(PREFIX + Authorization::token.name, login.token)
                putString(PREFIX + Authorization::expiryDate.name, login.expiryDate.toString())
                putString(PREFIX + Authorization::admin.name, login.admin.toString())
            }
        }

        @OptIn(ExperimentalTime::class)
        actual fun clear() {
            sharedPreferences.edit {
                remove(PREFIX + Authorization::token.name)
                remove(PREFIX + Authorization::expiryDate.name)
                remove(PREFIX + Authorization::admin.name)
            }
        }
    }
}
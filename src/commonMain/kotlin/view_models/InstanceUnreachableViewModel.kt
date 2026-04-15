package view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import extensions.navigateFresh
import infrastructure.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InstanceUnreachableViewModel(val navController: NavHostController) : ViewModel() {
    val instanceUrl: String? = Preferences.instanceUrl

    init {
        retry()
    }

    fun retry() {
        println("retrying")
        viewModelScope.launch {
            println("In 1")
            val url = Preferences.instanceUrl
            if(url != null) {
            println("In 2")
                val res = Api.ping(url)
                if(res) {
            println("In 3")
                    viewModelScope.launch(Dispatchers.Main) {
            println("In 4")
                        navController.navigateFresh(Navigation.Login)
                    }
                }
            }
        }
    }
}
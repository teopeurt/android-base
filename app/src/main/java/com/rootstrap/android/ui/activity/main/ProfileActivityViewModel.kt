package com.rootstrap.android.ui.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rootstrap.android.network.managers.IUserManager
import com.rootstrap.android.network.managers.SessionManager
import com.rootstrap.android.network.managers.UserManager
import com.rootstrap.android.ui.base.BaseViewModel
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.extensions.ApiErrorType
import com.rootstrap.android.util.extensions.ApiException
import kotlinx.coroutines.launch

open class ProfileActivityViewModel : BaseViewModel() {

    private val manager: IUserManager = UserManager

    private val _state = MutableLiveData<ProfileState>()
    val state: LiveData<ProfileState>
        get() = _state

    fun signOut() {
        _networkState.value = NetworkState.loading
        viewModelScope.launch {
            val result = manager.signOut()
            if (result.isSuccess) {
                _networkState.value = NetworkState.idle
                _state.value = ProfileState.signOutSuccess
                SessionManager.signOut()
            } else {
                handleError(result.exceptionOrNull())
            }
        }
    }

    private fun handleError(exception: Throwable?) {
        error = if (exception is ApiException && exception.errorType == ApiErrorType.apiError) {
            exception.message
        } else null

        _networkState.value = NetworkState.idle
        _networkState.value = NetworkState.error
        _state.value = ProfileState.signOutFailure
    }
}

enum class ProfileState {
    signOutFailure,
    signOutSuccess
}

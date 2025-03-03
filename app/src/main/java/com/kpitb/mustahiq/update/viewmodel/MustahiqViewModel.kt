package com.kpitb.mustahiq.update.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.kpitb.mustahiq.update.api.RetrofitClient
import com.kpitb.mustahiq.update.inappupdate.InAppUpdate
import com.kpitb.mustahiq.update.models.District
import com.kpitb.mustahiq.update.models.DistrictResponse
import com.kpitb.mustahiq.update.models.Hospital
import com.kpitb.mustahiq.update.models.HospitalDataResponse
import com.kpitb.mustahiq.update.models.Scheme
import com.kpitb.mustahiq.update.models.SchemesResponse
import com.kpitb.mustahiq.update.models.Tehsil
import com.kpitb.mustahiq.update.models.TehsilResponse
import com.kpitb.mustahiq.update.network.NetworkStatusObserver
import com.kpitb.mustahiq.update.screens.main.language_and_theme.LanguageNightModePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class MustahiqViewModel(private val context: Context) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // MutableStateFlow for observing the dialog state
    private val _showIntroDialog = MutableStateFlow(isDialogEnabled())
    val shouldShowIntroDialog: StateFlow<Boolean> = _showIntroDialog

    // Function to check if the dialog should be displayed
    private fun isDialogEnabled(): Boolean {
        return sharedPreferences.getBoolean("show_intro_dialog", true)
    }

    // Function to update the dialog state
    fun setShowIntroDialog(show: Boolean, neverAskAgain: Boolean = false) {
        if (neverAskAgain) {
            sharedPreferences.edit().putBoolean("show_intro_dialog", false).apply()
        }
        _showIntroDialog.value = show
    }

//    private val _currentDialogLanguage = MutableStateFlow(sharedPreferences.getString("language", "English") ?: "English")
//    val currentDialogLanguage: StateFlow<String> = _currentDialogLanguage

    // Function to update language in SharedPreferences
//    fun setLanguage(language: String) {
//        sharedPreferences.edit().putString("language", language).apply()
//        _currentLanguage.value = language
//    }

    private var schemesCache: List<Scheme>? = null
    private var districtOfficesCache: List<District>? = null
    private var districtHospitalsCache: List<Hospital>? = null

    private var localZakatCommitteesCache: List<Tehsil>? = null
    private val errorMessage = MutableLiveData<String>()
    val schemes = MutableLiveData<List<Scheme>?>()
    val districtOffices = MutableLiveData<List<District>?>()
    val districtHospitals = MutableLiveData<List<Hospital>?>()

    val localZakatCommittees = MutableLiveData<List<Tehsil>?>()

    private val networkObserver = NetworkStatusObserver(context)

    init {
        networkObserver.observeForever { isConnected ->
            if (isConnected) {
                Toast.makeText(context, "Online", Toast.LENGTH_SHORT).show()
                fetchActiveSchemes()
                fetchDistrictOffices()
                fetchDistrictHospitals()
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private var inAppUpdate: InAppUpdate? = null

    fun initializeInAppUpdate(activity: Activity) {
        inAppUpdate = InAppUpdate(appUpdateManager, activity)
        inAppUpdate?.onCreate()
    }

    fun checkForUpdates() {
        inAppUpdate?.checkForAppUpdates()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        inAppUpdate?.onActivityResult(requestCode, resultCode)
    }

    fun onResume() {
        inAppUpdate?.onResume()
    }

    fun onDestroy() {
        inAppUpdate?.onDestroy()
    }

    fun fetchLocalZakatCommittees(distId: String) {
        val isConnected = networkObserver.value ?: false

        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        localZakatCommittees.postValue(null)

        viewModelScope.launch {
            try {
                val response: Response<TehsilResponse> = RetrofitClient.instance.getLocalZakatCommittees(distId)
                if (response.isSuccessful) {
                    if (response.body()!!.data != null){
                        localZakatCommitteesCache = response.body()?.data
                        localZakatCommittees.postValue(localZakatCommitteesCache)
                    }
                } else {
                    localZakatCommitteesCache = null
                    localZakatCommittees.postValue(null)
                    errorMessage.postValue("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                localZakatCommitteesCache = null
                localZakatCommittees.postValue(null)
                errorMessage.postValue(e.message)
            }
        }
    }

    fun fetchActiveSchemes() {
        val isConnected = networkObserver.value ?: false

        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            if (schemesCache != null) {
                schemes.postValue(schemesCache)
                return@launch
            }

            try {
                val response: Response<SchemesResponse> = RetrofitClient.instance.getActiveSchemes()
                if (response.isSuccessful) {
                    schemesCache = response.body()?.data
                    schemes.postValue(schemesCache)
                } else {
                    errorMessage.postValue("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            }
        }
    }

    fun fetchDistrictOffices() {
        val isConnected = networkObserver.value ?: false

        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            if (districtOfficesCache != null) {
                districtOffices.postValue(districtOfficesCache)
                return@launch
            }

            try {
                val response: Response<DistrictResponse> = RetrofitClient.instance.getDistrictOffices()
                if (response.isSuccessful) {
                    districtOfficesCache = response.body()?.data
                    districtOffices.postValue(districtOfficesCache)
                } else {
                    errorMessage.postValue("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message.orEmpty())
            }
        }
    }

    fun fetchDistrictHospitals() {
        val isConnected = networkObserver.value ?: false

        if (!isConnected) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            if (districtHospitalsCache != null) {
                districtHospitals.postValue(districtHospitalsCache)
                return@launch
            }

            try {
                val response: Response<HospitalDataResponse> = RetrofitClient.instance.getHospitalsData()
                if (response.isSuccessful) {
                    districtHospitalsCache = response.body()?.data
                    districtHospitals.postValue(districtHospitalsCache)
                } else {
                    errorMessage.postValue("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue(e.message.orEmpty())
            }
        }
    }

    private val _currentLanguage = MutableStateFlow("en") // Default language
    val currentLanguage: StateFlow<String> get() = _currentLanguage

    private val _isNightMode = MutableStateFlow(false) // Default is light mode
    val isNightMode: StateFlow<Boolean> get() = _isNightMode

    private val preferences = LanguageNightModePreferences()

    init {
        viewModelScope.launch {
            val savedLanguage = preferences.getSavedLanguagePreference(context)
            _currentLanguage.value = savedLanguage

            val savedNightMode = preferences.getNightModePreference(context)
            _isNightMode.value = savedNightMode
        }
    }

    fun toggleNightMode() {
        viewModelScope.launch {
            val newNightMode = !_isNightMode.value
            preferences.saveNightModePreference(context, newNightMode)
            _isNightMode.value = newNightMode
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            preferences.saveLanguagePreference(context, languageCode)
            _currentLanguage.value = languageCode
        }
    }

    fun getHospitalByName(name: String?): Hospital? {
        return districtHospitals.value?.find { it.dh_name == name }
    }

    fun getSchemeByName(name: String?): Scheme? {
        return schemes.value?.find { it.scheme_title == name }
    }

    fun getDistrictByName(name: String?): District? {
        return districtOffices.value?.find { it.dist_name == name }
    }

    fun getLocalCommitteeByName(name: String?): Tehsil? {
        return localZakatCommittees.value?.find { it.lzc_name == name }
    }
}
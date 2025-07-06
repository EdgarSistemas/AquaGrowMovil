package com.example.aquagrow.data.repository//package com.example.aquagrow.data.repository
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.example.aquagrow.util.MQTTConnectionBinder
//
//class MQTTRepository(context: Context) {
//
//    private val binder = MQTTConnectionBinder(context)
//    private val _connectionStatus = MutableLiveData<Boolean>()
//    val connectionStatus: LiveData<Boolean> = _connectionStatus
//
//    init {
//        binder.connect { isConnected ->
//            _connectionStatus.postValue(isConnected)
//        }
//    }
//
//    fun publish(topic: String, message: String) {
//        binder.getService()?.publish(topic, message)
//    }
//
//    fun subscribe(topic: String, callback: (String, String) -> Unit) {
//        binder.getService()?.subscribe(topic, callback)
//    }
//}
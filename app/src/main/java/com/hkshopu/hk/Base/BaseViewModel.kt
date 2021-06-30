package com.HKSHOPU.hk.Base


import androidx.lifecycle.ViewModel
import com.HKSHOPU.hk.Base.response.UIDataBean


/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
open class BaseViewModel : ViewModel() {

    fun <T> createErrorUIData(e: Throwable): UIDataBean<T> {
        return UIDataBean(e)
    }

    override fun onCleared() {
        super.onCleared()

    }
}
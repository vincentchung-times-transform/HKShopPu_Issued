package com.HKSHOPU.hk.ui.user.vm


import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import com.google.gson.reflect.TypeToken
import com.HKSHOPU.hk.Base.BaseViewModel
import com.HKSHOPU.hk.Base.response.StatusResourceObserver
import com.HKSHOPU.hk.Base.response.UIDataBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.data.repository.ShopmanageRepository
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.GsonProvider
import okhttp3.*
import java.io.File
import java.io.IOException

class ShopVModel : BaseViewModel() {


    private val repository = ShopmanageRepository()
    val shopnameLiveData = MediatorLiveData<UIDataBean<Any>>()
    val addnewshopLiveData = MediatorLiveData<UIDataBean<Any>>()
    val addProductData = MediatorLiveData<UIDataBean<Any>>()
    val syncShippingfareData = MediatorLiveData<UIDataBean<Any>>()
    val updateProductStatusData = MediatorLiveData<UIDataBean<Any>>()

    fun shopnamecheck(lifecycleOwner: LifecycleOwner, shop_title: String) {
        repository.shopnamecheck(lifecycleOwner, shop_title)
            .subscribe(StatusResourceObserver(shopnameLiveData, silent = false))
    }

    fun adddnewshop(lifecycleOwner: LifecycleOwner, shop_title: String) {
        repository.adddnewshop(lifecycleOwner, shop_title)
            .subscribe(StatusResourceObserver(addnewshopLiveData, silent = false))
    }

    fun add_product(lifecycleOwner: LifecycleOwner,shop_id : String, product_category_id : String, product_sub_category_id :String, product_title : String, quantity : Int, product_description : String, product_price :Int, shipping_fee : Int, weight : Int, new_secondhand :String, product_pic_list : MutableList<File>, product_spec_list : String, user_id: String,  length : Int, width : Int, height : Int, shipment_method : String) {
        repository.add_product(lifecycleOwner, shop_id, product_category_id, product_sub_category_id, product_title, quantity, product_description, product_price, shipping_fee, weight, new_secondhand, product_pic_list, product_spec_list, user_id, length, width, height, shipment_method)
            .subscribe(StatusResourceObserver(addProductData, silent = false))
    }

    fun syncShippingfare(lifecycleOwner: LifecycleOwner, id : String, shipment_settings : String) {
        repository.syncShippingfare(lifecycleOwner, id, shipment_settings)
            .subscribe(StatusResourceObserver(syncShippingfareData, silent = false))
    }


    fun updateProductStatus(lifecycleOwner: LifecycleOwner, id : String, status : String) {
        repository.updateProductStatus(lifecycleOwner, id, status)
            .subscribe(StatusResourceObserver(updateProductStatusData, silent = false))
    }


    fun getShopCategory(){
        //测试环境 使用测试域名
        if (true) {
            ApiConstants.API_HOST = "https://hkshopu-20700.df.r.appspot.com/shop_category/index/"

        }
        //在正式环境下，先获取API域名
        val request = Request.Builder()
            .url(ApiConstants.API_HOST)
            .get()
            .build()
        OkHttpClient()
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("ShopVMmodel", "ShopCategory Response"+ response.body().toString())
                    }
                }
            })
    }

}

package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle

import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshUserAddressList
import com.HKSHOPU.hk.data.bean.BuyerAddressListBean

import com.HKSHOPU.hk.databinding.ActivityUseraddresspresetBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerAddressPresetAdapter

import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class BuyerAddressPresetActivity : BaseActivity() {
    private lateinit var binding: ActivityUseraddresspresetBinding

    private val adapter = BuyerAddressPresetAdapter()
    val userId = MMKV.mmkvWithID("http").getString("UserId", "");
    var url = ApiConstants.API_HOST + "shopping_cart/"+userId+"/buyer_address/"
    var presetid:String= ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUseraddresspresetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarUserAddressPreset.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.VISIBLE

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getBuyerAddressList(url)
    }

    private fun initView() {
        binding.progressBarUserAddressPreset.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.VISIBLE

        adapter.presetClick = {
            val presetId = it
            presetid = presetId
        }
    }

    private fun initVM() {
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun getBuyerAddressList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("ShopAddressPreset", "返回資料 resStr：" + resStr)
                    Log.d("ShopAddressPreset", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val userAddressListBean: BuyerAddressListBean =
                                Gson().fromJson(jsonObject.toString(), BuyerAddressListBean::class.java)

                            list.add(userAddressListBean)
                        }
                        adapter.setData(list)
                        runOnUiThread {
                            binding.recyclerview.adapter = adapter
                            binding.progressBarUserAddressPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBarUserAddressPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getBuyerAddressList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserAddressPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getBuyerAddressList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserAddressPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getBuyerAddressList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarUserAddressPreset.visibility = View.GONE
                    binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun do_BuyerAddressPreset(pressId: String) {
        binding.progressBarUserAddressPreset.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.VISIBLE

         val url = ApiConstants.API_HOST+"user_detail/userAddress_isDefault/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopAddressPreset", "返回資料 resStr：" + resStr)
                    Log.d("ShopAddressPreset", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            RxBus.getInstance().post(EventRefreshUserAddressList())
                            finish()
                            Toast.makeText(this@BuyerAddressPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarUserAddressPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@BuyerAddressPresetActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarUserAddressPreset.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("do_BuyerAddressPreset_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserAddressPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("do_BuyerAddressPreset_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserAddressPreset.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("do_BuyerAddressPreset_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarUserAddressPreset.visibility = View.GONE
                    binding.imgViewLoadingBackgroundUserAddressPreset.visibility =View.GONE
                }
            }
        })
        web.Do_BuyerAddressPreset(url,userId,pressId)
    }
    private fun initClick() {
        binding.tvBuyeraddresspreset.setOnClickListener {
            AlertDialog.Builder(this@BuyerAddressPresetActivity)
                .setTitle("")
                .setMessage("確定將此地址改為預設地址嗎?")
                .setPositiveButton("確定"){
                    // 此為 Lambda 寫法
                        dialog, which ->do_BuyerAddressPreset(presetid)
                }
                .setNegativeButton("取消"){ dialog, which -> dialog.cancel()

                }
                .show()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
//        btn_Signup.setOnClickListener {
//
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }
    }
}
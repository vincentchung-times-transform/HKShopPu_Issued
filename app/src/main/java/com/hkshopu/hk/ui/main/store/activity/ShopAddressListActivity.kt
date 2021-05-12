package com.hkshopu.hk.ui.main.store.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.application.App
import com.hkshopu.hk.component.CommonVariable
import com.hkshopu.hk.component.EventGetBankAccountSuccess
import com.hkshopu.hk.component.EventProductCatSelected
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityAccountsetupBinding
import com.hkshopu.hk.databinding.ActivityBankaccountlistBinding
import com.hkshopu.hk.databinding.ActivityShopaddresslistBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.adapter.BankListAdapter
import com.hkshopu.hk.ui.main.store.adapter.CategoryMultiAdapter
import com.hkshopu.hk.ui.main.store.adapter.ShopAddressListAdapter
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.extension.loadNovelCover
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopAddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityShopaddresslistBinding

    private val adapter = ShopAddressListAdapter()
    val shopId = MMKV.mmkvWithID("http").getInt("ShopId", 0)
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/get_shop_address"
    var addressIds: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopaddresslistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getShopAddressList(url)

    }

    private fun initView() {


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

    private fun getShopAddressList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopAddressListBean>()
                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopAddressListActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopAddressListActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopAddressListBean: ShopAddressListBean =
                                Gson().fromJson(jsonObject.toString(), ShopAddressListBean::class.java)
                            list.add(shopAddressListBean)
                        }

                        adapter.setData(list)
                        runOnUiThread {
                            if(list.size > 1){
                                binding.tvEdit.visibility = View.VISIBLE
                            }
                            binding.recyclerview.adapter = adapter

                        }


                    }
//                        initRecyclerView()

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener{
            finish()
        }

        binding.tvEdit.setOnClickListener {
            if(binding.tvEdit.text.equals("編輯")){
                binding.tvEdit.text = "完成"
                binding.tvEdit.textColor = Color.parseColor("#1DBCCF")
                adapter.updateData(true)
            }else{
                binding.tvEdit.text = "編輯"
                binding.tvEdit.textColor = Color.parseColor("#8E8E93")
                adapter.updateData(false)
                doShopAddressDel(addressIds)
            }

        }

        binding.tvAddshopaddress.setOnClickListener {
            val intent = Intent(this, AddShopAddress2Activity::class.java)
            startActivity(intent)
            finish()
        }
        adapter.cancelClick = {

            addressIds.add(it)

        }
        adapter.intentClick = {


        val intent = Intent(this@ShopAddressListActivity, ShopAddressPresetActivity::class.java)
            startActivity(intent)
            finish()
        }
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }
    private fun doShopAddressDel(list: ArrayList<String>) {

        var url = ApiConstants.API_PATH +"shop/delete_shop_address/"

        Log.d("ShopAddressListActivity", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopAddressListActivity", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    Log.d("ShopAddressListActivity", "返回資料 ret_val：" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {

                            Toast.makeText(this@ShopAddressListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@ShopAddressListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ShopAddressDel(url, list)
    }
}
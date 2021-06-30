package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventChangeShopEmailSuccess

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener

import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class EmailAddAfterIdentifyingActivity : BaseActivity(){
    private lateinit var binding: ActivityEmailAddAfterIdentifyingBinding

    private val VM = AuthVModel()
    var getstring : String? = null
    var email_old: String = ""
    var email: String = ""
    var email_on:String = ""
    var isEmailShow: String = ""
    var address_id:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailAddAfterIdentifyingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        address_id = intent.getBundleExtra("bundle")!!.getString("address_id","").toString()
        email_old = intent.getBundleExtra("bundle")!!.getString("email_old","")
        email_on = intent.getBundleExtra("bundle")!!.getString("email_on","")
        initView()
        initClick()

    }



    private fun initView() {
        binding.etAddEmail.setText(email_old)
        binding.etAddEmail.doAfterTextChanged {
            email = binding.etAddEmail.text.toString()
        }
        binding.layoutAddEmail.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }

        if(email_on.equals("Y")){
            isEmailShow = "Y"
            binding.switchview.openSwitcher()
        }else{
            isEmailShow = "N"
            binding.switchview.closeSwitcher()
        }

//        binding.switchview.openSwitcher()

    }


    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.tvSave.setOnClickListener {

            if(email.isNullOrEmpty()){
                email = email_old
            }
            Do_ShopEmailUpdate(email,isEmailShow)
        }

        binding.switchview.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {

                if(isOpen){
                    isEmailShow = "Y"
                }else{
                    isEmailShow = "N"
                }

            }
        })


    }

    private fun Do_ShopEmailUpdate(email: String,is_email_show:String) {
        val shopId = MMKV.mmkvWithID("http").getString("ShopId","").toString()
        var url = ApiConstants.API_PATH+"shop/"+shopId+"/update/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("Do_ShopEmailUpdate", "返回資料 resStr：" + resStr)
                    Log.d("Do_ShopEmailUpdate", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        RxBus.getInstance().post(EventChangeShopEmailSuccess(email))
                        finish()
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@EmailAddAfterIdentifyingActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopEmailUpdate(url,address_id,email,is_email_show)
    }


}
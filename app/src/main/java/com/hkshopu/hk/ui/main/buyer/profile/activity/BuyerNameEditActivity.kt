package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.paypal.android.sdk.payments.*
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerNameEditActivity : BaseActivity() {

    private lateinit var binding: ActivityUsernameeditBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    val url = ApiConstants.API_HOST + "user_detail/update_detail/"
    var name=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsernameeditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle = intent.getBundleExtra("bundle")
        name = bundle!!.getString("name").toString()

        initView()
        initClick()

    }

    private fun initView() {
        binding.progressBarUserNameEdit.visibility = View.GONE
        binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.GONE

        binding.etUsernameedit.setText(name)
        binding.etUsernameedit.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(10)))
        binding.etUsernameedit.doAfterTextChanged {
            name = binding.etUsernameedit.text.toString()
        }
    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvSave.setOnClickListener {
            binding.progressBarUserNameEdit.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.VISIBLE

            Log.d("BuyerNameEditActivity_checkName", name.toString())
            doUpdateName(url,name)
        }

    }

    private fun doUpdateName(url: String,name:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("UserInfoModifyActivity", "返回資料 resStr：" + resStr)
                    Log.d("UserInfoModifyActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        RxBus.getInstance().post(EventRefreshUserInfo())
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerNameEditActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBarUserNameEdit.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerNameEditActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBarUserNameEdit.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("doUpdateName_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserNameEdit.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doUpdateName_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserNameEdit.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doUpdateName_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarUserNameEdit.visibility = View.GONE
                    binding.imgViewLoadingBackgroundUserNameEdit.visibility = View.GONE
                }
            }
        })
        web.Do_UserInfoUpdate(url,userId,name,"","","","","","")
    }
    public override fun onDestroy() {
        // Stop service when done
        super.onDestroy()
    }


}
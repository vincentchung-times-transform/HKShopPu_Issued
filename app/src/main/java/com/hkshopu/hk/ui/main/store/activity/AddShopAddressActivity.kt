package com.hkshopu.hk.ui.main.store.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.component.EventAddShopSuccess
import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AddShopAddressActivity : BaseActivity(){
    private lateinit var binding: ActivityShopaddresseditBinding

    private val VM = AuthVModel()
    var companyName: String = ""
    var phone_country: String = ""
    var phone_number: String = ""
    var phone: String = ""
    var country: String = ""
    var admin: String = ""
    var thoroughfare: String = ""
    var feature: String = ""
    var subaddress: String = ""
    var floor: String = ""
    var room: String = ""

    val userId = MMKV.mmkvWithID("http").getInt("UserId", 0);
    private lateinit var settings: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopaddresseditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("shopdata", 0)
        initView()
        initVM()
        initClick()

    }

    private fun initView() {
        binding.editShopname.requestFocus()
        binding.editShopname.doAfterTextChanged {
            companyName = binding.editShopname.text.toString()
        }
        binding.editShopphoneNumber.doAfterTextChanged {
            phone_number = binding.editShopphoneNumber.text.toString()
            phone_country =binding.tvShopphoneCountry.text.toString()
            phone = phone_country + phone_number
        }

        binding.editShopname.doAfterTextChanged {
            companyName = binding.editShopname.text.toString()
        }

        binding.editCountry.doAfterTextChanged {
            country = binding.editCountry.text.toString()
        }

        binding.editAdmin.doAfterTextChanged {
            admin = binding.editAdmin.text.toString()
        }


        binding.editthoroughfare.doAfterTextChanged {
            thoroughfare = binding.editthoroughfare.text.toString()
        }

        binding.editfeature.doAfterTextChanged {
            feature = binding.editfeature.text.toString()
        }

        binding.editsubaddress.doAfterTextChanged {
            subaddress = binding.editsubaddress.text.toString()
        }

        binding.editfloor.doAfterTextChanged {
            floor = binding.editfloor.text.toString()
        }

        binding.editroom.doAfterTextChanged {
            room = binding.editroom.text.toString()
        }

        binding.layoutShopaddressEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }


    }

    private fun initVM() {
//        VM.socialloginLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    if (url.isNotEmpty()) {
//                        toast("登录成功")
//
//                    }
//
//                    finish()
//                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
//            }
//        })
    }
    fun EditText.showSoftKeyboard(){
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
    var file: File? = null
    private fun initClick() {
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.tvCreateshop.setOnClickListener {
            file = processImage()
            var shopName = settings.getString("shopname", "")
            var shop_category_id1 = settings.getInt("shop_category_id1", 0)
            var shop_category_id2= settings.getInt("shop_category_id2", 0)
            var shop_category_id3= settings.getInt("shop_category_id3", 0)
            var bankCode = settings.getString("bankcode", "")
            var bankName = settings.getString("bankname", "")
            var accountName = settings.getString("accountname", "")
            var accountNumber = settings.getString("accountnumber", "")
            doAddShop(
                shopName!!,
                userId.toString(),
                shop_category_id1,
                shop_category_id2,
                shop_category_id3,
                bankCode!!,
                bankName!!,
                accountName!!,
                accountNumber!!,
                companyName,
                phone_country,
                phone_number,
                "",
                country,
                admin,
                thoroughfare,
                feature,
                subaddress,
                floor,
                room,
                file!!
            )
        }

    }
    fun processImage(): File? {
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + ".jpg")
        var mImageUri = settings.getString("image", null);
        val decodedString: ByteArray = Base64.decode(mImageUri, Base64.DEFAULT)
        val bitmap:Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        val bos = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitmapdata: ByteArray = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file
    }



    private fun doAddShop(
        shop_title: String,
        user_id: String,
        shop_category_id1: Int,
        shop_category_id2: Int,
        shop_category_id3: Int,
        bank_code: String,
        bank_name: String,
        bank_account_name: String,
        bank_account: String,
        address_name: String,
        address_country_code: String,
        address_phone: String,
        address_is_phone_show: String,
        address_area: String,
        address_district: String,
        address_road: String,
        address_number: String,
        address_other: String,
        address_floor: String,
        address_room: String,
        postImg: File
    ) {
        val url = ApiConstants.API_HOST+"/shop/save/"
        val editor = settings.edit()
        editor.clear()
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddShopAddressActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddShopAddressActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        RxBus.getInstance().post(EventAddShopSuccess())
                        var shop_id: Int = json.getInt("shop_id")
//                        MMKV.mmkvWithID("http").putInt("ShopId", shop_id)
                        val intent = Intent(
                            this@AddShopAddressActivity,
                            ShopmenuActivity::class.java
                        )
                        startActivity(intent)
                        finish()
//                        runOnUiThread {
//
//                            Toast.makeText(
//                                this@AddShopAddressActivity,
//                                ret_val.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddShopAddressActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
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
        web.Do_ShopAdd(
            url,
            shop_title,
            user_id,
            shop_category_id1,
            shop_category_id2,
            shop_category_id3,
            bank_code,
            bank_name,
            bank_account_name,
            bank_account,
            address_name,
            address_country_code,
            address_phone,
            address_is_phone_show,
            address_area,
            address_district,
            address_road,
            address_number,
            address_other,
            address_floor,
            address_room,
            postImg
        )
    }

}
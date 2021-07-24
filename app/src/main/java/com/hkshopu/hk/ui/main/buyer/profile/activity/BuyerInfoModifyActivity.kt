package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.component.EventShopmenuToSpecificPage
import com.HKSHOPU.hk.data.bean.UserInfoBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.extension.load
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class BuyerInfoModifyActivity : BaseActivity() {
    private lateinit var binding: ActivityUserinfomodifyBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    var url = ApiConstants.API_HOST + "user_detail/" + userId + "/show/"
    private val PROFILE_IMAGE_REQ_CODE = 101
    private var mProfileUri: Uri? = null
    private var isSelectImage = false
    var gender: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserinfomodifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarUserInfoModify.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.VISIBLE
        getData(url)

        initVM()
        initClick()
        initEvent()
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


    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivShopImgEdit.setOnClickListener {
            ImagePicker.with(this)
                // Crop Square image
                .galleryOnly()
                .cropSquare()
                .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
                    Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                }
                .setDismissListener {
                    Log.d("ImagePicker", "Dialog Dismiss")
                }
                // Image resolution will be less than 512 x 512
                .maxResultSize(200, 200)
                .start(PROFILE_IMAGE_REQ_CODE)
        }
        binding.layoutNamemodify.setOnClickListener {
            val intent = Intent(this@BuyerInfoModifyActivity, BuyerNameEditActivity::class.java)
            var bundle = Bundle()
            bundle.putString("name", binding.tvUserName.text.toString())
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutGendermodify.setOnClickListener {
            val intent = Intent(this@BuyerInfoModifyActivity, GenderChangeActivity::class.java)
            var bundle = Bundle()
            bundle.putString("gender", gender.toString())
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutBirthdaymodify.setOnClickListener {
            val intent = Intent(this@BuyerInfoModifyActivity, BirthDayChangeActivity::class.java)
            var bundle = Bundle()
            bundle.putString("birthday", binding.tvBirthday.text.toString())
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutPhonemodify.setOnClickListener {
            val intent = Intent(this@BuyerInfoModifyActivity, BuyerPhoneEditActivity::class.java)
            var bundle = Bundle()
            bundle.putString("phone", binding.tvUserPhone.text.toString())
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutSocialacntmodify.setOnClickListener {
            val intent = Intent(this@BuyerInfoModifyActivity, BuyerSocialAccountSetActivity::class.java)
            startActivity(intent)
        }
        binding.layoutPasswordmodify.setOnClickListener {
            val intent = Intent(this@BuyerInfoModifyActivity, BuyerPWchange1Activity::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshUserInfo -> {
                        getData(url)
                    }
                }
            }, {
                it.printStackTrace()
            })

    }


    private fun getData(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("UserInfoModifyActivity", "返回資料 resStr：" + resStr)
                    Log.d("UserInfoModifyActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val translations: JSONObject = json.getJSONObject("data")
                        Log.d("UserInfoModifyActivity", "返回資料 JSONObject：" + translations.toString())

                        val userInfoBean: UserInfoBean =
                            Gson().fromJson(translations.toString(), UserInfoBean::class.java)
                        runOnUiThread {
                            binding.tvUserName.text = userInfoBean.name
                            if (userInfoBean.gender.equals("M")) {
                                binding.tvUserGender.text = "男性"
                                gender = "M"

                            } else if (userInfoBean.gender.equals("F")) {
                                binding.tvUserGender.text = "女性"
                                gender = "F"

                            } else if (userInfoBean.gender.equals("O")){
                                binding.tvUserGender.text = "其他"
                                gender = "O"

                            }
                            binding.tvBirthday.text = userInfoBean.birthday
                            binding.tvUserPhone.text = userInfoBean.phone
                            binding.tvUserEmail.text = userInfoBean.email
                            binding.ivChevronUserEmail.visibility = View.GONE
                            userInfoBean.pic ?. let {
                                if (userInfoBean.pic.isNotEmpty()) {
                                   binding.ivShopImg.load(userInfoBean.pic)
                                }
                                null // finally returns null
                            } ?: let {

                            }

                            binding.progressBarUserInfoModify.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                        }

                    }else{
                        runOnUiThread {
                            binding.progressBarUserInfoModify.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("getData_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserInfoModify.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getData_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserInfoModify.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getData_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarUserInfoModify.visibility = View.GONE
                    binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }
    private fun processImage(): File? {
        val drawable = binding.ivShopImg.drawable as BitmapDrawable
        val bmp = drawable.bitmap
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + ".jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException) // Catch the exception
        {
            e.printStackTrace()
            Log.d("processImage_errorMessage", "IOException: ${e.toString()}")
        }
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            when (requestCode) {
                PROFILE_IMAGE_REQ_CODE -> {
                    mProfileUri = uri
                    try {
                        binding.progressBarUserInfoModify.visibility = View.VISIBLE
                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.VISIBLE

                        uri?.let {
                            if(Build.VERSION.SDK_INT <= 28) {
                                val bitmap =
                                    MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri)
                                if (bitmap != null) {
                                    runOnUiThread {
                                        binding.ivShopImg.setImageBitmap(bitmap)
                                        isSelectImage = true
                                        val file = processImage()
                                        doUserImgUpdate(file!!)
                                    }
                                } else {
                                    isSelectImage = false
                                    runOnUiThread {
                                        binding.progressBarUserInfoModify.visibility = View.GONE
                                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                                    }
                                }
                            }else{
                                val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                if (bitmap != null) {
                                    runOnUiThread {
                                        binding.ivShopImg.setImageBitmap(bitmap)
                                        isSelectImage = true
                                        val file = processImage()
                                        doUserImgUpdate(file!!)
                                    }
                                }else{
                                    isSelectImage = false
                                    runOnUiThread {
                                        binding.progressBarUserInfoModify.visibility = View.GONE
                                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        //handle exception
                        Log.d("onActivityResult_errorMessage", e.toString())
                        isSelectImage = false
                        runOnUiThread {
                            binding.progressBarUserInfoModify.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                        }
                    }
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            runOnUiThread {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                binding.progressBarUserInfoModify.visibility = View.GONE
                binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                binding.progressBarUserInfoModify.visibility = View.GONE
                binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
            }
        }
    }

    private fun doUserImgUpdate(postImg: File) {
        var url = ApiConstants.API_HOST + "user_detail/add_pic/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("UserInfoModifyActivity", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@BuyerInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarUserInfoModify.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                        }
                        RxBus.getInstance().post(EventRefreshUserInfo())
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@BuyerInfoModifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarUserInfoModify.visibility = View.GONE
                            binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getData_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserInfoModify.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getData_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarUserInfoModify.visibility = View.GONE
                        binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getData_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarUserInfoModify.visibility = View.GONE
                    binding.imgViewLoadingBackgroundUserInfoModify.visibility = View.GONE
                }
            }
        })
        web.Do_UserImgUpdate(url, userId,postImg)
    }
}
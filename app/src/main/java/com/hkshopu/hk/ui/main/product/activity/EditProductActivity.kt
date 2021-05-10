package com.hkshopu.hk.ui.main.product.activity

import MyLinearLayoutManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.*
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.GsonProvider.gson
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.PicsAdapter
import com.hkshopu.hk.ui.main.product.adapter.ShippingFareCheckedAdapter
import com.hkshopu.hk.ui.main.product.fragment.StoreOrNotDialogFragment
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class EditProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewProductBinding
    private val VM = ShopVModel()
    val mAdapters_shippingFareChecked = ShippingFareCheckedAdapter()
    val REQUEST_EXTERNAL_STORAGE = 100


    //從本地端選取圖片轉換為bitmap後存的list
    var mutableList_pics = mutableListOf<ItemPics>()

    var product_edit_session = false

    //宣告頁面資料變數
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1
    var MMKV_editTextEntryProductName :String = ""
    var MMKV_editTextEntryProductDiscription :String = ""
    var MMKV_proCate_id: String = ""
    var MMKV_proSubCate_id: String = ""
    var MMKV_c_product_category: String = ""
    var MMKV_c_product_sub_category: String = ""
    var MMKV_textViewSeletedCategory :String = ""
    var MMKV_product_spec_on: String = ""
    var MMKV_editTextMerchanPrice :String = ""
    var MMKV_editTextMerchanQunt :String = ""
    var MMKV_inven_price_range: String = ""
    var MMKV_inven_quant_range: String = ""
    var MMKV_value_txtViewFareRange :String = ""
    var MMKV_boolean_needMoreTimeToStockUp = "y"
    var MMKV_editMoreTimeInput :String = ""
    var MMKV_weight: String = ""
    var MMKV_length:String = ""
    var MMKV_width: String = ""
    var MMKV_height: String = ""
    var MMKV_checked_brandNew = "new"
    var MMKV_jsonTutList_inven: String = "[{ \"spec_desc_1\": \"\",\"spec_desc_2\": \"\",\"spec_dec_1_items\": \"\",\"spec_dec_2_items\": \"\",\"price\": 0,\"quantity\": 0 }]"
    var MMKV_jsonTutList_fare: String = "[{\"shipment_desc\":\"\",\"price\":0,\"onoff\":\"of\",\"shop_id\" : 0 }]"

    lateinit var productInfoList :  ProductInfoBean

    //宣告運費項目陣列變數
    var mutableList_itemShipingFare = mutableListOf<ItemShippingFare>()
    var mutableList_itemShipingFare_filtered = mutableListOf<ItemShippingFare>()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)
        product_edit_session = MMKV.mmkvWithID("http").getBoolean("product_edit_session", false)
        Log.d("product_edit_session", "product_edit_session : ${product_edit_session}")
        if(product_edit_session.equals(false)){
            product_edit_session = true
            MMKV.mmkvWithID("http").putBoolean("product_edit_session", product_edit_session)
            getProductInfo(MMKV_product_id)
        }


        try{
            Thread.sleep(800)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        initVM()
        initView()

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initMMKV_and_initViewValue() {


        Thread(Runnable {

            //商品圖片
            var pics_list_size = MMKV.mmkvWithID("addPro").getInt("value_pics_size", 0)

            Log.d("fjjhgsgds", pics_list_size.toString())

            for (i in 0..pics_list_size - 1) {

                var previouslyEncodedImage: String? =
                    MMKV.mmkvWithID("addPro").getString("value_pic${i}", "")

                if (i == 0) {

                    if (!previouslyEncodedImage.equals("")) {
                        val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                        mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))
                    }

                } else {

                    if (!previouslyEncodedImage.equals("")) {
                        val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
                        mutableList_pics.add(ItemPics(bitmap, R.drawable.custom_unit_transparent))
                    }

                }
            }
            Log.d("mutableList_pics", pics_list_size.toString())

            runOnUiThread {
                val mAdapter = PicsAdapter()
                binding.rView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rView.adapter = mAdapter

                mAdapter.updateList(mutableList_pics)
                mAdapter.notifyDataSetChanged()
            }

        }).start()

        //商品名稱
        MMKV_editTextEntryProductName = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductName",
            MMKV_editTextEntryProductName
        ).toString()
        binding.editTextEntryProductName.setText(MMKV_editTextEntryProductName)

        //商品描述
        MMKV_editTextEntryProductDiscription = MMKV.mmkvWithID("addPro").getString(
            "value_editTextEntryProductDiscription",
            MMKV_editTextEntryProductDiscription
        ).toString()
        binding.editTextEntryProductDiscription.setText(MMKV_editTextEntryProductDiscription)

        //商品分類
        initProCategoryDatas()
        //商品庫存
        initInvenDatas()
        //商品運費
        initProFareDatas()


        //商品保存狀況
        MMKV_checked_brandNew = MMKV.mmkvWithID("addPro").getString(
            "value_checked_brandNew",
            MMKV_checked_brandNew
        ).toString()
        if(MMKV_checked_brandNew=="new"){

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e.toFloat())
            binding.tvSecondhand.setElevation(e_zero.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
        }else{

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e_zero.toFloat())
            binding.tvSecondhand.setElevation(e.toFloat())

            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)
        }





        //需要較長時間備貨嗎?
        binding.needMoreTimeToStockUp.text = getString(R.string.textView_more_time_to_stock)
        MMKV_boolean_needMoreTimeToStockUp = MMKV.mmkvWithID("addPro").getString(
            "boolean_needMoreTimeToStockUp",
            "n"
        ).toString()
        if(MMKV_boolean_needMoreTimeToStockUp=="n"){
            binding.needMoreTimeToStockUp.isChecked =false
        }else{
            binding.needMoreTimeToStockUp.isChecked =true
        }
        MMKV_editMoreTimeInput = MMKV.mmkvWithID("addPro").getString("value_editMoreTimeInput", "").toString()
        binding.editMoreTimeInput.setText(MMKV_editMoreTimeInput)

        if(MMKV_editMoreTimeInput.isNotEmpty() && MMKV_editMoreTimeInput.toInt()>0){
            binding.editMoreTimeInput.isVisible = true
            binding.needMoreTimeToStockUp.isChecked = true
        }else{
            binding.editMoreTimeInput.isVisible = false
            binding.needMoreTimeToStockUp.isChecked = false
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun initView() {

        binding.titleEditProduct.setText(R.string.title_editproduct)

        initMMKV_and_initViewValue()
        initEditText()
        initClick()

    }

    fun initEditText() {


        binding.editTextEntryProductName.singleLine = true
        binding.editTextEntryProductName.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editTextEntryProductName =
                        binding.editTextEntryProductName.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextEntryProductName",
                        MMKV_editTextEntryProductName
                    )

                    binding.editTextEntryProductName.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductName)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextEntryProductName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                MMKV_editTextEntryProductName =
                    binding.editTextEntryProductName.text.toString()
                MMKV.mmkvWithID("addPro").putString(
                    "value_editTextEntryProductName",
                    MMKV_editTextEntryProductName
                )
            }
        }
        binding.editTextEntryProductName.addTextChangedListener(textWatcher_editTextEntryProductName)

        binding.editTextEntryProductDiscription.singleLine = true
        binding.editTextEntryProductDiscription.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editTextEntryProductDiscription =
                        binding.editTextEntryProductDiscription.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextEntryProductDiscription",
                        MMKV_editTextEntryProductDiscription
                    )

                    binding.editTextEntryProductDiscription.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextEntryProductDiscription)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextEntryProductDiscription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                MMKV_editTextEntryProductDiscription =
                    binding.editTextEntryProductDiscription.text.toString()
                MMKV.mmkvWithID("addPro").putString(
                    "value_editTextEntryProductDiscription",
                    MMKV_editTextEntryProductDiscription
                )

            }
        }
        binding.editTextEntryProductDiscription.addTextChangedListener(
            textWatcher_editTextEntryProductDiscription
        )


        binding.editTextMerchanPrice.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.editTextMerchanPrice.setText("${MMKV_editTextMerchanPrice}")
            }
        }
        binding.editTextMerchanPrice.singleLine = true
        binding.editTextMerchanPrice.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {


                    if(binding.editTextMerchanPrice.text.isNotEmpty()){

                        binding.editTextMerchanPrice.setText("HKD$ ${binding.editTextMerchanPrice.text.toString()}")
                        MMKV_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString().substring(5)
                        MMKV.mmkvWithID("addPro").putString(
                            "value_editTextMerchanPrice",
                            MMKV_editTextMerchanPrice
                        )

                    }else{
                        binding.editTextMerchanPrice.setText("")
                        MMKV_editTextMerchanPrice = binding.editTextMerchanPrice.text.toString()
                        MMKV.mmkvWithID("addPro").putString(
                            "value_editTextMerchanPrice",
                            MMKV_editTextMerchanPrice
                        )

                    }

                    binding.editTextMerchanPrice.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextMerchanPrice)



                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextMerchanPrice = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(binding.editTextMerchanPrice.text.startsWith("HKD$ ")){


                    MMKV_editTextMerchanPrice =
                        binding.editTextMerchanPrice.text.toString().substring(5)
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextMerchanPrice",
                        MMKV_editTextMerchanPrice
                    )
                }else{
                    if(binding.editTextMerchanPrice.text.toString().length >= 2 && binding.editTextMerchanPrice.text.toString().startsWith("0")){
                        binding.editTextMerchanPrice.setText(binding.editTextMerchanPrice.text.toString().replace("0", "", false))
                        binding.editTextMerchanPrice.setSelection(binding.editTextMerchanPrice.text.toString().length)
                    }

                }

            }
        }
        binding.editTextMerchanPrice.addTextChangedListener(textWatcher_editTextMerchanPrice)


        binding.editTextMerchanQunt.singleLine = true
        binding.editTextMerchanQunt.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editTextMerchanQunt = binding.editTextMerchanQunt.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextMerchanQunt",
                        MMKV_editTextMerchanQunt
                    )

                    binding.editTextMerchanQunt.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editTextMerchanQunt)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editTextMerchanQunt = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(binding.editTextMerchanQunt.text.toString().length >= 2 && binding.editTextMerchanQunt.text.toString().startsWith("0")){
                    binding.editTextMerchanQunt.setText(binding.editTextMerchanQunt.text.toString().replace("0", "", false))
                    binding.editTextMerchanQunt.setSelection(binding.editTextMerchanQunt.text.toString().length)
                }

                MMKV_editTextMerchanQunt = binding.editTextMerchanQunt.text.toString()
                MMKV.mmkvWithID("addPro").putString(
                    "value_editTextMerchanQunt",
                    MMKV_editTextMerchanQunt
                )


            }
        }
        binding.editTextMerchanQunt.addTextChangedListener(textWatcher_editTextMerchanQunt)



        binding.editMoreTimeInput.singleLine = true
        binding.editMoreTimeInput.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV_editMoreTimeInput = binding.editMoreTimeInput.text.toString()
                    MMKV.mmkvWithID("addPro").putString(
                        "value_editMoreTimeInput",
                        MMKV_editMoreTimeInput
                    )
                    binding.editMoreTimeInput.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editMoreTimeInput)

                    true
                }

                else -> false
            }
        }
        val textWatcher_editMoreTimeInput = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                MMKV_editMoreTimeInput = binding.editMoreTimeInput.text.toString()
                MMKV.mmkvWithID("addPro").putString(
                    "value_editMoreTimeInput",
                    MMKV_editMoreTimeInput
                )
            }
        }
        binding.editMoreTimeInput.addTextChangedListener(textWatcher_editMoreTimeInput)


    }

    fun initClick() {

        binding.btnOnShelf.setOnClickListener {

            var pic_list : ArrayList<File> = arrayListOf()
            var file: File? = null
            for(i in 0..mutableList_pics.size-1){
                file = processImage(mutableList_pics.get(i).bitmap, i)
                pic_list.add(file!!)
            }

            Log.d("addNewPro", mutableList_pics.size.toString())
            Log.d("addNewPro", pic_list.toString())
            Log.d("addNewPro", "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }")
            Log.d("addNewPro", MMKV_jsonTutList_fare)

//            VM.add_product(this, 1, 1, 1, "0", 0, "0", 0, 0, 0, "new", pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", 1, 0, 0, 0, jsonTutList_fare)

            if(pic_list.size >=1){
                if(MMKV_editTextEntryProductName.isNotEmpty()){
                    if(MMKV_editTextEntryProductDiscription.isNotEmpty()){
                        if(MMKV_proCate_id.isNotEmpty()||MMKV_proSubCate_id.isNotEmpty()){
                            if(MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
                                if( !MMKV_editTextMerchanPrice.toString().equals("") && !MMKV_editTextMerchanQunt.equals("") &&  binding.iosSwitchSpecification.isOpened().equals(false) ){
                                    if(MMKV_value_txtViewFareRange.isNotEmpty()){

//                                        var inven_switch_off_json = "{ \"product_spec_list\" : [{\"price\": ${value_editTextMerchanPrice}, \"quantity\": ${value_editTextMerchanQunt}, \"spec_dec_1_items\":\"\",\"spec_dec_2_items\":\"\",\"spec_desc_1\":\"\",\"spec_desc_2\":\"\"}]}"
                                        var inven_switch_off_json = ""


                                        Log.d(
                                            "inven_switch_off_json",
                                            inven_switch_off_json.toString()
                                        )

                                        if(MMKV_editMoreTimeInput.equals("")){
                                            MMKV_editMoreTimeInput = "0"
                                        }

                                        //quantity and product_price is discarded
                                        doUpdateProduct(
                                            MMKV_product_id,
                                            MMKV_proCate_id.toInt(),
                                            MMKV_proSubCate_id.toInt(),
                                            MMKV_editTextEntryProductName,
                                            MMKV_editTextEntryProductDiscription,
                                            0,
                                            MMKV_weight.toInt(),
                                            MMKV_checked_brandNew,
                                            pic_list.size.toInt(),
                                            pic_list,
                                            inven_switch_off_json,
                                            MMKV_user_id,
                                            MMKV_length.toInt(),
                                            MMKV_width.toInt(),
                                            MMKV_height.toInt(),
                                            MMKV_jsonTutList_fare,
                                            MMKV_editMoreTimeInput.toInt(),
                                            "active",
                                            MMKV_boolean_needMoreTimeToStockUp
                                        )
                                        product_edit_session=false
                                        MMKV.mmkvWithID("http").putBoolean("product_edit_session", product_edit_session)
                                        Log.d(
                                            "doUpdateProduct",
                                            "MMKV_product_id: ${MMKV_product_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "product_spec_list : ${inven_switch_off_json}  ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "jsonTutList_fare: ${MMKV_jsonTutList_fare} ; " + "value_editMoreTimeInput: ${MMKV_editMoreTimeInput}"
                                        )

                                    }else{
                                        Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                }else if( binding.iosSwitchSpecification.isOpened()){
                                    if( MMKV_inven_price_range.isNotEmpty() && MMKV_inven_quant_range.isNotEmpty()){
                                        if(MMKV_value_txtViewFareRange .isNotEmpty()){

                                            MMKV_editTextMerchanPrice = "0"
                                            MMKV_editTextMerchanQunt = "0"

                                            if(MMKV_editMoreTimeInput.equals("")){
                                                MMKV_editMoreTimeInput = "0"
                                            }

                                            //quantity and product_price is discarded
                                            doUpdateProduct(
                                                MMKV_product_id,
                                                MMKV_proCate_id.toInt(),
                                                MMKV_proSubCate_id.toInt(),
                                                MMKV_editTextEntryProductName,
                                                MMKV_editTextEntryProductDiscription,
                                                0,
                                                MMKV_weight.toInt(),
                                                MMKV_checked_brandNew,
                                                pic_list.size.toInt(),
                                                pic_list,
                                                "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }",
                                                MMKV_user_id,
                                                MMKV_length.toInt(),
                                                MMKV_width.toInt(),
                                                MMKV_height.toInt(),
                                                MMKV_jsonTutList_fare,
                                                MMKV_editMoreTimeInput.toInt(),
                                                "active",
                                                MMKV_boolean_needMoreTimeToStockUp
                                            )
                                            product_edit_session=false
                                            MMKV.mmkvWithID("http").putBoolean("product_edit_session", product_edit_session)
                                            Log.d(
                                                "doUpdateProduct",
                                                "MMKV_product_id: ${MMKV_product_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "jsonTutList_fare: ${MMKV_jsonTutList_fare} ; " + "value_editMoreTimeInput: ${MMKV_editMoreTimeInput}"
                                            )

                                        }else{
                                            Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Log.d(
                                            "testtestetest",
                                            MMKV_inven_price_range.toString() + MMKV_inven_quant_range.toString()
                                        )
                                        Toast.makeText(this, "商品庫存尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                } else{
                                    Toast.makeText(this, "商品價格與數量尚未填寫", Toast.LENGTH_SHORT).show()
                                }
                            }else{

                                Log.d(
                                    "MMKV_shop_id",
                                    "MMKV_shop_id: ${MMKV_shop_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "jsonTutList_fare: ${MMKV_jsonTutList_fare}"
                                )
                                Toast.makeText(this, "包裹大小尚未輸入完成", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this, "商品分類尚未選擇", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "請輸入商品描述", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "請輸入商品名稱", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "請選取至少一張照片", Toast.LENGTH_SHORT).show()
            }
        }

        binding.needMoreTimeToStockUp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.editMoreTimeInput.isVisible = true
                MMKV.mmkvWithID("addPro").putString(
                    "boolean_needMoreTimeToStockUp",
                    "y"
                )

            } else {
                binding.editMoreTimeInput.isVisible = false
                MMKV.mmkvWithID("addPro").putString(
                    "boolean_needMoreTimeToStockUp",
                    "n"
                )
            }
        }

        binding.btnAddPics.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE
                )
//                    return;
            } else {
                launchGalleryIntent()
            }

        }

        //設置containerSpecification中的iosSwitchSpecification開關功能
        binding.iosSwitchSpecification.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onStateChanged(isOpen: Boolean) {
                if (isOpen) {

                    binding.containerAddSpecification.isVisible = true
                    binding.imgSpecLine.isVisible = true
                    binding.editTextMerchanPrice.isVisible = false
                    binding.editTextMerchanQunt.isVisible = false
                    binding.textViewMerchanPriceRange.isVisible = true
                    binding.textViewMerchanQuntRange.isVisible = true

                    val scale = baseContext.resources.displayMetrics.density
                    var elevation = 0
                    val e = (elevation * scale + 0.5f).toInt()

                    binding.containerProductSpecPrice.setElevation(e.toFloat())
                    binding.containerProductSpecQuant.setElevation(e.toFloat())
                    binding.containerProductSpecSwitch.setElevation(e.toFloat())

                } else {

                    binding.containerAddSpecification.isVisible = false
                    binding.imgSpecLine.isVisible = false

                    binding.editTextMerchanPrice.isVisible = true
                    binding.editTextMerchanQunt.isVisible = true
                    binding.textViewMerchanPriceRange.isVisible = false
                    binding.textViewMerchanQuntRange.isVisible = false

                    val scale = baseContext.resources.displayMetrics.density
                    var elevation = 10
                    val e = (elevation * scale + 0.5f).toInt()

                    binding.containerProductSpecSwitch.setElevation(e.toFloat())
                    binding.containerProductSpecPrice.setElevation(e.toFloat())
                    binding.containerProductSpecQuant.setElevation(e.toFloat())


                }
            }
        })


        binding.titleBackAddproduct.setOnClickListener {

            StoreOrNotDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")

        }


        binding.tvBrandnew.setOnClickListener {

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 0
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e.toFloat())
            binding.tvSecondhand.setElevation(e_zero.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)

            MMKV_checked_brandNew = "new"
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", MMKV_checked_brandNew)
        }
        binding.tvSecondhand.setOnClickListener {

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 0
            val e = (elevation * scale + 0.5f).toInt() //to dp
            var e_zero = 0

            binding.tvBrandnew.setElevation(e_zero.toFloat())
            binding.tvSecondhand.setElevation(e.toFloat())
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)

            MMKV_checked_brandNew = "secondhand"
            MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", MMKV_checked_brandNew)

        }

        binding.containerAddSpecification.setOnClickListener {
            val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()

        }
        binding.containerShippingFare.setOnClickListener {
            val intent = Intent(this, EditShippingFareActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.categoryContainer.setOnClickListener {
            val intent = Intent(this, MerchanCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnStore.setOnClickListener {

            var pic_list : ArrayList<File> = arrayListOf()
            var file: File? = null
            for(i in 0..mutableList_pics.size-1){
                file = processImage(mutableList_pics.get(i).bitmap, i)
                pic_list.add(file!!)
            }

            Log.d("addNewPro", mutableList_pics.size.toString())
            Log.d("addNewPro", pic_list.toString())
            Log.d("addNewPro", "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }")
            Log.d("addNewPro", MMKV_jsonTutList_fare)

//            VM.add_product(this, 1, 1, 1, "0", 0, "0", 0, 0, 0, "new", pic_list,  "{ \"product_spec_list\" : ${jsonTutList_inven} }", 1, 0, 0, 0, jsonTutList_fare)

            if(pic_list.size >=1){
                if(MMKV_editTextEntryProductName.isNotEmpty()){
                    if(MMKV_editTextEntryProductDiscription.isNotEmpty()){
                        if(MMKV_proCate_id.isNotEmpty()||MMKV_proSubCate_id.isNotEmpty()){
                            if(MMKV_weight.isNotEmpty() && MMKV_length.isNotEmpty() && MMKV_width.isNotEmpty() && MMKV_height.isNotEmpty()){
                                if( !MMKV_editTextMerchanPrice.toString().equals("") && !MMKV_editTextMerchanQunt.equals(
                                        ""
                                    ) &&  binding.iosSwitchSpecification.isOpened().equals(false) ){
                                    if(MMKV_value_txtViewFareRange.isNotEmpty()){

//                                        var inven_switch_off_json = "{ \"product_spec_list\" : [{\"price\": ${value_editTextMerchanPrice}, \"quantity\": ${value_editTextMerchanQunt}, \"spec_dec_1_items\":\"\",\"spec_dec_2_items\":\"\",\"spec_desc_1\":\"\",\"spec_desc_2\":\"\"}]}"
                                        var inven_switch_off_json = ""

                                        Log.d(
                                            "inven_switch_off_json",
                                            inven_switch_off_json.toString()
                                        )

                                        if(MMKV_editMoreTimeInput.equals("")){
                                            MMKV_editMoreTimeInput = "0"
                                        }

                                        //quantity and product_price is discarded
                                        doUpdateProduct(
                                            MMKV_product_id,
                                            MMKV_proCate_id.toInt(),
                                            MMKV_proSubCate_id.toInt(),
                                            MMKV_editTextEntryProductName,
                                            MMKV_editTextEntryProductDiscription,
                                            0,
                                            MMKV_weight.toInt(),
                                            MMKV_checked_brandNew,
                                            pic_list.size.toInt(),
                                            pic_list,
                                            "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }",
                                            MMKV_user_id,
                                            MMKV_length.toInt(),
                                            MMKV_width.toInt(),
                                            MMKV_height.toInt(),
                                            MMKV_jsonTutList_fare,
                                            MMKV_editMoreTimeInput.toInt(),
                                            "draft",
                                            MMKV_boolean_needMoreTimeToStockUp
                                        )

                                        product_edit_session=false
                                        MMKV.mmkvWithID("http").putBoolean("product_edit_session", product_edit_session)
                                        Log.d(
                                            "doUpdateProduct",
                                            "MMKV_product_id: ${MMKV_product_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "jsonTutList_fare: ${MMKV_jsonTutList_fare} ; " + "value_editMoreTimeInput: ${MMKV_editMoreTimeInput}"
                                        )


                                    }else{
                                        Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                }else if( binding.iosSwitchSpecification.isOpened()){
                                    if( MMKV_inven_price_range.isNotEmpty() && MMKV_inven_quant_range.isNotEmpty()){
                                        if(MMKV_value_txtViewFareRange .isNotEmpty()){

                                            MMKV_editTextMerchanPrice = "0"
                                            MMKV_editTextMerchanQunt = "0"

                                            if(MMKV_editMoreTimeInput.equals("")){
                                                MMKV_editMoreTimeInput = "0"
                                            }

                                            //quantity and product_price is discarded
                                            doUpdateProduct(
                                                MMKV_product_id,
                                                MMKV_proCate_id.toInt(),
                                                MMKV_proSubCate_id.toInt(),
                                                MMKV_editTextEntryProductName,
                                                MMKV_editTextEntryProductDiscription,
                                                0,
                                                MMKV_weight.toInt(),
                                                MMKV_checked_brandNew,
                                                pic_list.size.toInt(),
                                                pic_list,
                                                "{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }",
                                                MMKV_user_id,
                                                MMKV_length.toInt(),
                                                MMKV_width.toInt(),
                                                MMKV_height.toInt(),
                                                MMKV_jsonTutList_fare,
                                                MMKV_editMoreTimeInput.toInt(),
                                                "draft",
                                                MMKV_product_spec_on
                                            )

                                            product_edit_session=false
                                            MMKV.mmkvWithID("http").putBoolean("product_edit_session", product_edit_session)

                                            Log.d(
                                                "doUpdateProduct",
                                                "MMKV_product_id: ${MMKV_product_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "jsonTutList_fare: ${MMKV_jsonTutList_fare} ; " + "value_editMoreTimeInput: ${MMKV_editMoreTimeInput}"
                                            )

                                        }else{
                                            Toast.makeText(this, "商品運費尚未設定", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Log.d(
                                            "testtestetest",
                                            MMKV_inven_price_range.toString() + MMKV_inven_quant_range.toString()
                                        )
                                        Toast.makeText(this, "商品庫存尚未設定", Toast.LENGTH_SHORT).show()
                                    }
                                } else{
                                    Toast.makeText(this, "商品價格與數量尚未填寫", Toast.LENGTH_SHORT).show()
                                }
                            }else{

                                Log.d(
                                    "MMKV_shop_id",
                                    "MMKV_shop_id: ${MMKV_shop_id} ; " + "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id} ; " + "value_editTextEntryProductName: ${MMKV_editTextEntryProductName} ; " + "value_editTextMerchanQunt: ${MMKV_editTextMerchanQunt} ; " + "value_editTextEntryProductDiscription: ${MMKV_editTextEntryProductDiscription} ; " + "value_editTextMerchanPrice: ${MMKV_editTextMerchanPrice} ; " + "MMKV_weight: ${MMKV_weight} ; " + "value_checked_brandNew: ${MMKV_checked_brandNew} ; " + "pic_list.size: ${pic_list.size} ; " + "pic_list: ${pic_list} ; " + "${"{ \"product_spec_list\" : ${MMKV_jsonTutList_inven} }"} ; " + "MMKV_user_id: ${MMKV_user_id} ; " + "MMKV_length: ${MMKV_length} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_width: ${MMKV_width} ; " + "MMKV_height: ${MMKV_height} ; " + "jsonTutList_fare: ${MMKV_jsonTutList_fare}"
                                )
                                Toast.makeText(this, "包裹大小尚未輸入完成", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this, "商品分類尚未選擇", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "請輸入商品描述", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "請輸入商品名稱", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "請選取至少一張照片", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {

            Thread(Runnable {

                val clipData = data?.clipData
                if (clipData != null) {
                    //multiple images selecetd
                    if (mutableList_pics.size == 0) {
                        for (i in 0 until clipData.itemCount) {
                            if (i == 0) {
                                //取得圖片uri存到變數imageUri並轉成bitmap
                                val imageUri = clipData.getItemAt(i).uri
                                Log.d("URI", imageUri.toString())
                                try {
                                    val inputStream =
                                        contentResolver.openInputStream(imageUri)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)

                                    //新增所選圖片以及第一張cover image至mutableList_pics中
                                    mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))

                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }
                            } else {
                                //取得圖片uri存到變數imageUri並轉成bitmap
                                val imageUri = clipData.getItemAt(i).uri
                                Log.d("URI", imageUri.toString())
                                try {
                                    val inputStream =
                                        contentResolver.openInputStream(imageUri)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)

                                    //新增所選圖片以及第一張cover image至mutableList_pics中
                                    mutableList_pics.add(
                                        ItemPics(
                                            bitmap,
                                            R.drawable.custom_unit_transparent
                                        )
                                    )
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } else {
                        for (i in 0 until clipData.itemCount) {
                            //取得圖片uri存到變數imageUri並轉成bitmap
                            val imageUri = clipData.getItemAt(i).uri
                            Log.d("URI", imageUri.toString())
                            try {
                                val inputStream =
                                    contentResolver.openInputStream(imageUri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                //新增所選圖片以及第一張cover image至mutableList_pics中
                                mutableList_pics.add(
                                    ItemPics(
                                        bitmap,
                                        R.drawable.custom_unit_transparent
                                    )
                                )
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    MMKV.mmkvWithID("addPro").putInt(
                        "value_pics_size",
                        mutableList_pics.size.toInt()
                    )

                    for (i in 0..mutableList_pics.size - 1) {
                        //transfer to Base64
                        val baos = ByteArrayOutputStream()
                        mutableList_pics[i].bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                        MMKV.mmkvWithID("addPro").putString("value_pic${i}", encodedImage)
                    }

                } else {
                    //single image selected
                    val imageUri = data?.data
                    Log.d("URI", imageUri.toString())
                    try {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        if (mutableList_pics.size == 0) {
                            //新增所選圖片以及第一張cover image至mutableList_pics中
                            mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))
                        } else {
                            mutableList_pics.add(
                                ItemPics(bitmap, R.drawable.custom_unit_transparent)
                            )
                        }

                        MMKV.mmkvWithID("addPro").putInt("value_pics_size", mutableList_pics.size)

                        for (i in 0..mutableList_pics.size - 1) {
                            //transfer to Base64
                            val baos = ByteArrayOutputStream()
                            mutableList_pics[i].bitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                100,
                                baos
                            )
                            val b = baos.toByteArray()
                            val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                            MMKV.mmkvWithID("addPro").putString("value_pic${i}", encodedImage)
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }

                runOnUiThread {
                    val mAdapter = PicsAdapter()
                    mAdapter.updateList(mutableList_pics)     //傳入資料
                    binding.rView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.rView.adapter = mAdapter
                }

            }).start()
        }
    }

    fun initProCategoryDatas() {

        MMKV_proCate_id = MMKV.mmkvWithID("addPro").getString("product_category_id", "").toString()
        MMKV_proSubCate_id = MMKV.mmkvWithID("addPro").getString("product_sub_category_id", "").toString()
        MMKV_c_product_category = MMKV.mmkvWithID("addPro").getString("c_product_category", "").toString()
        MMKV_c_product_sub_category = MMKV.mmkvWithID("addPro").getString("c_product_sub_category", "").toString()
        MMKV_textViewSeletedCategory = MMKV.mmkvWithID("addPro").getString(
            "value_textViewSeletedCategory",
            MMKV_textViewSeletedCategory
        ).toString()
        binding.textViewSeletedCategory.setText(MMKV_textViewSeletedCategory)
//        binding.textViewSeletedCategory.setText("${MMKV_c_product_category} > ${MMKV_c_product_sub_category}")

        Log.d(
            "MMKV_proCate_id",
            "MMKV_proCate_id: ${MMKV_proCate_id} ; " + "MMKV_proSubCate_id: ${MMKV_proSubCate_id}" + "value_textViewSeletedCategory: ${MMKV_textViewSeletedCategory} ; "
        )

        if (MMKV_proCate_id.isEmpty() || MMKV_proSubCate_id.isEmpty()) {
            binding.textViewSeletedCategory.isVisible = false
            binding.btnAddcategory.isVisible = true
        } else {
            binding.textViewSeletedCategory.isVisible = true
            binding.btnAddcategory.isVisible = false
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initProFareDatas() {

        MMKV_weight = MMKV.mmkvWithID("addPro").getString("datas_packagesWeights", "").toString()
        MMKV_length = MMKV.mmkvWithID("addPro").getString("datas_length", "").toString()
        MMKV_width = MMKV.mmkvWithID("addPro").getString("datas_width", "").toString()
        MMKV_height = MMKV.mmkvWithID("addPro").getString("datas_height", "").toString()
        var fare_datas_size = MMKV.mmkvWithID("addPro").getString("fare_datas_size", "0").toString().toInt()
        var fare_datas_filtered_size = MMKV.mmkvWithID("addPro").getString("fare_datas_filtered_size", "0").toString().toInt()
        MMKV_value_txtViewFareRange = MMKV.mmkvWithID("addPro").getString("value_txtViewFareRange", "").toString()
        MMKV_jsonTutList_fare = MMKV.mmkvWithID("addPro").getString("jsonTutList_fare", MMKV_jsonTutList_fare).toString()
        //挑選最大與最小金額，回傳價格區間
        binding.txtViewFareRange.text = MMKV_value_txtViewFareRange
        if (fare_datas_size != null) {

            if(fare_datas_size > 0) {

                binding.rViewFareItem.isVisible = true
                binding.imgLineFare.isVisible = true

                //MMKV取出 Filtered Fare Item
                for (i in 0..fare_datas_filtered_size-1!!) {
                    var json_invens : String? = MMKV.mmkvWithID("addPro").getString(
                        "value_fare_item_filtered${i}",
                        ""
                    )
                    val json = json_invens
                    val value_fare_item_filtered = gson.fromJson(json, ItemShippingFare::class.java)
                    mutableList_itemShipingFare_filtered.add(value_fare_item_filtered) //顯示在UI
                }

                Log.d(
                    "MMKV_CheckValue",
                    "mutableList_itemShipingFare: ${mutableList_itemShipingFare}"
                )
                Log.d(
                    "MMKV_CheckValue",
                    "mutableList_itemShipingFare_filtered : ${mutableList_itemShipingFare_filtered}"
                )


                if(fare_datas_filtered_size >0){
                    //自訂layoutManager
                    binding.rViewFareItem.setLayoutManager(MyLinearLayoutManager(this, false))
                    binding.rViewFareItem.adapter = mAdapters_shippingFareChecked

                    Thread(Runnable {

                        mAdapters_shippingFareChecked.updateList(
                            mutableList_itemShipingFare_filtered
                        )
                        runOnUiThread {
                            mAdapters_shippingFareChecked.notifyDataSetChanged()
                        }


                    }).start()
                }else{
                    binding.rViewFareItem.isVisible = false
                    binding.imgLineFare.isVisible = false
                }
            }
            else{
                binding.rViewFareItem.isVisible = false
                binding.imgLineFare.isVisible = false
            }

        } else {
            binding.rViewFareItem.isVisible = false
            binding.imgLineFare.isVisible = false
        }




        Log.d(
            "MMKV_weight",
            "MMKV_weight : ${MMKV_weight}, MMKV_length : ${MMKV_length}, MMKV_width : ${MMKV_width}, MMKV_height : ${MMKV_height}, fare_datas_size : ${fare_datas_size}, fare_datas_filtered_size : ${fare_datas_filtered_size}, MMKV_value_txtViewFareRange: ${MMKV_value_txtViewFareRange}"
        )
        Log.d(
            "MMKV_jsonTutList_fare",
            "MMKV_jsonTutList_fare : " + MMKV_jsonTutList_fare.toString()
        )

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun initInvenDatas() {

        MMKV_product_spec_on = MMKV.mmkvWithID("addPro").getString("product_spec_on", "n").toString()
        MMKV.mmkvWithID("addPro").getInt("inven_datas_size", 0)
        var inven_datas_size = MMKV.mmkvWithID("addPro").getInt("inven_datas_size", 0)


        MMKV_jsonTutList_inven = MMKV.mmkvWithID("addPro").getString(
            "jsonTutList_inven",
            MMKV_jsonTutList_inven
        ).toString()
        Log.d(
            "MMKV_jsonTutList_inven",
            "MMKV_jsonTutList_inven : " + MMKV_jsonTutList_inven.toString()
        )


        //挑選最大與最小金額，回傳價格區間

        MMKV_inven_price_range = MMKV.mmkvWithID("addPro").getString(
            "inven_price_range",
            MMKV_inven_price_range
        ).toString()
        MMKV_inven_quant_range = MMKV.mmkvWithID("addPro").getString(
            "inven_quant_range",
            MMKV_inven_quant_range
        ).toString()
        MMKV_editTextMerchanPrice = MMKV.mmkvWithID("addPro").getString(
            "value_editTextMerchanPrice",
            MMKV_editTextMerchanPrice
        ).toString()
        MMKV_editTextMerchanQunt = MMKV.mmkvWithID("addPro").getString(
            "value_editTextMerchanQunt",
            MMKV_editTextMerchanQunt
        ).toString()
        binding.editTextMerchanPrice.setText(MMKV_editTextMerchanPrice)
        binding.editTextMerchanQunt.setText(MMKV_editTextMerchanQunt)
        binding.textViewMerchanPriceRange.setText(MMKV_inven_price_range)
        binding.textViewMerchanQuntRange.setText(MMKV_inven_quant_range)

        if(MMKV_product_spec_on.equals("n")){

            binding.iosSwitchSpecification.closeSwitcher()

            binding.containerAddSpecification.isVisible = false
            binding.imgSpecLine.isVisible = false

            binding.editTextMerchanPrice.isVisible = true
            binding.editTextMerchanQunt.isVisible = true
            binding.textViewMerchanPriceRange.isVisible = false
            binding.textViewMerchanQuntRange.isVisible = false

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 10
            val e = (elevation * scale + 0.5f).toInt()

            binding.containerProductSpecSwitch.setElevation(e.toFloat())
            binding.containerProductSpecPrice.setElevation(e.toFloat())
            binding.containerProductSpecQuant.setElevation(e.toFloat())

        }else{
            binding.iosSwitchSpecification.openSwitcher()

            binding.containerAddSpecification.isVisible = true
            binding.imgSpecLine.isVisible = true

            binding.editTextMerchanPrice.isVisible = false
            binding.editTextMerchanQunt.isVisible = false
            binding.textViewMerchanPriceRange.isVisible = true
            binding.textViewMerchanQuntRange.isVisible = true

            val scale = baseContext.resources.displayMetrics.density
            var elevation = 0
            val e = (elevation * scale + 0.5f).toInt()

            binding.containerProductSpecPrice.setElevation(e.toFloat())
            binding.containerProductSpecQuant.setElevation(e.toFloat())
            binding.containerProductSpecSwitch.setElevation(e.toFloat())

        }
    }



    private fun initVM() {

        VM.addProductData.observe(
            this,
            Observer {
                when (it?.status) {
                    Status.Success -> {
                        if (it.ret_val.toString().equals("產品新增成功!!")) {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        } else {

                            Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        }

                    }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
                }
            }
        )

    }

    private fun processImage(bitmap: Bitmap, i: Int): File? {

        val bmp = bitmap
        val bmpCompress = getResizedBitmap(bmp, 200)
        val file: File
        val path = getExternalFilesDir(null).toString()
        file = File(path, "image" + i + ".jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bmpCompress!!.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            stream?.flush()
            stream?.close()
        } catch (e: IOException) // Catch the exception
        {
            e.printStackTrace()
        }
        return file
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        width = maxSize
        height = (width / bitmapRatio).toInt()
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    override fun onBackPressed() {
        StoreOrNotDialogFragment(this).show(supportFragmentManager, "MyCustomFragment")
    }



    private fun doUpdateProduct(
        product_id: Int,
        product_category_id: Int,
        product_sub_category_id: Int,
        product_title: String,
        product_description: String,
        shipping_fee: Int,
        weight: Int,
        new_secondhand: String,
        product_pic_list_size: Int,
        product_pic_list: ArrayList<File>,
        product_spec_list: String,
        user_id: Int,
        length: Int,
        width: Int,
        height: Int,
        shipment_method: String,
        longterm_stock_up: Int,
        product_status: String,
        product_spec_on: String
    ) {
        val url = ApiConstants.API_HOST+"product/${product_id}/update/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doUpdateProduct", "返回資料 resStr：" + resStr)
                    Log.d("doUpdateProduct", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("產品新增成功!")) {

                        runOnUiThread {
                            Toast.makeText(
                                this@EditProductActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

//                        var user_id: Int = json.getInt("user_id")
//                        var shop_id:Int = json.getInt("shop_id")
//                        MMKV.mmkvWithID("http").putInt("UserId", user_id)
//                        MMKV.mmkvWithID("http").putInt("ShopId", shop_id)
//                        val intent = Intent(this@AddShopActivity, ShopmenuActivity::class.java)
//                        startActivity(intent)
//                        finish()

                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@EditProductActivity,
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
        web.Do_ProductUpdate(
            url,
            product_category_id,
            product_sub_category_id,
            product_title,
            product_description,
            shipping_fee,
            weight,
            new_secondhand,
            product_pic_list_size,
            product_pic_list,
            product_spec_list,
            user_id,
            length,
            width,
            height,
            shipment_method,
            longterm_stock_up,
            product_status,
            product_spec_on
        )
    }

    private fun getProductInfo(product_id: Int) {

        val url = ApiConstants.API_HOST+"product/${product_id}/product_info_forAndroid/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductInfoBean>()
//                val product_id_list = ArrayList<String>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("getProductInfo", "返回資料 resStr：" + resStr)
                    Log.d("getProductInfo", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("已取得商品資訊!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getProductInfo", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            productInfoList = Gson().fromJson(
                                jsonObject.toString(),
                                ProductInfoBean::class.java
                            )

                        }
                        Log.d("getProductInfo", "返回資料 productInfoList：" + productInfoList.toString())



                        MMKV.mmkvWithID("addPro").putInt("value_pics_size", productInfoList.pic_path.size.toInt())
                        for (i in 0..productInfoList.pic_path.size - 1) {

                            mutableList_pics.add(ItemPics(getBitmapFromURL(productInfoList.pic_path.get(i))!!, R.mipmap.cover_pic))

                        }
                        Log.d("mutableList_pics", "mutableList_pics : ${mutableList_pics}")

                        for (i in 0..productInfoList.pic_path.size - 1) {

                            //transfer to Base64
                            val baos = ByteArrayOutputStream()
                            mutableList_pics[i].bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val b = baos.toByteArray()
                            val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                            MMKV.mmkvWithID("addPro").putString("value_pic${i}", encodedImage)
                        }

                        MMKV.mmkvWithID("addPro").putString("value_editTextEntryProductName", productInfoList.product_title.toString())
                        MMKV.mmkvWithID("addPro").putString("value_editTextEntryProductDiscription", productInfoList.product_description.toString())
                        MMKV.mmkvWithID("addPro").putString("product_category_id", productInfoList.product_category_id.toString())
                        MMKV.mmkvWithID("addPro").putString("product_sub_category_id", productInfoList.product_category_sub_id.toString())
                        MMKV.mmkvWithID("addPro").putString("c_product_category", productInfoList.c_product_category.toString())
                        MMKV.mmkvWithID("addPro").putString("c_product_sub_category", productInfoList.c_sub_product_category.toString())
                        MMKV.mmkvWithID("addPro").putString("value_textViewSeletedCategory", "${productInfoList.c_product_category.toString()}>${ productInfoList.c_sub_product_category.toString()}")
                        MMKV.mmkvWithID("addPro").putString("value_checked_brandNew", productInfoList.new_secondhand.toString())
                        MMKV.mmkvWithID("addPro").putString("product_spec_on", productInfoList.product_spec_on.toString())
                        MMKV.mmkvWithID("addPro").putString("product_price", productInfoList.product_price.toString())
                        MMKV.mmkvWithID("addPro").putString("quantity", productInfoList.quantity.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_packagesWeights", productInfoList.weight.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_length", productInfoList.length.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_width",  productInfoList.width.toString())
                        MMKV.mmkvWithID("addPro").putString("datas_height", productInfoList.height.toString())
                        MMKV.mmkvWithID("addPro").putString("value_editMoreTimeInput", productInfoList.longterm_stock_up.toString())
                        MMKV.mmkvWithID("addPro").putString("inven_price_range", "HKD$${productInfoList.min_price}-HKD${productInfoList.max_price}")
                        MMKV.mmkvWithID("addPro").putString("inven_quant_range", "HKD$${productInfoList.min_quantity}-HKD${productInfoList.max_quantity}")
                        MMKV.mmkvWithID("addPro").putString("value_editTextMerchanPrice", productInfoList.product_price.toString())
                        MMKV.mmkvWithID("addPro").putString("value_editTextMerchanQunt", productInfoList.quantity.toString())
                        MMKV.mmkvWithID("addPro").putInt("inven_datas_size", 0)

                        for (i in 0..productInfoList.product_shipment_list.size - 1) {

                            mutableList_itemShipingFare_filtered.add( ItemShippingFare(productInfoList.product_shipment_list.get(i).shipment_desc, productInfoList.product_shipment_list.get(i).price, R.drawable.custom_unit_transparent, productInfoList.product_shipment_list.get(i).onoff, MMKV_shop_id))
                            var json_shippingItem = GsonProvider.gson.toJson(mutableList_itemShipingFare_filtered.get(i))
                            MMKV.mmkvWithID("addPro").putString("value_fare_item_filtered${i}",json_shippingItem)
                        }

                        MMKV.mmkvWithID("addPro").putString("fare_datas_size", mutableList_itemShipingFare_filtered.size.toString())
                        MMKV.mmkvWithID("addPro").putString("fare_datas_filtered_size",mutableList_itemShipingFare_filtered.size.toString())



                    }else{
                    }

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


    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }
}
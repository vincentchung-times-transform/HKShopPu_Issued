package com.hkshopu.hk.ui.main.product.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.data.bean.ProductInfoBean
import com.hkshopu.hk.databinding.ActivityAddProductDescriptionMainBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.product.adapter.SpecificationSizeAdapter
import com.hkshopu.hk.ui.main.product.adapter.SpecificationSpecAdapter
import com.hkshopu.hk.ui.main.product.fragment.SpecificationInfoDialogFragment
import com.hkshopu.hk.ui.main.product.fragment.StoreOrNotDialogFragment
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditProductSpecificationMainActivity : BaseActivity() {

    private lateinit var binding: ActivityAddProductDescriptionMainBinding

    val mAdapter_spec = SpecificationSpecAdapter()
    val mAdapter_size = SpecificationSizeAdapter()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var EDIT_MODE_SPEC = "0"
    var EDIT_MODE_SIZE = "0"

    var firstSpecGrpTitle_check = 0
    var secondSpecGrpTitle_check = 0

    //頁面資料變數宣告
    var value_editTextProductSpecFirst = ""
    var value_editTextProductSpecSecond = ""
    var value_datas_spec_size = 0
    var value_datas_size_size = 0

    lateinit var productInfoList: ProductInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductDescriptionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMMKV()
        initView()
    }

    fun initMMKV() {

        value_editTextProductSpecFirst =
            MMKV.mmkvWithID("addPro").getString("value_editTextProductSpecFirst", "").toString()
        value_editTextProductSpecSecond =
            MMKV.mmkvWithID("addPro").getString("value_editTextProductSpecSecond", "").toString()
        value_datas_spec_size =
            MMKV.mmkvWithID("addPro").getString("datas_spec_size", "0").toString().toInt()
        value_datas_size_size =
            MMKV.mmkvWithID("addPro").getString("datas_size_size", "0").toString().toInt()

        binding.editTextProductSpecFirst.setText(value_editTextProductSpecFirst)
        binding.editTextProductSpecSecond.setText(value_editTextProductSpecSecond)

        Thread(Runnable {

            for (i in 0..value_datas_spec_size - 1) {
                var item_name = MMKV.mmkvWithID("addPro").getString("datas_spec_item${i}", "")
                mutableList_spec.add(
                    ItemSpecification(
                        item_name.toString(),
                        R.drawable.custom_unit_transparent
                    )
                )
            }

            runOnUiThread {
                //更新或新增item
                mAdapter_spec.updateList(mutableList_spec)
                mAdapter_spec.notifyDataSetChanged()

                //判斷Next Step Btn is enable or disable
                changeStatusOfNextStepBtn()

            }

        }).start()


        Thread(Runnable {

            for (i in 0..value_datas_size_size - 1) {
                var item_name = MMKV.mmkvWithID("addPro").getString("datas_size_item${i}", "")
                mutableList_size.add(
                    ItemSpecification(
                        item_name.toString(),
                        R.drawable.custom_unit_transparent
                    )
                )
            }

            runOnUiThread {

                //更新或新增item
                mAdapter_size.updateList(mutableList_size)
                mAdapter_size.notifyDataSetChanged()

                //判斷Next Step Btn is enable or disable
                changeStatusOfNextStepBtn()
            }

        }).start()


    }

    fun initView() {

        binding.titleSpec.setText(R.string.title_editSpecification)

        //預設btnClearAllSpec和btnClearAllSpec隱藏
        binding.btnClearAllSpec.isVisible = false
        binding.btnClearAllSize.isVisible = false


        //Spec and Size item recyclerview setting
        binding.rViewSpecificationItemSpec.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rViewSpecificationItemSpec.adapter = mAdapter_spec

        binding.rviewSpecificationitemSize.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rviewSpecificationitemSize.adapter = mAdapter_size


        //預設btnNextStep disable
        binding.btnNextStep.isEnabled = false
        binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)


        initEditText()
        initClick()

    }


    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.iconSpecificationhelp.setOnClickListener {
            SpecificationInfoDialogFragment().show(supportFragmentManager, "MyCustomFragment")
        }

        binding.btnClearAllSpec.setOnClickListener {

            binding.editTextProductSpecFirst.setText("")
            clearAllSpecItem()

        }
        binding.btnClearAllSize.setOnClickListener {

            binding.editTextProductSpecSecond.setText("")
            clearAllSizeItem()

        }

        binding.btnNextStep.setOnClickListener {

            val intent = Intent(this, EditInventoryAndPriceActivity::class.java)
            var datas_spec_item: MutableList<ItemSpecification> = mAdapter_spec.get_spec_list()
            var datas_size_item: MutableList<ItemSpecification> = mAdapter_size.get_size_list()
            var datas_spec_size: Int = mAdapter_spec.get_datas_spec_size()
            var datas_size_size: Int = mAdapter_size.get_datas_size_size()
            var datas_spec_title_first: String = binding.editTextProductSpecFirst.text.toString()
            var datas_spec_title_second: String = binding.editTextProductSpecSecond.text.toString()

            var bundle = Bundle()

            bundle.putInt("datas_spec_size", datas_spec_size)
            bundle.putInt("datas_size_size", datas_size_size)
            bundle.putString("datas_spec_title_first", datas_spec_title_first)
            bundle.putString("datas_spec_title_second", datas_spec_title_second)




            for (key in 0..datas_spec_item.size - 1) {
                bundle.putParcelable("spec" + key.toString(), datas_spec_item.get(key)!!)
            }

            for (key in 0..datas_size_item.size - 1) {
                bundle.putParcelable("size" + key.toString(), datas_size_item.get(key)!!)
            }

            intent.putExtra("bundle_AddProductSpecificationMainActivity", bundle)


            //MMKV input datas
            MMKV.mmkvWithID("addPro")
                .putString("value_editTextProductSpecFirst", datas_spec_title_first)
            MMKV.mmkvWithID("addPro")
                .putString("value_editTextProductSpecSecond", datas_spec_title_second)
            MMKV.mmkvWithID("addPro").putString("datas_spec_size", datas_spec_size.toString())
            MMKV.mmkvWithID("addPro").putString("datas_size_size", datas_size_size.toString())

            for (i in 0..datas_spec_size - 1) {

                MMKV.mmkvWithID("addPro")
                    .putString("datas_spec_item${i}", datas_spec_item.get(i).spec_name.toString())
            }

            for (i in 0..datas_size_size - 1) {

                MMKV.mmkvWithID("addPro")
                    .putString("datas_size_item${i}", datas_size_item.get(i).spec_name.toString())

            }

            startActivity(intent)
            finish()
        }

        //first specification "spec" item add
        binding.btnAddspecificationSpec.setOnClickListener {
            if (mutableList_spec.size < 3) {

                if (EDIT_MODE_SPEC == "0") {

                    if (mAdapter_spec.get_check_empty() == true && mutableList_spec.size > 0) {
                        Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                    } else {

                        Thread(Runnable {

                            mutableList_spec = mAdapter_spec.get_spec_list()
                            mutableList_spec.add(
                                ItemSpecification(
                                    "",
                                    R.drawable.custom_unit_transparent
                                )
                            )

                            runOnUiThread {
                                //更新或新增item
                                mAdapter_spec.updateList(mutableList_spec)
                                mAdapter_spec.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()

                            }

                        }).start()
                    }


                } else {

                    if (mAdapter_spec.get_check_empty() == true && mutableList_spec.size > 0) {
                        Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                    } else {
                        Thread(Runnable {

                            mutableList_spec.add(
                                ItemSpecification(
                                    "",
                                    R.mipmap.btn_delete_spec_item
                                )
                            )

                            runOnUiThread {

                                //更新或新增item
                                mAdapter_spec.updateList(mutableList_spec)
                                mAdapter_spec.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()

                            }

                        }).start()
                    }

                }

            } else {

                Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()

            }
        }

        //second specification "spec" setting enable
        binding.btnEditspecificationEnableSpec.setOnClickListener {

            binding.btnClearAllSpec.isVisible = true

            EDIT_MODE_SPEC = "1"

            binding.btnEditspecificationEnableSpec.isEnabled = false
            binding.btnEditspecificationEnableSpec.isVisible = false
            binding.btnEditspecificationDisableSpec.isEnabled = true
            binding.btnEditspecificationDisableSpec.isVisible = true

            Thread(Runnable {

                mutableList_spec = mAdapter_spec.get_spec_list()

                for (i in 0..mutableList_spec.size - 1) {
                    mutableList_spec[i] = ItemSpecification(
                        mutableList_spec[i].spec_name.toString(),
                        R.mipmap.btn_delete_spec_item
                    )
                }

                runOnUiThread {

                    //更新或新增item
                    mAdapter_spec.updateList(mutableList_spec)
                    mAdapter_spec.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()

                }

            }).start()

        }

        //second specification "spec" item setting cancel
        binding.btnEditspecificationDisableSpec.setOnClickListener {

            binding.btnClearAllSpec.isVisible = false

            EDIT_MODE_SPEC = "0"

            binding.btnEditspecificationDisableSpec.isEnabled = false
            binding.btnEditspecificationDisableSpec.isVisible = false
            binding.btnEditspecificationEnableSpec.isEnabled = true
            binding.btnEditspecificationEnableSpec.isVisible = true

            Thread(Runnable {

                mutableList_spec = mAdapter_spec.get_spec_list()

                for (i in 0..mutableList_spec.size - 1) {
                    mutableList_spec[i] = ItemSpecification(
                        mutableList_spec[i].spec_name,
                        R.drawable.customborder_specification
                    )
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_spec.updateList(mutableList_spec)
                    mAdapter_spec.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()
                }

            }).start()

        }


        //second specification "size" item add
        binding.btnAddspecificationSize.setOnClickListener {

            mutableList_size = mAdapter_size.get_size_list()

            if (mutableList_size.size < 3) {

                if (EDIT_MODE_SIZE == "0") {

                    if (mAdapter_size.get_check_empty() == true && mutableList_size.size > 0) {
                        Toast.makeText(this, "請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                    } else {
                        Thread(Runnable {

                            mutableList_size = mAdapter_size.get_size_list()
                            mutableList_size.add(
                                ItemSpecification(
                                    "",
                                    R.drawable.custom_unit_transparent
                                )
                            )

                            runOnUiThread {

                                //更新或新增item
                                mAdapter_size.updateList(mutableList_size)
                                mAdapter_size.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()
                            }

                        }).start()

                    }


                } else {


                    if (mAdapter_size.get_check_empty() == true && mutableList_size.size > 0) {
                        Toast.makeText(this, "請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                    } else {

                        Thread(Runnable {

                            mutableList_size = mAdapter_size.get_size_list()
                            mutableList_size.add(
                                ItemSpecification(
                                    "",
                                    R.mipmap.btn_delete_spec_item
                                )
                            )

                            runOnUiThread {

                                //更新或新增item
                                mAdapter_size.updateList(mutableList_size)
                                mAdapter_size.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()
                            }


                        }).start()

                    }


                }

            } else {
                Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()
            }

        }

        //second specification "size" item settings cancel
        binding.btnEditspecificationDisableSize.setOnClickListener {

            binding.btnClearAllSize.isVisible = false

            EDIT_MODE_SIZE = "0"

            binding.btnEditspecificationDisableSize.isEnabled = false
            binding.btnEditspecificationDisableSize.isVisible = false
            binding.btnEditspecificationEnableSize.isEnabled = true
            binding.btnEditspecificationEnableSize.isVisible = true

            Thread(Runnable {

                mutableList_size = mAdapter_size.get_size_list()

                for (i in 0..mutableList_size.size - 1) {
                    mutableList_size[i] = ItemSpecification(
                        mutableList_size[i].spec_name,
                        R.drawable.customborder_specification
                    )
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_size.updateList(mutableList_size)
                    mAdapter_size.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()
                }

            }).start()


        }

        //second specification "size" item settings enable
        binding.btnEditspecificationEnableSize.setOnClickListener {

            binding.btnClearAllSize.isVisible = true

            EDIT_MODE_SIZE = "1"

            binding.btnEditspecificationEnableSize.isEnabled = false
            binding.btnEditspecificationEnableSize.isVisible = false
            binding.btnEditspecificationDisableSize.isEnabled = true
            binding.btnEditspecificationDisableSize.isVisible = true

            Thread(Runnable {

                mutableList_size = mAdapter_size.get_size_list()

                for (i in 0..mutableList_size.size - 1) {
                    mutableList_size[i] = ItemSpecification(
                        mutableList_size[i].spec_name,
                        R.mipmap.btn_delete_spec_item
                    )
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_size.updateList(mutableList_size)
                    mAdapter_size.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()
                }

            }).start()

        }
    }


    fun initEditText() {

        val first_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString() == "") {

                    MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecFirst", "")
                    firstSpecGrpTitle_check = 0

                } else {

                    MMKV.mmkvWithID("addPro")
                        .putString("value_editTextProductSpecFirst", s.toString())
                    firstSpecGrpTitle_check = 1

                }
            }
        }
        val second_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString() == "") {

                    MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecSecondt", "")
                    secondSpecGrpTitle_check = 0

                } else {

                    MMKV.mmkvWithID("addPro")
                        .putString("value_editTextProductSpecSecond", s.toString())
                    secondSpecGrpTitle_check = 1

                }
            }
        }
        binding.editTextProductSpecFirst.addTextChangedListener(first_textWatcher)
        binding.editTextProductSpecSecond.addTextChangedListener(second_textWatcher)

        //editTextProductSpecFirst編輯模式
        binding.editTextProductSpecFirst.singleLine = true
        binding.editTextProductSpecFirst.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextProductSpecFirst",
                        binding.editTextProductSpecFirst.text.toString()
                    )

                    binding.editTextProductSpecFirst.hideKeyboard()
                    binding.editTextProductSpecFirst.clearFocus()

                    changeStatusOfNextStepBtn()

                    true
                }
                else -> false
            }
        }
        //editTextProductSpecFirst編輯模式
        binding.editTextProductSpecSecond.singleLine = true
        binding.editTextProductSpecSecond.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    MMKV.mmkvWithID("addPro").putString(
                        "value_editTextProductSpecSecondt",
                        binding.editTextProductSpecSecond.text.toString()
                    )
                    binding.editTextProductSpecSecond.hideKeyboard()
                    binding.editTextProductSpecSecond.clearFocus()

                    changeStatusOfNextStepBtn()

                    true
                }
                else -> false
            }
        }
    }

    fun clearAllSpecItem() {
        mutableList_spec.clear()
        mAdapter_spec.notifyDataSetChanged()

    }

    fun clearAllSizeItem() {

        mutableList_size.clear()
        mAdapter_size.notifyDataSetChanged()
    }

    fun changeStatusOfNextStepBtn() {

        if (firstSpecGrpTitle_check != 0 && mAdapter_spec.nextStepEnableOrNot()) {
            binding.btnNextStep.isEnabled = true
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
        } else if (secondSpecGrpTitle_check != 0 && mAdapter_size.nextStepEnableOrNot()) {
            binding.btnNextStep.isEnabled = true
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
        } else {
            binding.btnNextStep.isEnabled = false
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
        }

    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


    override fun onBackPressed() {

        val intent = Intent(this, EditProductActivity::class.java)
        startActivity(intent)
        finish()

    }


    private fun getProductInfo(product_id: Int) {

        val url = ApiConstants.API_HOST + "product/${product_id}/product_info_forAndroid/"
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
                        Log.d(
                            "getProductInfo",
                            "返回資料 productInfoList：" + productInfoList.toString()
                        )


                        MMKV.mmkvWithID("addPro").putString(
                            "value_editTextProductSpecFirst",
                            productInfoList.spec_desc_1.get(0)
                        )
                        MMKV.mmkvWithID("addPro").putString(
                            "value_editTextProductSpecSecond",
                            productInfoList.spec_desc_2.get(0)
                        )

                        var mutableSet_spec_dec_1_items: MutableSet<String> =
                            productInfoList.spec_dec_1_items.toMutableSet()
                        var mutableSet_spec_dec_2_items: MutableSet<String> =
                            productInfoList.spec_dec_2_items.toMutableSet()
                        var mutableList_spec_dec_1_items: MutableList<String> =
                            mutableSet_spec_dec_1_items.toMutableList()
                        var mutableList_spec_dec_2_items: MutableList<String> =
                            mutableSet_spec_dec_2_items.toMutableList()

                        MMKV.mmkvWithID("addPro").putString(
                            "datas_spec_size",
                            mutableSet_spec_dec_1_items.size.toString()
                        )
                        MMKV.mmkvWithID("addPro").putString(
                            "datas_size_size",
                            mutableSet_spec_dec_2_items.size.toString()
                        )

                        Thread(Runnable {

                            for (i in 0..mutableSet_spec_dec_1_items.size - 1) {
                                MMKV.mmkvWithID("addPro").putString(
                                    "datas_spec_item${i}",
                                    mutableList_spec_dec_1_items.get(i)
                                )
                            }

                        }).start()


                        Thread(Runnable {

                            for (i in 0..mutableSet_spec_dec_2_items.size - 1) {
                                MMKV.mmkvWithID("addPro").putString(
                                    "datas_size_item${i}",
                                    mutableList_spec_dec_2_items.get(i)
                                )
                            }

                            runOnUiThread {

                            }

                        }).start()


                    } else {
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


}
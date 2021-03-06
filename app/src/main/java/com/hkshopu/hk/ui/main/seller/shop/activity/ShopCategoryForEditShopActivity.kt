package com.HKSHOPU.hk.ui.main.seller.shop.activity


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventChangeShopCategory
import com.HKSHOPU.hk.component.EventShopCatSelected
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.databinding.ActivityShopcategoryBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.login.vm.ShopVModel
import com.HKSHOPU.hk.ui.main.seller.shop.adapter.CategoryMultiAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopCategoryForEditShopActivity : BaseActivity() {
    private lateinit var binding: ActivityShopcategoryBinding

    private val VM = ShopVModel()
    private val adapter = CategoryMultiAdapter()
    var toShopFunction: Boolean = false
    val choseListFiltered= ArrayList<ShopCategoryBean>()

    val shopId = MMKV.mmkvWithID("http").getString(
        "ShopId",
        ""
    ).toString()

    var url_defaultCat = ApiConstants.API_HOST + "/shop_category/index/"
    var url_shopCat = ApiConstants.API_HOST + "shop/" + shopId + "/show/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toShopFunction = intent.getBundleExtra("bundle")!!.getBoolean("toShopFunction")

        initView()
        initVM()
        initEditText()
        initClick()
    }

    private fun initVM() {
    }

    private fun initView() {
        getDefaultShopCategory(url_defaultCat)

        if (binding.tvSelected.text == "???????????????") {
            binding.tvSelected.isClickable = false
        }

    }

    private fun initRecyclerView() {

        //----------------------Default RecylcerView Datas----------------------

        for (i in 0 until CommonVariable.shopCategorySelectedListForEdit.size){
            for(j in 0 until  CommonVariable.shopCategoryListForEdit.size){
                if(CommonVariable.shopCategorySelectedListForEdit.get(i) == CommonVariable.shopCategoryListForEdit.get(j).id){
                    CommonVariable.shopCategoryListForEdit[j].isSelect = true
                }else{
                    continue
                }

            }
        }


        for(i in 0 until CommonVariable.shopCategoryListForEdit.size ){
            if( CommonVariable.shopCategoryListForEdit.get(i).isSelect.equals(true)){
                choseListFiltered.add(CommonVariable.shopCategoryListForEdit.get(i))
            }
        }

        if (choseListFiltered.isEmpty()) {
            binding.tvSelected.text = "???????????????"
            binding.tvSelected.setTextColor(getColor(R.color.turquoise))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_turquise)
        }else{
            val items = choseListFiltered.size
            binding.tvSelected.text = "?????????"+items+"?????????"
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        }
        if(choseListFiltered.size > 3){
            binding.tvSelected.isClickable = false
            runOnUiThread {
                Toast.makeText(this@ShopCategoryForEditShopActivity, "??????????????????3?????????", Toast.LENGTH_SHORT).show()
            }
        }else{
            binding.tvSelected.isClickable = true
        }
        if (choseListFiltered.size == 1) {
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        } else if (choseListFiltered.size == 2) {
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        } else if (choseListFiltered.size == 3) {
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        }
        //----------------------Default RecylcerView Datas----------------------


        val layoutManager = GridLayoutManager(this, 3)
        binding.recyclerview.layoutManager = layoutManager

        adapter.setData(CommonVariable.shopCategoryListForEdit)

        binding.recyclerview.adapter = adapter

        adapter.itemClick = {

//            Log.d("ShopCategoryActivity", "Item ID???" + id_cat)
//            Log.d("ShopCategoryActivity", "Item selected???" + it.isSelect)
            if (it.isSelect == true) {
                choseListFiltered.add(it)
            }
            if (it.isSelect == false) {
                choseListFiltered.remove(it)
            }

            if (choseListFiltered.isEmpty()) {
                binding.tvSelected.text = "???????????????"
                binding.tvSelected.setTextColor(getColor(R.color.turquoise))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_turquise)
            }else{
                val items = choseListFiltered.size
                binding.tvSelected.text = "?????????"+items+"?????????"
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            }
            if(choseListFiltered.size > 3){
                binding.tvSelected.isClickable = false
                runOnUiThread {
                    Toast.makeText(this@ShopCategoryForEditShopActivity, "??????????????????3?????????", Toast.LENGTH_SHORT).show()
                }
            }else{
                binding.tvSelected.isClickable = true
            }
            if (choseListFiltered.size == 1) {
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else if (choseListFiltered.size == 2) {
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else if (choseListFiltered.size == 3) {
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            }


        }
    }

    private fun initClick() {
        binding.titleBackShopcategory.setOnClickListener {
            finish()
        }
        binding.tvSelected.setOnClickListener {
            binding.progressBarShopCategory.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundShopCategory.visibility = View.VISIBLE

            if (toShopFunction) {
                var category_id_list: ArrayList<String> = arrayListOf()

                var shop_category_id1: String = ""
                var shop_category_id2: String = ""
                var shop_category_id3: String = ""
                if (choseListFiltered.size == 1) {

                    shop_category_id1 = choseListFiltered[0].id
                    category_id_list.add(shop_category_id1.toString())

                } else if (choseListFiltered.size == 2) {
                    shop_category_id1 = choseListFiltered[0].id
                    shop_category_id2 = choseListFiltered[1].id

                    category_id_list.add(shop_category_id1.toString())
                    category_id_list.add(shop_category_id2.toString())

                } else {
                    shop_category_id1 = choseListFiltered[0].id
                    shop_category_id2 = choseListFiltered[1].id
                    shop_category_id3 = choseListFiltered[2].id

                    category_id_list.add(shop_category_id1.toString())
                    category_id_list.add(shop_category_id2.toString())
                    category_id_list.add(shop_category_id3.toString())
                }

                RxBus.getInstance().post(EventChangeShopCategory(choseListFiltered))
                doShopCategoryUpdate(category_id_list)

            } else {
                RxBus.getInstance().post(EventShopCatSelected(choseListFiltered))
                binding.progressBarShopCategory.visibility = View.GONE
                binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
            }

            CommonVariable.shopCategoryListForEdit = adapter.getDatas()

            finish()
        }
    }

    private fun initEditText() {
//        binding.etShopname.addTextChangedListener(this)
//        password1.addTextChangedListener(this)
    }

    private fun doShopCategoryUpdate(list: ArrayList<String>) {

        Log.d("doShopCategoryUpdate", "???????????? list???" + list.toString())

        val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        var url = ApiConstants.API_PATH +"shop/"+ shopId + "/updateSelectedShopCategory/"
        Log.d("doShopCategoryUpdate", "???????????? Url???" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doShopCategoryUpdate", "???????????? resStr???" + resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("doShopCategoryUpdate", "???????????? ret_val???" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {

                        runOnUiThread {
                            Toast.makeText(this@ShopCategoryForEditShopActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }

                    } else {
                       runOnUiThread {

                            Toast.makeText(this@ShopCategoryForEditShopActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
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
        web.Do_ShopCategoryUpdate(url, list)
    }

    private fun getShopCategoryList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ShopInfoBean>()
                list.clear()
                val shop_category_id_list = ArrayList<String>()

                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("getShopCategorysList", "???????????? resStr???" + resStr)
                    Log.d("getShopCategorysList", "???????????? ret_val???" + json.get("ret_val"))

                    if (ret_val.equals("?????????????????????!")) {
                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("getShopCategorysList", "???????????? Object???" + jsonObject.toString())
                        val shopInfoBean: ShopInfoBean =
                            Gson().fromJson(jsonObject.toString(), ShopInfoBean::class.java)
                        list.add(shopInfoBean)
//
                        val translations: JSONArray = jsonObject.getJSONArray("shop_category_id")

                        for (i in 0 until translations.length()) {
                            val shop_category_id = translations.get(i)
                            if (!shop_category_id.equals(0)) {
                                shop_category_id_list.add(shop_category_id.toString().toString())
                                Log.d(
                                    "getShopCategorysList",
                                    "???????????? shop_category_id???" + shop_category_id.toString()
                                )
                            }
                        }

                        CommonVariable.shopCategorySelectedListForEdit = shop_category_id_list
                    }

                    runOnUiThread {
                        initRecyclerView()
                        binding.progressBarShopCategory.visibility = View.GONE
                        binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                    }

                } catch (e: JSONException) {
                    Log.d("getShopCategoryList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarShopCategory.visibility = View.GONE
                        binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getShopCategoryList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarShopCategory.visibility = View.GONE
                        binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getShopCategoryList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarShopCategory.visibility = View.GONE
                    binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }
    private fun getDefaultShopCategory(url: String) {
        binding.progressBarShopCategory.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundShopCategory.visibility = View.VISIBLE


        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var categoryList = arrayListOf<ShopCategoryBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("getDefaultShopCategory", "???????????? resStr???" + resStr)
                    Log.d("getDefaultShopCategory", "???????????? ret_val???" + json.get("ret_val"))

                    if (ret_val.equals("?????????????????????!")) {
                        val translations: JSONArray = json.getJSONArray("shop_category_list")
                        Log.d("getDefaultShopCategory", "???????????? List???" + translations.toString())
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopCategoryBean: ShopCategoryBean =
                                Gson().fromJson(jsonObject.toString(), ShopCategoryBean::class.java)

                            categoryList.add(shopCategoryBean)
                        }
                        CommonVariable.shopCategoryListForEdit = categoryList
                    }

                    getShopCategoryList(url_shopCat)

                } catch (e: JSONException) {
                    Log.d("getDefaultShopCategory_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarShopCategory.visibility = View.GONE
                        binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getDefaultShopCategory_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarShopCategory.visibility = View.GONE
                        binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getDefaultShopCategory_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarShopCategory.visibility = View.GONE
                    binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }
}
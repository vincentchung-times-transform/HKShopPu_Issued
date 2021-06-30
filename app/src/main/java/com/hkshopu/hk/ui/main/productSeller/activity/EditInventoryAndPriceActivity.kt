package com.HKSHOPU.hk.ui.main.productSeller.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventCheckInvenSpecEnableBtnOrNot
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityInventoryAndPriceBinding
import com.HKSHOPU.hk.ui.main.productSeller.adapter.InventoryAndPriceSpecAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.disable
import com.HKSHOPU.hk.widget.view.enable
import com.tencent.mmkv.MMKV

class EditInventoryAndPriceActivity : BaseActivity(), TextWatcher{

    private lateinit var binding : ActivityInventoryAndPriceBinding
    var hkd_dollarSign = ""


    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var mutableList_price = mutableListOf<Int>()
    var mutableList_quant = mutableListOf<Int>()
    var inven_price_range: String = ""
    var inven_quant_range: String = ""
    var mutableList_InvenDatas = mutableListOf<InventoryItemDatas>()

    val mAdapter = InventoryAndPriceSpecAdapter()
    var mutableList_Inventory = mutableListOf<ItemInventory>()

    var datas_spec_size: Int = 0
    var datas_size_size: Int = 0
    var datas_spec_title_first : String = ""
    var datas_spec_title_second : String = ""
    var datas_price_size: Int = 0
    var datas_quant_size: Int = 0



    var specGroup_only:Boolean = false


    //宣告頁面資料變數
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""
    var MMKV_inven_datas_size=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryAndPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hkd_dollarSign = getResources().getString(R.string.hkd_dollarSign)


        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

        initMMKV()
        initView()
    }

    fun initMMKV() {

        datas_spec_title_first = MMKV.mmkvWithID("editPro_temp").getString("value_editTextProductSpecFirst", "").toString()
        datas_spec_title_second = MMKV.mmkvWithID("editPro_temp").getString("value_editTextProductSpecSecond", "").toString()
        datas_spec_size = MMKV.mmkvWithID("editPro_temp").getString("datas_spec_size", "0").toString().toInt()
        datas_size_size = MMKV.mmkvWithID("editPro_temp").getString("datas_size_size", "0").toString().toInt()

        for(i in 0..datas_spec_size-1){
            var item_name = MMKV.mmkvWithID("editPro_temp").getString("datas_spec_item${i}", "")
            mutableList_spec.add(ItemSpecification(item_name.toString()))
        }


        for(i in 0..datas_size_size-1){
            var item_name = MMKV.mmkvWithID("editPro_temp").getString("datas_size_item${i}", "")
            mutableList_size.add(ItemSpecification(item_name.toString()))
        }

        datas_price_size = MMKV.mmkvWithID("editPro").getString(
            "datas_price_size",
            "0"
        ).toString().toInt()
        datas_quant_size = MMKV.mmkvWithID("editPro").getString(
            "datas_quant_size",
            "0"
        ).toString().toInt()

        for (i in 0..datas_price_size - 1) {
            var price_item = MMKV.mmkvWithID("editPro").getString("spec_price${i}", "0").toString().toInt()
            mutableList_price.add(price_item)
        }

        for (i in 0..datas_quant_size - 1) {
            var quant_item = MMKV.mmkvWithID("editPro").getString("spec_quantity${i}", "0").toString().toInt()
            mutableList_quant.add(quant_item)
        }

        var rebuild_datas = MMKV.mmkvWithID("editPro_temp").getBoolean("rebuild_datas", false)


        if(!datas_spec_title_first.equals("") && datas_spec_title_second.equals("") ){

            specGroup_only = true

            if(mutableList_spec.size== datas_price_size  &&  !rebuild_datas){

                for(i in 0..datas_spec_size-1){
                    mutableList_Inventory.add(ItemInventory(datas_spec_title_first, "", mutableList_spec.get(i).spec_name, "","", ""))

                }


                for(i in 0..datas_spec_size-1){
                    mutableList_Inventory.get(i).price = mutableList_price.get(i).toString()
                    mutableList_Inventory.get(i).quantity = mutableList_quant.get(i).toString()
                }


            }else{

                for(i in 0..datas_spec_size-1){
                    mutableList_Inventory.add(ItemInventory(datas_spec_title_first, "", mutableList_spec.get(i).spec_name, "","", ""))

                }
            }


        }else{
            specGroup_only = false

            if(mutableList_spec.size*mutableList_size.size  == datas_price_size &&mutableList_spec.size*mutableList_size.size   == datas_quant_size &&  rebuild_datas.equals(false)){


                for(i in 0..datas_spec_size-1){

                    for(j in 0..datas_size_size-1){

                        mutableList_Inventory.add(ItemInventory(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i).spec_name, mutableList_size.get(j).spec_name,"", ""))

                    }
                }

                for(i in 0..datas_spec_size*datas_size_size-1){
                    mutableList_Inventory.get(i).price = mutableList_price.get(i).toString()
                    mutableList_Inventory.get(i).quantity = mutableList_quant.get(i).toString()
                }




            }else{

                for(i in 0..datas_spec_size-1){

                    for(j in 0..datas_size_size-1){

                        mutableList_Inventory.add(ItemInventory(datas_spec_title_first, datas_spec_title_second, mutableList_spec.get(i).spec_name, mutableList_size.get(j).spec_name,"", ""))

                    }
                }
            }

        }



        binding.rViewInventory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rViewInventory.adapter = mAdapter

        mAdapter.updateList(mutableList_Inventory, specGroup_only, datas_size_size)


    }

    fun initView() {

        binding.titleInven.setText(R.string.title_editInventoryAndPrice)


        initEvent()
        initClick()


    }

    fun initClick() {
        binding.titleBackAddshop.setOnClickListener {
            MMKV.mmkvWithID("editPro_temp").putBoolean("get_temp", false)

            val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnInvenStore.setOnClickListener {


                MMKV.mmkvWithID("editPro").putString("datas_spec_size", mutableList_spec.size.toString())
                MMKV.mmkvWithID("editPro").putString("datas_size_size", mutableList_size.size.toString())
                MMKV.mmkvWithID("editPro").putString("value_editTextProductSpecFirst", datas_spec_title_first)
                MMKV.mmkvWithID("editPro").putString("value_editTextProductSpecSecond", datas_spec_title_second)


                for (i in 0..datas_spec_size - 1) {

                    MMKV.mmkvWithID("editPro")
                        .putString("datas_spec_item${i}", mutableList_spec.get(i).spec_name.toString())
                }

                for (i in 0..datas_size_size - 1) {

                    MMKV.mmkvWithID("editPro")
                        .putString("datas_size_item${i}", mutableList_size.get(i).spec_name.toString())

                }



            mutableList_Inventory = mAdapter.getDatas_invenSpec()

            for (i in 0..mutableList_Inventory.size-1){
                mutableList_InvenDatas.add(
                    InventoryItemDatas(
                        mutableList_Inventory.get(i).spec_desc_1,
                        mutableList_Inventory.get(i).spec_desc_2,
                        mutableList_Inventory.get(i).spec_dec_1_items,
                        mutableList_Inventory.get(i).spec_dec_2_items,
                        mutableList_Inventory.get(i).price.toInt(),
                        mutableList_Inventory.get(i).quantity.toInt()))
            }

            MMKV.mmkvWithID("editPro").putInt("inven_datas_size", mutableList_InvenDatas.size)

//            save_Price_Quant_Datas()

            val gson = Gson()
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonTutList_inven: String = gson.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutList_inven.toString())
            val jsonTutListPretty_inven: String = gsonPretty.toJson(mutableList_InvenDatas)
            Log.d("AddNewProductActivity", jsonTutListPretty_inven.toString())

            MMKV.mmkvWithID("editPro").putString("jsonTutList_inven", jsonTutList_inven)

            //MMKV放入mutableList_InvenDatas
            for(i in 0..mutableList_InvenDatas.size!!-1){

                val gson = Gson()
                val jsonTutList: String = gson.toJson(mutableList_InvenDatas.indexOf(i))

                MMKV.mmkvWithID("editPro").putString("value_inven${i}", jsonTutList)

            }

            //挑選最大與最小金額，回傳價格區間
            inven_price_range = inven_price_pick_max_and_min_num()
            inven_quant_range = inven_quant_pick_max_and_min_num()
            MMKV.mmkvWithID("editPro").putString("inven_price_range", inven_price_range)
            MMKV.mmkvWithID("editPro").putString("inven_quant_range", inven_quant_range)


            MMKV.mmkvWithID("editPro").putString(
                "datas_price_size",
                mutableList_InvenDatas.size.toString()
            )

            MMKV.mmkvWithID("editPro").putString(
                "datas_quant_size",
                mutableList_InvenDatas.size.toString()
            )


            for (i in 0..mutableList_InvenDatas.size - 1) {

                MMKV.mmkvWithID("editPro").putString(
                    "spec_price${i}",
                    mutableList_InvenDatas.get(i).price.toString()
                )
            }

            for (i in 0..mutableList_InvenDatas.size - 1) {
                MMKV.mmkvWithID("editPro").putString(
                    "spec_quantity${i}",
                    mutableList_InvenDatas.get(i).quantity.toString()
                )
            }


            MMKV.mmkvWithID("editPro_temp").clear()

            val intent = Intent(this, EditProductActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {

        MMKV.mmkvWithID("editPro_temp").putBoolean("get_temp", false)

        val intent = Intent(this, EditProductSpecificationMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun afterTextChanged(s: Editable?) {

    }



    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }



    //計算庫存"費用"最大最小範圍
    fun inven_price_pick_max_and_min_num(): String {

        var list : MutableList<Int> = mutableListOf()
        var min: Int = 0
        var max: Int = 0

        for(i in 0 until mutableList_InvenDatas.size){
            list.add(mutableList_InvenDatas.get(i).price)
        }
        min = list.min()!!
        max = list.max()!!

        return "${hkd_dollarSign}${min}-${hkd_dollarSign}${max}"
    }

    //計算庫存"數量"最大最小範圍
    fun inven_quant_pick_max_and_min_num(): String {

        var list : MutableList<Int> = mutableListOf()
        var min: Int = 0
        var max: Int = 0

        for(i in 0 until mutableList_InvenDatas.size){
            list.add(mutableList_InvenDatas.get(i).quantity)
        }
        min = list.min()!!
        max = list.max()!!


        return "${min}-${max}"

    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        var boolean: Boolean

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventCheckInvenSpecEnableBtnOrNot -> {

                        boolean = it.boolean

                        if(!boolean){
                            binding.btnInvenStore.disable()
                            binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)

                        }else{


                            var empty_count = 0

                            for(i in 0..mutableList_Inventory.size -1){

                                if(mutableList_Inventory.get(i).price.equals("")){
                                    empty_count+=1

                                }
                                if(mutableList_Inventory.get(i).quantity.equals("")){
                                    empty_count+=1

                                }

                            }


                            if(empty_count>0){

                                binding.btnInvenStore.disable()
                                binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_disable)

                            }else{
                                binding.btnInvenStore.enable()
                                binding.btnInvenStore.setImageResource(R.mipmap.btn_inven_store_enable)

                            }

                        }


                    }
                }
            }, {
                it.printStackTrace()
            })

    }
}
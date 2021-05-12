package com.hkshopu.hk.ui.main.store.activity


import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.CommonVariable
import com.hkshopu.hk.component.EventChangeShopCategory
import com.hkshopu.hk.component.EventShopCatSelected
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.ActivityShopcategoryBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.ui.main.store.adapter.CategoryMultiAdapter
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.utils.rxjava.RxBus


class ShopCategoryActivity : BaseActivity() {
    private lateinit var binding: ActivityShopcategoryBinding

    private val VM = ShopVModel()
    private val adapter = CategoryMultiAdapter()
    var toShopFunction:Boolean = false
    val list = ArrayList<ShopCategoryBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toShopFunction = intent.getBundleExtra("bundle")!!.getBoolean("toShopFunction")
        initView()
        initRecyclerView()
        initVM()
        initEditText()
        initClick()


    }

    private fun initVM() {

    }

    private fun initView() {
        if(binding.tvSelected.text =="未選擇分類") {
            binding.tvSelected.isClickable = false
        }

    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        binding.recyclerview.layoutManager = layoutManager
        adapter.setData(CommonVariable.list)
        binding.recyclerview.adapter = adapter
        adapter.itemClick = {

//            Log.d("ShopCategoryActivity", "Item ID：" + id_cat)
//            Log.d("ShopCategoryActivity", "Item selected：" + it.isSelect)
            if (it.isSelect == true && list.isEmpty()) {
                list.add(it)
            }
            if (it.isSelect == true && !list.isEmpty()) {

                for (x in list) {

                    if (x.id==it.id) {
                        break
                    }else{
                        list.add(it)
                    }
                }
            }
            if (it.isSelect == false && !list.isEmpty()) {

                for (x in list) {

                    if (x.id==it.id) {
                        list.remove(it)
                    }

                }
                if(list.isEmpty()){
                    binding.tvSelected.text = "未選擇分類"
                    binding.tvSelected.setTextColor(getColor(R.color.turquoise))
                    binding.tvSelected.setBackgroundResource(R.drawable.customborder_turquise)
                }
            }

            if (list.size == 1) {
                binding.tvSelected.text = "已選擇1項分類"
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else if (list.size == 2) {
                binding.tvSelected.text = "已選擇2項分類"
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else if (list.size == 3) {
                binding.tvSelected.text = "已選擇3項分類"
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

            if(toShopFunction){
                RxBus.getInstance().post(EventChangeShopCategory(list))
            }else {
                RxBus.getInstance().post(EventShopCatSelected(list))

            }
            finish()
        }

    }


    private fun initEditText() {
//        binding.etShopname.addTextChangedListener(this)
//        password1.addTextChangedListener(this)
    }



}
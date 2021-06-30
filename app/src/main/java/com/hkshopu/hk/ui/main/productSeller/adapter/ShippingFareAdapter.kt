package com.HKSHOPU.hk.ui.main.adapter

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventCheckShipmentEnableBtnOrNot
import com.HKSHOPU.hk.data.bean.ItemShippingFare
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import org.jetbrains.anko.singleLine
import java.util.*


class ShippingFareAdapter(var activity: Activity): RecyclerView.Adapter<ShippingFareAdapter.mViewHolder>(),
    ITHelperInterface {

    var mutableList_shipMethod = mutableListOf<ItemShippingFare>()
    var empty_item_num = 0
    private var editStatus: Boolean = false

    var MMKV_shop_id : String =MMKV.mmkvWithID("http").getString("ShopId", "").toString()

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //資料變數宣告
        lateinit var value_shipping_name : String
        var value_shipping_fare : String = ""
        var value_shipping_isChecked : String = "off"

        //把layout檔的元件們拉進來，指派給當地變數
        val editText_shipping_name = itemView.findViewById<EditText>(R.id.editText_value_shipping_name)
        val editText_shipping_fare = itemView.findViewById<EditText>(R.id.edtText_shipping_fare)
        val imgv_delFare = itemView.findViewById<ImageView>(R.id.imgView_deleteFare)
        val switch_view = itemView.findViewById<EasySwitcher>(R.id.mEasySwitcher)
        val textView_HKdolors = itemView.findViewById<TextView>(R.id.textView_HKdolors)


        init {


            Thread(Runnable {

                activity.runOnUiThread {
                    addEmptyItem()
                }

            }).start()

            editText_shipping_name.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus ){

                    RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(false))

                }
            }


            val textWatcher_editText_shipping_name = object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {


                    value_shipping_name = editText_shipping_name.text.toString()
                    value_shipping_fare = editText_shipping_fare.text.toString()

                    if(switch_view.isOpened()){
                        value_shipping_isChecked = "on"
                    }else{
                        value_shipping_isChecked = "off"
                    }

                    if(value_shipping_name.isNullOrEmpty()){

                        onItemUpdate(

                            "",
                            value_shipping_fare.toString(),
                            "off",
                            adapterPosition

                        )


                        RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(false))

                        Thread(Runnable {

                            activity.runOnUiThread {

                                delEmptyItem()
                            }

                        }).start()


                    }else {


                        if(value_shipping_name.equals(mutableList_shipMethod.get(adapterPosition).shipment_desc)){

                            onItemUpdate(

                                value_shipping_name,
                                value_shipping_fare.toString(),
                                value_shipping_isChecked,
                                adapterPosition

                            )

                            Thread(Runnable {

                                activity.runOnUiThread {
                                    addEmptyItem()
                                }

                            }).start()


                        }else{
                            //檢查名稱是否重複
                            var check_duplicate = 0

                            for (i in 0..mutableList_shipMethod.size - 1) {
                                if (value_shipping_name == mutableList_shipMethod[i].shipment_desc) {
                                    check_duplicate = check_duplicate + 1
                                } else {
                                    check_duplicate = check_duplicate + 0
                                }
                            }

                            if (check_duplicate > 0) {
                                editText_shipping_name.setText("")
                                Toast.makeText(itemView.context, "貨運商不可重複", Toast.LENGTH_SHORT).show()

                            } else {

                                onItemUpdate(
                                    value_shipping_name,
                                    value_shipping_fare.toString(),
                                    value_shipping_isChecked,
                                    adapterPosition
                                )

                                Thread(Runnable {

                                    activity.runOnUiThread {
                                        addEmptyItem()
                                    }

                                }).start()

                            }

                        }

                        RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))
                    }

                }
            }
            editText_shipping_name.addTextChangedListener(textWatcher_editText_shipping_name)

            val textWatcher_editText_shipping_fare = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {


                    if(editText_shipping_fare.text.toString().length >= 2 && editText_shipping_fare.text.toString().startsWith("0")){
                        editText_shipping_fare.setText(editText_shipping_fare.text.toString().replace("0", "", false))
                        editText_shipping_fare.setSelection(editText_shipping_fare.text.toString().length)
                    }

                    if(switch_view.isOpened()){

                        if(editText_shipping_fare.text.toString().isNullOrEmpty()){
                            textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                        }else{
                            textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                            editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                            editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                        }

                    }else{

                        textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                        editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                        editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))

                    }

                    value_shipping_name = editText_shipping_name.text.toString()
                    value_shipping_fare = editText_shipping_fare.text.toString()

                    if( switch_view.isOpened()){
                        value_shipping_isChecked = "on"
                    }else{
                        value_shipping_isChecked = "off"
                    }

                    if (value_shipping_fare == "") {
                        value_shipping_fare = ""

                        onItemUpdate(
                            value_shipping_name,
                            value_shipping_fare.toString(),
                            value_shipping_isChecked,
                            adapterPosition
                        )


                        Thread(Runnable {

                            activity.runOnUiThread {

                                delEmptyItem()

                            }

                        }).start()



                    } else {

                        onItemUpdate(
                            value_shipping_name,
                            value_shipping_fare.toString(),
                            value_shipping_isChecked,
                            adapterPosition
                        )


                        Thread(Runnable {

                            activity.runOnUiThread {
                                addEmptyItem()
                            }

                        }).start()

                    }

                    RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))

                }
            }
            editText_shipping_fare.addTextChangedListener(textWatcher_editText_shipping_fare)

            //editText_shipping_name編輯鍵盤監控
            editText_shipping_name.singleLine = true
            editText_shipping_name.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        editText_shipping_name.clearFocus()
                        editText_shipping_name.hideKeyboard()

                        true
                    }
                    else -> false
                }
            }


            editText_shipping_fare.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus ){
                    RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(false))

                }
            }
            //editText_shipping_fare編輯鍵盤監聽
            editText_shipping_fare.singleLine = true
            editText_shipping_fare.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        editText_shipping_fare.clearFocus()
                        editText_shipping_fare.hideKeyboard()

                        true
                    }
                    else -> false
                }
            }

            //item上的刪除按鈕設定
            imgv_delFare.setOnClickListener(this)

            //打開switch_view則增加空白item，關掉則刪除空白item
            switch_view.setOnStateChangedListener(object : EasySwitcher.SwitchStateChangedListener {
                override fun onStateChanged(isOpen: Boolean) {
                    if (isOpen) {

                        value_shipping_name = editText_shipping_name.text.toString()
                        value_shipping_fare = editText_shipping_fare.text.toString()

                        if (value_shipping_name == "") {
                            Toast.makeText(itemView.context, "請先填入自訂項目名稱", Toast.LENGTH_SHORT)
                                .show()
                            switch_view.closeSwitcher()
                        } else {

                            value_shipping_isChecked = "on"


                            onItemUpdate(
                                value_shipping_name,
                                value_shipping_fare.toString(),
                                value_shipping_isChecked,
                                adapterPosition
                            )


                            Thread(Runnable {
                                activity.runOnUiThread {
                                    addEmptyItem()
                                }
                            }).start()

                        }

                        RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))



                        if(switch_view.isOpened()){

                            if(editText_shipping_fare.text.toString().isNullOrEmpty()){
                                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            }else{
                                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                            }

                        }else{

                            textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))

                        }

                    } else {

                        value_shipping_name = editText_shipping_name.text.toString()
                        value_shipping_fare = editText_shipping_fare.text.toString()

                        value_shipping_isChecked = "off"

                        onItemUpdate(
                            value_shipping_name,
                            value_shipping_fare.toString(),
                            value_shipping_isChecked,
                            adapterPosition
                        )

                        Thread(Runnable {

                            activity.runOnUiThread {

                                delEmptyItem()
                            }

                        }).start()

                        RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))

                        if(switch_view.isOpened()){

                            if(editText_shipping_fare.text.toString().isNullOrEmpty()){
                                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            }else{
                                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                            }

                        }else{

                            textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                            editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))

                        }

                    }


                }
            })





        }

        fun bind(item: ItemShippingFare){

            //綁定當地變數與dataModel中的每個值
            imgv_delFare.setImageResource(R.mipmap.btn_delete_fare)
            editText_shipping_name.setText(item.shipment_desc)
            editText_shipping_fare.setText(item.price.toString())

            if(item.onoff == "on") {
                switch_view.openSwitcher()
                value_shipping_isChecked
            }else{
                switch_view.closeSwitcher()
            }

            if(item.onoff == "on"){

                if(editText_shipping_fare.text.toString().isNullOrEmpty()){
                    textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                    editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                    editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                }else{
                    textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                    editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                    editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                }

            }else{

                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.bright_gray))

            }

            var value_shipping_name_check = editText_shipping_name.text.toString()

            if (editStatus) {
                if (value_shipping_name_check.isNotEmpty()) {
                    imgv_delFare.visibility = View.VISIBLE
                }else{
                    imgv_delFare.visibility = View.GONE
                }
            } else {
                imgv_delFare.visibility = View.GONE
            }


        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.imgView_deleteFare -> onItemDissmiss(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.shipping_fare_list_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = mutableList_shipMethod.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(mutableList_shipMethod[position] as ItemShippingFare)

    }



    //新增空白項目
    fun addEmptyItem(){

        empty_item_num=0
        if(mutableList_shipMethod.size>0){

            for(i in 0..mutableList_shipMethod.size-1){
                if (mutableList_shipMethod[i].shipment_desc == ""){
                    empty_item_num += 1
                }else{
                    empty_item_num += 0
                }
            }

            if(empty_item_num == 0 ){
                mutableList_shipMethod.add(
                    ItemShippingFare(
                        "",
                        "",
                        "off",
                        MMKV_shop_id
                    )
                )

                try{
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                notifyDataSetChanged()
            }

        }else{
            mutableList_shipMethod.add(
                ItemShippingFare(
                    "",
                    "",
                    "off",
                    MMKV_shop_id
                )
            )
            try{
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            notifyDataSetChanged()
        }





//        storeStatus()

    }

    //刪除空白項目
    fun delEmptyItem(){

        empty_item_num=0


        for(i in 0..mutableList_shipMethod.size-1){
            if (mutableList_shipMethod[i].shipment_desc == "" && mutableList_shipMethod[i].price == ""){
                empty_item_num += 1
            }else{
                empty_item_num += 0
            }
        }

        if(empty_item_num>1){

            mutableList_shipMethod.remove(
                ItemShippingFare(
                    "",
                    "",
                    "off",
                    MMKV_shop_id
                )
            )

            try{
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            notifyDataSetChanged()
        }


//        storeStatus()

    }

    //更新資料用
    fun updateList(list: MutableList<ItemShippingFare>){
        mutableList_shipMethod = list

//        storeStatus()
    }


    fun onItemUpdate(update_txt: String, update_fare: String, is_checked: String, position: Int) {

        mutableList_shipMethod[position] = ItemShippingFare(
            update_txt,
            update_fare,
            is_checked,
            MMKV_shop_id
        )

    }




    override fun onItemDissmiss(position: Int) {
        mutableList_shipMethod.removeAt(position)
        notifyItemRemoved(position)

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_shipMethod, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    fun get_shipping_method_datas(): MutableList<ItemShippingFare> {
        return mutableList_shipMethod
    }

    //更新資料用
    fun onOff_editStatus(status: Boolean) {
        editStatus = status
        this.notifyDataSetChanged()
    }


    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}


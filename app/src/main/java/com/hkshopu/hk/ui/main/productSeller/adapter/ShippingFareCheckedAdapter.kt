package com.hkshopu.hk.ui.main.productSeller.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemShippingFare_Certained
import com.hkshopu.hk.ui.main.store.adapter.ITHelperInterface


class ShippingFareCheckedAdapter: RecyclerView.Adapter<ShippingFareCheckedAdapter.mViewHolder>(),
    ITHelperInterface {

    var mutableList_shipMethod: MutableList<ItemShippingFare_Certained> = mutableListOf()

    inner class mViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //把layout檔的元件們拉進來，指派給當地變數

        val editText_shipping_name = itemView.findViewById<TextView>(R.id.editText_value_shipping_name)
        val textView_HKdolors = itemView.findViewById<TextView>(R.id.textView_HKdolors)
        val textView_shipping_fare = itemView.findViewById<TextView>(R.id.textView_shipping_fare)


        init {


        }

        fun bind(item: ItemShippingFare_Certained) {
            Log.d("fdkodfkod", item.toString())
            //綁定當地變數與dataModel中的每個值
            editText_shipping_name.setText(item.shipment_desc)
            textView_shipping_fare.setText(item.price.toString())
            if(item.price.equals("0")){
                textView_shipping_fare.isVisible = false
                textView_HKdolors.isVisible =false
            }else{
                textView_shipping_fare.isVisible = true
                textView_HKdolors.isVisible = true
            }
        }

        override fun onClick(v: View?) {
            when (v?.id) {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.shipping_fare_list_item_addpro, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = mutableList_shipMethod.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(mutableList_shipMethod[position] )

    }

    //更新資料用
    fun updateList(list: MutableList<ItemShippingFare_Certained>) {

            mutableList_shipMethod =  list

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        TODO("Not yet implemented")
    }


    override fun onItemDissmiss(position: Int) {
        mutableList_shipMethod.removeAt(position)
        notifyItemRemoved(position)
    }

}



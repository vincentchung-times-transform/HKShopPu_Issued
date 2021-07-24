package com.HKSHOPU.hk.ui.main.seller.order.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerPendingDeliver_OrderDatailAdapter
import com.HKSHOPU.hk.utils.extension.load
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class SellerOrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitySellerOrderdetailBinding


    private val adapter = BuyerPendingDeliver_OrderDatailAdapter()
    var orderId =""
    var OrderNumberValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerOrderdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarSellerOrderDetail.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.VISIBLE

        var bundle = intent.getBundleExtra("bundle")
        orderId = bundle!!.getString("order_id").toString()


        initView()
        initClick()
        doGetData(orderId!!)
    }

    private fun initView() {
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.buttomForPendingDelever.setOnClickListener {
            val intent = Intent(this, ShippingNotificationActivity::class.java)
            var bundle = Bundle()
            bundle.putString("order_id", orderId)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun initRecyclerView(){
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
    }

    private fun doGetData(order_id: String) {

        Log.d("doGetSaleOrderDetail", "order_id: ${order_id.toString()}")
        var url = ApiConstants.API_HOST + "user/"+order_id +"/sale_order_detail/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list_product = ArrayList<OrderProductBean>()
                list_product.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("doGetSaleOrderDetail", "返回資料 resStr：" + resStr)
                    Log.d("doGetSaleOrderDetail", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("doGetSaleOrderDetail", "返回資料 jsonObject：" + jsonObject.toString())

                        val state = jsonObject.getString("status")

                        val myOrderBean: MyOrderBean =
                            Gson().fromJson(jsonObject.toString(), MyOrderBean::class.java)
                        val jsonArray_product: JSONArray = jsonObject.getJSONArray("productList")
                        for (i in 0 until jsonArray_product.length()) {
                            val jsonObject_product: JSONObject = jsonArray_product.getJSONObject(i)
                            val orderProductBean: OrderProductBean =
                                Gson().fromJson(jsonObject_product.toString(), OrderProductBean::class.java)
                            list_product.add(orderProductBean)
                        }

                        //        待發貨 - Pending Delivery
                        //        待收貨 - Pending Good Receive
                        //        訂單已完成 - Completed
                        //        已取消 - Cancelled
                        //        退貨/退款 - Refunded
                        if(state.equals("Pending Delivery")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.tobedelivered))
                                binding.tvShipnumber.visibility = View.VISIBLE
                                binding.tvNumber.visibility = View.GONE

                                binding.buttomArea.visibility = View.VISIBLE
                                binding.buttomForPendingDelever.visibility = View.VISIBLE
                                binding.buttomForPendingReceive.visibility = View.GONE
                                binding.buttomForOrderCompleted.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE

                                binding.layoutOrderNumber.visibility = View.GONE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.GONE
                                binding.layoutExpectedArrivalTime.visibility = View.GONE
                                binding.layoutCompleteTime.visibility = View.GONE
                                binding.layoutCancelTime.visibility = View.GONE

                            }
                        }else if(state.equals("Pending Good Receive")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.tobereceived))

                                binding.tvShipnumber.visibility = View.VISIBLE
                                binding.tvNumber.visibility = View.VISIBLE

                                binding.buttomArea.visibility = View.GONE
                                binding.buttomForPendingDelever.visibility = View.GONE
                                binding.buttomForPendingReceive.visibility = View.VISIBLE
                                binding.buttomForOrderCompleted.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE

                                binding.layoutOrderNumber.visibility = View.VISIBLE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.VISIBLE
                                binding.layoutExpectedArrivalTime.visibility = View.VISIBLE
                                binding.layoutCompleteTime.visibility = View.GONE
                                binding.layoutCancelTime.visibility = View.GONE
                            }
                        }else if(state.equals("Completed")){
                            runOnUiThread {
                                binding.tvStatus2.setText(getText(R.string.sales_completed))

                                binding.tvShipnumber.visibility = View.VISIBLE
                                binding.tvNumber.visibility = View.VISIBLE

                                binding.buttomArea.visibility = View.VISIBLE
                                binding.buttomForPendingDelever.visibility = View.GONE
                                binding.buttomForPendingReceive.visibility = View.GONE
                                binding.buttomForOrderCompleted.visibility = View.VISIBLE
                                binding.btnReviewsPublishColorful.visibility = View.VISIBLE
                                binding.btnReviewsViewing.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.GONE

                                binding.layoutOrderNumber.visibility = View.VISIBLE
                                binding.layoutPaidTime.visibility = View.VISIBLE
                                binding.layoutDeliveryTime.visibility = View.VISIBLE
                                binding.layoutExpectedArrivalTime.visibility = View.VISIBLE
                                binding.layoutCompleteTime.visibility = View.VISIBLE
                                binding.layoutCancelTime.visibility = View.GONE
                            }
                        }else if(state.equals("Cancelled")){
                            runOnUiThread {

                                binding.buttomArea.visibility = View.VISIBLE
                                binding.buttomForPendingDelever.visibility = View.GONE
                                binding.buttomForPendingReceive.visibility = View.GONE
                                binding.buttomForOrderCompleted.visibility = View.GONE
                                binding.buttomForOrderCanceled.visibility = View.VISIBLE

                                binding.layoutOrderNumber.visibility = View.GONE
                                binding.layoutPaidTime.visibility = View.GONE
                                binding.layoutDeliveryTime.visibility = View.GONE
                                binding.layoutExpectedArrivalTime.visibility = View.GONE
                                binding.layoutCompleteTime.visibility = View.GONE
                                binding.layoutCancelTime.visibility = View.VISIBLE
                            }
                        }

                        adapter.setData(list_product)
                        runOnUiThread {
                            binding.tvStatus.setText(myOrderBean.shop_message_title)
                            binding.tvReceive.setText(myOrderBean.shop_message_content)

                            binding.tvLogistic.text = myOrderBean.shipment_info
                            binding.tvNumber.setText("")

                            binding.tvBuyername.text = myOrderBean.name_in_address
                            binding.tvBuyerphone.text = myOrderBean.phone
                            binding.tvBuyeraddress.text = myOrderBean.full_address

                            binding.ivStore.load(myOrderBean.shop_icon)
                            binding.tvStoreName.text = myOrderBean.shop_title
                            binding.tvSubtotal.setText("HKD$ ${myOrderBean.subtotal.toString()}")
                            binding.tvShippingFee.setText("HKD$ ${myOrderBean.shipment_price.toString()}")
                            val total_amount= myOrderBean.subtotal + myOrderBean.shipment_price
                            binding.tvTotal.setText("HKD$ ${total_amount.toString().toString()}")

                            binding.tvOrderNumberValue.text = myOrderBean.order_number
                            OrderNumberValue = myOrderBean.order_number
                            binding.tvPaytime.text = myOrderBean.pay_time
                            binding.tvDeliveryTimeValue.setText(myOrderBean.actual_deliver_at)
                            binding.tvExpectedArrivalTimeValue.setText(myOrderBean.estimated_deliver_at)
                            binding.tvCompleteTimeValue.setText(myOrderBean.actual_finished_at)
                            binding.progressBarSellerOrderDetail.visibility = View.GONE
                            binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE


                            initRecyclerView()
                        }


                    }else{
                        runOnUiThread {
                            binding.progressBarSellerOrderDetail.visibility = View.GONE
                            binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doGetSaleOrderDetail_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarSellerOrderDetail.visibility = View.GONE
                        binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doGetSaleOrderDetail_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarSellerOrderDetail.visibility = View.GONE
                        binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doGetSaleOrderDetail_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarSellerOrderDetail.visibility = View.GONE
                    binding.imgViewLoadingBackgroundSellerOrderDetail.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }
}
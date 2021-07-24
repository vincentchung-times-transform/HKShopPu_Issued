package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BuyerOrderDetailBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.activity.BuyerPurchaseList_recieveActivity
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerPendingRecieveAdapter
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList

class BuyerPendingRecieveFragment : Fragment() {

    companion object {
        fun newInstance(): BuyerPendingRecieveFragment {
            val args = Bundle()
            val fragment = BuyerPendingRecieveFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar

    private val adapter = BuyerPendingRecieveAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pending_recieve_buyer, container, false)

        allProduct = v.find<RecyclerView>(R.id.recyclerview)
        progressBar = v.find<ProgressBar>(R.id.progressBar_pendingReceiveBuyer)
        progressBar.isVisible = true
        val userId= MMKV.mmkvWithID("http").getString("UserId", "");
        val url = ApiConstants.API_HOST+"user_detail/shopping_list/"
        val status = "Pending Good Receive"
        getProduct(url,userId!!,status)
        initView()

        return v
    }

    private fun initView(){

    }


    private fun initRecyclerView(){


        val layoutManager = LinearLayoutManager(requireActivity())
        allProduct.layoutManager = layoutManager

        allProduct.adapter = adapter
//        adapter.intentClick = {
//        }

    }

    private fun getProduct(url: String,userId:String,status:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerOrderDetailBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerPendingRecieveFragment", "返回資料 resStr：" + resStr)
                    Log.d("BuyerPendingRecieveFragment", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("BuyerPendingRecieveFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val buyerOrderDetailBean: BuyerOrderDetailBean =
                                Gson().fromJson(jsonObject.toString(), BuyerOrderDetailBean::class.java)
                            list.add(buyerOrderDetailBean)
                        }

                    }

                    if(list.size > 0){
                        adapter.setData(list)
                        activity!!.runOnUiThread {
                            initRecyclerView()
                            progressBar.isVisible = false
                        }
                    }else{
                        activity!!.runOnUiThread {
                            progressBar.isVisible = false
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("BuyerPendingRecieveFragment_errorMessage", "JSONException：" + e.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("BuyerPendingRecieveFragment_errorMessage", "IOException：" + e.toString())
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("BuyerPendingRecieveFragment_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
            }
        })
        web.Do_GetOrderList(url,userId,status)
    }


}
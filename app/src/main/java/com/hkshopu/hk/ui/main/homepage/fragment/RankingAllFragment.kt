package com.HKSHOPU.hk.ui.main.homepage.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ProductShopPreviewBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.adapter.ProductShopPreviewAdapter
import com.HKSHOPU.hk.ui.main.buyer.product.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopPreviewActivity
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class RankingAllFragment : Fragment() {

    companion object {
        fun newInstance(): RankingAllFragment {
            val args = Bundle()
            val fragment = RankingAllFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var layout_empty_result: LinearLayout
    lateinit var layout_refresh_request: LinearLayout
    lateinit var btn_refresh: ImageView
    lateinit var refreshLayout: SmartRefreshLayout
    lateinit var allProduct :RecyclerView
    lateinit var progressBar: ProgressBar
    var defaultLocale = Locale.getDefault()
    var currency: Currency = Currency.getInstance(defaultLocale)
    var userId = MMKV.mmkvWithID("http").getString("UserId", "")
    private val adapter = ProductShopPreviewAdapter(currency, userId!!)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ranking_all, container, false)
        val activity: ShopPreviewActivity? = activity as ShopPreviewActivity?
        val shopId: String? = activity!!.getShopId()
        val userId: String? = activity!!.getUserId()
        Log.d("RankingAllFragment", "返回資料 shopId：" + shopId)

        allProduct = v.find<RecyclerView>(R.id.recyclerview_rankall)
        progressBar = v.find<ProgressBar>(R.id.progressBar_product_all)
        progressBar.visibility = View.VISIBLE
        layout_empty_result = v.find(R.id.layout_empty_result)
        layout_empty_result.visibility = View.GONE
        layout_refresh_request = v.find(R.id.layout_refresh_request)
        layout_refresh_request.visibility = View.GONE
        refreshLayout = v.find<SmartRefreshLayout>(R.id.refreshLayout)
        refreshLayout.visibility = View.VISIBLE
        btn_refresh =  v.find<ImageView>(R.id.btn_refresh)
        btn_refresh.setOnClickListener {
            getProductOverAll(userId!!, shopId!!)
        }

        getProductOverAll(userId!!, shopId!!)
        initView()

        return v
    }

    private fun initView(){
    }
    private fun initRecyclerView(){

        val layoutManager = GridLayoutManager(requireActivity(),2)
        allProduct.layoutManager = layoutManager

        allProduct.adapter = adapter
//        adapter.itemClick = {
//
//            val intent = Intent(requireActivity(), ProductDetailedPageBuyerViewActivity::class.java)
//            var bundle = Bundle()
//            bundle.putString("product_id", it)
//            intent.putExtra("bundle_product_id", bundle)
//            requireActivity().startActivity(intent)
//        }

    }

    private fun getProductOverAll(userId:String, shopId:String) {
        val url = ApiConstants.API_HOST+"/product/"+shopId+"/"+"overall"+"/shop_product_analytics/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<ProductShopPreviewBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("RankingAllFragment", "返回資料 resStr：" + resStr)
                    Log.d("RankingAllFragment", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("已取得商品清單!")) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("RankingAllFragment", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val productShopPreviewBean: ProductShopPreviewBean =
                                Gson().fromJson(jsonObject.toString(), ProductShopPreviewBean::class.java)
                            list.add(productShopPreviewBean)
                        }
                    }

                    Log.d("RankingAllFragment", "返回資料 list：" + list.toString())

                    if(list.size > 0){
                        adapter.setData(list)
                        activity!!.runOnUiThread {
                            initRecyclerView()
                            progressBar.visibility = View.GONE
                            layout_empty_result.visibility = View.GONE
                            layout_refresh_request.visibility = View.GONE
                            refreshLayout.visibility = View.VISIBLE
                        }
                    }else{
                        activity!!.runOnUiThread {
                            progressBar.visibility = View.GONE
                            layout_empty_result.visibility = View.VISIBLE
                            layout_refresh_request.visibility = View.GONE
                            refreshLayout.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("errormessage", "getStoreOverAll: JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                        layout_empty_result.visibility = View.GONE
                        layout_refresh_request.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getStoreOverAll: IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                        layout_empty_result.visibility = View.GONE
                        layout_refresh_request.visibility = View.VISIBLE
                        refreshLayout.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getStoreOverAll: ErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    progressBar.visibility = View.GONE
                    layout_empty_result.visibility = View.GONE
                    layout_refresh_request.visibility = View.VISIBLE
                    refreshLayout.visibility = View.GONE
                }
            }
        })
        web.Do_GetShopProduct(url,userId)
    }
}
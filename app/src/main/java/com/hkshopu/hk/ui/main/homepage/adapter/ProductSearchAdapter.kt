package com.HKSHOPU.hk.ui.main.homepage.adapter


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.data.bean.ProductLikedBean
import com.HKSHOPU.hk.data.bean.ProductSearchBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.product.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.click
import com.google.gson.Gson
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class ProductSearchAdapter (var currency: Currency, var user_id: String): RecyclerView.Adapter<ProductSearchAdapter.TopProductLinearHolder>(){
    private var mData: ArrayList<ProductSearchBean> = ArrayList()
//    private var newData: ArrayList<ProductSearchBean> = ArrayList()
//    var itemClick : ((id: String) -> Unit)? = null
//    var likeClick : ((id: String,like:String) -> Unit)? = null
    private var like_inner = ""

    fun setData(list : ArrayList<ProductSearchBean>){
        list?:return
        mData = list
        notifyDataSetChanged()
    }
    fun add(list: ArrayList<ProductSearchBean>) {
        list?:return
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductLinearHolder {
        val v = parent.context.inflate(R.layout.item_top_products,parent,false)

        return TopProductLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    //更新資料用
    fun updateData(like: String){
        like_inner =like
        this.notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }


    fun clear() {
        val size = mData.size
        mData.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onBindViewHolder(holder: TopProductLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    inner class TopProductLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val container = itemView.find<RelativeLayout>(R.id.layout_product)
        val container = itemView.find<RelativeLayout>(R.id.container)
        val cardvView_product = itemView.find<CardView>(R.id.cardvView_product)
        val image = itemView.find<ImageView>(R.id.iv_product)
        val like = itemView.find<ImageView>(R.id.iv_product_like)
//        val like_click = itemView.find<ImageView>(R.id.iv_product_like_click)
        val title = itemView.find<TextView>(R.id.tv_productname)
        val shopname = itemView.find<TextView>(R.id.tv_shopname)
        val price = itemView.find<TextView>(R.id.tv_price)
        var liked_status = "N"

        fun bindShop(productSearchBean : ProductSearchBean){

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailedPageBuyerViewActivity::class.java)
                var bundle = Bundle()
                bundle.putString("product_id", productSearchBean.product_id)
                intent.putExtra("bundle_product_id", bundle)
                itemView.context.startActivity(intent)

            }

            image.loadNovelCover(productSearchBean.pic_path)
            title .text = productSearchBean.product_title
            shopname.text = productSearchBean.shop_title
            price.text = currency.toString()+ productSearchBean.min_price.toString() + "-"+productSearchBean.max_price.toString()

            liked_status = productSearchBean.liked

            if(liked_status.equals("Y")){
                like.setImageResource(R.mipmap.ic_heart_red)
            }else{
                like.setImageResource(R.mipmap.ic_heart_white)
            }
            like.click {
                if (user_id!!.isNullOrEmpty()) {
                    val intent = Intent(itemView.context, OnBoardActivity::class.java)
                    itemView.context.startActivity(intent)
                }else{
                    if(liked_status.equals("Y")){
                        doProductLike(user_id, productSearchBean.product_id, "N")
                        //                    likeClick?.invoke(productSearchBean.product_id,"N")

                    }else{
                        doProductLike(user_id, productSearchBean.product_id, "Y")
//                        likeClick?.invoke(productSearchBean.product_id,"Y")
                    }
                }
            }

            val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
            val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

            if(width.equals(1080)){

                val params_container: ViewGroup.LayoutParams = container.getLayoutParams()
                var width_scaling =  (width*168)/375

                params_container.width = width_scaling
                container.setLayoutParams(params_container)

                val params_layout_product: ViewGroup.LayoutParams = cardvView_product.getLayoutParams()
                params_layout_product.width = width_scaling
                params_layout_product.height = (width_scaling*18)/21
                cardvView_product.setLayoutParams(params_layout_product)
            }
        }
        private fun doProductLike(userId: String, productId: String, like_status: String) {
            Log.d("doProductLike", "userId: ${userId.toString()}")
            val url = ApiConstants.API_HOST + "product/like_product/"
            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {
                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        val ret_val = json.get("ret_val")
                        val status = json.get("status")
                        Log.d("doProductLike", "返回資料 resStr：" + resStr)
                        Log.d("doProductLike", "返回資料 ret_val：" + ret_val)

                        if (ret_val.equals("商品收藏成功!")) {
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                                like.setImageResource(R.mipmap.ic_heart_red)
                                liked_status = "Y"
                            }
                            RxBus.getInstance().post(EventRefreshUserInfo())
                        } else if(ret_val.equals("取消收藏成功")){
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                                like.setImageResource(R.mipmap.ic_heart_white)
                                liked_status = "N"
                            }
                            RxBus.getInstance().post(EventRefreshUserInfo())
                        }else{
                            runOnUiThread {
                                Toast.makeText(
                                    itemView.context, ret_val.toString(), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        Log.d("doProductLike_errorMessage", "JSONException：" + e.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("doProductLike_errorMessage", "IOException：" + e.toString())
                    }
                }
                override fun onErrorResponse(ErrorResponse: IOException?) {
                    Log.d("doProductLike_errorMessage", "ErrorResponse：" + ErrorResponse.toString())
                }
            })
            web.Product_Like(url, productId, userId, like_status)
        }
    }



}
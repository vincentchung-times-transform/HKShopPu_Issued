package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopProductBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("product_category_id")
    var product_category_id: String = ""

    @SerializedName("product_title")
    var product_title: String= ""

    @SerializedName("quantity")
    var quantity: Int = 0

    @SerializedName("product_description")
    var product_description: String= ""

    @SerializedName("product_price")
    var product_price: Int = 0

    @SerializedName("shipping_fee")
    var shipping_fee: Int = 0

    @SerializedName("weight")
    var weight: Int = 0

    @SerializedName("longterm_stock_up")
    var longterm_stock_up: String= ""

    @SerializedName("new_secondhand")
    var new_secondhand: String= ""

    @SerializedName("length")
    var length: Int = 0

    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("like")
    var like: Int = 0

    @SerializedName("seen")
    var seen: Int = 0

    @SerializedName("sold_quantity")
    var sold_quantity: Int = 0

    @SerializedName("pic_path")
    var pic_path: String= ""

    @SerializedName("min_price")
    var min_price: Int = 0

    @SerializedName("max_price")
    var max_price: Int = 0

    @SerializedName("sum_quantity")
    var sum_quantity: Int = 0
}
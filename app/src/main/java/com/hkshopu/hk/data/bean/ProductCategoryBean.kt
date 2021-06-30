package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductCategoryBean {

    @SerializedName("id")
    var id: String = ""

    @SerializedName("c_product_category")
    var c_product_category: String = ""

    @SerializedName("e_product_category")
    var e_product_category: String= ""

    @SerializedName("unselected_product_category_icon")
    var unselected_product_category_icon: String= ""

    @SerializedName("selected_product_category_icon")
    var selected_product_category_icon: String= ""

    @SerializedName("product_category_background_color")
    var product_category_background_color: String= ""

    @SerializedName("product_category_seq")
    var product_category_seq: Int= 0


    @SerializedName("is_delete")
    var is_delete: String= ""

    @SerializedName("created_at")
    var created_at: String= ""

    @SerializedName("updated_at")
    var updated_at: String= ""

    var isSelect: Boolean = false

}
package com.HKSHOPU.hk.data.bean
import com.google.gson.annotations.SerializedName

class ShoppingCartProductItemNestedLayer {

    var product_checked: Boolean = false

    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    var shipmentList: MutableList<ShoppingCartProductShipmentItem> = mutableListOf()

    @SerializedName("selected_shipment")
    var selected_shipment: ShoppingCartProductShipmentItem = ShoppingCartProductShipmentItem()

    @SerializedName("product_spec")
    var product_spec: ShoppingCartProductItemNestedLayerProductSepcBean = ShoppingCartProductItemNestedLayerProductSepcBean()

}
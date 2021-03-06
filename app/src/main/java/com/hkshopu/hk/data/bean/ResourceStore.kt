package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.MyStoreFragment
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.ShopFunctionFragment

interface ResourceStore {
    companion object {
        val tabList = listOf(
            R.string.tab1, R.string.tab2
        )
        val pagerFragments = listOf(
            ShopFunctionFragment.newInstance(), MyStoreFragment.newInstance()
        )
    }
}
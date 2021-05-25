package com.hkshopu.hk.ui.main.store.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.*
import com.tencent.mmkv.MMKV

//import kotlinx.android.synthetic.main.activity_main.*

class AccountSetupActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountsetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {


        binding.containerLogout.setOnClickListener {

            var settings_rememberEmail = this.getSharedPreferences("rememberEmail", 0)
            var settings_rememberPassword = getSharedPreferences("rememberPassword", 0)
            var settings_rememberMe = this.getSharedPreferences("rememberMe", 0)

            val editor_email : SharedPreferences.Editor = settings_rememberEmail.edit()
            editor_email.apply {
                putString("rememberEmail", "false")
            }.apply()

            settings_rememberPassword = getSharedPreferences("rememberPassword", 0)

            val editor_password : SharedPreferences.Editor = settings_rememberPassword.edit()
            editor_password.apply {
                putString("rememberPassword", "false")
            }.apply()

            val sharedPreferences : SharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply {
                putString("rememberMe", "false")
            }.apply()

            MMKV.mmkvWithID("http")
                .putInt("UserId", 0)
                .putString("Email","")
                .putString("Password","")

            val intent = Intent(this, OnBoardActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivChevronShopInfo.setOnClickListener {
            val intent = Intent(this, ShopInfoModifyActivity::class.java)
            startActivity(intent)
        }
        binding.ivChevronShopAddress.setOnClickListener {
            val intent = Intent(this, ShopAddressListActivity::class.java)
            startActivity(intent)
        }
        binding.ivChevronShopBankaccount.setOnClickListener {
            val intent = Intent(this, BankListActivity::class.java)
            startActivity(intent)
        }
        binding.ivChevronSetupNotify.setOnClickListener {

        }
        binding.ivChevronSetupLan.setOnClickListener {

        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }


}
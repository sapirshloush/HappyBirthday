package com.sapir.happybirthday.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.sapir.happybirthday.databinding.ActivityMainBinding
import com.sapir.happybirthday.model.BabyUser
import com.sapir.happybirthday.util.Utilities
import com.sapir.happybirthday.viewModels.DataStoreViewModel
import com.sapir.happybirthday.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imagePath: String
    private val mainViewModel: MainViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private lateinit var babyUser: BabyUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIfUserHasSavedDetails()

        handleClick()
        makeButtonNotClickableAtFirst()
    }

    private fun handleClick(){
        binding.showBirthDayScreen.setOnClickListener {
            setBabyUser()
        }

        binding.chooseImage.setOnClickListener {
            ImagePicker.with(this).start()
        }
    }

    private fun setBabyUser() {
        val name = binding.userName.text.toString()
        val image = imagePath
        val date = Utilities.getDate(birthDayPicker.year,
            birthDayPicker.month, birthDayPicker.dayOfMonth)
        val age = Utilities.getBabyAge(birthDayPicker.year,
            birthDayPicker.month + 1,birthDayPicker.dayOfMonth)
        val babyUser = BabyUser(1, name, age.second, date, image, age.first)
        mainViewModel.insertBabyUserDetails(babyUser)
        mainViewModel.response.observe(this) {
            dataStoreViewModel.setSavedKey(true)
            val intent = Intent(this, BirthDayActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkIfUserHasSavedDetails(){
        dataStoreViewModel.savedKey.observe(this){
            if (it == true) {
                this.lifecycleScope.launch {
                    mainViewModel.getBabyUserDetails()
                    mainViewModel.babyUserDetails.collect { babyUsers->
                        for(baby in babyUsers) {
                            binding.userName.setText(baby.name)
                            binding.userImage.setImageURI(Uri.parse(baby.imagePath))
                            if(baby.birthday!= null) {
                                binding.birthDayPicker.updateDate(
                                    Utilities.getYear(baby.birthday),
                                    Utilities.getMonth(baby.birthday), Utilities.getDay(baby.birthday)
                                )
                            }

                        }
                    }
                }
            }
            else {
                initViews()
            }

        }
    }

    private fun initViews(){
        handleClick()
    }

    private fun makeButtonNotClickableAtFirst() {
        showBirthDayScreen.visibility = View.INVISIBLE
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val nameEt = binding.userName.text.toString()
                if (nameEt.isEmpty()) {
                    showBirthDayScreen.visibility = View.INVISIBLE
                } else {
                    showBirthDayScreen.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable) {

            }
        }

        binding.userName.addTextChangedListener(watcher)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            userImage.setImageURI(data.data)
            imagePath = data.data.toString()
        }
    }
}
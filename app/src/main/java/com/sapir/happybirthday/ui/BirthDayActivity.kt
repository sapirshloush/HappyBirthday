package com.sapir.happybirthday.ui

import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sapir.happybirthday.R
import com.sapir.happybirthday.model.BabyUser
import com.sapir.happybirthday.util.Utilities.isStoragePermissionGranted
import com.sapir.happybirthday.viewModels.DataStoreViewModel
import com.sapir.happybirthday.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.age_text_layout.*
import kotlinx.android.synthetic.main.arrow_back_btn_layout.*
import kotlinx.android.synthetic.main.baby_image_layout.*
import kotlinx.android.synthetic.main.birthday_layout.*
import kotlinx.android.synthetic.main.camera_icon_layout.*
import kotlinx.android.synthetic.main.share_btn_layout.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BirthDayActivity : AppCompatActivity() {

    private val babyUserViewModel: MainViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private lateinit var babyUser : BabyUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_birth_day)
        getBabyUserDetails()
        changeScreenColorRandomly()
        initClickListeners()

    }

    private fun initClickListeners() {
        shareBtn.setOnClickListener { isStoragePermissionGranted(this) }
        back_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        camera_icon_button.setOnClickListener { uploadImageResource() }
    }

    private fun uploadImageResource() {
        babyUserViewModel.uploadImageResource(this)
    }

    private fun getBabyUserDetails() {
        this.lifecycleScope.launch {
            babyUserViewModel.getBabyUserDetails()
            babyUserViewModel.babyUserDetails.collect { babyUsers->
                for(baby in babyUsers) {
                    updateUI(baby)
                }
            }
        }
    }

    private fun changeScreenColorRandomly() {
        val backgroundImages = resources.obtainTypedArray(R.array.loading_background_images)
        val babyImages = resources.obtainTypedArray(R.array.loading_baby_images)
        val appBackground = resources.obtainTypedArray(R.array.loading_background_color)
        val cameraIcons = resources.obtainTypedArray(R.array.loading_camera_icon_images)
        val imageBorderColor = resources.obtainTypedArray(R.array.loading_border_color)
        val choice = (Math.random() * backgroundImages.length()).toInt()
        birthday_background.setBackgroundResource(
            backgroundImages.getResourceId(
                choice,
                R.drawable.i_os_bg_elephant
            )
        )
        baby_image.setImageResource(babyImages.getResourceId(choice, R.drawable.baby_image_blue))
        baby_image.borderColor = resources.getColor(
            imageBorderColor.getResourceId(
                choice,
                R.color.blue_image_stroke
            )
        )
        root_container.setBackgroundResource(
            appBackground.getResourceId(
                choice,
                R.color.blue_app_background
            )
        )
        camera_icon_button.setImageResource(cameraIcons.getResourceId(choice, R.drawable.camera_icon_blue))
        changeStatusBarColor(appBackground, choice)
        backgroundImages.recycle()
        babyImages.recycle()
        cameraIcons.recycle()
        imageBorderColor.recycle()
    }

    private fun changeStatusBarColor(color: TypedArray, choice: Int) {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(
            color.getResourceId(
                choice, R.color.blue_app_background
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        val permissionsList = ArrayList<String>()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults == null) {
            shareScreenLayout()
            return
        }
        for (i in grantResults.indices) {
            if (grantResults[i] == 0) {
                permissionsList.add(permissions[i])
            }
        }
        if (permissionsList.contains("android.permission.READ_EXTERNAL_STORAGE")) {
            shareScreenLayout()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val uri = data.data
            baby_image.setImageURI(uri)
            babyUser.imagePath = uri.toString()
            babyUserViewModel.updateBabyUserDetails(babyUser)
        }
    }

    private fun shareScreenLayout() {
        back_button.setVisibility(View.INVISIBLE)
        shareBtn.setVisibility(View.INVISIBLE)
        camera_icon_button.setVisibility(View.INVISIBLE)
        val image: Bitmap = babyUserViewModel.getBitmapFromView(root_container)
        back_button.setVisibility(View.VISIBLE)
        shareBtn.setVisibility(View.VISIBLE)
        camera_icon_button.setVisibility(View.VISIBLE)
        babyUserViewModel.openShareDialog(this, image)
    }

    private fun updateUI(baby: BabyUser) {
        val backgroundImages = resources.obtainTypedArray(R.array.loading_numbers)

        babyName.text = baby.name?.let { String.format("today %s is ", it).toUpperCase() }
        baby_image.setImageURI(Uri.parse(baby.imagePath))
        age_in_words.text = String.format("%s old!", baby.ageType).toUpperCase()
        baby.age?.toInt()?.let {
            backgroundImages.getResourceId(
                it,
                R.drawable.number_0)
        }?.let { age_number_image_view.setImageResource(it) }

        backgroundImages.recycle()
    }

}
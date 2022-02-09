package com.sapir.happybirthday.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.with
import com.sapir.happybirthday.model.BabyUser
import com.sapir.happybirthday.repository.BabyUserRepository
import com.sapir.happybirthday.util.AgeCalculator
import com.sapir.happybirthday.util.Utilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.Period
import java.io.FileNotFoundException
import java.io.OutputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class MainViewModel  @Inject constructor(
    private val babyRepo: BabyUserRepository
): ViewModel() {

    private val MEDIA_PATH = "Pictures/Screen Layouts/"
    private val MIME_TYPE = "image/jpeg"

    /**
     * Insert baby user details
     */
    private val _response = MutableLiveData<Long>()
    val response: LiveData<Long> = _response

    //insert baby user details to room database
    fun insertBabyUserDetails(babyUser: BabyUser){
        viewModelScope.launch(Dispatchers.IO) {
            _response.postValue(babyRepo.createBabyUserRecord(babyUser))
        }
    }

    //insert baby user details to room database
    fun updateBabyUserDetails(babyUser: BabyUser){
        viewModelScope.launch(Dispatchers.IO) {
           babyRepo.updateBabyUserRecord(babyUser)
        }
    }

    /**
     * Retrieve baby user details
     */
    private val _babyUserDetails = MutableStateFlow<List<BabyUser>>(emptyList())
    val babyUserDetails : StateFlow<List<BabyUser>> =  _babyUserDetails

    fun getBabyUserDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            babyRepo.getBabyUserDetails
                .catch { e->
                    //Log error here
                }
                .collect {
                    _babyUserDetails.value = it
                }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getBabyAge(date: Date): Period {
        return AgeCalculator.getBabyAge(
            Utilities.getYear(date),
            Utilities.getMonth(date),
            Utilities.getDay(date)
        )
    }

    fun getBitmapFromView(view: View): Bitmap {
        val returnBitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnBitmap
    }

    fun openShareDialog(activity: Activity, image: Bitmap) {
        val imageUri: Uri? = saveImage(image, activity)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.type = "*/*"
        activity.startActivity(intent)
    }

    protected open fun saveImage(finalBitmap: Bitmap, activity: Activity): Uri? {
        val outputStream: OutputStream?
        var imageUri: Uri?
        val resolver = activity.contentResolver
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "screenLayout" + ".jpg")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, MEDIA_PATH)
        }
        try {
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri))
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return imageUri
    }

    open fun uploadImageResource(activity: Activity) {
        ImagePicker.with(activity).start()
    }
}
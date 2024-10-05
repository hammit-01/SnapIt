package com.example.snapit

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snapit.Datastore.UserDataClass
import com.example.snapit.Datastore.UserDataHolder
import com.example.snapit.Datastore.UserDataHolder.userData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Comment
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profile: ImageView
    private lateinit var back: ImageView

    companion object {
        private const val IMAGE_PICK_REQUEST_CODE = 1
    }

    private var selectedImageUri: Uri? = null
    private lateinit var currentImageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editprofile_layout)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userData = UserDataHolder.userData

        userData?.let {
            Glide.with(this)
                .load(it.profile)
                .into(findViewById(R.id.profile))
        }

        currentImageUrl = intent.getStringExtra("currentImageUrl") ?: ""
        val userUid = sharedPreferences.getString("userUid", null)

        findViewById<ImageView>(R.id.back).setOnClickListener { finish() }

        findViewById<Button>(R.id.imgEdit).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        findViewById<Button>(R.id.editprofile).setOnClickListener { editProfile(userUid) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(this).load(selectedImageUri).into(findViewById(R.id.profile))
        }
    }

    private fun editProfile(userUid: String?) {
        userUid?.let { uid ->
            deleteCurrentImageAndUploadNew(uid)
        } ?: Toast.makeText(this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
    }

    private fun deleteCurrentImageAndUploadNew(userUid: String) {
        if (currentImageUrl.isNotEmpty()) {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentImageUrl)
            storageRef.delete().addOnSuccessListener {
                Log.d("EditProfileActivity", "Existing image deleted successfully.")
                uploadNewImage(userUid)
            }.addOnFailureListener { exception ->
                Log.e("EditProfileActivity", "Error deleting existing image", exception)
                uploadNewImage(userUid) // 삭제 실패 시에도 새 이미지 업로드 시도
            }
        } else {
            uploadNewImage(userUid)
        }
    }

    private fun uploadNewImage(userUid: String) {
        val storageRef = FirebaseStorage.getInstance().getReference("profiles/$userUid/profile_image.jpg")
        selectedImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    Log.d("EditProfileActivity", "New image uploaded successfully.")
                    updateProfileData(userUid) // 새 이미지 업로드 후 데이터 업데이트
                }
                .addOnFailureListener { exception ->
                    Log.e("EditProfileActivity", "Error uploading new image", exception)
                }
        } ?: updateProfileData(userUid) // 선택된 이미지가 없는 경우
    }

    private fun updateProfileData(userUid: String) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users_data").document(userUid)

        val nickname = findViewById<EditText>(R.id.nickname).text.toString()
        var comment = findViewById<EditText>(R.id.comment).text.toString()

        val updates = mutableMapOf<String, Any?>() // 업데이트할 데이터를 담을 Map

        if (nickname.isNotEmpty()) {
            updates["nick_name"] = nickname
        }
        if (comment.isNotEmpty()) {
            updates["comment"] = comment
        }
        selectedImageUri?.let { updates["profile"] = it.toString() }

        if (updates.isNotEmpty()) {
            userDocRef.update(updates)
                .addOnSuccessListener {
                    UserDataHolder.userData?.apply {
                        nick_name = nickname
                        comment = comment
                        profile = selectedImageUri.toString()
                    }
                    Toast.makeText(this, "프로필이 업데이트되었습니다.", Toast.LENGTH_LONG).show()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "프로필 업데이트에 문제가 있습니다.", Toast.LENGTH_LONG).show()
                    Log.e("EditProfileActivity", "Error updating profile", exception)
                }
        } else {
            Toast.makeText(this, "수정할 정보가 없습니다.", Toast.LENGTH_LONG).show()
        }
    }
}
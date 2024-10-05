package com.example.snapit

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snapit.Adapter.ImageAdapter
import com.example.snapit.Datastore.UserDataClass
import com.example.snapit.Datastore.UserDataHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserActivity: AppCompatActivity() {
    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()

    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var profile: ImageView
    private lateinit var back: ImageView
    private lateinit var logout: Button
    private lateinit var editprofile: Button
    private lateinit var signout: TextView
    private lateinit var nickname: TextView
    private lateinit var bday: TextView
    private lateinit var comment: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_layout)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // UserDataClass 가져오기
        val userData = UserDataHolder.userData
        userData?.let {
            // userData를 사용하여 UI 업데이트 등의 작업 수행
            val nick_name = it.nick_name
            val b_day = it.b_day
            val comments = it.comment
            val profileimg = it.profile

            nickname = findViewById(R.id.nickname)
            bday = findViewById(R.id.bday)
            comment = findViewById(R.id.comment)
            profile = findViewById(R.id.profile)

            nickname.text = nick_name
            bday.text = b_day
            comment.text = comments

            // 이미지 설정
            Glide.with(this)
                .load(profileimg)
                .into(profile)
        }

        // 뒤로가기 버튼
        back = findViewById(R.id.back)
        back.setOnClickListener {
            if (isTaskRoot) {
                // 현재 액티비티가 스택에서 마지막 액티비티라면 앱을 종료하지 않고 홈으로
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                // 이전 액티비티가 있으면 뒤로가기 동작 실행
                finish()
            }
        }

        // 로그아웃 버튼
        logout = findViewById(R.id.logout)
        logout.setOnClickListener {
            auth.signOut() // Firebase에서 로그아웃
            val editor = sharedPreferences.edit()
            editor.remove("userUid") // 세션에서 UID 삭제
            editor.apply()
            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_LONG).show()
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 프로필 수정 버튼
        editprofile = findViewById(R.id.editprofile)
        editprofile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // 프로필 수정 버튼
        signout = findViewById(R.id.signout)
        signout.setOnClickListener {
            deleteAccount()
        }
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        val userUid = sharedPreferences.getString("userUid", null)

        if (user != null && userUid != null) {
            // Firestore와 Storage에서 사용자 데이터 삭제
            deleteFirestoreData(userUid)
        } else {
            Toast.makeText(this, "사용자 정보가 없습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteFirestoreData(userUid: String) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users_data").document(userUid)

        // Firestore 데이터 삭제
        userDocRef.delete().addOnSuccessListener {
            Log.d("EditProfileActivity", "Firestore data deleted successfully.")
            deleteStorageData(userUid)
        }.addOnFailureListener { exception ->
            Log.e("EditProfileActivity", "Error deleting Firestore data", exception)
            Toast.makeText(this, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteStorageData(userUid: String) {
        // Todo profiles만 삭제함 images랑 videos도 삭제하도록 시켜야함
        val storageRef = FirebaseStorage.getInstance().getReference("profiles/$userUid/")

        // Storage 데이터 삭제
        storageRef.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { fileRef ->
                fileRef.delete().addOnSuccessListener {
                    Log.d("EditProfileActivity", "Storage file deleted successfully.")
                }.addOnFailureListener { exception ->
                    Log.e("EditProfileActivity", "Error deleting file", exception)
                }
            }
            // Storage 데이터 삭제 후 Firebase Authentication 계정 삭제
            deleteFirebaseAccount()
        }.addOnFailureListener { exception ->
            Log.e("EditProfileActivity", "Error accessing Storage files", exception)
            Toast.makeText(this, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteFirebaseAccount() {
        val user = auth.currentUser
        user?.delete()?.addOnSuccessListener {
            Log.d("EditProfileActivity", "Firebase account deleted successfully.")
            logoutAndRedirect() // 탈퇴 후 로그아웃 및 UI 처리
        }?.addOnFailureListener { exception ->
            Log.e("EditProfileActivity", "Error deleting Firebase account", exception)
            Toast.makeText(this, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun logoutAndRedirect() {
        // 로그아웃 처리
        auth.signOut()

        // SharedPreferences 초기화
        sharedPreferences.edit().clear().apply()

        // 시작 화면으로 이동
        Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
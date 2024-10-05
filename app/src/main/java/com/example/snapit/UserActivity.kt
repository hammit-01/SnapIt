package com.example.snapit

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class UserActivity: AppCompatActivity() {
    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()

    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var back: ImageView
    private lateinit var logout: Button
    private lateinit var editprofile: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_layout)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

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
    }
}
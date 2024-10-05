package com.example.snapit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editprofile_layout)

        // 프로필 수정 페이지

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
    }
}
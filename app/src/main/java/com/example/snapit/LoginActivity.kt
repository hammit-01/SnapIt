package com.example.snapit

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var signin: Button
    private lateinit var id: EditText
    private lateinit var password: EditText
    private lateinit var back: ImageView

    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()

    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        login = findViewById(R.id.login)
        signin = findViewById(R.id.signin)
        id = findViewById(R.id.id)
        password = findViewById(R.id.password)

        // 로그인 버튼 클릭 리스너
        login.setOnClickListener {
            val id = id.text.toString().trim()
            val password = password.text.toString().trim()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 로그인 로직 처리
                auth.signInWithEmailAndPassword(id, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                val uid = it.uid
                                saveUidToSession(uid) // 세션에 UID 저장
                                // 다음 화면으로 이동
                                Toast.makeText(this, "로그인 완료", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
            }
        }

        signin.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }

        back = findViewById(R.id.back)
        back.setOnClickListener {
            if (isTaskRoot) {
                // 현재 액티비티가 스택에서 마지막 액티비티라면 앱을 종료하지 않고 홈으로
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // 이전 액티비티가 있으면 뒤로가기 동작 실행
                finish()
            }
        }
    }

    private fun saveUidToSession(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userUid", uid) // UID를 세션에 저장
        editor.apply()
    }
}
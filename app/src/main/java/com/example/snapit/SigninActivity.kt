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
import com.example.snapit.Datastore.FirestoreOperations

class SigninActivity: AppCompatActivity() {
    private lateinit var firestoreOperations: FirestoreOperations

    private lateinit var back: ImageView
    private lateinit var login: Button
    private lateinit var signin: Button

    private lateinit var id: EditText
    private lateinit var pwd: EditText
    private lateinit var name: EditText
    private lateinit var nickname: EditText
    private lateinit var bday: EditText
    private lateinit var comment: EditText

    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()

    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)

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

        // 로그인 버튼
        login = findViewById(R.id.login)
        login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 회원가입 버튼
        signin = findViewById(R.id.signin)
        signin.setOnClickListener {
            firestoreOperations = FirestoreOperations()

            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

            id = findViewById(R.id.id)
            pwd = findViewById(R.id.pwd)
            name = findViewById(R.id.name)
            nickname = findViewById(R.id.nickname)
            bday = findViewById(R.id.bday)
            comment = findViewById(R.id.comment)
            val id = id.text.toString().trim()
            val pwd = pwd.text.toString().trim()
            val name = name.text.toString().trim()
            val nickname = nickname.text.toString().trim()
            val bday = bday.text.toString().trim()
            val comment = comment.text.toString().trim()

            if (id.isEmpty() || pwd.isEmpty() || name.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(this, "기재 사항을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 회원가입 로직 처리
                auth.createUserWithEmailAndPassword(id, pwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                val uid = it.uid
                                saveUidToSession(uid) // 세션에 UID 저장
                                firestoreOperations.addUser(uid, bday, comment, id, 0, name, nickname, pwd, 0)
                                Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
            }
        }
    }

    private fun saveUidToSession(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userUid", uid) // UID를 세션에 저장
        editor.apply()
    }
}
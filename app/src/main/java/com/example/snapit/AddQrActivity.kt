package com.example.snapit

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddQrActivity : AppCompatActivity() {
    // Firestore 인스턴스 초기화
    private val db = FirebaseFirestore.getInstance()
    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()
    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 카메 앱 실행 후 qr인식 가능시 레이아웃 표시
        setContentView(R.layout.addqr_layout)

        // Logic:
        // 카메라 앱 실행
        // qr 인식
        // qr 해당 사이트로 이동
        // 자동으로 이미지, 동영상 다운로드
        // 다운로드된 항목 firebase 업데이트
        // 다운로드 완료 알림
    }
}
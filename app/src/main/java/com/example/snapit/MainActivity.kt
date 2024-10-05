package com.example.snapit

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.example.snapit.Adapter.ImageAdapter
import com.example.snapit.Adapter.VideoAdapter

class MainActivity : AppCompatActivity() {
    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()

    // 사용자 UID 확인
    private lateinit var name: TextView
    private lateinit var comment: TextView
    private lateinit var pic_num: TextView
    private lateinit var video_num: TextView

    private lateinit var logout: Button
    private lateinit var editprofile: Button
    private lateinit var image: Button
    private lateinit var add_qr: Button
    private lateinit var video: Button

    private lateinit var login: Button
    private lateinit var signin: Button

    // 리싸이클러뷰 그리드방식
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var videoAdapter: VideoAdapter
    private val imageUrls = mutableListOf<String>()
    private val videoUrls = mutableListOf<String>()

    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // test

        // 현재 사용자 가져오기
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 사용자 로그인시 보여지는 메인 화면
            setContentView(R.layout.main_layout)

            // 세션에서 UID 가져오기
            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val userUid = sharedPreferences.getString("userUid", null)
            // 사진, 동영상 개수 표시
            // Todo 이미지와 동영상 버튼을 눌러야 firebase로부터 데이터 개수를 구해버려서 초기에는 뜨지가 않음
            // Todo 차라리 사용자 정보에 사진 개수와 동영상 개수를 등록해 거기서 데이터를 빼오는게 나을 것 같음
            name = findViewById(R.id.name)
            comment = findViewById(R.id.comment)
            pic_num = findViewById(R.id.pic_num)
            video_num = findViewById(R.id.video_num)


            initialRecyclerView("imageType", userUid.toString())

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
                startActivity(Intent(this, UserActivity::class.java))
            }

            // 리싸이클러 뷰 그리드 화면 전환 버튼
            // Todo grid버튼을 다시 한번 눌렀을 때 원래 있던 사진들이 얹혀져서 또 나옴
            image = findViewById(R.id.image)
            image.setOnClickListener {
                // Todo 리싸이클러 뷰를 그리드 형식으로 전환
                // 이미지 리스트 초기화
                imageUrls.clear()
                // 이미지 리스트 초기화
                videoUrls.clear()

                initialRecyclerView("imageType", userUid.toString())
            }

            // QR 추가 버튼
            add_qr = findViewById(R.id.add_qr)
            add_qr.setOnClickListener {
                // Todo QR 추가 화면 전환(카메라 앱 실행 후 로직 실행)
            }

            // 리싸이클러 뷰 비디오 화면 전환 버튼
            video = findViewById(R.id.video)
            video.setOnClickListener {
                // Todo 리싸이클러 뷰를 캘린더 형식으로 전환
                // 이미지 리스트 초기화
                imageUrls.clear()
                // 이미지 리스트 초기화
                videoUrls.clear()

                initialRecyclerView("videoType", userUid.toString())
            }
        } else {
            // 사용자가 로그인을 하지 않았을 때 보여지는 튜토리얼 화면
            setContentView(R.layout.tutorial_layout)


            // 로그인 버튼
            login = findViewById(R.id.login)
            login.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            // 회원가입 버튼
            signin = findViewById(R.id.signin)
            signin.setOnClickListener {
                startActivity(Intent(this, SigninActivity::class.java))
                finish()
            }
        }
    }

    private fun initialRecyclerView(type: String, uid: String) {
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerview)

        // StaggeredGridLayoutManager 설정: 2열 레이아웃 (VERTICAL: 세로로 스크롤)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager
        recyclerView.setHasFixedSize(true)

        if (type == "imageType") {
            // 어댑터 설정
            imageAdapter = ImageAdapter(this, imageUrls)
            recyclerView.adapter = imageAdapter

            // Firebase에서 이미지 가져오기
            if (uid != null) {
                fetchImagesFromFirebase(uid)
            }
        } else if (type == "videoType") {
            // 어댑터 설정
            videoAdapter = VideoAdapter(this, videoUrls)
            recyclerView.adapter = videoAdapter

            // Firebase에서 동영상 가져오기
            if (uid != null) {
                fetchVideosFromFirebase(uid)
            }
        }
    }

    private fun fetchImagesFromFirebase(userUid: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$userUid/")

        // Firebase Storage에서 이미지 리스트 가져오기
        storageReference.listAll().addOnSuccessListener { listResult ->
            val totalItems = listResult.items.size // 총 이미지 개수
            var loadedItems = 0 // 로드된 이미지 개수 추적

            for (fileRef in listResult.items) {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // 각 이미지의 URL을 리스트에 추가
                    imageUrls.add(uri.toString())
                    loadedItems++

                    // 어댑터에 데이터 변경 알림
                    imageAdapter.notifyDataSetChanged()

                    // 모든 이미지를 다 로드했을 때 사진 개수 반환
                    if (loadedItems == totalItems) {
                        val num = imageAdapter.itemCount.toString()
                        pic_num.text = num
                    }
                }.addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting image URL", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error listing files", exception)
        }
    }


    private fun fetchVideosFromFirebase(userUid: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child("videos/$userUid/")

        // Firebase Storage에서 동영상 리스트 가져오기
        storageReference.listAll().addOnSuccessListener { listResult ->
            val totalItems = listResult.items.size // 총 이미지 개수
            var loadedItems = 0 // 로드된 이미지 개수 추적

            for (fileRef in listResult.items) {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // 각 동영상의 URL을 리스트에 추가
                    videoUrls.add(uri.toString())
                    loadedItems++

                    // 어댑터에 데이터 변경 알림
                    videoAdapter.notifyDataSetChanged()

                    // 모든 이미지를 다 로드했을 때 사진 개수 반환
                    if (loadedItems == totalItems) {
                        val num = videoAdapter.itemCount.toString()
                        video_num.text = num
                    }
                }.addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting image URL", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error listing files", exception)
        }
    }
}
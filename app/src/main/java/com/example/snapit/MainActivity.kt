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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.example.snapit.Adapter.ImageAdapter
import com.example.snapit.Adapter.VideoAdapter
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // Firestore 인스턴스 초기화
    private val db = FirebaseFirestore.getInstance()
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

    private lateinit var imageView: ImageView

    // Firebase 사용자 uid 관련
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            initialRecyclerView("imageType", userUid.toString())

            readPosts()

            imageView = findViewById(R.id.imageView)
            fetchProfileFromFirebase(userUid.toString(), imageView)

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

    // 리싸이클러뷰 초기화, 어뎁터 설정 함수
    private fun initialRecyclerView(type: String, uid: String) {
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recyclerview)

        // StaggeredGridLayoutManager 설정: 2열 레이아웃 (VERTICAL: 세로로 스크롤)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager
        recyclerView.setHasFixedSize(true)

        when (type) {
            "imageType" -> {
                // 어댑터 설정
                imageAdapter = ImageAdapter(this, imageUrls)
                recyclerView.adapter = imageAdapter
                fetchImagesFromFirebase(uid)
            }
            "videoType" -> {
                // 어댑터 설정
                videoAdapter = VideoAdapter(this, videoUrls)
                recyclerView.adapter = videoAdapter
                fetchVideosFromFirebase(uid)
            } else -> Toast.makeText(this, "함수 오류", Toast.LENGTH_LONG).show()
        }
    }

    // firestore에서 이미지 가져오기
    private fun fetchImagesFromFirebase(userUid: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$userUid/")

        // Firebase Storage에서 이미지 리스트 가져오기
        storageReference.listAll().addOnSuccessListener { listResult ->
            for (fileRef in listResult.items) {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // 각 이미지의 URL을 리스트에 추가
                    imageUrls.add(uri.toString())

                    // 어댑터에 데이터 변경 알림
                    imageAdapter.notifyDataSetChanged()
                }.addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting image URL", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error listing files", exception)
        }
    }

    // firestore에서 동영상 가져오기
    private fun fetchVideosFromFirebase(userUid: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child("videos/$userUid/")

        // Firebase Storage에서 동영상 리스트 가져오기
        storageReference.listAll().addOnSuccessListener { listResult ->
            for (fileRef in listResult.items) {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // 각 동영상의 URL을 리스트에 추가
                    videoUrls.add(uri.toString())

                    // 어댑터에 데이터 변경 알림
                    videoAdapter.notifyDataSetChanged()
                }.addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting image URL", exception)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error listing files", exception)
        }
    }

    // firestore에서 사용자 프로필 가져오기
    private fun fetchProfileFromFirebase(userUid: String, imageView: ImageView) {
        // Firebase Storage 참조
        val storageReference = FirebaseStorage.getInstance().reference.child("profiles/$userUid/")

        // 폴더 내의 파일들 리스트 가져오기
        storageReference.listAll().addOnSuccessListener { listResult ->
            if (listResult.items.isNotEmpty()) {
                // 첫 번째 파일 참조 (이미지는 하나만 있다고 가정)
                val fileRef = listResult.items[0]

                // 파일의 다운로드 URL 가져오기
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // Glide를 사용하여 이미지 URL을 ImageView에 로드
                    Glide.with(this)
                        .load(uri)
                        .into(imageView)

                    Log.d("MainActivity", "Image loaded successfully")
                }.addOnFailureListener { exception ->
                    Log.e("MainActivity", "Error getting image URL", exception)
                }
            } else {
                Log.e("MainActivity", "No files found in the folder")
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error listing files in the folder", exception)
        }
    }

    // 오류 메시지 표시 함수
    private fun showError(message: String) {
        // 간단한 Toast 메시지로 오류 표시
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // 사용자 정보 읽기 함수
    private fun readPosts() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Firestore의 users_data 컬렉션에서 사용자 문서 가져오기
            db.collection("users_data").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Firestore에서 가져온 데이터
                        val name = document.getString("name") ?: ""
                        val nickName = document.getString("nick_name") ?: ""
                        val comment = document.getString("comment") ?: ""
                        val bDay = document.getString("b_day") ?: ""
                        val id = document.getString("id") ?: ""
                        val imgNum = document.getLong("img_num") ?: 0
                        val videoNum = document.getLong("video_num") ?: 0

                        // TextView에 데이터를 표시
                        val nickNameTextView = findViewById<TextView>(R.id.nickname)
                        val commentTextView = findViewById<TextView>(R.id.comment)
                        val imgNumTextView = findViewById<TextView>(R.id.pic_num)
                        val videoNumTextView = findViewById<TextView>(R.id.video_num)

                        nickNameTextView.text = nickName
                        commentTextView.text = comment
                        imgNumTextView.text = imgNum.toString()
                        videoNumTextView.text = videoNum.toString()

                    } else {
                        // 문서가 없을 경우 처리
                        showError("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    // 오류 처리
                    showError("Failed to fetch data: ${exception.message}")
                }
        } else {
            // 사용자 로그인 정보가 없을 때 처리
            showError("User not logged in")
        }
    }
}
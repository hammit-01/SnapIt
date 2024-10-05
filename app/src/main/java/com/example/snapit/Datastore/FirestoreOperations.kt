package com.example.snapit.Datastore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreOperations {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 사용자 추가 (Create)
    fun addUser(uid: String, bDay: String, comment: String, id: String, imgNum: Int, name: String, nickName: String, pwd: String, videoNum: Int) {
        val user = hashMapOf(
            "b_day" to bDay,
            "comment" to comment,
            "id" to id,
            "img_num" to imgNum,
            "name" to name,
            "nick_name" to nickName,
            "pwd" to pwd,
            "video_num" to videoNum
        )

        db.collection("users_data")
            .document(uid)  // UID를 문서 이름으로 사용
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "User successfully added!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding user", e)
            }
    }

    // 사용자 삭제 (Delete)
    fun deleteUser(uid: String) {
        db.collection("users_data")
            .document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "User successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting user", e)
            }
    }

    // 사용자 업데이트 (Update)
    fun updateUser(uid: String, newData: Map<String, Any>) {
        db.collection("users_data")
            .document(uid)
            .update(newData)
            .addOnSuccessListener {
                Log.d("Firestore", "User successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating user", e)
            }
    }
}
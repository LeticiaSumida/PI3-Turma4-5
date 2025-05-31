import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

fun checarVerificado(callback: (Boolean) -> Unit){
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user!!.uid
    db.collection("Usuario").document(uid).get()
        .addOnSuccessListener { document ->
            if (document != null){
                val verificado = document.getBoolean("emailVerificado") ?: false
                callback(verificado)
            } else{
                callback(false)
            }
        }
        .addOnFailureListener {  e ->
            callback(false)
        }}
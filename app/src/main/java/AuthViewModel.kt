import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.mark.shereheke.models.UserModel
import com.mark.shereheke.navigation.ROUTE_DASHBOARD
import com.mark.shereheke.navigation.ROUTE_SIGNUP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mark.shereheke.navigation.ROUTE_HOME

class AuthViewModel(var navController: NavController, var context: Context){
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(username:String, email:String, password:String, confirmpassword:String){

        if (email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        } else if (password != confirmpassword) {
            Toast.makeText(context,"Password do not match", Toast.LENGTH_LONG).show()
        } else {

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){

                    val uid = mAuth.currentUser!!.uid
                    val role = "user"

                    val userdata = UserModel(
                        fullname = username,
                        email = email,
                        password = password,
                        userId = uid,
                        role = role
                    )

                    val regRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    regRef.setValue(userdata).addOnCompleteListener { result ->

                        if (result.isSuccessful){
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "${result.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_SIGNUP)
                        }

                    }

                } else {
                    navController.navigate(ROUTE_SIGNUP)
                }
            }
        }
    }

    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = mAuth.currentUser!!.uid

                    val userRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    userRef.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value?.toString() ?: "user"

                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

                        if (role == "admin") {
                            navController.navigate(ROUTE_DASHBOARD)
                        } else {
                            navController.navigate(ROUTE_DASHBOARD)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_DASHBOARD)
                    }

                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUTE_DASHBOARD)
    }

    fun isLoggedIn(): Boolean = mAuth.currentUser != null
}
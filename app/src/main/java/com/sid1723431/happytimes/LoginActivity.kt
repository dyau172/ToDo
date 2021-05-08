package com.sid1723431.happytimes

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

const  val RC_SIGN_IN = 0

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        tv_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))

        }

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestEmail()
                  //.requestIdToken(getString(R.string.default_web_client_id))
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()

        button_google_signin.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }


        button_login.setOnClickListener {
            when{
                TextUtils.isEmpty(login_username.text.toString().trim{ it <= ' '}) ->{
                    Toast.makeText(this@LoginActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(login_password.text.toString().trim{ it <= ' '}) ->{
                    Toast.makeText(this@LoginActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else ->{
                    val email: String = login_username.text.toString().trim{ it <= ' '}
                    val password: String = login_password.text.toString().trim{it <= ' '}

                    //Create an instance and create register user with email and password
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult>{ task ->
                                //If registration is successful
                                if(task.isSuccessful) {
                                    //Firebase registered user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!
                                    Toast.makeText(this@LoginActivity,
                                        "Hello " + email + "You have logged in successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                }else {
                                    // If registration is not successful, show error message
                                    Toast.makeText(
                                        this@LoginActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }
                        )
                }

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acct =
                completedTask.getResult(ApiException::class.java)
            val name = acct!!.givenName

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this,
                    "Hello $name",
            Toast.LENGTH_LONG).show()

            startActivity(Intent(this, MainActivity::class.java))

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("googleSign", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(
                this@LoginActivity,
                "Google Sign In Failed " + e.statusCode + " code",
                 Toast.LENGTH_SHORT
            ).show()
        }
    }


}
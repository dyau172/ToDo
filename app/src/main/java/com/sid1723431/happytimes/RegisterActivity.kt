package com.sid1723431.happytimes

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        text_view_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))

        }

        button_register.setOnClickListener{
            when{
                TextUtils.isEmpty(edit_text_register_username.text.toString().trim{ it <= ' '}) ->{
                    Toast.makeText(this@RegisterActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                TextUtils.isEmpty(edit_text_register_password.text.toString().trim{ it <= ' '}) ->{
                    Toast.makeText(this@RegisterActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else ->{
                    val email: String = edit_text_register_username.text.toString().trim{ it <= ' '}
                    val password: String = edit_text_register_password.text.toString().trim{it <= ' '}

                    //Create an instance and create register user with email and password
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult>{ task ->

                                //If registration is successful
                                if(task.isSuccessful) {
                                    //Firebase registered user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(this@RegisterActivity,
                                        "You have registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()



                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                }else {
                                    // If registration is not successful, show error message
                                    Toast.makeText(
                                        this@RegisterActivity,
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
}
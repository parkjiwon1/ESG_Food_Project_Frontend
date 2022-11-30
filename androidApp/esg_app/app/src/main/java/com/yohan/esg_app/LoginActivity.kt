package com.yohan.esg_app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {


    companion object{
        val EMAIL_ADDRESS: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

    }
    private lateinit var auth: FirebaseAuth
    private lateinit var customProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth= Firebase.auth

        //로딩창 객체 생성
        customProgressDialog=ProgressDialog(this)
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(
            android.graphics.Color.TRANSPARENT))


        val loginBtn=findViewById<Button>(R.id.btn_login)
        loginBtn.setOnClickListener{

            customProgressDialog.show()

            val email=findViewById<EditText>(R.id.area_email)
            val password=findViewById<EditText>(R.id.area_password)

            try {
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "로그인성공")
                            Toast.makeText(this, "로그인에 성공하였습니다!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            Intent()
                            startActivity(intent)


                            customProgressDialog.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "로그인실패")
                            Toast.makeText(this, "로그인에 실패하였습니다!", Toast.LENGTH_SHORT).show()


                            customProgressDialog.dismiss();
                        }
                    }
            } catch (exception: Exception) {

                Toast.makeText(this, "입력을 모두 해주세요!", Toast.LENGTH_SHORT).show()
                email.setBackgroundResource(R.drawable.red_edittext)
                password.setBackgroundResource(R.drawable.red_edittext)
                email.setText("")
                password.setText("")

                customProgressDialog.dismiss();
            }

        }


        //회원가입 페이지로 이동한다.
        val registerBtn=findViewById<Button>(R.id.btn_register)
        registerBtn.setOnClickListener {
            val intent=Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }


        //이메일형식대로 입력받지 않으면, 조치를 취한다.
        val emailArea=findViewById<EditText>(R.id.area_email)
        val emailErrArea=findViewById<TextView>(R.id.errArea_email)

        emailArea.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    Log.w("LoginActivity","text가 변경됨.")
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                        emailErrArea.setText("이메일 형식으로 입력해주세요.");
                        emailErrArea.setTextColor(Color.parseColor("#E91E63"))
                        emailArea.setBackgroundResource(R.drawable.red_edittext)
                    } else {
                        emailErrArea.setText("");
                        emailErrArea.setTextColor(Color.parseColor("#E91E63"))
                        emailArea.setBackgroundResource(R.drawable.white_edittext)
                    }
                }
            }

        )


    }
}
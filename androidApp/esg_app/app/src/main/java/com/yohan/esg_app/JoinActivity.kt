package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {

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

    private lateinit var auth:FirebaseAuth
    private lateinit var customProgressDialog:ProgressDialog
    private var mToast:Toast?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth= Firebase.auth

        //로딩창 객체 생성
        customProgressDialog=ProgressDialog(this)
        customProgressDialog.window?.setBackgroundDrawable(
            ColorDrawable(
            Color.TRANSPARENT)
        )

        val registerBtn=findViewById<Button>(R.id.btn_register)
        val emailArea=findViewById<EditText>(R.id.area_email)
        val emailErrArea=findViewById<TextView>(R.id.errArea_email)
        val password1=findViewById<EditText>(R.id.area_password1)
        val password2=findViewById<EditText>(R.id.area_password2)


        registerBtn.setOnClickListener point@{

            customProgressDialog.show()

            //이메일을 입력하지 않는다면?
            if (emailArea.text.toString().equals("")) {
                showToast(this,"이메일을 입력해주세요!")
                emailArea.setBackgroundResource(R.drawable.red_edittext)


                customProgressDialog.dismiss()
                return@point
            }
            //비밀번호와 비밀번호 확인이 일치하지 않는다면?
            if (!(password1.text.toString()).equals(password2.text.toString())) {
                showToast(this,"비밀번호가 일치하지 않습니다!")
                password1.setText("")
                password2.setText("")
                password1.setBackgroundResource(R.drawable.red_edittext)
                password2.setBackgroundResource(R.drawable.red_edittext)


                customProgressDialog.dismiss()
                return@point

            }
            //Firebase에서는 비밀번호가 6자리이상 이여야 한다.
            else if ((password1.length() < 6) || (password2.length() < 6))
            {
                showToast(this,"비밀번호를 6자리이상 입력해주세요!")
                password1.setText("")
                password2.setText("")
                password1.setBackgroundResource(R.drawable.red_edittext)
                password2.setBackgroundResource(R.drawable.red_edittext)


                customProgressDialog.dismiss()
                return@point
            }


            //회원가입 시도.
            auth . createUserWithEmailAndPassword (emailArea.text.toString(),password1.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.w("LoginActivity","회원가입성공")
                        showToast(this,"회원가입 성공!")
                        val intent= Intent(this,LoginActivity::class.java)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LoginActivity","회원가입실패!",task.exception)

                        showToast(this,"다시 확인해주세요!")
                        password1.setText("")
                        password2.setText("")
                    }


                }

            customProgressDialog.dismiss()
        }
        //이메일영역에 이메일형식으로 입력되지 않으면 조치를 취한다.
        emailArea.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    Log.w("JoinActivity","text가 변경됨.")
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                        emailErrArea.setText("이메일 형식으로 입력해주세요.");
                        emailErrArea.setTextColor(Color.parseColor("#E91E63"))
                        emailArea.setBackgroundResource(R.drawable.red_edittext)
                    } else {
                        emailErrArea.setText("가입하실 이메일을 입력해주세요.");
                        emailErrArea.setTextColor(Color.parseColor("#000000"))
                        emailArea.setBackgroundResource(R.drawable.white_edittext)
                    }
                }
            }

        )
    }

    // Toast 겹치지 않고 자연스럽게 만들기 위한 코드
    override fun onStop() {
        super.onStop()
        Thread.sleep(500)
        mToast?.cancel()
    }
    fun showToast(context: Context, message:String) {

        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        }
        else {
            mToast!!.setText(message);
        }
        mToast!!.show()
    }
    // End of Toast 겹치지 않고 자연스럽게 만들기 위한 코드

}

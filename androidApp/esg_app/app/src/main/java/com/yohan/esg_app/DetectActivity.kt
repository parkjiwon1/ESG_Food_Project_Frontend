package com.yohan.esg_app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class DetectActivity : AppCompatActivity() {

    private lateinit var customProgressDialog: ProgressDialog
    var bitmap: Bitmap?=null
    var imageView: ImageView?=null

    private var file:File?=null
    private lateinit var mToast:Toast
    private lateinit var auth: FirebaseAuth

    private val permissionList_storage = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private val permissionList_camera = arrayOf(android.Manifest.permission.CAMERA)

    private val checkPermission_storage = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        var isGranted=true
        result.forEach grant@{

            if(!it.value) {
                Toast.makeText(applicationContext, "권한 동의 필요!", Toast.LENGTH_SHORT).show()
                isGranted=false
                return@grant
            }
        }
        if(isGranted==true)readImage.launch("image/*")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private val checkPermission_camera = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        var isGranted=true
        result.forEach grant@{

            if(!it.value) {
                Toast.makeText(applicationContext, "권한 동의 필요!", Toast.LENGTH_SHORT).show()
                isGranted=false
                return@grant
            }
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(isGranted==true)activityResult.launch(intent)
    }

    //사진촬영 후 실행되는 함수.
    @RequiresApi(Build.VERSION_CODES.O)
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        if(it.resultCode == RESULT_OK && it.data != null) {
            //값 담기
            val extras = it.data!!.extras

            //비트맵으로 타입 변경
            bitmap =extras?.get("data") as Bitmap
            Log.d("mymymy","detect88")
            bitmapConvertFile(bitmap,"/data/data/com.yohan.esg_app/files/testImage.jpg")
            file=File("/data/data/com.yohan.esg_app/files/testImage.jpg")
            Log.d("mymymy","detect91")
            //화면에 보여주기
            imageView!!.setImageBitmap(bitmap)

        }
    }

    private val readImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

        imageView?.setImageURI(uri)
        Log.d("mymymy","경로테스트: "+ uri?.path.toString())
        if(uri!=null)file=File(getRealPathFromURI(uri))

        Log.d("mymymy",uri.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)
        mToast=Toast.makeText(this,"",Toast.LENGTH_SHORT)
        auth = Firebase.auth

        //로딩창 객체 생성
        customProgressDialog=ProgressDialog(this)
        customProgressDialog.setCancelable(false)
        customProgressDialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        //수동으로 collapsingToolbarLayout에 있는 글씨 font 바꾸기
        lateinit var toolbar_layout: CollapsingToolbarLayout
        toolbar_layout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)

        val tf = ResourcesCompat.getFont(this, R.font.bmjua)
        toolbar_layout.setCollapsedTitleTypeface(tf);
        toolbar_layout.setExpandedTitleTypeface(tf);
        //end of 수동으로 collapsingToolbarLayout에 있는 글씨 font 바꾸기

        //상단 Toolbar 관련 이벤트 메소드
        val topAppBar = findViewById<MaterialToolbar>(R.id.toolbar)
        topAppBar.setOnClickListener {
            // Handle navigation icon press
            //Toast.makeText(this,"아직 준비중입니다!",Toast.LENGTH_SHORT).show()
            showToast(this,"사이드 메뉴입니다!")
        }

        topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {

                R.id.home->{
                    //Toast.makeText(this,"홈으로 이동합니다.",Toast.LENGTH_SHORT).show()
                    showToast(this,"홈으로 이동합니다.")
                    finish()//인텐트 종료

                    true
                }
                R.id.reload->{

                    //Toast.makeText(this,"새로고침!",Toast.LENGTH_SHORT).show()
                    showToast(this,"새로고침!")

                    finish()//인텐트 종료
                    overridePendingTransition(0,0)//인텐트 효과 없애기
                    val intent=intent//인텐트
                    startActivity(intent)//액티비티 열기
                    overridePendingTransition(0,0)//인텐트 효과 없애기
                    true
                }
                R.id.information->{
                    //Toast.makeText(this,"아직 준비중입니다!",Toast.LENGTH_SHORT).show()
                    //showToast(this,"아직 준비중입니다!")
                    val mDialogView=LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
                    val mBuilder=AlertDialog.Builder(this)
                        .setView(mDialogView)
                        .setTitle("서버 URL 입력창.")

                    val mAlertDialog=mBuilder.show()

                    val urlInfo=mAlertDialog.findViewById<TextView>(R.id.urlInfo)
                    urlInfo!!.setText("baseUrl: "+CSVReader.baseUrl+"\n"+"innerUrl: "+CSVReader.innerUrl)

                    val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)

                    saveBtn?.setOnClickListener {
                        val ip=mAlertDialog.findViewById<EditText>(R.id.ip)
                        val port=mAlertDialog.findViewById<EditText>(R.id.port)
                        val innerUrl=mAlertDialog.findViewById<EditText>(R.id.innerUrl)

                        val url="http://"+ip!!.text.toString()+":"+port!!.text.toString()+"/"
                        CSVReader.baseUrl=url
                        CSVReader.innerUrl=innerUrl!!.text.toString()
                        Log.d("adcdefg",url)
                        Log.d("adcdefg",CSVReader.baseUrl)


                        showToast(this,"URL 설정 완료!")
                        mAlertDialog.dismiss()
                    }

                    val defaultButton=mAlertDialog.findViewById<Button>(R.id.defaultButton)
                    defaultButton!!.setOnClickListener {

                        CSVReader.baseUrl=CSVReader.defaultUrl
                        CSVReader.innerUrl=CSVReader.defaultInnerUrl

                        showToast(this,"URL 초기화 완료!")
                        mAlertDialog.dismiss()
                    }

                    true
                }
                R.id.logout->{
                    //Toast.makeText(this,"로그아웃 되었습니다!",Toast.LENGTH_SHORT).show()
                    showToast(this,"로그아웃 되었습니다!")

                    auth.signOut()
//                    val intent= Intent(this,LoginActivity::class.java)
//                    startActivity(intent)

                    Handler().postDelayed({
                        ActivityCompat.finishAffinity(this)
                        System.exit(0);
                    },500)

                    true
                }
                else-> false
            }

        }
        //end of 상단 Toolbar 관련 이벤트 메소드

        //view 가져오기
        imageView = findViewById((R.id.imageView))
        val picBtn: Button = findViewById(R.id.pic_btn)
        val bringBtn=findViewById<Button>(R.id.bringBtn)


        //가져오기 버튼 이벤트
        bringBtn.setOnClickListener{
            checkPermission_storage.launch(permissionList_storage)

        }

        //촬영 버튼 이벤트
        picBtn.setOnClickListener(){
        //사진 촬영
            checkPermission_camera.launch(permissionList_camera)
        }

        //전송버튼 클릭했을때,
        val goButton=findViewById<Button>(R.id.go_btn)
        goButton.setOnClickListener goButton@{

            if (file==null) {
                showToast(baseContext,"이미지를 찍어주세요!")
                return@goButton
            }

            try {
                //로딩중 띄우기.
                customProgressDialog.show()
                Log.d("adcdefg","1")
                var gson= GsonBuilder().setLenient().create()

                val retrofit = Retrofit.Builder().baseUrl(CSVReader.baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())// 이건 잘 모름.
                    .addConverterFactory(GsonConverterFactory.create(gson))//json 형식과 자바 객체사이의 변환을 도와주는 라이브러리.

                    .build();
                Log.d("adcdefg","1k")
                val service = retrofit.create(RetrofitService::class.java);//인터페이스를 통하여 service 객체 생성.

                Log.d("adcdefg","2")// 로그는 디버깅할때 좋아요.

                //이미지 가져오기
                var imageString:String=""
//                    String으로 이미지를 보내는 방법.
//                    imageString=BitmapConverter().bitmapToString(bitmap!!)

//                비트맵을 requestbody에 담는 방법
//                val byteArrayOutputStream=ByteArrayOutputStream()
//                bitmap?.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
//                val requestBody= RequestBody.create(MediaType.parse("image/jpeg"),byteArrayOutputStream.toByteArray())
//                val uploadFile: MultipartBody.Part=MultipartBody.Part.createFormData("file","file",requestBody)
//                File객체를 requestBody에 담는 방법


                val requestBody=RequestBody.create(MediaType.parse("image/jpeg"),file)
                val uploadFile:MultipartBody.Part=MultipartBody.Part.createFormData("file",file?.name,requestBody)

                Log.d("adcdefg","3")
                //Log.d("abcdefg",imageString)

                //end of 이미지 가져오기

                //서버에 보낼 해쉬Map 객체 만들기.
                //var param:HashMap<String,String> = java.util.HashMap<String,String>()
                Log.d("adcdefg","4")
                //param.put("image",imageString)//이미지 Base64형으로 변환해서 해쉬map에 담기.

                val call=service.sendImage(CSVReader.innerUrl,uploadFile)
                Log.d("adcdefg","5")
                call?.enqueue(object : Callback<User> {// callback 함수로 서버로부터 응답오면, 다음을 수행.
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if(response.isSuccessful){
                            // 정상적으로 통신이 성고된 경우
                            var result: User? = response.body()
                            Log.d("YMC", "onResponse 성공: " + result?.toString());
                            Log.d("YMC","filename="+result?.toString());

                            val idx=result!!.idx
                            val ingredients=result!!.ingredients

                            val postList= arrayListOf<String>()

                            for (i in idx) {
                                postList.add(i.toString())
                            }
                            var postString="["
                            for ((idx,i) in ingredients.withIndex()) {
                                if(idx!=ingredients.size-1)postString +=i+", "
                                else postString+=i
                            }
                            postString+="]\n가 검출되었습니다."

                            if ((postList.get(0).toInt() == 0) && (postList.size == 1)) {
                                showToast(baseContext, "레시피 검출 실패! 랜덤추천되었습니다.")
                                startRandomRecommend()
                                customProgressDialog.dismiss()
                            } else {

                                showToast(baseContext,"레시피 검출이 성공했습니다!")
                                val intent=Intent(baseContext,RecommendActivity::class.java);
                                intent.putStringArrayListExtra("list",postList)
                                intent.putExtra("ingredients",postString)
                                startActivity(intent)
                                customProgressDialog.dismiss()
                            }


                        }else{
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("YMC", "onResponse 실패")
                            showToast(baseContext,"오류발생! 랜덤추천되었습니다")
                            customProgressDialog.dismiss()

                            startRandomRecommend()//실패시 기본설정으로 진행.

                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("YMC", "onFailure 에러: " + t.message.toString());
                        showToast(baseContext,"오류발생! 랜덤추천되었습니다")
                        customProgressDialog.dismiss()

                        startRandomRecommend()//실패시 기본설정으로 진행.

                    }
                })

            } catch (exception:Exception) {
                showToast(baseContext,"오류발생! 랜덤추천되었습니다")
                customProgressDialog.dismiss()

                startRandomRecommend()//실패시 기본설정으로 진행.
            }

        }

    }//oncreate

    private fun startRandomRecommend() {

        val postList= arrayListOf<String>()
        postList.add(Random().nextInt(1999).toString())
        postList.add(Random().nextInt(1999).toString())
        postList.add(Random().nextInt(1999).toString())
        postList.add(Random().nextInt(1999).toString())
        postList.add(Random().nextInt(1999).toString())
        val intent=Intent(baseContext,RecommendActivity::class.java);
        intent.putStringArrayListExtra("list",postList)
        intent.putExtra("ingredients","랜덤추천 레시피 입니다!")

        startActivity(intent)
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

    private fun bitmapConvertFile(bitmap: Bitmap?, strFilePath: String) {
        // 파일 선언 -> 경로는 파라미터에서 받는다
        val file = File(strFilePath)
        if(bitmap!=null)Log.d("mymymy","bitmap is not null")
        Log.d("mymymy",strFilePath)
        // OutputStream 선언 -> bitmap데이터를 OutputStream에 받아 File에 넣어주는 용도
        var out: OutputStream? = null
        try {
            // 파일 초기화
            file.createNewFile()
            Log.d("mymymy","1")
            // OutputStream에 출력될 Stream에 파일을 넣어준다
            out = FileOutputStream(file)
            Log.d("mymymy","2")
            // bitmap 압축
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                out!!.close()
                Log.d("mymymy","3")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        if (contentUri.path!!.startsWith("/storage")) {
            return contentUri.path
        }
        val id = DocumentsContract.getDocumentId(contentUri).split(":".toRegex()).toTypedArray()[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + " = " + id
        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            null
        )
        try {
            val columnIndex = cursor!!.getColumnIndex(columns[0])
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor!!.close()

        }
        return null
    }
}

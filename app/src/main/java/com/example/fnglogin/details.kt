package com.example.fnglogin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_details.*
import android.content.Intent
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import org.json.JSONObject


class details : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    var mGoogleApiClient : GoogleApiClient ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        updateDetails()

    }

    private fun updateDetails() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()


        val intent = intent
        val jsondata = intent.getStringExtra("userProfile")

        if(jsondata==null){
            txtUsername.text = intent.getStringExtra("name")
            txtEmail.text = intent.getStringExtra("email")
            Glide.with(this@details).load(intent.getStringExtra("photourl")).into(imageView)

        }else{
            var json_object = JSONObject(jsondata)

            val first_name = json_object.getString("name")
            txtUsername!!.text = first_name

            val id = json_object.getString("id")
            val image_url = "https://graph.facebook.com/$id/picture?type=normal"

            if(json_object.has("email"))
            {
                txtEmail.text = json_object.getString("email")
            }

            Glide.with(applicationContext).load(image_url).into(imageView)

        }


        signout.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            startActivity(Intent(this@details,MainActivity::class.java))
        }
        LoginManager.getInstance().logOut()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

}

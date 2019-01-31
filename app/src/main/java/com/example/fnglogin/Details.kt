package com.example.fnglogin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_details.*
import android.content.Intent
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import org.json.JSONObject


class Details : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    private var mGoogleApiClient: GoogleApiClient? = null

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
            Glide.with(this@Details).load(intent.getStringExtra("photourl")).into(imageView)
            userid.text = intent.getStringExtra("userid")

        }else{
            val jsonObject = JSONObject(jsondata)

            val firstName = jsonObject.getString("name")
            txtUsername!!.text = firstName

            val id = jsonObject.getString("id")
            val imageUrl = "https://graph.facebook.com/$id/picture?type=normal"

            if (jsonObject.has("email"))
            {
                txtEmail.text = jsonObject.getString("email")
            }
            userid.text = id
            Glide.with(applicationContext).load(imageUrl).into(imageView)

        }


        signout.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            startActivity(Intent(this@Details, LoginActivity::class.java))
        }
        LoginManager.getInstance().logOut()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

}

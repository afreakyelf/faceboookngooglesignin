package com.example.fnglogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.facebook.*

import java.util.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient






open class MainActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    var callbackManager: CallbackManager? = null
    var mGoogleSignInClient : GoogleSignInClient?=null
    private val RC_SIGN_IN = 7
    var mGoogleApiClient : GoogleApiClient?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googlesignin()
        facebooksignin()

    }



    private fun googlesignin(){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        mGoogleApiClient!!.connect()

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener(this)

    }

    private fun facebooksignin(){

        callbackManager = CallbackManager.Factory.create()

        val loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton.setReadPermissions(Arrays.asList(
            "public_profile", "email", "user_birthday", "user_friends"))

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("status","User logged in successfully")
                getFacebookUserDetails(loginResult)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode,resultCode,data)

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }

    }

        private fun getFacebookUserDetails(loginResult:LoginResult) {
        val data_request = GraphRequest.newMeRequest(
        loginResult.accessToken
        ) { json_object, response ->
            try {
                val intent = Intent(this@MainActivity, details::class.java)
                intent.putExtra("userProfile", json_object.toString())
                startActivity(intent)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
            val permissionparam = Bundle()
        permissionparam.putString("fields", "id,name,email,picture.width(120).height(120)")
            data_request.parameters = permissionparam
        data_request.executeAsync()

        }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in_button -> signInGoogleAcc()
        }// ...


    }

    private fun signInGoogleAcc() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        try {
            var account= result.signInAccount
            val intent = Intent(this@MainActivity, details::class.java)
            intent.putExtra("email", account!!.email)
            intent.putExtra("name",account!!.displayName)
            intent.putExtra("photourl",account!!.photoUrl.toString())
            startActivity(intent)
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

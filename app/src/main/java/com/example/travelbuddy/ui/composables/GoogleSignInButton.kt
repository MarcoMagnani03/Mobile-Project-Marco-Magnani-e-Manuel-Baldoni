package com.example.travelbuddy.ui.composables

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.material3.*
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes

@Composable
fun GoogleSignInButton(onResult: (GoogleSignInAccount?) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    println("✅ Google Sign-In success: ${account?.email}")
                    onResult(account)
                } catch (e: ApiException) {
                    val errorMessage = when (e.statusCode) {
                        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign-in was canceled by user"
                        GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Sign-in failed. Try again"
                        GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Sign-in already in progress"
                        else -> "Error during sign-in: ${e.statusCode} - ${e.message}"
                    }
                    println("❌ Google Sign-In error: $errorMessage")
                    onResult(null)
                }
            }
            Activity.RESULT_CANCELED -> {
                val errorDetails = result.data?.extras?.keySet()?.joinToString { key ->
                    "$key=${result.data?.extras?.get(key)}"
                } ?: "no details"
                println("❌ Google Sign-In canceled or failed. Details: $errorDetails")
                onResult(null)
            }
            else -> {
                println("❌ Unexpected result code: ${result.resultCode}")
                onResult(null)
            }
        }
    }

    TravelBuddyButton(
        label = "Sign up with +Google",
        style = ButtonStyle.PRIMARY_OUTLINED,
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(context, gso)
            launcher.launch(client.signInIntent)
        }
    )
}
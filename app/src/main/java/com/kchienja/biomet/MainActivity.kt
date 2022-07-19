package com.kchienja.biomet

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.kchienja.biomet.ui.theme.BiometTheme

class MainActivity : ComponentActivity() {

    private var cancellationSignal: CancellationSignal? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(onClick = {launchBiometric()})
                }
            }
        }
    }

    private val authenticationCalBack: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                notifyUser("Authenticacion Error $errorCode")
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                notifyUser("Gracias por comprobar su identidad..")
                super.onAuthenticationSucceeded(result)
            }
        }


    private fun checkBiometricSupport(): Boolean{
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isDeviceSecure){
            notifyUser("la seguridad de la pantalla de bloqueo no está habilitada en la configuracion")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("El permiso de autenticación de huellas dactilares no está habilitado")
            return false
        }
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)


    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun launchBiometric(){
        if (checkBiometricSupport()){
            val  biometricPrompt = BiometricPrompt
                .Builder(this)
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Ud. no va a necesitar usuario y clave para ingresar")
                .setDescription("Utilice Autenticación Biométrica para protejer sus datos")
                .setNegativeButton("Ahora No", this.mainExecutor, {
                    dialogInterface,i ->
                    notifyUser("Autenticación cancelada")

                })
                .build()
        }
    }


    private fun getCancelletionSignal(): CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Cancelada via señal")
        }

        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String){
        Toast.makeText(this, "Gracias por autenticarse...", Toast.LENGTH_SHORT).show()
        Log.d("BIOMETRIC", message)
    }
}

@Composable
fun Greeting(onClick: () -> Unit) {

    val username = remember {mutableStateOf(TextFieldValue())}
    val password = remember {mutableStateOf(TextFieldValue())}
    val checked = remember {mutableStateOf(false)}

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = username.value,
            onValueChange = {
                username.value = it
            },
            leadingIcon = {Icon(Icons.Filled.Person, contentDescription = "person")},
            label = { Text(text = "Usuario")}
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = {
                password.value = it
            },
            leadingIcon = {Icon(Icons.Filled.Edit, contentDescription = "person")},
            label = { Text(text = "Password")}
        )

        Spacer(modifier = Modifier.padding(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row{
                Text(text = "Autenticar..")
                Switch(checked = checked.value, onCheckedChange = {
                  checked.value = it

                  if (checked.value){
                      onClick()
                  }
                })
            }


        }
        Spacer(modifier = Modifier.padding(vertical = 25.dp))
        Button(onClick,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ){
            Text(text = "Login")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BiometTheme {
        Greeting{}
    }
}
package com.example.airblock

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.airblock.data.TagStorage
import com.example.airblock.state.AirBlockState
import com.example.airblock.ui.AirBlockHomeScreen
import com.example.airblock.ui.theme.AirBlockTheme

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val savedTag = TagStorage.getSavedTag(this)
        if (savedTag != null) {
            // ¡Teníamos uno guardado! Lo restauramos en la RAM.
            AirBlockState.registeredTagId = savedTag
            AirBlockState.hasTagRegistered = true
        }

        AirBlockState.isLocked = TagStorage.getLockState(this)
        if(AirBlockState.isLocked){
            AirBlockState.startTime = TagStorage.getStartTime(this)
        }

        setContent {
            AirBlockTheme {
                AirBlockHomeScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

            if (tag != null) {
                // Convertimos el ID del Tag a texto (ej: "A1B2C3D4")
                val scannedId = bytesToHex(tag.id)

                // 👇 AQUÍ ESTÁ LA LÓGICA QUE PIDES 👇

                // CASO 1: NO hay nada registrado (Modo Aprendizaje)
                if (!AirBlockState.hasTagRegistered) {
                    // Guardamos este ID como el MAESTRO
                    AirBlockState.registeredTagId = scannedId
                    AirBlockState.hasTagRegistered = true

                    TagStorage.saveTag(this, scannedId)

                    Toast.makeText(this, R.string.nfc_tag_registered, Toast.LENGTH_SHORT).show()
                }

                // CASO 2: YA hay un tag registrado (Modo Seguridad)
                // Al entrar en este 'else', IMPEDIMOS que se guarde el nuevo tag.
                else {
                    // Comparamos el que acaba de llegar con el que tenemos guardado
                    if (scannedId == AirBlockState.registeredTagId) {
                        // ¡COINCIDEN! Es la llave correcta -> Accionamos
                        AirBlockState.isLocked = !AirBlockState.isLocked
                        TagStorage.saveLockState(this, AirBlockState.isLocked)
                        Toast.makeText(this, R.string.nfc_tag_detected, Toast.LENGTH_SHORT).show()
                    } else {
                        // NO COINCIDEN -> Es un intruso
                        // No guardamos nada, solo avisamos.
                        Toast.makeText(this, R.string.wrong_nfc_tag_detected, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun enableNfcForegroundDispatch() {
        // 1. Creamos una "Intención" que apunte a ESTA misma actividad (MainActivity)
        // addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) evita que la app se reinicie al escanear
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        // 2. Creamos un PendingIntent (una intención pendiente de ejecutarse)
        // Es necesario diferenciar entre versiones nuevas (Android 12+) y viejas
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // 3. Definimos qué queremos escanear (FILTROS)
        // En este caso: ACTION_TAG_DISCOVERED (Cualquier etiqueta física)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))

        // 4. ¡ACTIVAMOS LA TRAMPA! 🪤
        // Le decimos al adaptador: "Si ves algo que coincida con los filtros, mándalo al PendingIntent"
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    private fun disableNfcForegroundDispatch() {
        // 5. APAGAMOS LA TRAMPA 🛑
        // Importante hacerlo en onPause para dejar libre el NFC a otras apps
        nfcAdapter?.disableForegroundDispatch(this)
    }

}


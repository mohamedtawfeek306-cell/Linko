package com.linko.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val serviceId = "com.linko.app.OFFLINE_CHAT"
    private val strategy = Strategy.P2P_CLUSTER
    private var connectedEndpoint: String? = null
    private val messages = mutableStateListOf<String>()
    private var status by mutableStateOf("غير متصل")
    private var remoteName by mutableStateOf("")

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) startNearby()
        else status = "لازم تسمح بأذونات الأجهزة القريبة"
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val text = payload.asBytes()?.toString(StandardCharsets.UTF_8) ?: return
                runOnUiThread { messages.add("صديق: $text") }
            }
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) = Unit
    }

    private val connectionLifecycle = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(this@MainActivity)
                .acceptConnection(endpointId, payloadCallback)
            remoteName = info.endpointName
            status = "جاري الاتصال بـ ${info.endpointName}"
        }
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                connectedEndpoint = endpointId
                status = "متصل بـ $remoteName"
                messages.add("تم الاتصال بـ $remoteName")
            } else status = "فشل الاتصال"
        }
        override fun onDisconnected(endpointId: String) {
            if (connectedEndpoint == endpointId) connectedEndpoint = null
            status = "انقطع الاتصال"
        }
    }

    private val endpointCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            status = "تم العثور على ${info.endpointName} — جاري طلب الاتصال"
            Nearby.getConnectionsClient(this@MainActivity)
                .requestConnection("LINKO", endpointId, connectionLifecycle)
        }
        override fun onEndpointLost(endpointId: String) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFF62D6B0),
                background = Color(0xFF101817),
                surface = Color(0xFF192321)
            )) {
                LinkoScreen(
                    status = status,
                    messages = messages,
                    onStart = { ensurePermissions() },
                    onSend = { sendMessage(it) },
                    connected = connectedEndpoint != null
                )
            }
        }
    }

    private fun requiredPermissions(): Array<String> {
        val p = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= 33) {
            p += Manifest.permission.NEARBY_WIFI_DEVICES
        } else if (Build.VERSION.SDK_INT >= 23) {
            p += Manifest.permission.ACCESS_FINE_LOCATION
        }
        if (Build.VERSION.SDK_INT >= 31) {
            p += Manifest.permission.BLUETOOTH_SCAN
            p += Manifest.permission.BLUETOOTH_CONNECT
            p += Manifest.permission.BLUETOOTH_ADVERTISE
        }
        return p.toTypedArray()
    }

    private fun ensurePermissions() {
        val missing = requiredPermissions().filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) startNearby() else permissionLauncher.launch(missing.toTypedArray())
    }

    private fun startNearby() {
        status = "جاري البحث عن أجهزة LINKO القريبة..."
        val client = Nearby.getConnectionsClient(this)
        client.startAdvertising("LINKO", serviceId, connectionLifecycle,
            AdvertisingOptions.Builder().setStrategy(strategy).build())
        client.startDiscovery(serviceId, endpointCallback,
            DiscoveryOptions.Builder().setStrategy(strategy).build())
            .addOnFailureListener { status = "تعذر بدء البحث: ${it.localizedMessage}" }
    }

    private fun sendMessage(value: String) {
        val endpoint = connectedEndpoint ?: run { status = "اتصل بجهاز أولاً"; return }
        if (value.isBlank()) return
        Nearby.getConnectionsClient(this).sendPayload(
            endpoint, Payload.fromBytes(value.toByteArray(StandardCharsets.UTF_8))
        ).addOnSuccessListener { messages.add("أنا: $value") }
            .addOnFailureListener { status = "فشل الإرسال: ${it.localizedMessage}" }
    }

    override fun onDestroy() {
        Nearby.getConnectionsClient(this).stopAllEndpoints()
        Nearby.getConnectionsClient(this).stopAdvertising()
        Nearby.getConnectionsClient(this).stopDiscovery()
        super.onDestroy()
    }
}

@Composable
private fun LinkoScreen(
    status: String,
    messages: List<String>,
    onStart: () -> Unit,
    onSend: (String) -> Unit,
    connected: Boolean
) {
    var draft by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().background(Color(0xFF101817)).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("LINKO", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF62D6B0))
        Text("تواصل قريب منك، من غير إنترنت", color = Color.White)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF192321))) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(status, color = Color.White)
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                    Text("ابحث عن أجهزة قريبة")
                }
            }
        }
        Text(if (connected) "المحادثة" else "ابدأ البحث ثم اتصل بجهاز آخر", color = Color(0xFF62D6B0))
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { message ->
                Surface(color = Color(0xFF20332E), shape = RoundedCornerShape(12.dp)) {
                    Text(message, color = Color.White, modifier = Modifier.padding(12.dp))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = draft, onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("اكتب رسالة") },
                singleLine = true
            )
            Button(onClick = { onSend(draft); draft = "" }, enabled = connected) { Text("إرسال") }
        }
        Text("نسخة تجريبية: الرسائل النصية المباشرة بين جهازين قريبين.", color = Color.LightGray,
            style = MaterialTheme.typography.bodySmall)
    }
}

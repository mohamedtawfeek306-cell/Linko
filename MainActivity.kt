package com.linko.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LinkoApp() }
    }
}

@Composable
fun LinkoApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val bg = Color(0xFF0B1020)
    val panel = Color(0xFF151D32)
    val cyan = Color(0xFF54D6D0)

    MaterialTheme(colorScheme = darkColorScheme(
        primary = cyan, background = bg, surface = panel,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = bg,
            bottomBar = {
                NavigationBar(containerColor = panel) {
                    NavigationBarItem(selectedTab == 0, { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chats") })
                    NavigationBarItem(selectedTab == 1, { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Group, null) }, label = { Text("Nearby") })
                    NavigationBarItem(selectedTab == 2, { selectedTab = 2 },
                        icon = { Icon(Icons.Default.QrCode, null) }, label = { Text("My QR") })
                    NavigationBarItem(selectedTab == 3, { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("LINKO", color = cyan, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("Connect. Nearby. Offline.", color = Color.LightGray, fontSize = 13.sp)
                    }
                    Box(Modifier.size(42.dp).background(cyan.copy(alpha = .15f), CircleShape),
                        contentAlignment = Alignment.Center) {
                        Text("L", color = cyan, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.height(26.dp))
                when (selectedTab) {
                    0 -> {
                        Text("Your conversations", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = panel),
                            shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(18.dp)) {
                                Text("No chats yet", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("Find people nearby to start an offline conversation.",
                                    color = Color.LightGray)
                            }
                        }
                    }
                    1 -> {
                        Text("People nearby", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = panel),
                            shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(18.dp)) {
                                Text("Nearby discovery", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("The discovery and device-pairing flow will be connected in the next development step.",
                                    color = Color.LightGray)
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { }) { Text("Start finding devices") }
                            }
                        }
                    }
                    2 -> {
                        Text("Your LINKO QR", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = panel),
                            shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCode, null, tint = cyan, modifier = Modifier.size(110.dp))
                                Text("Your personal code", fontWeight = FontWeight.SemiBold)
                                Text("QR pairing will be enabled in a later build.", color = Color.LightGray)
                            }
                        }
                    }
                    else -> {
                        Text("Settings", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Text("LINKO • Offline communication", color = Color.LightGray)
                    }
                }
            }
        }
    }
}

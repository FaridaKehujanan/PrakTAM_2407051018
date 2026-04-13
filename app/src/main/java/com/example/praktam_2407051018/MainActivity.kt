package com.example.praktam_2407051018

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2407051018.model.Monster
import com.example.praktam_2407051018.model.MonsterList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonsterListScreen()
        }
    }
}

@Composable
fun MonsterListScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "⚔️ Monster Hunter",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE94560),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "${MonsterList.listMonster.size} monster ditemukan",
            fontSize = 14.sp,
            color = Color(0xFFAAAAAA),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Monster Cards
        MonsterList.listMonster.forEach { monster ->
            MonsterCard(monster = monster)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MonsterCard(monster: Monster) {
    // State untuk toggle favorit — setiap card punya state-nya sendiri
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Box: menumpuk Image + IconButton favorit
            Box {
                Image(
                    painter = painterResource(id = monster.gambar),
                    contentDescription = monster.nama,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )


                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite Icon",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                // Nama Monster
                Text(
                    text = monster.nama,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lokasi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = monster.lokasi,
                        fontSize = 14.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Level Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = getLevelColor(monster.level),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LVL ${monster.level}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = getLevelLabel(monster.level),
                        fontSize = 13.sp,
                        color = getLevelColor(monster.level)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tombol Lihat Detail
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE94560)
                    )
                ) {
                    Text(
                        text = "🔍 Lihat Detail",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

fun getLevelColor(level: Int): Color {
    return when {
        level < 10 -> Color(0xFF4CAF50)
        level < 30 -> Color(0xFF2196F3)
        level < 50 -> Color(0xFFFF9800)
        else -> Color(0xFFE94560)
    }
}

fun getLevelLabel(level: Int): String {
    return when {
        level < 10 -> "Mudah"
        level < 30 -> "Normal"
        level < 50 -> "Sulit"
        else -> "Sangat Sulit"
    }
}

@Preview(showBackground = true)
@Composable
fun MonsterListPreview() {
    MonsterListScreen()
}
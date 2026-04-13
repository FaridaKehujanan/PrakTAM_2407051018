package com.example.praktam_2407051018

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER + LAZYROW
        item {
            Text(
                text = "⚔️ Monster Hunter",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE94560)
            )

            Text(
                text = "${MonsterList.listMonster.size} monster ditemukan",
                fontSize = 14.sp,
                color = Color(0xFFAAAAAA),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "🔥 Monster Populer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(MonsterList.listMonster) { monster ->
                    MonsterRowItem(monster)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📜 Semua Monster",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // LIST UTAMA (VERTICAL)
        items(MonsterList.listMonster) { monster ->
            MonsterCard(monster = monster)
        }
    }
}

@Composable
fun MonsterRowItem(monster: Monster) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))
    ) {
        Column {
            Image(
                painter = painterResource(id = monster.gambar),
                contentDescription = monster.nama,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = monster.nama,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "LVL ${monster.level}",
                    fontSize = 12.sp,
                    color = getLevelColor(monster.level)
                )
            }
        }
    }
}

@Composable
fun MonsterCard(monster: Monster) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

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
                        imageVector = if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite Icon",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = monster.nama,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

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
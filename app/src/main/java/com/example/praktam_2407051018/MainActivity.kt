package com.example.praktam_2407051018

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.praktam_2407051018.model.Monster
import com.example.praktam_2407051018.model.MonsterList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MonsterListScreen()
        }
    }

    @Composable
    fun MonsterItem(monster: Monster) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(8.dp)) {

                Image(
                    painter = painterResource(id = monster.gambar),
                    contentDescription = monster.nama,
                    modifier = Modifier.size(80.dp)
                )

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = "Nama: ${monster.nama}")
                    Text(text = "Lokasi: ${monster.lokasi}")
                    Text(text = "Level: ${monster.level}")
                }
            }
        }
    }

    @Composable
    fun MonsterListScreen() {
        LazyColumn {
            items(MonsterList.listMonster) { monster ->
                MonsterItem(monster)
            }
        }
    }
}
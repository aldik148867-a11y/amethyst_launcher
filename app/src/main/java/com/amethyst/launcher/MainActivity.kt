package com.amethyst.launcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.roundToInt

val Bg = Color(0xFF14121F)
val S1 = Color(0xFF1E1A30)
val S2 = Color(0xFF2A2444)
val Am = Color(0xFF9B7BFF)
val Gl = Color(0xFFFFB347)
val Ok = Color(0xFF5BE3A4)
val Mu = Color(0xFF9A93BD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Am, background = Bg, surface = S1)) {
                App()
            }
        }
    }
}

@Composable
fun App() {
    var tab by remember { mutableStateOf(0) }
    val names = listOf("Играть", "Моды", "Управление")
    Scaffold(
        containerColor = Bg,
        bottomBar = {
            NavigationBar(containerColor = S1) {
                names.forEachIndexed { i, n ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Box(Modifier.size(2.dp)) },
                        label = { Text(n, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (tab) {
                0 -> Home()
                1 -> Mods()
                else -> Controls()
            }
        }
    }
}

@Composable
fun Chip(text: String, on: Boolean, onClick: () -> Unit) {
    FilterChip(selected = on, onClick = onClick, label = { Text(text) })
}

@Composable
fun Home() {
    val ctx = LocalContext.current
    val versions = listOf("1.21.11", "1.20.1", "1.16.5", "1.12.2", "1.8.9")
    val loaders = listOf("Vanilla", "Fabric", "Forge", "NeoForge", "OptiFine", "Quilt")
    val modes = listOf(
        "Эконом" to "Низкая дальность прорисовки, минимум эффектов.",
        "Баланс" to "Sodium и Lithium, средняя дальность.",
        "Максимум" to "Шейдеры и высокая дальность. Для мощных телефонов."
    )
    var v by remember { mutableStateOf(versions[0]) }
    var l by remember { mutableStateOf("Fabric") }
    var m by remember { mutableStateOf(1) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Amethyst Launcher", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(14.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF4A31A8), S1))).padding(20.dp)
        ) {
            Text("Готов к запуску", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text("Выбери версию и загрузчик.", color = Mu)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                versions.forEach { Chip(it, v == it) { v = it } }
            }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                loaders.forEach { Chip(it, l == it) { l = it } }
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { Toast.makeText(ctx, "Запуск $v $l: движок подключим следующим шагом", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gl, contentColor = Color(0xFF2A1300))
            ) { Text("Играть", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold) }
        }
        Spacer(Modifier.height(16.dp))
        Text("Режим производительности", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            modes.forEachIndexed { i, p -> Chip(p.first, m == i) { m = i } }
        }
        Text(modes[m].second, color = Mu, modifier = Modifier.padding(top = 8.dp))
    }
}

data class Item(val icon: String, val name: String, val desc: String)

@Composable
fun Mods() {
    val cats = listOf("Моды", "Ресурспаки", "Миры", "Шейдеры")
    var c by remember { mutableStateOf(0) }
    var q by remember { mutableStateOf("") }
    val list = listOf(
        Item("⚡", "Sodium", "Ускоряет отрисовку"),
        Item("🧮", "Lithium", "Оптимизирует логику игры"),
        Item("🌈", "Iris Shaders", "Поддержка шейдеров"),
        Item("🗺", "Xaero's Minimap", "Мини-карта")
    )
    val done = remember { mutableStateListOf<String>() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = q, onValueChange = { q = it }, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Найти мод, ресурспак, мир или шейдер") }, singleLine = true
        )
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cats.forEachIndexed { i, n -> Chip(n, c == i) { c = i } }
        }
        LazyColumn {
            items(list.filter { it.name.contains(q, true) }) { m ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(S2),
                        contentAlignment = Alignment.Center
                    ) { Text(m.icon, fontSize = 20.sp) }
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(m.name, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(m.desc, color = Mu, fontSize = 13.sp)
                    }
                    val d = m.name in done
                    Button(
                        onClick = { if (!d) done.add(m.name) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (d) S2 else Am,
                            contentColor = if (d) Ok else Color(0xFF120B30)
                        )
                    ) { Text(if (d) "Установлено" else "Установить") }
                }
            }
        }
    }
}

data class VBtn(
    val id: Int, val label: String, val key: String,
    val x: Float, val y: Float, val size: Float, val scroll: Boolean
)

val KEYS = listOf(
    "Esc", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
    "A", "S", "D", "F", "G", "H", "J", "K", "L",
    "Shift", "Z", "X", "C", "V", "B", "N", "M", "Ctrl",
    "Tab", "Space", "Enter", "ЛКМ", "ПКМ", "Колесо"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Controls() {
    val ctx = LocalContext.current
    val btns = remember {
        mutableStateListOf(
            VBtn(1, "W", "W", .20f, .55f, 56f, false),
            VBtn(2, "A", "A", .10f, .78f, 56f, false),
            VBtn(3, "S", "S", .20f, .78f, 56f, false),
            VBtn(4, "D", "D", .30f, .78f, 56f, false),
            VBtn(5, "Прыжок", "Space", .88f, .74f, 68f, false),
            VBtn(6, "Хотбар", "Колесо", .50f, .88f, 70f, true)
        )
    }
    var sel by remember { mutableStateOf<Int?>(null) }
    var kb by remember { mutableStateOf(false) }
    var nid by remember { mutableStateOf(10) }
    var sw by remember { mutableStateOf(1) }
    var sh by remember { mutableStateOf(1) }
    val dens = LocalDensity.current

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Редактор управления", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().aspectRatio(2f).clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF3B5A8F), Color(0xFF6D8FC4), Color(0xFF35592A))))
                .onSizeChanged { sw = it.width; sh = it.height }
        ) {
            btns.forEach { b ->
                val px = with(dens) { b.size.dp.toPx() }
                val shape = if (b.scroll) RoundedCornerShape(14.dp) else RoundedCornerShape(50)
                Box(
                    Modifier
                        .offset { IntOffset((b.x * sw - px / 2).roundToInt(), (b.y * sh - px / 2).roundToInt()) }
                        .size(b.size.dp)
                        .clip(shape)
                        .background(Color(0x8C14121F))
                        .border(2.dp, if (sel == b.id) Gl else Color(0x8CFFFFFF), shape)
                        .pointerInput(b.id) {
                            detectDragGestures(onDragStart = { sel = b.id }) { change, drag ->
                                change.consume()
                                val i = btns.indexOfFirst { it.id == b.id }
                                if (i >= 0) {
                                    val c = btns[i]
                                    btns[i] = c.copy(
                                        x = (c.x + drag.x / sw).coerceIn(0.03f, 0.97f),
                                        y = (c.y + drag.y / sh).coerceIn(0.05f, 0.95f)
                                    )
                                }
                            }
                        }
                        .clickable { sel = b.id },
                    contentAlignment = Alignment.Center
                ) { Text(b.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Text(
            "Тяни кнопку, чтобы переместить. Нажми, чтобы выбрать.",
            color = Mu, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp)
        )
        val cur = btns.firstOrNull { it.id == sel }
        if (cur != null) {
            Text("Размер: ${cur.size.roundToInt()}", color = Mu, modifier = Modifier.padding(top = 8.dp))
            Slider(value = cur.size, valueRange = 36f..120f, onValueChange = { v ->
                val i = btns.indexOfFirst { it.id == cur.id }
                if (i >= 0) btns[i] = btns[i].copy(size = v)
            })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { kb = true }) { Text("Клавиша: ${cur.key}") }
                OutlinedButton(onClick = {
                    val i = btns.indexOfFirst { it.id == cur.id }
                    if (i >= 0) btns[i] = btns[i].copy(scroll = !cur.scroll)
                }) { Text(if (cur.scroll) "Прокрутка" else "Кнопка") }
                OutlinedButton(onClick = { btns.removeAll { it.id == cur.id }; sel = null }) { Text("Удалить") }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                btns.add(VBtn(nid, "Новая", "F", .5f, .4f, 60f, false)); sel = nid; nid++
            }) { Text("Добавить кнопку") }
            Button(
                onClick = { Toast.makeText(ctx, "Раскладка применена", Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(containerColor = Ok, contentColor = Color(0xFF052A1A))
            ) { Text("Применить") }
        }
    }

    if (kb && sel != null) {
        Dialog(onDismissRequest = { kb = false }) {
            Column(Modifier.clip(RoundedCornerShape(20.dp)).background(S1).padding(12.dp)) {
                Text("Выбери клавишу", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KEYS.forEach { k ->
                        Box(
                            Modifier.clip(RoundedCornerShape(8.dp)).background(S2)
                                .clickable {
                                    val i = btns.indexOfFirst { it.id == sel }
                                    if (i >= 0) btns[i] = btns[i].copy(key = k, label = k)
                                    kb = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) { Text(k, color = Color.White, fontSize = 13.sp) }
                    }
                }
            }
        }
    }
}

package com.example.travelmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

// ===================== Lab4 核心1：导航路由定义（3个屏幕）=====================
@Serializable
object Home // 首页
@Serializable
data class TravelDetail(val placeId: Int) // 景点详情页（传参：景点ID）
@Serializable
object AddTravel // 添加旅行页

// ===================== 数据类（你原有代码，保留）=====================
data class TravelPlace(
    val id: Int,
    val name: String,
    val address: String,
    val date: String,
    val desc: String
)

data class QuickFunction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
)

data class CategoryCard(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String,
)

data class BottomNavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val isSelected: Boolean
)

// ===================== Lab4 核心2：ViewModel（跨页面共享数据，旋转不丢失）=====================
class TravelViewModel : ViewModel() {
    // 共享的旅行数据，所有页面都能访问
    private val _travelList = mutableStateListOf(
        TravelPlace(1,"Sanya Yalong Bay","Yalong Bay Road, Jiyang District, Sanya, Hainan","2026.01.20 - 2026.01.25","No.1 Bay in the world, fine sand and clear sea water."),
        TravelPlace(2,"Dali Ancient City","Dali City, Dali Bai Autonomous Prefecture, Yunnan","2026.02.05 - 2026.02.10","Capital of Nanzhao Kingdom, Ming and Qing architecture, rich ethnic customs."),
        TravelPlace(3,"Xi'an Terracotta Army","Terracotta Army Scenic Area, Lintong District, Xi'an, Shaanxi","2026.03.10 - 2026.03.15","World Cultural Heritage, stunning underground army.")
    )
    val travelList: List<TravelPlace> = _travelList

    // 根据ID获取景点详情
    fun getPlaceById(id: Int): TravelPlace? {
        return _travelList.find { it.id == id }
    }

    // 添加新的旅行地点
    fun addTravelPlace(place: TravelPlace) {
        _travelList.add(place)
    }
}

// ===================== 模拟数据（你原有代码）=====================
val quickFunctions = listOf(
    QuickFunction(Icons.Filled.DriveEta, "Drive"),
    QuickFunction(Icons.Filled.DirectionsBus, "Bus"),
    QuickFunction(Icons.Filled.LocalTaxi, "Taxi"),
    QuickFunction(Icons.Filled.Hotel, "Hotel"),
    QuickFunction(Icons.AutoMirrored.Filled.DirectionsWalk, "Walk"),
    QuickFunction(Icons.Filled.Place, "Nearby"),
    QuickFunction(Icons.Filled.Bookmark, "Favorites"),
    QuickFunction(Icons.Filled.Map, "Street View"),
    QuickFunction(Icons.Filled.CarRental, "Rent Car"),
    QuickFunction(Icons.Filled.MoreHoriz, "More")
)

val categoryList = listOf(
    CategoryCard(Icons.Filled.Restaurant, "Food", "Food Recommendations"),
    CategoryCard(Icons.Filled.Attractions, "Attractions", "Must-visit Spots"),
    CategoryCard(Icons.Filled.Hotel, "Hotels", "Comfortable Stays"),
    CategoryCard(Icons.Filled.LocalBar, "Entertainment", "Relaxation Spots")
)

// ===================== 主Activity（改造为导航容器）=====================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkMode by remember { mutableStateOf(false) }
            TravelMapTheme(darkTheme = darkMode) {
                // Lab4：导航控制器
                val navController = rememberNavController()
                // 获取共享的 ViewModel
                val travelViewModel: TravelViewModel = viewModel()

                // 导航主机：管理3个页面跳转
                NavHost(
                    navController = navController,
                    startDestination = Home
                ) {
                    // 页面1：首页
                    composable<Home> {
                        HomeScreen(
                            darkMode = darkMode,
                            onDarkModeChange = { darkMode = it },
                            navController = navController,
                            viewModel = travelViewModel
                        )
                    }

                    // 页面2：景点详情页
                    composable<TravelDetail> { backStackEntry ->
                        val args = backStackEntry.toRoute<TravelDetail>()
                        TravelDetailScreen(
                            placeId = args.placeId,
                            viewModel = travelViewModel,
                            navController = navController
                        )
                    }

                    // 页面3：添加旅行页
                    composable<AddTravel> {
                        AddTravelScreen(
                            navController = navController,
                            viewModel = travelViewModel
                        )
                    }
                }
            }
        }
    }
}

// ===================== Lab4 页面1：首页（你原有所有UI）=====================
@Composable
fun HomeScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    navController: NavController,
    viewModel: TravelViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Text(
                    text = "A206702",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                MapPreviewSection(
                    onDarkModeToggle = { onDarkModeChange(!darkMode) },
                    isDarkMode = darkMode
                )
            }

            item { SearchSection() }

            // 跳转到添加旅行页面的按钮
            item {
                Button(
                    onClick = { navController.navigate(AddTravel) },
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add New Travel")
                }
            }

            item { QuickFunctionSection() }
            item { CategorySection() }

            item {
                Text(
                    "My Travel Footprints",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 点击卡片跳转到详情页
            items(viewModel.travelList) { place ->
                TravelPlaceItem(place) {
                    navController.navigate(TravelDetail(placeId = place.id))
                }
            }
        }
    }
}

// ===================== Lab4 页面2：景点详情页 =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelDetailScreen(
    placeId: Int,
    viewModel: TravelViewModel,
    navController: NavController
) {
    val place = viewModel.getPlaceById(placeId) ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Address: ${place.address}", style = MaterialTheme.typography.bodyLarge)
                    Text("Date: ${place.date}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Description:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(place.desc, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ===================== Lab4 页面3：添加旅行页面 =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTravelScreen(
    navController: NavController,
    viewModel: TravelViewModel
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Travel") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Place Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addTravelPlace(
                            TravelPlace(
                                id = (viewModel.travelList.size + 1),
                                name = name,
                                address = address,
                                date = date,
                                desc = desc
                            )
                        )
                        navController.popBackStack() // 添加完成返回首页
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Save Travel")
            }
        }
    }
}

// ===================== 你原有UI组件（无修改，完整保留）=====================
@Composable
fun SearchSection() {
    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf("Enter a location and search") }

    Column {
        SearchBarSection(searchText) { searchText = it }
        SearchActionSection(
            searchText = searchText,
            searchResult = searchResult,
            onSearchClick = {
                searchResult = if (searchText.isBlank()) {
                    "Please enter a valid location!"
                } else {
                    "Searching for: $searchText \nRelated places found~"
                }
            }
        )
    }
}

@Composable
fun MapPreviewSection(
    onDarkModeToggle: () -> Unit,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                Modifier.align(Alignment.TopEnd)
                    .padding(16.dp)
                    .width(48.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onDarkModeToggle) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton({}) { Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.onPrimary) }
                IconButton({}) { Icon(Icons.Filled.Layers, null, tint = MaterialTheme.colorScheme.onPrimary) }
                IconButton({}) { Icon(Icons.Filled.MyLocation, null, tint = MaterialTheme.colorScheme.onPrimary) }
                IconButton({}) { Icon(Icons.Filled.Directions, null, tint = MaterialTheme.colorScheme.onPrimary) }
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.LocationOn, null, Modifier.size(48.dp), MaterialTheme.colorScheme.primary)
                Text(
                    "Current Location",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), MaterialTheme.shapes.small)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBarSection(
    searchText: String,
    onTextChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onTextChanged,
        placeholder = { Text("Search places, bus, subway") },
        leadingIcon = { Icon(Icons.Filled.Search, null) },
        trailingIcon = {
            Row(
                Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Filled.QrCode, null)
                Icon(Icons.Filled.Mic, null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SearchActionSection(
    searchText: String,
    searchResult: String,
    onSearchClick: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Button(onClick = onSearchClick, modifier = Modifier.fillMaxWidth()) {
            Text("Search")
        }
        Text(
            text = searchResult,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun QuickFunctionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Quick Functions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        quickFunctions.chunked(5).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                rowItems.forEach {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(it.icon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Text(it.title, Modifier.padding(top = 4.dp), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection() {
    Column(Modifier.padding(16.dp)) {
        Text("Categories", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        categoryList.forEach {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(it.icon, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(it.title, fontWeight = FontWeight.Medium)
                        Text(it.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// 新增点击事件
@Composable
fun TravelPlaceItem(place: TravelPlace, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick // 点击跳转
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(place.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(place.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(place.date, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(4.dp))
            Text(place.desc, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        val items = listOf(
            BottomNavItem(Icons.Filled.Map, "Map", true),
            BottomNavItem(Icons.Filled.Explore, "Explore", false),
            BottomNavItem(Icons.Filled.Person, "Profile", false)
        )
        items.forEach {
            NavigationBarItem(
                icon = { Icon(it.icon, null) },
                label = { Text(it.title) },
                selected = it.isSelected,
                onClick = {}
            )
        }
    }
}

// ===================== 主题（你原有代码，保留）=====================
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFF2196F3),
    tertiary = Color(0xFFFF9800)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF64B5F6),
    tertiary = Color(0xFFFFB74D)
)

@Composable
fun TravelMapTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TravelMapTheme {
        HomeScreen(false, {}, rememberNavController(), viewModel())
    }
}

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

// ===================== 导航路由（7个页面，满足≥5要求）=====================
@Serializable
object Home        // 地图首页
@Serializable
data class TravelDetail(val placeId: Int) // 详情页
@Serializable
object AddTravel   // 添加旅行
@Serializable
object SdgIntro    // SDG介绍
@Serializable
object TravelStats // 旅行统计
@Serializable
object Explore     // 发现页（新增）
@Serializable
object Profile     // 个人页（新增）

// ===================== 数据类 =====================
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

// ===================== ViewModel（全局共享数据）=====================
class TravelViewModel : ViewModel() {
    private val _travelList = mutableStateListOf(
        TravelPlace(1,"Sanya Yalong Bay","Yalong Bay Road, Jiyang District, Sanya, Hainan","2026.01.20 - 2026.01.25","No.1 Bay in the world, fine sand and clear sea water."),
        TravelPlace(2,"Dali Ancient City","Dali City, Dali Bai Autonomous Prefecture, Yunnan","2026.02.05 - 2026.02.10","Capital of Nanzhao Kingdom, Ming and Qing architecture, rich ethnic customs."),
        TravelPlace(3,"Xi'an Terracotta Army","Terracotta Army Scenic Area, Lintong District, Xi'an, Shaanxi","2026.03.10 - 2026.03.15","World Cultural Heritage, stunning underground army.")
    )
    val travelList: List<TravelPlace> = _travelList

    fun getPlaceById(id: Int): TravelPlace? {
        return _travelList.find { it.id == id }
    }

    fun addTravelPlace(place: TravelPlace) {
        _travelList.add(place)
    }
}

// ===================== 模拟数据 =====================
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

// ===================== 主Activity =====================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkMode by remember { mutableStateOf(false) }
            TravelMapTheme(darkTheme = darkMode) {
                val navController = rememberNavController()
                val travelViewModel: TravelViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Home
                ) {
                    // 首页
                    composable<Home> {
                        HomeScreen(
                            darkMode = darkMode,
                            onDarkModeChange = { darkMode = it },
                            navController = navController,
                            viewModel = travelViewModel
                        )
                    }
                    // 详情页
                    composable<TravelDetail> { backStackEntry ->
                        val args = backStackEntry.toRoute<TravelDetail>()
                        TravelDetailScreen(
                            placeId = args.placeId,
                            viewModel = travelViewModel,
                            navController = navController
                        )
                    }
                    // 添加旅行
                    composable<AddTravel> {
                        AddTravelScreen(navController, travelViewModel)
                    }
                    // SDG介绍
                    composable<SdgIntro> {
                        SdgIntroScreen(navController)
                    }
                    // 旅行统计
                    composable<TravelStats> {
                        TravelStatsScreen(travelViewModel, navController)
                    }
                    // 发现页（新增）
                    composable<Explore> {
                        ExploreScreen(navController)
                    }
                    // 个人中心（新增）
                    composable<Profile> {
                        ProfileScreen(travelViewModel, navController)
                    }
                }
            }
        }
    }
}

// ===================== 页面1：首页 =====================
@Composable
fun HomeScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    navController: NavController,
    viewModel: TravelViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Text(
                    "A206702",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item { MapPreviewSection({ onDarkModeChange(!darkMode) }, darkMode) }
            item { SearchSection() }

            item {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { navController.navigate(AddTravel) }, Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add")
                    }
                    Button(onClick = { navController.navigate(SdgIntro) }, Modifier.weight(1f)) {
                        Icon(Icons.Filled.Info, null)
                        Spacer(Modifier.width(4.dp))
                        Text("SDG")
                    }
                    Button(onClick = { navController.navigate(TravelStats) }, Modifier.weight(1f)) {
                        Icon(Icons.Filled.BarChart, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Stats")
                    }
                }
            }

            item { QuickFunctionSection() }
            item { CategorySection() }

            item {
                Text("My Travel Footprints",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp))
            }

            items(viewModel.travelList) { place ->
                TravelPlaceItem(place) {
                    navController.navigate(TravelDetail(placeId = place.id))
                }
            }
        }
    }
}

// ===================== 页面2：景点详情 =====================
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
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(place.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Address: ${place.address}")
                    Text("Date: ${place.date}")
                    Spacer(Modifier.height(16.dp))
                    Text("Description:", fontWeight = FontWeight.Medium)
                    Text(place.desc)
                }
            }
        }
    }
}

// ===================== 页面3：添加旅行 =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTravelScreen(navController: NavController, viewModel: TravelViewModel) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Travel") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp))

            Button(onClick = {
                if (name.isNotBlank()) {
                    viewModel.addTravelPlace(
                        TravelPlace(viewModel.travelList.size + 1, name, address, date, desc)
                    )
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Save")
            }
        }
    }
}

// ===================== 页面4：SDG介绍 =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SdgIntroScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SDG 11: Sustainable Cities") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Problem", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Malaysian cities face traffic congestion, carbon emissions, and lack of sustainable travel tracking tools.", modifier = Modifier.padding(top = 8.dp))

            Spacer(Modifier.height(16.dp))
            Text("Solution", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("This app helps users record green travel, promote low-carbon transportation, and support sustainable urban mobility (SDG 11).", modifier = Modifier.padding(top = 8.dp))
        }
    }
}

// ===================== 页面5：旅行统计 =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelStatsScreen(viewModel: TravelViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Travel Records", fontSize = 18.sp)
                    Text("${viewModel.travelList.size}", fontSize = 40.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

// ===================== 页面6：发现页（新增）=====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sustainable Travel Explore") })
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Malaysia Green Travel Recommendations",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("1. Use Public Transport", fontWeight = FontWeight.Medium)
                    Text("Reduce carbon footprint by taking buses & trains (SDG 11)")
                }
            }
            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("2. Eco-Friendly Tourist Spots", fontWeight = FontWeight.Medium)
                    Text("Visit sustainable attractions in Kuala Lumpur & Penang")
                }
            }
            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("3. Walk & Cycle", fontWeight = FontWeight.Medium)
                    Text("Healthy and green way to explore cities")
                }
            }
        }
    }
}

// ===================== 页面7：个人中心（新增）=====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: TravelViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Profile") })
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像
            Card(shape = CircleShape, modifier = Modifier.size(100.dp)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, null, modifier = Modifier.size(60.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Travel User", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("A206702", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))

            // 统计卡片
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${viewModel.travelList.size}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("Trips")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("03", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("Countries")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Green", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF4CAF50))
                        Text("Traveler")
                    }
                }
            }
        }
    }
}

// ===================== 底部导航（支持点击切换）=====================
@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Map, null) },
            label = { Text("Map") },
            selected = currentRoute == Home::class.qualifiedName,
            onClick = { navController.navigate(Home) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Explore, null) },
            label = { Text("Explore") },
            selected = currentRoute == Explore::class.qualifiedName,
            onClick = { navController.navigate(Explore) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, null) },
            label = { Text("Profile") },
            selected = currentRoute == Profile::class.qualifiedName,
            onClick = { navController.navigate(Profile) }
        )
    }
}

// ===================== UI 公共组件 =====================
@Composable
fun SearchSection() {
    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf("Enter a location") }
    Column {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search places") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = CircleShape
        )
        Button(onClick = {
            searchResult = if (searchText.isBlank()) "Enter location!" else "Searching: $searchText"
        }, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Text("Search")
        }
        Text(searchResult, Modifier.padding(16.dp))
    }
}

@Composable
fun MapPreviewSection(onDarkModeToggle: () -> Unit, isDarkMode: Boolean) {
    Card(Modifier.fillMaxWidth().height(280.dp).padding(16.dp)) {
        Box {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                IconButton(onClick = onDarkModeToggle) { Icon(if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode, null) }
                IconButton({}) { Icon(Icons.Filled.Add, null) }
                IconButton({}) { Icon(Icons.Filled.Layers, null) }
            }
        }
    }
}

@Composable
fun QuickFunctionSection() {
    Column(Modifier.padding(16.dp)) {
        Text("Quick Functions", style = MaterialTheme.typography.titleMedium)
        quickFunctions.chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
                row.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(modifier = Modifier.size(48.dp), shape = CircleShape) { Box(Modifier.fillMaxSize(), Alignment.Center) { Icon(it.icon, null) } }
                        Text(it.title, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
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
        categoryList.forEach {
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Card(modifier = Modifier.size(40.dp), shape = CircleShape) { Box(Modifier.fillMaxSize(), Alignment.Center) { Icon(it.icon, null) } }
                    Spacer(Modifier.width(12.dp))
                    Column { Text(it.title); Text(it.subtitle, fontSize = 12.sp) }
                }
            }
        }
    }
}

@Composable
fun TravelPlaceItem(place: TravelPlace, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .animateContentSize(),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(place.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(place.address)
            Text(place.date)
            Text(place.desc, maxLines = 2)
        }
    }
}

// ===================== 应用主题 =====================
private val LightColorScheme = lightColorScheme(primary = Color(0xFF4CAF50), secondary = Color(0xFF2196F3))
private val DarkColorScheme = darkColorScheme(primary = Color(0xFF81C784), secondary = Color(0xFF64B5F6))

@Composable
fun TravelMapTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme, content = content)
}
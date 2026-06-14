package com.example.travelmap

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Community Green Board — the read side of the Cloud Integration pillar.
 *
 * Posts come straight from a live Firestore snapshot via [CommunityViewModel.posts],
 * so anything anyone publishes appears here in real time. When Firebase has not
 * been configured yet, a friendly banner explains how to enable it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(viewModel: CommunityViewModel, navController: NavController) {
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Community Green Board") }) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            if (viewModel.isCloudAvailable) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(AddCommunityPost) },
                    icon = { Icon(Icons.Filled.Add, null) },
                    text = { Text("Share") }
                )
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).fillMaxSize()) {
            if (!viewModel.isCloudAvailable) {
                CloudNotConfiguredBanner()
            }

            if (posts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (viewModel.isCloudAvailable)
                            "No community posts yet.\nBe the first to share a green-travel tip!"
                        else
                            "Community posts will appear here once Firebase is connected.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts) { post -> CommunityPostCard(post) }
                }
            }
        }
    }
}

@Composable
private fun CloudNotConfiguredBanner() {
    Card(
        Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CloudOff, null)
            Spacer(Modifier.width(12.dp))
            Text(
                "Firebase is not configured. Add google-services.json to enable the " +
                    "cloud community board.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CommunityPostCard(post: CommunityPost) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    post.title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(onClick = {}, label = { Text(post.sdg) })
            }
            if (post.message.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(post.message)
            }
            if (post.location.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(post.location, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    post.author.ifBlank { "Anonymous" },
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.weight(1f))
                post.createdAt?.let {
                    Text(
                        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Add-post screen — the write side of the Cloud Integration pillar. The form
 * pushes a new document straight to Firestore through [CommunityViewModel.addPost].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommunityPostScreen(viewModel: CommunityViewModel, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val addStatus by viewModel.addStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // React to the publish result: pop back on success, show the error otherwise.
    LaunchedEffect(addStatus) {
        when (val s = addStatus) {
            is AddPostStatus.Success -> {
                viewModel.resetStatus()
                navController.popBackStack()
            }
            is AddPostStatus.Error -> {
                snackbarHostState.showSnackbar("Could not publish: ${s.message}")
                viewModel.resetStatus()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share a Green Tip") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = message, onValueChange = { message = it },
                label = { Text("Your tip / description") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            OutlinedTextField(
                value = location, onValueChange = { location = it },
                label = { Text("Location (optional)") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = author, onValueChange = { author = it },
                label = { Text("Your name (optional)") }, modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.addPost(title, message, author, location) },
                enabled = title.isNotBlank() && addStatus !is AddPostStatus.Saving,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                if (addStatus is AddPostStatus.Saving) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Publishing…")
                } else {
                    Text("Publish to Cloud")
                }
            }
        }
    }
}

package com.rameeza.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationShowcase() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Compose Animations") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { SectionTitle("AnimateAsState") }
            item { AnimateAsStateDemo() }

            item { SectionTitle("AnimatedVisibility") }
            item { AnimatedVisibilityDemo() }

            item { SectionTitle("AnimatedContent") }
            item { AnimatedContentDemo() }

            item { SectionTitle("Crossfade") }
            item { CrossfadeDemo() }

            item { SectionTitle("UpdateTransition") }
            item { UpdateTransitionDemo() }

            item { SectionTitle("InfiniteTransition") }
            item { InfiniteTransitionDemo() }

            item { SectionTitle("AnimateContentSize") }
            item { AnimateContentSizeDemo() }

            item { SectionTitle("Animatable") }
            item { AnimatableDemo() }

            item { SectionTitle("LazyList Item Animations") }
            item { LazyListItemAnimationDemo() }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun AnimateAsStateDemo() {
    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.5f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val color by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        label = "color"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .background(color, RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded },
        contentAlignment = Alignment.Center
    ) {
        Text("Tap Me", color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun AnimatedVisibilityDemo() {
    var isVisible by remember { mutableStateOf(true) }
    Column {
        Button(onClick = { isVisible = !isVisible }) {
            Text(if (isVisible) "Hide" else "Show")
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + expandVertically() + slideInVertically(),
            exit = fadeOut() + shrinkVertically() + slideOutVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("I appear and disappear")
            }
        }
    }
}

@Composable
fun AnimatedContentDemo() {
    var state by remember { mutableIntStateOf(0) }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { state = 0 }) { Text("0") }
            Button(onClick = { state = 1 }) { Text("1") }
            Button(onClick = { state = 2 }) { Text("2") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "content"
        ) { targetState ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        when (targetState) {
                            0 -> Color(0xFFE91E63)
                            1 -> Color(0xFF9C27B0)
                            else -> Color(0xFF673AB7)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = targetState.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}

@Composable
fun CrossfadeDemo() {
    var currentPage by remember { mutableStateOf("A") }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { currentPage = "A" }) { Text("A") }
            Button(onClick = { currentPage = "B" }) { Text("B") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Crossfade(targetState = currentPage, label = "crossfade") { screen ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Content $screen", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun UpdateTransitionDemo() {
    var isSelected by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isSelected, label = "boxTransition")

    val size by transition.animateDp(label = "size") { selected ->
        if (selected) 120.dp else 80.dp
    }
    val color by transition.animateColor(label = "color") { selected ->
        if (selected) Color(0xFFFF9800) else Color(0xFF607D8B)
    }
    val elevation by transition.animateDp(label = "elevation") { selected ->
        if (selected) 12.dp else 2.dp
    }

    Card(
        modifier = Modifier
            .size(size)
            .clickable { isSelected = !isSelected },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun InfiniteTransitionDemo() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .rotate(rotation)
            .background(MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .offset(y = (-20).dp)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun AnimateContentSizeDemo() {
    var isExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Text(
            text = if (isExpanded) {
                "This is a longer text that will make the container grow. Jetpack Compose automatically animates the size change when you use Modifier.animateContentSize()."
            } else {
                "Tap to expand"
            }
        )
    }
}

@Composable
fun AnimatableDemo() {
    val offset = remember { Animatable(0f) }
    var target by remember { mutableStateOf(0f) }

    LaunchedEffect(target) {
        offset.animateTo(
            targetValue = target,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    }

    Column {
        Button(onClick = { target = if (target == 0f) 150f else 0f }) {
            Text("Move Box")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = offset.value
                }
                .size(50.dp)
                .background(Color(0xFF00BCD4), RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun LazyListItemAnimationDemo() {
    var items by remember { mutableStateOf((1..5).toList()) }
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { items = items.shuffled() }) { Text("Shuffle") }
            Button(onClick = { items = items + (items.maxOrNull() ?: 0).plus(1) }) { Text("Add") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.height(200.dp)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items, key = { it }) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                            .padding(16.dp)
                    ) {
                        Text("Item $item")
                    }
                }
            }
        }
    }
}

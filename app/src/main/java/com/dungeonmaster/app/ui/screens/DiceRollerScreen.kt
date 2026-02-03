package com.dungeonmaster.app.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dungeonmaster.app.data.models.DiceRoll
import com.dungeonmaster.app.data.models.DiceType
import com.dungeonmaster.app.ui.theme.*
import com.dungeonmaster.app.utils.DiceRoller
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen(
    onBack: () -> Unit
) {
    var selectedDice by remember { mutableStateOf<DiceType?>(null) }
    var diceCount by remember { mutableStateOf(1) }
    var modifier by remember { mutableStateOf(0) }
    var rollHistory by remember { mutableStateOf<List<DiceRoll>>(emptyList()) }
    var isRolling by remember { mutableStateOf(false) }
    var currentRoll by remember { mutableStateOf<DiceRoll?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun performRoll() {
        selectedDice?.let { dice ->
            coroutineScope.launch {
                isRolling = true
                delay(500) // Animation time
                val roll = DiceRoller.roll(
                    diceType = dice,
                    count = diceCount,
                    modifier = modifier,
                    purpose = "${diceCount}${dice.displayName}${if (modifier != 0) (if (modifier > 0) "+$modifier" else "$modifier") else ""}"
                )
                currentRoll = roll
                rollHistory = listOf(roll) + rollHistory.take(19) // Keep last 20 rolls
                isRolling = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dice Roller") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { rollHistory = emptyList() },
                        enabled = rollHistory.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear history")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Dice Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DiceType.entries.forEach { dice ->
                    DiceButton(
                        diceType = dice,
                        isSelected = selectedDice == dice,
                        onClick = { selectedDice = dice }
                    )
                }
            }

            // Count and Modifier
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dice Count
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Count",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (diceCount > 1) diceCount-- }
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = "$diceCount",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { if (diceCount < 20) diceCount++ }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .height(60.dp)
                            .width(1.dp)
                    )

                    // Modifier
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Modifier",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (modifier > -20) modifier-- }
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = if (modifier >= 0) "+$modifier" else "$modifier",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    modifier > 0 -> AccentGreen
                                    modifier < 0 -> AccentRed
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                            IconButton(
                                onClick = { if (modifier < 20) modifier++ }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }

            // Roll Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isRolling) {
                    RollingAnimation()
                } else {
                    currentRoll?.let { roll ->
                        RollResultDisplay(roll = roll)
                    } ?: Text(
                        text = "Select a die and roll!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Roll Button
            Button(
                onClick = { performRoll() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                enabled = selectedDice != null && !isRolling
            ) {
                Icon(Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedDice != null)
                        "Roll ${diceCount}${selectedDice!!.displayName}${if (modifier != 0) (if (modifier > 0) "+$modifier" else "$modifier") else ""}"
                    else "Select a Die",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Quick Roll Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickRollChip(
                    text = "1d20",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedDice = DiceType.D20
                        diceCount = 1
                        modifier = 0
                        performRoll()
                    }
                )
                QuickRollChip(
                    text = "2d6",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedDice = DiceType.D6
                        diceCount = 2
                        modifier = 0
                        performRoll()
                    }
                )
                QuickRollChip(
                    text = "4d6",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedDice = DiceType.D6
                        diceCount = 4
                        modifier = 0
                        performRoll()
                    }
                )
            }

            // Roll History
            Text(
                text = "History",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rollHistory) { roll ->
                    RollHistoryItem(roll = roll)
                }
            }
        }
    }
}

@Composable
private fun DiceButton(
    diceType: DiceType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (diceType) {
        DiceType.D4 -> DiceD4
        DiceType.D6 -> DiceD6
        DiceType.D8 -> DiceD8
        DiceType.D10 -> DiceD10
        DiceType.D12 -> DiceD12
        DiceType.D20 -> DiceD20
        DiceType.D100 -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = diceType.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RollingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "roll")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Icon(
        Icons.Default.Casino,
        contentDescription = "Rolling",
        modifier = Modifier
            .size(80.dp)
            .rotate(rotation),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun RollResultDisplay(roll: DiceRoll) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${roll.total}",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
            fontWeight = FontWeight.Bold,
            color = when {
                roll.isNatural20 -> AccentGreen
                roll.isNatural1 -> AccentRed
                else -> MaterialTheme.colorScheme.primary
            }
        )
        if (roll.isNatural20) {
            Text(
                text = "NATURAL 20!",
                style = MaterialTheme.typography.titleLarge,
                color = AccentGreen,
                fontWeight = FontWeight.Bold
            )
        } else if (roll.isNatural1) {
            Text(
                text = "NATURAL 1!",
                style = MaterialTheme.typography.titleLarge,
                color = AccentRed,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = roll.results.joinToString(" + ") + if (roll.modifier != 0) {
                if (roll.modifier > 0) " + ${roll.modifier}" else " - ${-roll.modifier}"
            } else "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickRollChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text)
    }
}

@Composable
private fun RollHistoryItem(roll: DiceRoll) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = roll.purpose.ifEmpty { "${roll.count}${roll.diceType.displayName}" },
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${roll.total}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    roll.isNatural20 -> AccentGreen
                    roll.isNatural1 -> AccentRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

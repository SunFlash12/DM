package com.dungeonmaster.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dungeonmaster.app.data.models.*
import com.dungeonmaster.app.ui.theme.*
import com.dungeonmaster.app.ui.viewmodels.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    sessionId: Long,
    onNavigateToCharacterSheet: (Long) -> Unit,
    onNavigateToDiceRoller: () -> Unit,
    onBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.character?.name ?: "Adventure",
                            style = MaterialTheme.typography.titleMedium
                        )
                        uiState.session?.currentLocation?.let { location ->
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Character HP
                    uiState.character?.let { character ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = AccentRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${character.currentHitPoints}/${character.maxHitPoints}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                    IconButton(onClick = { uiState.character?.id?.let { onNavigateToCharacterSheet(it) } }) {
                        Icon(Icons.Default.Person, contentDescription = "Character Sheet")
                    }
                    IconButton(onClick = onNavigateToDiceRoller) {
                        Icon(Icons.Default.Casino, contentDescription = "Dice Roller")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Pending Roll Dialog
                AnimatedVisibility(
                    visible = uiState.pendingRoll != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    uiState.pendingRoll?.let { rollRequest ->
                        PendingRollCard(
                            rollRequest = rollRequest,
                            onRoll = { viewModel.rollDice(rollRequest) },
                            onDismiss = { viewModel.dismissPendingRoll() }
                        )
                    }
                }

                // Message Input
                MessageInputBar(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    onSend = {
                        if (messageInput.isNotBlank()) {
                            viewModel.sendMessage(messageInput)
                            messageInput = ""
                        }
                    },
                    isEnabled = !uiState.isAiThinking,
                    isAiThinking = uiState.isAiThinking
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.messages) { message ->
                        ChatMessageItem(message = message)
                    }

                    // Typing indicator
                    if (uiState.isAiThinking) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }

            // Combat indicator
            if (uiState.combatState.isActive) {
                CombatBanner(
                    round = uiState.combatState.round,
                    onEndCombat = { viewModel.endCombat() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val backgroundColor = when (message.role) {
        MessageRole.USER -> MaterialTheme.colorScheme.primary
        MessageRole.DM -> MaterialTheme.colorScheme.surfaceVariant
        MessageRole.SYSTEM -> MaterialTheme.colorScheme.secondaryContainer
        MessageRole.NARRATOR -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val textColor = when (message.role) {
        MessageRole.USER -> MaterialTheme.colorScheme.onPrimary
        MessageRole.DM -> MaterialTheme.colorScheme.onSurfaceVariant
        MessageRole.SYSTEM -> MaterialTheme.colorScheme.onSecondaryContainer
        MessageRole.NARRATOR -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    val alignment = when (message.role) {
        MessageRole.USER -> Alignment.End
        else -> Alignment.Start
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Role label for non-user messages
        if (message.role != MessageRole.USER) {
            Text(
                text = when (message.role) {
                    MessageRole.DM -> "Dungeon Master"
                    MessageRole.SYSTEM -> "System"
                    MessageRole.NARRATOR -> "Narrator"
                    else -> ""
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp, start = 12.dp)
            )
        }

        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.role == MessageRole.USER) 16.dp else 4.dp,
                bottomEnd = if (message.role == MessageRole.USER) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = textColor,
                    style = if (message.role == MessageRole.NARRATOR)
                        MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                    else MaterialTheme.typography.bodyMedium
                )

                // Show dice roll results if present
                message.metadata?.diceRolls?.forEach { roll ->
                    Spacer(modifier = Modifier.height(8.dp))
                    DiceRollResult(roll = roll)
                }
            }
        }
    }
}

@Composable
private fun DiceRollResult(roll: DiceRoll) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Casino,
                contentDescription = null,
                tint = when {
                    roll.isNatural20 -> AccentGreen
                    roll.isNatural1 -> AccentRed
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = roll.purpose.ifEmpty { "${roll.count}${roll.diceType.displayName}" },
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Result: ${roll.total}",
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
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dungeon Master",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp, 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) { index ->
                val alpha = when (index) {
                    0 -> 1f
                    1 -> 0.7f
                    else -> 0.4f
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                )
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isEnabled: Boolean,
    isAiThinking: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        if (isAiThinking) "The DM is thinking..."
                        else "What do you do?"
                    )
                },
                enabled = isEnabled,
                maxLines = 3,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = onSend,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (isAiThinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingRollCard(
    rollRequest: com.dungeonmaster.app.ai.DiceRollRequest,
    onRoll: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Roll Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rollRequest.purpose,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = rollRequest.diceNotation,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Skip")
                }
                Button(onClick = onRoll) {
                    Icon(Icons.Default.Casino, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Roll!")
                }
            }
        }
    }
}

@Composable
private fun CombatBanner(
    round: Int,
    onEndCombat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AccentRed,
        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "COMBAT - Round $round",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(
                onClick = onEndCombat,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("End")
            }
        }
    }
}

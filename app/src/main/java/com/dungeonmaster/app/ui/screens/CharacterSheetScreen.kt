package com.dungeonmaster.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dungeonmaster.app.data.models.*
import com.dungeonmaster.app.data.models.Character
import com.dungeonmaster.app.data.repository.GameRepository
import com.dungeonmaster.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class CharacterSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository
) : ViewModel() {
    private val characterId: Long = savedStateHandle.get<Long>("characterId") ?: 0L

    private val _character = MutableStateFlow<Character?>(null)
    val character: StateFlow<Character?> = _character.asStateFlow()

    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())
    val sessions: StateFlow<List<GameSession>> = _sessions.asStateFlow()

    init {
        loadCharacter()
        loadSessions()
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            gameRepository.getCharacterByIdFlow(characterId).collect {
                _character.value = it
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            gameRepository.getSessionsForCharacter(characterId).collect {
                _sessions.value = it
            }
        }
    }

    fun createNewSession(onSessionCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val session = GameSession(
                name = "New Adventure",
                characterId = characterId,
                currentLocation = "Starting Area"
            )
            val sessionId = gameRepository.createSession(session)
            onSessionCreated(sessionId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    characterId: Long,
    onStartAdventure: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: CharacterSheetViewModel = hiltViewModel()
) {
    val character by viewModel.character.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "Character Sheet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.createNewSession(onStartAdventure) },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                text = { Text("Start Adventure") }
            )
        }
    ) { paddingValues ->
        character?.let { char ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Row
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Stats") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Skills") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Equipment") }
                    )
                }

                when (selectedTab) {
                    0 -> StatsTab(character = char)
                    1 -> SkillsTab(character = char)
                    2 -> EquipmentTab(character = char)
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun StatsTab(character: Character) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Character Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character.name.take(2).uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Level ${character.level} ${character.race.displayName} ${character.characterClass.displayName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = character.background.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Combat Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CombatStatCard(
                    modifier = Modifier.weight(1f),
                    title = "HP",
                    value = "${character.currentHitPoints}/${character.maxHitPoints}",
                    icon = Icons.Default.Favorite,
                    color = if (character.currentHitPoints < character.maxHitPoints / 2)
                        AccentRed else AccentGreen
                )
                CombatStatCard(
                    modifier = Modifier.weight(1f),
                    title = "AC",
                    value = "${character.armorClass}",
                    icon = Icons.Default.Shield,
                    color = MaterialTheme.colorScheme.primary
                )
                CombatStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Speed",
                    value = "${character.speed} ft",
                    icon = Icons.Default.DirectionsRun,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        // Ability Scores
        item {
            Text(
                text = "Ability Scores",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(Ability.entries) { ability ->
                    val score = character.getAbilityScore(ability)
                    val modifier = character.getAbilityModifier(ability)

                    AbilityCard(
                        ability = ability,
                        score = score,
                        modifier = modifier
                    )
                }
            }
        }

        // Proficiency Bonus
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Proficiency Bonus",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "+${character.proficiencyBonus}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Saving Throws
        item {
            Text(
                text = "Saving Throws",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Ability.entries.forEach { ability ->
                    val isProficient = character.savingThrowProficiencies.contains(ability)
                    val modifier = character.getSavingThrowModifier(ability)

                    SavingThrowRow(
                        ability = ability,
                        modifier = modifier,
                        isProficient = isProficient
                    )
                }
            }
        }
    }
}

@Composable
private fun CombatStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AbilityCard(
    ability: Ability,
    score: Int,
    modifier: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ability.abbreviation,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (modifier >= 0) "+$modifier" else "$modifier",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun SavingThrowRow(
    ability: Ability,
    modifier: Int,
    isProficient: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isProficient) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Proficient",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Not proficient",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = ability.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = if (modifier >= 0) "+$modifier" else "$modifier",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isProficient) FontWeight.Bold else FontWeight.Normal,
            color = if (isProficient) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SkillsTab(character: Character) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(Skill.entries.size) { index ->
            val skill = Skill.entries[index]
            val isProficient = character.skillProficiencies.contains(skill)
            val modifier = character.getSkillModifier(skill)

            SkillRow(
                skill = skill,
                modifier = modifier,
                isProficient = isProficient
            )
        }
    }
}

@Composable
private fun SkillRow(
    skill: Skill,
    modifier: Int,
    isProficient: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isProficient) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Proficient",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Not proficient",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = skill.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = skill.ability.abbreviation,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = if (modifier >= 0) "+$modifier" else "$modifier",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isProficient) FontWeight.Bold else FontWeight.Normal,
            color = if (isProficient) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EquipmentTab(character: Character) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gold
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = GoldSecondary.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gold",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "${character.gold} gp",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Equipped Items
        item {
            Text(
                text = "Equipped",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (character.equippedItems.isEmpty()) {
            item {
                Text(
                    text = "No items equipped",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(character.equippedItems.size) { index ->
                ItemCard(item = character.equippedItems[index], isEquipped = true)
            }
        }

        // Inventory
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inventory",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (character.inventory.isEmpty()) {
            item {
                Text(
                    text = "Inventory is empty",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(character.inventory.size) { index ->
                ItemCard(item = character.inventory[index], isEquipped = false)
            }
        }
    }
}

@Composable
private fun ItemCard(item: Item, isEquipped: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.type.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                item.properties?.damage?.let { damage ->
                    Text(
                        text = "Damage: $damage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (item.quantity > 1) {
                Text(
                    text = "x${item.quantity}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

package com.dungeonmaster.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dungeonmaster.app.data.models.*
import com.dungeonmaster.app.ui.theme.*
import com.dungeonmaster.app.ui.viewmodels.CharacterCreationViewModel
import com.dungeonmaster.app.ui.viewmodels.CreationStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: CharacterCreationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val abilityRolls by viewModel.abilityRolls.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Character") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.step == CreationStep.NAME) {
                            onBack()
                        } else {
                            viewModel.previousStep()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Progress Indicator
            StepProgressIndicator(
                currentStep = state.step,
                modifier = Modifier.padding(16.dp)
            )

            // Content
            AnimatedContent(
                targetState = state.step,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    CreationStep.NAME -> NameStep(
                        name = state.name,
                        onNameChange = viewModel::setName
                    )
                    CreationStep.RACE -> RaceStep(
                        selectedRace = state.selectedRace,
                        selectedSubrace = state.selectedSubrace,
                        onRaceSelect = viewModel::selectRace,
                        onSubraceSelect = viewModel::selectSubrace
                    )
                    CreationStep.CLASS -> ClassStep(
                        selectedClass = state.selectedClass,
                        onClassSelect = viewModel::selectClass
                    )
                    CreationStep.BACKGROUND -> BackgroundStep(
                        selectedBackground = state.selectedBackground,
                        onBackgroundSelect = viewModel::selectBackground
                    )
                    CreationStep.ABILITIES -> AbilitiesStep(
                        abilityScores = state.abilityScores,
                        abilityRolls = abilityRolls,
                        selectedRace = state.selectedRace,
                        onRollAbilities = viewModel::rollAbilityScores
                    )
                    CreationStep.SKILLS -> SkillsStep(
                        selectedClass = state.selectedClass,
                        selectedSkills = state.selectedSkills,
                        onSkillToggle = viewModel::toggleSkill
                    )
                    CreationStep.REVIEW -> ReviewStep(state = state)
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.step != CreationStep.NAME) {
                    OutlinedButton(
                        onClick = { viewModel.previousStep() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                }

                Button(
                    onClick = {
                        if (state.step == CreationStep.REVIEW) {
                            viewModel.createCharacter(onCharacterCreated)
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading && isStepValid(state)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (state.step == CreationStep.REVIEW) "Create Character" else "Next")
                    }
                }
            }
        }
    }
}

private fun isStepValid(state: com.dungeonmaster.app.ui.viewmodels.CharacterCreationState): Boolean {
    return when (state.step) {
        CreationStep.NAME -> state.name.isNotBlank()
        CreationStep.RACE -> state.selectedRace != null
        CreationStep.CLASS -> state.selectedClass != null
        CreationStep.BACKGROUND -> state.selectedBackground != null
        CreationStep.ABILITIES -> state.abilityScores.size == 6
        CreationStep.SKILLS -> state.selectedSkills.size == (state.selectedClass?.skillChoices ?: 0)
        CreationStep.REVIEW -> true
    }
}

@Composable
private fun StepProgressIndicator(
    currentStep: CreationStep,
    modifier: Modifier = Modifier
) {
    val steps = CreationStep.entries
    val currentIndex = steps.indexOf(currentStep)

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(steps.size) { index ->
            val isCompleted = index < currentIndex
            val isCurrent = index == currentIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isCurrent -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isCurrent) MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = steps[index].name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NameStep(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What is your name, adventurer?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Character Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

@Composable
private fun RaceStep(
    selectedRace: RaceType?,
    selectedSubrace: Subrace?,
    onRaceSelect: (RaceType) -> Unit,
    onSubraceSelect: (Subrace?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Choose Your Race",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(RaceType.entries) { race ->
            RaceCard(
                race = race,
                isSelected = selectedRace == race,
                onSelect = { onRaceSelect(race) }
            )

            // Show subraces if this race is selected and has subraces
            if (selectedRace == race && race.subraces.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose Subrace",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                race.subraces.forEach { subrace ->
                    SubraceCard(
                        subrace = subrace,
                        isSelected = selectedSubrace == subrace,
                        onSelect = { onSubraceSelect(subrace) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun RaceCard(
    race: RaceType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = race.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = race.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                race.abilityBonuses.forEach { (ability, bonus) ->
                    AssistChip(
                        onClick = { },
                        label = { Text("${ability.abbreviation} +$bonus") }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubraceCard(
    subrace: Subrace,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = subrace.displayName, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                subrace.abilityBonuses.forEach { (ability, bonus) ->
                    Text(
                        text = "${ability.abbreviation} +$bonus",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassStep(
    selectedClass: ClassType?,
    onClassSelect: (ClassType) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Choose Your Class",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(ClassType.entries) { classType ->
            ClassCard(
                classType = classType,
                isSelected = selectedClass == classType,
                onSelect = { onClassSelect(classType) }
            )
        }
    }
}

@Composable
private fun ClassCard(
    classType: ClassType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = classType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "d${classType.hitDie} HD",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = classType.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                classType.primaryAbility.forEach { ability ->
                    AssistChip(
                        onClick = { },
                        label = { Text(ability.abbreviation) }
                    )
                }
                if (classType.isSpellcaster) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Spellcaster") }
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundStep(
    selectedBackground: Background?,
    onBackgroundSelect: (Background) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Choose Your Background",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(Background.entries) { background ->
            BackgroundCard(
                background = background,
                isSelected = selectedBackground == background,
                onSelect = { onBackgroundSelect(background) }
            )
        }
    }
}

@Composable
private fun BackgroundCard(
    background: Background,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = background.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = background.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                background.skillProficiencies.forEach { skill ->
                    AssistChip(
                        onClick = { },
                        label = { Text(skill.displayName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AbilitiesStep(
    abilityScores: Map<Ability, Int>,
    abilityRolls: Map<Ability, List<Int>>,
    selectedRace: RaceType?,
    onRollAbilities: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Roll Your Abilities",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (abilityScores.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onRollAbilities,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Roll 4d6 Drop Lowest")
            }
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(Ability.entries) { ability ->
                    val score = abilityScores[ability] ?: 10
                    val racialBonus = selectedRace?.abilityBonuses?.get(ability) ?: 0
                    val rolls = abilityRolls[ability] ?: emptyList()

                    AbilityScoreCard(
                        ability = ability,
                        score = score,
                        racialBonus = racialBonus,
                        rolls = rolls
                    )
                }
            }

            Button(
                onClick = onRollAbilities,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reroll")
            }
        }
    }
}

@Composable
private fun AbilityScoreCard(
    ability: Ability,
    score: Int,
    racialBonus: Int,
    rolls: List<Int>
) {
    val modifier = Character.calculateModifier(score + racialBonus)

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
                text = ability.displayName,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${score + racialBonus}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (modifier >= 0) "+$modifier" else "$modifier",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            if (racialBonus > 0) {
                Text(
                    text = "(+$racialBonus racial)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            if (rolls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = rolls.joinToString(" "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SkillsStep(
    selectedClass: ClassType?,
    selectedSkills: List<Skill>,
    onSkillToggle: (Skill) -> Unit
) {
    val availableSkills = selectedClass?.availableSkills ?: emptyList()
    val maxSkills = selectedClass?.skillChoices ?: 2

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Choose Your Skills",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Select $maxSkills skills (${selectedSkills.size}/$maxSkills)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableSkills) { skill ->
                val isSelected = selectedSkills.contains(skill)
                val canSelect = selectedSkills.size < maxSkills || isSelected

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = canSelect) { onSkillToggle(skill) }
                        .then(
                            if (isSelected) Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(12.dp)
                            ) else Modifier
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            !canSelect -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = skill.displayName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = skill.ability.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewStep(
    state: com.dungeonmaster.app.ui.viewmodels.CharacterCreationState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Review Your Character",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = buildString {
                            append("Level 1 ")
                            append(state.selectedSubrace?.displayName ?: state.selectedRace?.displayName ?: "")
                            append(" ")
                            append(state.selectedClass?.displayName ?: "")
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.selectedBackground?.displayName ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Ability Scores",
                style = MaterialTheme.typography.titleLarge
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(Ability.entries) { ability ->
                    val baseScore = state.abilityScores[ability] ?: 10
                    val racialBonus = state.selectedRace?.abilityBonuses?.get(ability) ?: 0
                    val totalScore = baseScore + racialBonus
                    val modifier = Character.calculateModifier(totalScore)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = ability.abbreviation,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "$totalScore",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (modifier >= 0) "+$modifier" else "$modifier",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Skill Proficiencies",
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allSkills = state.selectedSkills +
                        (state.selectedBackground?.skillProficiencies ?: emptyList())
                allSkills.distinct().forEach { skill ->
                    AssistChip(
                        onClick = { },
                        label = { Text(skill.displayName) }
                    )
                }
            }
        }
    }
}

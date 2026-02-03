package com.dungeonmaster.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungeonmaster.app.data.models.*
import com.dungeonmaster.app.data.repository.GameRepository
import com.dungeonmaster.app.utils.DiceRoller
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharacterCreationState(
    val step: CreationStep = CreationStep.NAME,
    val name: String = "",
    val selectedRace: RaceType? = null,
    val selectedSubrace: Subrace? = null,
    val selectedClass: ClassType? = null,
    val selectedBackground: Background? = null,
    val abilityScores: Map<Ability, Int> = emptyMap(),
    val selectedSkills: List<Skill> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class CreationStep {
    NAME,
    RACE,
    CLASS,
    BACKGROUND,
    ABILITIES,
    SKILLS,
    REVIEW
}

@HiltViewModel
class CharacterCreationViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterCreationState())
    val state: StateFlow<CharacterCreationState> = _state.asStateFlow()

    private val _abilityRolls = MutableStateFlow<Map<Ability, List<Int>>>(emptyMap())
    val abilityRolls: StateFlow<Map<Ability, List<Int>>> = _abilityRolls.asStateFlow()

    fun setName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun selectRace(race: RaceType) {
        _state.update { it.copy(selectedRace = race, selectedSubrace = null) }
    }

    fun selectSubrace(subrace: Subrace?) {
        _state.update { it.copy(selectedSubrace = subrace) }
    }

    fun selectClass(characterClass: ClassType) {
        _state.update { it.copy(selectedClass = characterClass, selectedSkills = emptyList()) }
    }

    fun selectBackground(background: Background) {
        _state.update { it.copy(selectedBackground = background) }
    }

    fun rollAbilityScores() {
        val scores = mutableMapOf<Ability, Int>()
        val rolls = mutableMapOf<Ability, List<Int>>()

        Ability.entries.forEach { ability ->
            val diceRolls = (1..4).map { DiceRoller.rollDie(DiceType.D6) }
            val sortedRolls = diceRolls.sorted()
            val score = sortedRolls.drop(1).sum() // Drop lowest
            scores[ability] = score
            rolls[ability] = diceRolls
        }

        _abilityRolls.value = rolls
        _state.update { it.copy(abilityScores = scores) }
    }

    fun setAbilityScore(ability: Ability, score: Int) {
        _state.update { current ->
            current.copy(abilityScores = current.abilityScores + (ability to score))
        }
    }

    fun toggleSkill(skill: Skill) {
        _state.update { current ->
            val maxSkills = current.selectedClass?.skillChoices ?: 2
            val newSkills = if (current.selectedSkills.contains(skill)) {
                current.selectedSkills - skill
            } else if (current.selectedSkills.size < maxSkills) {
                current.selectedSkills + skill
            } else {
                current.selectedSkills
            }
            current.copy(selectedSkills = newSkills)
        }
    }

    fun nextStep() {
        _state.update { current ->
            val nextStep = when (current.step) {
                CreationStep.NAME -> if (current.name.isNotBlank()) CreationStep.RACE else current.step
                CreationStep.RACE -> if (current.selectedRace != null) CreationStep.CLASS else current.step
                CreationStep.CLASS -> if (current.selectedClass != null) CreationStep.BACKGROUND else current.step
                CreationStep.BACKGROUND -> if (current.selectedBackground != null) CreationStep.ABILITIES else current.step
                CreationStep.ABILITIES -> if (current.abilityScores.size == 6) CreationStep.SKILLS else current.step
                CreationStep.SKILLS -> {
                    val requiredSkills = current.selectedClass?.skillChoices ?: 2
                    if (current.selectedSkills.size == requiredSkills) CreationStep.REVIEW else current.step
                }
                CreationStep.REVIEW -> current.step
            }
            current.copy(step = nextStep)
        }
    }

    fun previousStep() {
        _state.update { current ->
            val prevStep = when (current.step) {
                CreationStep.NAME -> current.step
                CreationStep.RACE -> CreationStep.NAME
                CreationStep.CLASS -> CreationStep.RACE
                CreationStep.BACKGROUND -> CreationStep.CLASS
                CreationStep.ABILITIES -> CreationStep.BACKGROUND
                CreationStep.SKILLS -> CreationStep.ABILITIES
                CreationStep.REVIEW -> CreationStep.SKILLS
            }
            current.copy(step = prevStep)
        }
    }

    fun createCharacter(onSuccess: (Long) -> Unit) {
        val currentState = _state.value
        if (!isValid()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val race = Race(
                    type = currentState.selectedRace!!,
                    subrace = currentState.selectedSubrace
                )

                val characterClass = CharacterClass(type = currentState.selectedClass!!)

                // Apply racial ability bonuses
                val finalAbilities = currentState.abilityScores.toMutableMap()
                race.abilityBonuses.forEach { (ability, bonus) ->
                    finalAbilities[ability] = (finalAbilities[ability] ?: 10) + bonus
                }

                val constitutionMod = Character.calculateModifier(finalAbilities[Ability.CONSTITUTION] ?: 10)
                val hitDie = characterClass.hitDie
                val maxHp = hitDie + constitutionMod

                // Combine skill proficiencies from class and background
                val skillProficiencies = currentState.selectedSkills +
                        currentState.selectedBackground!!.skillProficiencies

                val character = Character(
                    name = currentState.name,
                    race = race,
                    characterClass = characterClass,
                    level = 1,
                    background = currentState.selectedBackground!!,
                    strength = finalAbilities[Ability.STRENGTH] ?: 10,
                    dexterity = finalAbilities[Ability.DEXTERITY] ?: 10,
                    constitution = finalAbilities[Ability.CONSTITUTION] ?: 10,
                    intelligence = finalAbilities[Ability.INTELLIGENCE] ?: 10,
                    wisdom = finalAbilities[Ability.WISDOM] ?: 10,
                    charisma = finalAbilities[Ability.CHARISMA] ?: 10,
                    maxHitPoints = maxHp,
                    currentHitPoints = maxHp,
                    armorClass = 10 + Character.calculateModifier(finalAbilities[Ability.DEXTERITY] ?: 10),
                    speed = race.speed,
                    savingThrowProficiencies = characterClass.savingThrowProficiencies,
                    skillProficiencies = skillProficiencies.distinct(),
                    gold = currentState.selectedBackground!!.gold
                )

                val characterId = gameRepository.saveCharacter(character)
                _state.update { it.copy(isLoading = false) }
                onSuccess(characterId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun isValid(): Boolean {
        val state = _state.value
        return state.name.isNotBlank() &&
                state.selectedRace != null &&
                state.selectedClass != null &&
                state.selectedBackground != null &&
                state.abilityScores.size == 6 &&
                state.selectedSkills.size == (state.selectedClass?.skillChoices ?: 0)
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

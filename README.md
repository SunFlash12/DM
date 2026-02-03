# AI Dungeon Master

A complete Android app that provides a D&D 5th Edition experience with an AI Dungeon Master powered by Google's Gemini API.

## Features

### Character Creation
- Full D&D 5e character creation wizard
- All Player's Handbook races (Human, Elf, Dwarf, Halfling, Dragonborn, Gnome, Half-Elf, Half-Orc, Tiefling)
- All PHB classes (Barbarian, Bard, Cleric, Druid, Fighter, Monk, Paladin, Ranger, Rogue, Sorcerer, Warlock, Wizard)
- 13 backgrounds with unique features
- 4d6 drop lowest ability score rolling
- Skill proficiency selection

### AI Dungeon Master
- Powered by Google Gemini (free tier: 60 requests/minute)
- Dynamic storytelling and narrative generation
- Proper D&D 5e rules enforcement
- Combat management
- NPC interactions
- Multiple adventure settings:
  - Classic Fantasy
  - Dark Gothic
  - High Magic
  - Sword and Sorcery
  - Seafaring Adventure

### Game Features
- Full chat interface with the AI DM
- Automatic dice roll prompts
- Combat tracking with initiative order
- Character sheet with:
  - Ability scores and modifiers
  - Saving throws
  - Skill proficiencies
  - Equipment and inventory
  - Gold tracking
- Standalone dice roller with:
  - All standard dice (d4, d6, d8, d10, d12, d20, d100)
  - Configurable count and modifier
  - Roll history
  - Quick roll buttons

### Technical Features
- Modern Android development with Kotlin
- Jetpack Compose UI
- MVVM architecture with ViewModels
- Room database for persistence
- Hilt dependency injection
- DataStore for preferences
- Dark/Light theme support

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- A free Google Gemini API key

### Getting Your Free API Key
1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API key"
4. Copy the key

### Building the App
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on an emulator or device (API 26+)

### First Run
1. Launch the app
2. Go to Settings
3. Enter your Gemini API key
4. Create a character
5. Start your adventure!

## Project Structure

```
app/src/main/java/com/dungeonmaster/app/
├── ai/                     # AI integration
│   └── DungeonMasterAI.kt  # Gemini API integration
├── data/
│   ├── database/           # Room database
│   │   ├── AppDatabase.kt
│   │   ├── CharacterDao.kt
│   │   ├── ChatMessageDao.kt
│   │   ├── Converters.kt
│   │   └── GameSessionDao.kt
│   ├── models/             # Data models
│   │   ├── Background.kt
│   │   ├── Character.kt
│   │   ├── CharacterClass.kt
│   │   ├── GameState.kt
│   │   ├── Item.kt
│   │   ├── Race.kt
│   │   └── Spell.kt
│   └── repository/         # Data repositories
│       ├── GameRepository.kt
│       └── SettingsRepository.kt
├── di/                     # Dependency injection
│   └── AppModule.kt
├── ui/
│   ├── components/         # Reusable UI components
│   ├── screens/            # App screens
│   │   ├── CharacterCreationScreen.kt
│   │   ├── CharacterListScreen.kt
│   │   ├── CharacterSheetScreen.kt
│   │   ├── DiceRollerScreen.kt
│   │   ├── GameScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── NewGameScreen.kt
│   │   └── SettingsScreen.kt
│   ├── theme/              # App theming
│   │   ├── Theme.kt
│   │   └── Typography.kt
│   ├── viewmodels/         # ViewModels
│   │   ├── CharacterCreationViewModel.kt
│   │   ├── GameViewModel.kt
│   │   ├── HomeViewModel.kt
│   │   └── SettingsViewModel.kt
│   └── Navigation.kt       # Navigation setup
├── utils/
│   └── DiceRoller.kt       # Dice rolling utilities
├── DungeonMasterApp.kt     # Application class
└── MainActivity.kt         # Main activity
```

## Dependencies

- **Jetpack Compose** - Modern declarative UI
- **Room** - SQLite database abstraction
- **Hilt** - Dependency injection
- **Navigation Compose** - Navigation
- **DataStore** - Preferences storage
- **Kotlin Coroutines** - Asynchronous programming
- **Kotlin Serialization** - JSON serialization
- **Google Generative AI** - Gemini API client

## D&D 5e Content

This app implements core D&D 5e rules including:
- Ability score generation and modifiers
- Proficiency bonuses by level
- Skill checks and saving throws
- Combat mechanics (attack rolls, damage, AC)
- Spellcasting basics
- Conditions and status effects
- Experience and leveling

Content is based on the Open Gaming License (OGL) SRD.

## License

This project is for educational and personal use. D&D content is based on material available under the Open Gaming License.

## Contributing

Feel free to submit issues and pull requests for:
- Bug fixes
- New features
- Additional D&D content
- UI improvements

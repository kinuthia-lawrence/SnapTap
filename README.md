# SnapTap

SnapTap is a desktop application built with JavaFX for managing profiles and providing a customizable user experience with light and dark themes.

## Features

- Profile management (create, switch, and manage profiles)
- Light and dark mode support
- Modern UI with JavaFX and Ikonli icons
- Persistent settings and preferences
- Keyboard shortcut support (via JNativeHook)
- Logging with SLF4J and Logback

## Requirements

- Java 21 or higher
- Maven 3.6+

## Getting Started

1. **Clone the repository:**
   ```sh
   git clone https://github.com/uziar-abrar/SnapTap.git
   cd SnapTap
   ```

2. **Build the project:**
   ```sh
   mvn clean package
   ```

3. **Run the application:**
   ```sh
   mvn javafx:run
   ```

## Project Structure

- `src/main/java` — Application source code
- `src/main/resources` — FXML, icons, and other resources
- `pom.xml` — Maven build configuration

## Dependencies

- JavaFX (controls, FXML)
- Ikonli (FontAwesome5)
- SLF4J & Logback (logging)
- JNativeHook (global hotkeys)
- Jackson (JSON serialization)

## Customization

- **Themes:** The app supports both light and dark modes, automatically applying styles to dialogs and UI elements.
- **Profiles:** Easily switch between different user profiles from the main interface.

## License

This project is licensed under the MIT License.

---
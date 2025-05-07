# RoomForge - Smart Furniture Preview

## Fixed Issues

1. **Encoding Issues**: Fixed character encoding issues in the admin and designer dashboard panels by replacing problematic emoji characters with simpler text icons.

2. **SuperAdmin Setup**: Ensured the SuperAdmin setup form appears correctly on first application launch.

3. **Build Configuration**: Added proper UTF-8 encoding configuration to the Gradle build script.

## Running the Application

To run the application with the fixed configuration:

```bash
./gradlew run
```

## Building the Application

To create a standalone JAR file:

```bash
./gradlew fatJar
```

The JAR file will be located in `app/build/libs/app-fat.jar`.

## Technologies Used

- Java Swing for the UI
- FlatLaf for modern UI styling
- MigLayout for flexible layouts
- SQLite for local database
- JBCrypt for password hashing
- JMonkeyEngine for 3D rendering
```

## Running the Application

After making these changes, you should be able to build and run the application successfully:

```bash
./gradlew run
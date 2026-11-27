# Tavernkeeper

> Every community deserves a tavern.

Tavernkeeper is a modular Discord bot designed for community
management, member progression, virtual economies and role automation.

## Project status

Early development.

## Requirements

- Java 25 or later
- Apache Maven

## Configuration

Tavernkeeper supports configuration through environment
variables and an optional `.env` file.

Copy `.env.example` to `.env` in the project root and
provide your Discord bot token.

| Variable        | Required | Default                | Description                      |
| --------------- | -------- | ---------------------- | -------------------------------- |
| `DISCORD_TOKEN` | Yes      | —                      | Discord bot authentication token |
| `DATABASE_PATH` | No       | `data/tavernkeeper.db` | SQLite database location         |

Configuration is resolved in the following order:

1. Environment variables
2. Local `.env` file
3. Application defaults

Environment variables take precedence over values defined
in `.env`.

Never commit the `.env` file or share your Discord bot token.

## Local development (Visual Studio Code)

1. Clone the repository.
2. Install Java 25 and Maven.
3. Open the project in Visual Studio Code.
4. Install the Extension Pack for Java.
5. Configure the `.env` file.
6. Create `.vscode/launch.json` with the following configuration:

   ```json
   {
     "version": "0.2.0",
     "configurations": [
       {
         "type": "java",
         "name": "Tavernkeeper",
         "request": "launch",
         "mainClass": "io.github.binarybaggins.tavernkeeper.App",
         "cwd": "${workspaceFolder}",
         "vmArgs": "--enable-native-access=ALL-UNNAMED",
         "console": "integratedTerminal"
       }
     ]
   }
   ```

7. Press `F5` to start Tavernkeeper.
8. Press `Ctrl+C` in the integrated terminal to shut down
   Tavernkeeper gracefully.

The database and its parent directory are created automatically
during application startup. Database migrations are managed
by Flyway.

### Security

Tavernkeeper loads `.env` directly through dotenv-java.
Do not configure `envFile` in the VS Code launch configuration,
as this may expose credentials in the generated terminal command.

The `.env` file, local IDE configuration and runtime database
are excluded from version control.

## Testing

```powershell
mvn clean verify
```

Unit tests do not require a Discord connection or bot token.

## Contributing

Create a feature branch and submit a pull request.
All changes must pass the automated CI checks before merging.

## License

Tavernkeeper is licensed under the MIT License.
See [LICENSE](LICENSE) for details.

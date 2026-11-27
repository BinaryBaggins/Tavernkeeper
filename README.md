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

Copy `.env.example` to `.env` and provide your Discord bot token:

    DISCORD_TOKEN=your_bot_token

Never commit the `.env` file or share your bot token.

## Local development

1. Clone the repository.
2. Install Java 25 and Maven.
3. Open the project in Visual Studio Code.
4. Configure a Java launch configuration with `.env`
   as its environment file.
5. Run the application.

## Testing

    mvn clean verify

Unit tests do not require a Discord connection or bot token.

## Contributing

Create a feature branch and submit a pull request.
All changes must pass the automated CI checks before merging.

## License

Tavernkeeper is licensed under the MIT License.
See [LICENSE](LICENSE) for details.

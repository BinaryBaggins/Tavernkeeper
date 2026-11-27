CREATE TABLE
    guild_settings (
        guild_id TEXT PRIMARY KEY NOT NULL,
        locale TEXT NOT NULL DEFAULT 'en',
        FOREIGN KEY (guild_id) REFERENCES guilds (guild_id) ON DELETE CASCADE
    );

CREATE TABLE
    guild_features (
        guild_id TEXT NOT NULL,
        feature_key TEXT NOT NULL,
        enabled INTEGER NOT NULL DEFAULT 0 CHECK (enabled IN (0, 1)),
        PRIMARY KEY (guild_id, feature_key),
        FOREIGN KEY (guild_id) REFERENCES guilds (guild_id) ON DELETE CASCADE
    );
# Environment Configuration (Web Only)

This configuration is **only used for the web (JS) build**. The Mapbox map feature is web-only.

## Quick Start

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Get your Mapbox token:**
   - Go to https://account.mapbox.com/access-tokens/
   - Copy your token (starts with `pk.`)

3. **Add to `.env`:**
   ```env
   MAPBOX_ACCESS_TOKEN=pk.eyJ1IjoieW91cl90b2tlbiIsImEiOiJjbHh4eHgifQ.xxxx
   ```

4. **Run the web app:**
   ```bash
   ./gradlew :composeApp:jsBrowserDevelopmentRun
   ```

That's it! The token is automatically loaded and injected into the web build.

## Setup Instructions

### 1. Create your .env file

Copy the example file:
```bash
cp .env.example .env
```

### 2. Get your Mapbox Access Token

1. Sign up or log in at [Mapbox](https://account.mapbox.com/)
2. Go to [Access Tokens](https://account.mapbox.com/access-tokens/)
3. Create a new token or copy an existing one
4. Paste it into your `.env` file

### 3. Configure .env

Edit `.env` and replace the placeholder:

```env
MAPBOX_ACCESS_TOKEN=pk.eyJ1IjoieW91cl91c2VybmFtZSIsImEiOiJjbHh4eHh4eHgifQ.xxxxxxxxxxxxxxxxx
```

**⚠️ IMPORTANT:** Never commit the `.env` file to git! It's already in `.gitignore`.

## How it Works

### Web/JS Build (Only Platform Using .env)
- `webpack.config.d/env.js` loads the `.env` file during build
- Environment variables are injected via webpack DefinePlugin
- Available at runtime via `process.env.MAPBOX_ACCESS_TOKEN`
- **This is the only platform that needs the API key**

### Other Platforms (JVM, Android, iOS)
- Map feature is web-only
- Config returns empty string on other platforms
- No .env configuration needed

### Common Code
- Access via `Config.MAPBOX_ACCESS_TOKEN` from anywhere
- Defined in `composeApp/src/commonMain/kotlin/org/example/project/config/Config.kt`
- Platform-specific implementations handle loading

## Troubleshooting

### "MAPBOX_ACCESS_TOKEN is not set" error

1. Verify `.env` file exists in project root
2. Check that it contains: `MAPBOX_ACCESS_TOKEN=pk.ey...`
3. Restart the webpack dev server
4. Check browser console for webpack environment injection logs

### Token not working

1. Verify token is valid at https://account.mapbox.com/access-tokens/
2. Ensure token has the required scopes (at minimum: `styles:read`, `fonts:read`)
3. Check for extra whitespace in `.env` file

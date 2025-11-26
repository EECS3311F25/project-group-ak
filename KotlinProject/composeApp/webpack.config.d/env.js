/**
 * Load environment variables from .env file
 * and inject them into the webpack build
 */

console.log('==========================================');
console.log('🔧 WEBPACK ENV.JS IS EXECUTING');
console.log('==========================================');

const path = require('path');
const fs = require('fs');
const webpack = require('webpack');

// Function to load .env file
function loadEnvFile() {
    // When webpack runs, __dirname is in build/js/packages/projectName/webpack.config.d
    // We need to go up to the project root
    // Try multiple possible paths
    const possiblePaths = [
        path.resolve(__dirname, '../../.env'),           // Original guess
        path.resolve(__dirname, '../../../.env'),        // One more level up
        path.resolve(__dirname, '../../../../.env'),     // Two more levels up
        path.resolve(__dirname, '../../../../../.env'),  // Three more levels up
        path.resolve(__dirname, '../../../../../../.env'), // Four more levels up (likely the right one)
    ];
    
    const env = {};
    let foundPath = null;
    
    console.log('📂 Searching for .env file...');
    
    for (const envPath of possiblePaths) {
        console.log('   Trying:', envPath);
        if (fs.existsSync(envPath)) {
            foundPath = envPath;
            console.log('   ✅ Found!');
            break;
        }
    }
    
    if (!foundPath) {
        console.warn('⚠️  No .env file found in any of the searched paths');
        console.warn('__dirname is:', __dirname);
        return env;
    }
    
    console.log('📂 Loading .env from:', foundPath);
    
    const envFile = fs.readFileSync(foundPath, 'utf-8');
    envFile.split('\n').forEach(line => {
        line = line.trim();
        // Skip comments and empty lines
        if (line && !line.startsWith('#')) {
            const [key, ...valueParts] = line.split('=');
            const value = valueParts.join('=').trim();
            env[key.trim()] = value;
        }
    });
    console.log('✅ Loaded .env file with keys:', Object.keys(env));
    console.log('✅ MAPBOX_ACCESS_TOKEN length:', env.MAPBOX_ACCESS_TOKEN ? env.MAPBOX_ACCESS_TOKEN.length : 0);
    
    return env;
}

// Load environment variables
const env = loadEnvFile();

// This function is called by Kotlin/JS webpack integration
// The config object should be available in the global scope
(function() {
    if (typeof config === 'undefined') {
        console.error('❌ webpack config object is not available!');
        return;
    }
    
    console.log('✅ webpack config object is available');
    
    // Initialize plugins array if it doesn't exist
    config.plugins = config.plugins || [];
    
    // Inject into webpack DefinePlugin
    config.plugins.push(
        new webpack.DefinePlugin({
            'process.env.MAPBOX_ACCESS_TOKEN': JSON.stringify(env.MAPBOX_ACCESS_TOKEN || ''),
        })
    );
    
    console.log('✅ Added DefinePlugin with MAPBOX_ACCESS_TOKEN');
    
    // Also make available on window object for runtime access
    config.plugins.push(
        new webpack.BannerPlugin({
            banner: `window.MAPBOX_ACCESS_TOKEN = ${JSON.stringify(env.MAPBOX_ACCESS_TOKEN || '')};`,
            raw: true,
            entryOnly: false,
        })
    );
    
    console.log('✅ Added BannerPlugin to inject into window object');
    console.log('✅ Environment variables injected into webpack build');
})();

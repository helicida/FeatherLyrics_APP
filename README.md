# Feather Lyrics

This repository contains the source code for the Feather Lyrics Android APP.

## How does it work?

The APP's puts working on background a BroadcastReceiver that gets the song name and artist from the the current playing track.
When the user interface is opened, the APP takes the data collected by the BroadcastReceiver. When user demands it, lyrics from the actual song are downloaded.

· The app uses two API's:

- Vagalume API -> http://api.vagalume.com.br/docs/
- Spotify API -> https://developer.spotify.com/web-api/

The lyrics, and album arts are taken from Vagalume's API, artist pictures are taken from Spotfy's API.

· Plus one aditional storage service to handle the user system:

- Firebase -> https://www.firebase.com/


## Features:

The application concept is fully modular, so the structure is based on a Navigation Drawer formed by fragments. The app has differents menus wich allow the user to search an specific song or artist, reading recent news from music world and lets the user personalize the APP to its liking using the Preference menu.

- Follows the Material Design philosophy
- User friendly interface
- On background BroadcastReceiver tracking your playing music
- Automatic current playing song search
- Artist discography searching menu
- Played songs history (using a ContentProvider)
- Beautifull Splash Screen with a simple animation
- Audio playing when starting as the application tone
- Notifications (Toasts) with playing song information
- Firebase based login
- You are able to create your own account and track your friends music
- Meet new people in your area with similar music tastes (AOSMap services)
- Cute widget for tracking the current song lyrics
- Custom PreferenceFragment menu to custom the APP to your liking
- "About" section with terms and conditions
- Battery life friendly :)

## Which audio players are supported?

Feather Lyrics tracks the next players:

* Android stock music player
* Google Music
* LastFM Player
* Samsung Galaxy S3 Music Player
* Winamp
* Amazon Music
* MIUI Player
* Real
* Sony Ericsson Player
* Rdio
* Apollo Music Player
* Spotify
* PowerAmp
* Rhapsody Music Player

## Screenshots

<img src="http://i.imgur.com/JDCUmOG.png" width="250">
<img src="http://i.imgur.com/ObSxjv0.jpg" width="250">
<img src="http://i.imgur.com/Y4do4gK.png" width="250">
<img src="http://i.imgur.com/1CDRmde.png" width="250">
<img src="http://i.imgur.com/tnExHHU.jpg" width="250">
<img src="http://i.imgur.com/2GfK3RI.png" width="250">
<img src="http://i.imgur.com/fkU3dhN.png" width="250">
<img src="http://i.imgur.com/5qANarU.jpg" width="250">



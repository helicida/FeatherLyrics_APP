# Feather Lyrics - Stable Version

This repository contains the source code for the Feather Lyrics (Android APP).

## How does it work?

The APP's puts working on background a BroadcastReceiver that gets listens at the song and artist name from the the current playing track on Android media.
When the user interface is opened, the APP takes the data collected by the BroadcastReceiver. When requested it, lyrics from the actual song are downloaded.

· API Rest Services:

The following API's have been already implemented in the same software layer, are set to communicate beetwen them and are working together to achieve great results.

- Vagalume API -> http://api.vagalume.com.br/docs/
- Spotify API -> https://developer.spotify.com/web-api/
- Last.Fm -> http://www.last.fm/es/api

The lyrics, and album arts are taken from Vagalume's API, artist pictures are taken from Spotfy's API and the artist's bio is requested to LastFm API.

· Plus one aditional storage service to handle the users system:

- Firebase -> https://www.firebase.com/


## Features:

The application concept is fully modular, so the structure is based on a Navigation Drawer filled by fragments. The app has differents menus wich allow the user to search an specific song or artist, reading recent news from music world, looking at other user's profiles... and a Preference Menu that allows the user custom the APP behaviour to its liking.

- Follows the Material Design philosophy
- User friendly interface
- On background BroadcastReceiver tracking your playing music
- Automatic current playing song search
- Artist discography searching menu
- Played songs history (using a ContentProvider)
- Beautifull Splash Screen with a simple animation
- Audio playing when starting as the application tone
- Notifications (Toasts) with playing song information
- Firebase based login to create your own account with your personal profile.
- Automatic data sync 
- Meet new people in your area with similar music tastes (AOSMap services)
- Cute widget to quickly get the playing song lyrics
- Custom PreferenceFragment menu to custom the APP to your liking
- "About" section with terms and conditions
- Battery life friendly :)
- The application is available in three languages: English, Spanish and Catalan.

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

<img src="http://i.imgur.com/DvU2PgC.png" width="250">
<img src="http://i.imgur.com/N3FMvXL.png" width="250">
<img src="https://i.imgur.com/3xFZWlF.png" width="250">
<img src="http://i.imgur.com/Y4do4gK.png" width="250">
<img src="http://i.imgur.com/aDUEhtE.png" width="250">
<img src="http://i.imgur.com/IcIJCIl.png" width="250">
<img src="http://i.imgur.com/Iib4Plu.jpg" width="250">
<img src="http://i.imgur.com/k27CaCs.png" width="250">
<img src="http://i.imgur.com/T4aZzis.png" width="250">
<img src="http://i.imgur.com/1CDRmde.png" width="250">
<img src="http://i.imgur.com/2GfK3RI.png" width="250">
<img src="http://i.imgur.com/fkU3dhN.png" width="250">
<img src="http://i.imgur.com/5qANarU.jpg" width="250">

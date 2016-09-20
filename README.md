# Feather Lyrics - Stable Version

This repository contains the source code for the Feather Lyrics (Android APP).

## How does it work?

The APP's puts working on background a BroadcastReceiver that pulls out the song and artist name from the the current playing track on Android media.
When the user interface is opened, the APP takes the data collected by the BroadcastReceiver. When its demanded, the lyrics from the actual played song are downloaded.

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
- On background BroadcastReceiver tracking the playing music
- Automatic current playing song search
- Artist discography searching menu
- Played songs history (using a ContentProvider)
- Beautifull Splash Screen that shows dynamically while the main thread loads it content
- Startup application tone
- Notifications (Toasts) showing playing song information
- Firebase based login and register to create your own account with your personal profile.
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


<img src="http://i.imgur.com/CtvBySK.png" width="250">
<img src="http://i.imgur.com/yXLIj4A.png" width="250">
<img src="http://i.imgur.com/NEjmdy5.png" width="250">
<img src="http://i.imgur.com/Xb1VGsz.png" width="250">
<img src="http://i.imgur.com/PDdeapV.png" width="250">
<img src="http://i.imgur.com/6oxqY16.png" width="250">
<img src="http://i.imgur.com/40IY3kK.png" width="250">
<img src="http://i.imgur.com/v0SNTCu.png" width="250">
<img src="http://i.imgur.com/Kf3KNpZ.png" width="250">
<img src="http://i.imgur.com/n55x9to.png" width="250">
<img src="http://i.imgur.com/qh7VuIU.png" width="250">
<img src="http://i.imgur.com/2GfK3RI.png" width="250">
<img src="http://i.imgur.com/S3VRpCz.png" width="250">
<img src="http://i.imgur.com/5qANarU.jpg" width="250">

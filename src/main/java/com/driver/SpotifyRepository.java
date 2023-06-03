package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile)                                             // 1st API - Done
    {
        //create the user with given name and number

        for(User st : users){
            if(mobile.equals(st.getMobile()))
                return st;
        }

        User user = new User(name,mobile);
        users.add(user);
        userPlaylistMap.put(user,new ArrayList<>());

        return user;
    }

    // -------------------------------------------------------------------------------
    public Artist createArtist(String name)                                                        // 2nd API - Done
    {
        //create the artist with given name

        for(Artist st : artists){
            if(name.equals(st.getName()))
                return st;
        }

        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist,new ArrayList<>());

        return artist;
    }

  // -----------------------------------------------------------------------------------
    public Artist checkIfArtistExists(String artistName){

        for(Artist artist : artists){

            if(artistName.equals(artist.getName()))
                return artist;
        }
        return  null;
    }

    public Album createAlbum(String title, String artistName)                                      // 3rd API - Done
    {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist

        Artist artist = checkIfArtistExists(artistName);

        if(artist == null) {
            artist = createArtist(artistName);
        }

        Album album = new Album(title);
        albums.add(album);

        artistAlbumMap.get(artist).add(album);
        albumSongMap.put(album,new ArrayList<>());

        return album;
    }

    // ----------------------------------------------------------------------------------
    public Album checkIfAlbumExists(String albumName){

        for(Album album : albums){

            if(album.getTitle().equals(albumName))
                return album;
        }
        return null;
    }

    public Song createSong(String title, String albumName, int length) throws Exception            // 4th API - Done
    {
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album

        Album album = checkIfAlbumExists(albumName);

        if(album == null)
            throw new Exception("Album does not exist");

        // Create Song
        Song song = new Song(title,length);
        songs.add(song);

        albumSongMap.get(album).add(song);
        songLikeMap.put(song,new ArrayList<>());

        return song;
    }

    // ---------------------------------------------------------------------------------
    public List<Song> getSongWithGivenLength(int length){

        List<Song> listOfSong = new ArrayList<>();

        for(Song song : songs){

            if(song.getLength() == length)
                listOfSong.add(song);
        }
        return listOfSong;
    }

    public User checkIfUserExists(String mobile){

        for(User user : users)
        {
            if(user.getMobile().equals(mobile))
                return user;
        }
        return null;
    }      // whether user exist or not

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception   // 5th API - Done
    {
        // Create a playlist with given title and add all songs having the given length in the database to that playlist
        // The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        // If the user does not exist, throw "User does not exist" exception

        User user = checkIfUserExists(mobile);
        if(user == null)
            throw new Exception("User does not exist");

        Playlist playlist = new Playlist(title);

        //  Put in playlist-Song-Map , playlist-Listener-Map , creatorPlaylistMap , user-Playlist-Map  (HashMap)

        List<Song> listOfSongs = getSongWithGivenLength(length);

        playlistSongMap.put(playlist,listOfSongs);

        playlistListenerMap.put(playlist,new ArrayList<>());
        playlistListenerMap.get(playlist).add(user);

        creatorPlaylistMap.put(user,playlist);

        userPlaylistMap.get(user).add(playlist);

        playlists.add(playlist);
        return playlist;
    }

    // ---------------------------------------------------------------------------------


    public void getSongsWithGivenTitle(String title,List<Song> songList){

        for(Song song : songs){

            if(song.getTitle().equals(title))
            {
                if(!songList.contains(song))
                    songList.add(song);
            }
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception  // 6th API - Done
    {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        User user = checkIfUserExists(mobile);

        if(user == null)
            throw new Exception("User does not exist");

        Playlist playlist = new Playlist(title);
        List<Song> listOfSongs = new ArrayList<>();

        for(String st : songTitles){
            getSongsWithGivenTitle(st,listOfSongs);
        }

        //  Put in playlist-Song-Map , playlist-Listener-Map , creatorPlaylistMap , user-Playlist-Map  (HashMap)

        playlistSongMap.put(playlist,listOfSongs);

        playlistListenerMap.put(playlist,new ArrayList<>());
        playlistListenerMap.get(playlist).add(user);

        creatorPlaylistMap.put(user,playlist);

        userPlaylistMap.get(user).add(playlist);

        playlists.add(playlist);
        return playlist;
    }

    // -------------------------------------------------------------------------------------
    public Playlist checkIfPlaylistExists(String playlistTitle){

        for(Playlist playlist : playlists){

            if(playlist.getTitle().equals(playlistTitle))
                return playlist;
        }
        return null;
    }

    public boolean checkIfUserIsACreator(User user , Playlist playlist) {
        if(creatorPlaylistMap.containsKey(user))
            return creatorPlaylistMap.get(user).equals(playlist);

        return false;
    }

    public boolean checkIfUserIsAListener(User user , Playlist playlist)
    {
        return playlistListenerMap.get(playlist).contains(user);
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception             // 7th API - done
    {
        // 1. Find the playlist with given title and add user as listener of that playlist and update user accordingly
        // 2. If the user is creator or already a listener, do nothing
        // 3. If the user does not exist, throw "User does not exist" exception
        // 4. If the playlist does not exist, throw "Playlist does not exist" exception
        // 5. Return the playlist after updating.

        Playlist playlist = null;

        playlist = checkIfPlaylistExists(playlistTitle);

        if(playlist == null)
            throw new Exception("Playlist does not exist");

        User user = null;
        user = checkIfUserExists(mobile);

        if(user == null)
            throw new Exception("User does not exist");

        boolean isUserAListenerOrACreator = checkIfUserIsACreator(user,playlist) ||
                checkIfUserIsAListener(user,playlist);

        if(isUserAListenerOrACreator)
            return playlist;

        playlistListenerMap.get(playlist).add(user);

        userPlaylistMap.get(user).add(playlist);

        return playlist;
    }

    // ---------------------------------------------------------------------------------------

    public Song checkIfSongExists(String songTitle){

        for(Song song : songs)
        {
            if(song.getTitle().equals(songTitle))
                return song;
        }
        return null;
    }

    public Album getAlbumOfTheSong(Song song){

        for(Album album : albumSongMap.keySet())
        {
            if(albumSongMap.get(album).contains(song))
                return album;
        }
        return null;
    }

    public Artist getArtistOfTheAlbum(Album album){

        for(Artist artist : artistAlbumMap.keySet())
        {
            if(artistAlbumMap.get(artist).contains(album))
                return artist;
        }
        return null;
    }


    public Song likeSong(String mobile, String songTitle) throws Exception                         // 8th API - done
    {
        // 1. The user likes the given song. The corresponding artist of the song gets auto-liked
        // 2. A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        // 3. However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        // 4. If the user does not exist, throw "User does not exist" exception
        // 5. If the song does not exist, throw "Song does not exist" exception
        // 6. Return the song after updating

        User user = null;
        user = checkIfUserExists(mobile);

        if(user == null)
            throw new Exception("User does not exist");

        // 6.
        Song song = null;
        song = checkIfSongExists(songTitle);

        if(song == null)
            throw new Exception("Song does not exist");

        Album album = getAlbumOfTheSong(song);
        Artist artist = getArtistOfTheAlbum(album);

        int songLikes = 0;
        int artistLikes = 0;

        if(songLikeMap.containsKey(song))
        {
            if(!songLikeMap.get(song).contains(user))
            {
                songLikeMap.get(song).add(user);
                songLikes = song.getLikes() + 1;
                artistLikes = artist.getLikes() + 1;
                song.setLikes(songLikes);
                artist.setLikes(artistLikes);
            }
        }

        else
        {
            songLikeMap.put(song,new ArrayList<>());
            songLikeMap.get(song).add(user);

            songLikes = song.getLikes() + 1;
            artistLikes = artist.getLikes() + 1;
            song.setLikes(songLikes);
            artist.setLikes(artistLikes);
        }

        return song;
    }

    // -----------------------------------------------------------------------------------------

    public String mostPopularArtist()                                                              // 9th API - Done
    {
        //Return the artist name with maximum likes

        String ans = "";
        int max = 0;

        for(Artist st : artists)
        {
            if(max < st.getLikes()){
                max = st.getLikes();
                ans = st.getName();
            }
        }
        return ans;
    }

    // ------------------------------------------------------------------------------------------

    public String mostPopularSong()                                                                // 10th API - Done
    {
        //return the song title with maximum likes

        String ans = "";
        int max = 0;

        for(Song st : songs)
        {
            if(max < st.getLikes()){
                max = st.getLikes();
                ans = st.getTitle();
            }
        }
        return ans;
    }
}

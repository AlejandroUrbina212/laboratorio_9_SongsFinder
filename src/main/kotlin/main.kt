import com.github.kittinunf.fuel.Fuel
import dbmodels.FavoriteSongs
import models.Song
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun initializeDatabase() {
    val url = "https://next.json-generator.com/api/json/get/EkeSgmXycS"

    val (request, response, result) = Fuel.get(url).responseObject(Song.SongArrayDeserializer())
    val (songs, err) = result


    Database.connect(
            "jdbc:postgresql:songs",
            "org.postgresql.Driver",
            "postgres",
            "Admin"
    )
    transaction {
        SchemaUtils.create(FavoriteSongs)
        songs?.forEach { currentSong -> FavoriteSongs.insert {
            it[year] = currentSong.year
            it[country]=currentSong.country
            it[region]=currentSong.region
            it[artistName] = currentSong.artistName
            it[song] = currentSong.song
            it[artistGender] = currentSong.artistGender
            it[groupOrSolo] = currentSong.groupOrSolo
            it[place] = currentSong.place
            it[points] = currentSong.points
            it[isFinal] = currentSong.isFinal
            it[isSongInEnglish] = currentSong.isSongInEnglish
            it[songQuality] = currentSong.songQuality
            it[normalizedPoints] = currentSong.normalizedPoints
            it[energy] =currentSong.energy
            it[duration] = currentSong.duration
            it[acousticness] = currentSong.acousticness
            it[danceability] = currentSong.danceability
            it[tempo] = currentSong.tempo
            it[speechiness] = currentSong.speechiness
            it[key] = currentSong.key
            it[liveness] = currentSong.liveness
            it[timeSignature] = currentSong.timeSignature
            it[mode] = currentSong.mode
            it[loudness] = currentSong.loudness
            it[valence] = currentSong.valence
            it[happiness] = currentSong.happiness
            it[isFavorite] = "false"

        } }

    }

    Thread.sleep(10000)
}












fun getMenu():String {
    return """
        Menu principal
        ----------------
        1. Buscar canciones por nombre
        2. Buscar canciones por artista
        3. Mostrar todas mis canciones favoritas
        4. Salir
        """.trimIndent()

}

fun main (args: Array<String>) {
    initializeDatabase() //Llenado inicial de base de datos

    var wantsToContinue = true
    // ciclo principal, segun la opcion ingresada realiza un accion
    do {
        println(getMenu())
        print(">>> ")
        val strOption = readLine()!!
        val option = strOption.toInt()
        when (option) {
            1 -> {
                //Filtro por nombre de cancion
                print("""
                        Menu
                        ------
                        Ingrese parte del nombre de la cancion que desea buscar:
                    """.trimIndent())

                val songName = readLine()!!

                println("CANCIONES QUE CONTENGAN: $songName")
                val songs:ArrayList<Song> = getSongsByName(songName)
                songs.forEachIndexed { index, song -> println("$index. ${song.song}") }

                print("Desea guardar alguna cancion como favorita? (si/no)")  //Escribir 'si'
                val answer = readLine()!!
                if(answer.toLowerCase() == "si") {
                    print("Cual?: ")
                    val strIndexSong = readLine()!!
                    val indexSong = strIndexSong.toInt()

                    setFavoriteSong(songs[indexSong])
                    println("OK, Listo!")
                }
            }
            2 -> {
                //Filtro por nombre de artista
                print("""
                        Menu
                        ------
                        Ingrese parte del nombre del artista que desea buscar:
                    """.trimIndent())

                val artistName = readLine()!!

                println("CANCIONES DE: $artistName")
                val songs:ArrayList<Song> = getSongsByArtist(artistName)
                songs.forEachIndexed { index, song -> println("$index. ${song.song}") }

                print("Desea guardar alguna cancion como favorita? (si/no)")  //Escribir 'si'
                val answer = readLine()!!
                if(answer.toLowerCase() == "si") {
                    print("Cual?: ")
                    val strIndexSong = readLine()!!
                    val indexSong = strIndexSong.toInt()

                    setFavoriteSong(songs[indexSong])
                    println("OK, Listo!")
                }
            }
            3 -> {
                //Filtro donde isFavorite sea True
                val favoriteSongs:ArrayList<Song> = getFavoriteSongs()
                favoriteSongs.forEachIndexed { index, song -> println("$index. ${song.song}") }
            }
            4 -> {
                //Salir
                wantsToContinue = false
            }
        }

    } while (wantsToContinue)
}

fun setFavoriteSong(song: Song) {
    //TODO: hacer una busqueda por id, comparar y cambiar isFavorite a True
}

fun getSongsByArtist(artist: String): ArrayList<Song> {
    return ArrayList()
}

fun getSongsByName(name:String): ArrayList<Song> {
    return ArrayList()
}

fun getFavoriteSongs(): ArrayList<Song> {
    return ArrayList()
}



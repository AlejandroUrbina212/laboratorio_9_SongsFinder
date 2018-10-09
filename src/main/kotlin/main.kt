import com.github.kittinunf.fuel.Fuel
import dbmodels.FavoriteSongs
import models.SimpleSong
import models.Song
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun initializeDatabase() {
    val url = "https://next.json-generator.com/api/json/get/EkeSgmXycS"

    val (request, response, result) = Fuel.get(url).responseObject(Song.SongArrayDeserializer())
    val (songs, err) = result


    Database.connect(
            "jdbc:postgresql:songs?createDatabaseIfNotExist=true",
            "org.postgresql.Driver",
            "postgres",
            "Admin"
    )
    transaction {
        SchemaUtils.drop(FavoriteSongs)
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
                val songs = getSongsByName(songName)

                if(songs.isNotEmpty()){
                    songs.forEachIndexed { index, song -> println("${index + 1}. ${song.name} Artista: ${song.artistName}") }

                    print("Desea guardar alguna cancion como favorita? (si/no): ")  //Escribir 'si'
                    val answer = readLine()!!
                    if(answer.toLowerCase() == "si") {
                        print("Cual?: ")
                        val strIndexSong = readLine()!!
                        val indexSong = strIndexSong.toInt()

                        setFavoriteSong(songs[indexSong - 1].id )
                        println("OK, Listo!")
                    }
                } else {
                    println("Lista vacia :( intente de nuevo...")
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
                val songs = getSongsByArtist(artistName)

                if(songs.isNotEmpty()){
                    songs.forEachIndexed { index, song ->
                        println("${index + 1}. ${song.name} Artista: ${song.artistName}") }

                    print("Desea guardar alguna cancion como favorita? (si/no): ")  //Escribir 'si'
                    val answer = readLine()!!
                    if(answer.toLowerCase() == "si") {
                        print("Cual?: ")
                        val strIndexSong = readLine()!!
                        val indexSong = strIndexSong.toInt()

                        setFavoriteSong(songs[indexSong - 1].id )
                        println("OK, Listo!")
                    }
                } else {
                    println("Lista vacia :( intente de nuevo...")
                }

            }
            3 -> {
                //Filtro donde isFavorite sea True
                val favoriteSongs = getFavoriteSongs()
                favoriteSongs.forEachIndexed { index, song -> println("${index + 1}. ${song.name}") }
            }
            4 -> {
                //Salir
                wantsToContinue = false
            }
        }

    } while (wantsToContinue)
}

fun setFavoriteSong(songId: Int) {
    //transacción que hace update en la colección FavoriteSongs donde el ID de la canción sea igual al solicitado en
    //los parámetros. Cambiándolo a true
    transaction {
        FavoriteSongs.update({ FavoriteSongs.id.eq(songId)}) {
            it[FavoriteSongs.isFavorite] = "true"
        }
    }
}

fun getSongsByArtist(artist: String): List<SimpleSong> {
    //inicialización del array de canciones que solo contienen tres atributos.
    var simpleSongList:List<SimpleSong> = ArrayList()
    transaction {
        //mapeo a una lista de SimpleSongs de  todos los resultados devueltos por select, buscando por artista.
        simpleSongList = FavoriteSongs.select{ FavoriteSongs.artistName.like("%${artist}%") }.map{
            SimpleSong(it[FavoriteSongs.id], it[FavoriteSongs.song], it[FavoriteSongs.artistName])
        }
    }
    return simpleSongList
}

fun getSongsByName(name:String): List<SimpleSong> {
    var simpleSongList:List<SimpleSong> = ArrayList()
    transaction {
        //mapeo a una lista de SimpleSongs de  todos los resultados devueltos por select, buscando por nombre o que contenga
        // el string name provisto en los atributos de la firma de esa función
        simpleSongList = FavoriteSongs.select{ FavoriteSongs.song.like("%${name}%") }.map{
            SimpleSong(it[FavoriteSongs.id], it[FavoriteSongs.song], it[FavoriteSongs.artistName])
        }
    }
    return simpleSongList
}

fun getFavoriteSongs(): List<SimpleSong> {
    var simpleSongList:List<SimpleSong> = ArrayList()
    transaction {
        simpleSongList = FavoriteSongs.select{ FavoriteSongs.isFavorite.eq("true") }.map{
            SimpleSong(it[FavoriteSongs.id], it[FavoriteSongs.song], it[FavoriteSongs.artistName])
        }
    }
    return simpleSongList
}



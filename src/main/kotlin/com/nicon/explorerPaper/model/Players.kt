import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "players")
data class Players(
    @DatabaseField(id = true)
    var uuid: String = "",

    @DatabaseField(columnName = "user_name")
    var userName: String = "",

    @DatabaseField
    var gold: Int = 0,

    @DatabaseField
    var amethyst: Int = 0,

    @DatabaseField(columnName = "blocks_mined")
    var blocksMined: Int = 0,

    @DatabaseField(columnName = "unlocked_recipes", defaultValue = "[]")
    var unlockedRecipes: String = "[]",

    @DatabaseField
    var level: Int = 0,

    @DatabaseField
    var xp: Int = 0
)
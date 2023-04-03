import java.sql.Connection
import java.sql.DriverManager

import java.sql.SQLException
import java.time.Instant

class Store {
    private var connection: Connection? = null

    @Throws(SQLException::class)
    fun connect(file: String) {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:$file")
        connection!!.createStatement().use { statement ->
            statement.execute(
                """CREATE TABLE IF NOT EXISTS usage (
                    id INTEGER PRIMARY KEY,
                    gym varchar(255) NOT NULL,
                    date timestamp NOT NULL,
                    current int NOT NULL,
                    allowed int NOT NULL,
                    usage float NOT NULL
                );
                """
            )
        }
    }

    @Throws(SQLException::class)
    fun storeInfo(gym: String, current: Int, allowed: Int, usage: Double) {
        val query = """
            INSERT INTO usage (gym, date, current, allowed, usage)
            VALUES (?, ?, ? ,?, ?)
        """;
        connection!!.prepareStatement(query).use { statement ->
            statement.setString(1, gym)
            statement.setObject(2, Instant.now())
            statement.setInt(3, current)
            statement.setInt(4, allowed)
            statement.setFloat(5, usage.toFloat())
            statement.executeUpdate()
        }
    }
}
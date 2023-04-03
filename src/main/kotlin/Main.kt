import org.json.JSONObject
import java.net.URL
import java.text.NumberFormat

const val API = "https://www.fitnessfirst.de/club/api/checkins/";

fun main() {
    val studios = linkedMapOf(
        "Westend" to "frankfurt9",
        "Eckenheim" to "frankfurt3",
        "MyZeil" to "frankfurt10",
        "Opernplatz" to "frankfurt12",
        "Ostend" to "frankfurt11",
        "Eschenheimer Turm" to "frankfurt7",
        "Sachsenhausen" to "frankfurt4"
    )
    val store = Store()
    store.connect("database.db")

    studios.forEach { (name, identifier) ->
        val response = JSONObject(URL("$API$identifier").readText()).getJSONObject("data")
        val current = response.getInt("check_ins")
        val allowed = response.getInt("allowed_people")
        val usage = current / allowed.toDouble();
        val percentage = NumberFormat.getPercentInstance().format(usage)
        println("$name: $current/$allowed $percentage")
        store.storeInfo(name, current, allowed, usage)
    }
}
package extract.demo

import java.text.SimpleDateFormat
import java.util.*


fun epochSecondsToString(seconds: Int): String {
    val date = Date(seconds * 1000L)
    val sdf = SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val formattedDate = sdf.format(date)

    return formattedDate
}
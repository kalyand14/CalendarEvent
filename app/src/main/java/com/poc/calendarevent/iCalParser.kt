package com.poc.calendarevent

import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import java.io.InputStream


class ICALParser(ics: InputStream?) {
    private val calendar: Calendar
    private val event: VEvent?

    init {
        val builder = CalendarBuilder()
        calendar = builder.build(ics)
        event = findFirstEvent()
    }

    fun buildIntent(): Intent {
        val i = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
        if (DEBUG) {
            for (o in event?.properties!!) {
                val property: Property = o as Property
                Log.d(TAG,
                    "Property [" + property.getName().toString() + ", " + property.getValue()
                        .toString() + "]"
                )
            }
        }
        i.putExtra(CalendarContract.Events.TITLE, getValueOrNull(Property.SUMMARY))
        i.putExtra(CalendarContract.Events.DESCRIPTION, getValueOrNull(Property.DESCRIPTION))
        i.putExtra(CalendarContract.Events.EVENT_LOCATION, getValueOrNull(Property.LOCATION))
        val start: Long = event?.startDate?.date?.time ?: 0
        val end: Long = event?.endDate?.date?.time ?: 0
        i.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
        i.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)

        /*val allDayEvent =
            ((event?.getProperty("X-MICROSOFT-CDO-ALLDAYEVENT")!! != null //Microsoft's custom
                    // field exists
                    && event?.getProperty("X-MICROSOFT-CDO-ALLDAYEVENT")!!.getValue()
                .equals("true")) //  and is true, or
                    || end - start % 1000 * 60 * 60 * 24 == 0L) //the duration is an integer number of days*/


        i.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        i.putExtra(CalendarContract.Events.RRULE, getValueOrNull(Property.RRULE))
        return i
    }

    private fun getValueOrNull(name: String): String? {
        return event?.getProperty<Property?>(name)?.value
    }

    private fun findFirstEvent(): VEvent? {
        for (o in calendar.components) {
            val c: Component = o as Component
            val e: VEvent? = if (c is VEvent) c as VEvent else null
            if (e != null) {
                return e
            }
        }
        return null
    }

    companion object {
        val TAG = ICALParser::class.java.canonicalName
        val DEBUG: Boolean = true
    }
}
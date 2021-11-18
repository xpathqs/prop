package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IValueProcessor
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class ProcessDate(source: IValueProcessor) : ValueProcessorDecorator(source) {
    private val formatter = SimpleDateFormat("dd.MM.yyyy")
    override fun selfProcess(obj: Any): Any {
        if(obj is String) {
            if(obj.contains("\${")) {
                val start = obj.indexOf("\${")
                val end = obj.indexOf("}", start + 2)
                val varName = obj.substring(start + 2, end)

                val dmy = varName.split(".")
                if(dmy.size == 3
                    && dmy[0].lowercase().startsWith("d")
                    && dmy[1].lowercase().startsWith("m")
                    && dmy[2].lowercase().startsWith("y")
                ) {
                    var ldt = LocalDateTime.now()

                    if(dmy[0].contains("+")) {
                        val plus = dmy[0].substringAfter("+").toLong()
                        ldt = ldt.plusDays(plus)
                    }
                    if(dmy[0].contains("-")) {
                        val plus = dmy[0].substringAfter("-").toLong()
                        ldt = ldt.minusDays(plus)
                    }

                    if(dmy[1].contains("+")) {
                        val plus = dmy[1].substringAfter("+").toLong()
                        ldt = ldt.plusMonths(plus)
                    }
                    if(dmy[1].contains("-")) {
                        val plus = dmy[1].substringAfter("-").toLong()
                        ldt = ldt.minusMonths(plus)
                    }

                    if(dmy[2].contains("+")) {
                        val plus = dmy[2].substringAfter("+").toLong()
                        ldt = ldt.plusYears(plus)
                    }
                    if(dmy[2].contains("-")) {
                        val plus = dmy[2].substringAfter("-").toLong()
                        ldt = ldt.minusYears(plus)
                    }

                    return obj.replace("\${$varName}",
                        "${ldt.dayOfMonth}.${ldt.monthValue}.${ldt.year}"
                    )
                }
            }
        }
        return obj
    }
}
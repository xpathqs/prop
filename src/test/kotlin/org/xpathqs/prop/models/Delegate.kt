package org.xpathqs.prop.models

import kotlin.properties.Delegates

class Delegate {
    var s: String by Delegates.observable("asd") { prop, old, new ->
        println("new")
    }
}
package com.ibbd.laba4.entities.enums

enum class Checkings {
    PHONE_NUMBER {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is String ||
                !Regex("""^\(?9\d{2}\)?[\s.-]?\d{3}[\s.-]?((\d{4})|(\d{2}[\s.-]?\d{2}))$""").matches(value)
            )
                "Phone number is invalid!\n"
            else null
        }
    },
    USERNAME {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is String ||
                value == ""
            )
                "Username is invalid!\n"
            else null
        }
    },
    NAME {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is String ||
                value == ""
            )
                "Name is invalid!\n"
            else null
        }
    },
//    RATING {
//        override fun check(value: Any?): String? {
//            return if (value == null ||
//                value !is Double ||
//                value < 0.0 || value > 5.0
//            )
//                "Rating is invalid!\n"
//            else null
//        }
//    },TODO("Delete rating check")
    WORK_EXPERIENCE {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is Int ||
                value < 0
            )
                "Work experience is invalid!\n"
            else null
        }
    },
    LOCATION {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is Pair<*, *> ||
                value.first !is Double ||
                value.second !is Double
            )
                "Pick up location is invalid!\n"
            else null
        }
    },
    INT {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is Int
            )
                "Int is invalid!\n"
            else null
        }
    },
    STRING {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is String
            )
                "String is invalid!\n"
            else null
        }
    },
    PLATE {
        override fun check(value: Any?): String? {
            return if (value == null ||
                value !is String ||
                !Regex("""^([АВЕКМНОРСТУХ]){1}\d{3}([АВЕКМНОРСТУХ]){2}\d{2,3}$""").matches(value)
            )
                "Plate is invalid!\n"
            else null
        }
    };

    abstract fun check(value: Any?): String?
}
package pt.dourobats.app.core.domain.model

/**
 * Represents application theme options.
 *
 * @property displayName Human-readable name for the theme
 */
enum class Theme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System Default");

    companion object {
        /**
         * Converts a string value to a Theme enum.
         * Returns SYSTEM as the default if the value doesn't match any theme.
         *
         * @param value The string representation of the theme
         * @return The corresponding Theme enum value, or SYSTEM if not found
         */
        fun fromValue(value: String): Theme {
            return entries.find { it.name == value } ?: SYSTEM
        }
    }
}

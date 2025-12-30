package pt.dourobats.app.features.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.dourobats.app.features.settings.ProfileEditState
import pt.dourobats.app.features.settings.SettingsUiState

/**
 * Dialog for editing user profile information.
 *
 * @param editState Current state of edited profile fields
 * @param email User's email (read-only, shown but disabled)
 * @param validationErrors Current validation errors
 * @param onDisplayNameChange Callback when display name changes
 * @param onPhoneNumberChange Callback when phone number changes
 * @param onSave Callback when save button is clicked
 * @param onDismiss Callback when dialog should be dismissed
 * @param modifier Optional modifier for the dialog
 */
@Composable
fun ProfileEditDialog(
    editState: ProfileEditState,
    email: String,
    validationErrors: SettingsUiState.ValidationErrors,
    onDisplayNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit Profile")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display Name field
                OutlinedTextField(
                    value = editState.displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Display Name") },
                    isError = validationErrors.displayName != null,
                    supportingText = validationErrors.displayName?.let {
                        { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Email field (read-only)
                OutlinedTextField(
                    value = email,
                    onValueChange = { }, // Read-only, no action
                    label = { Text("Email") },
                    enabled = false, // Disabled to show it's read-only
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Phone Number field
                OutlinedTextField(
                    value = editState.phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    label = { Text("Phone Number") },
                    isError = validationErrors.phoneNumber != null,
                    supportingText = validationErrors.phoneNumber?.let {
                        { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                enabled = !validationErrors.hasErrors
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

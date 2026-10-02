package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.WorkshopSettings
import com.example.data.sync.SupabaseSyncService
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPlaceholder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun SettingsScreen(
    currentSettings: WorkshopSettings,
    language: AppLanguage,
    onSaveSettings: (WorkshopSettings) -> Unit,
    onOpenBackupExport: () -> Unit,
    onTestSupabase: (String, String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onSyncSupabase: ((Boolean, String) -> Unit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var workshopName by remember(currentSettings) { mutableStateOf(currentSettings.workshopName) }
    var workshopPhone by remember(currentSettings) { mutableStateOf(currentSettings.workshopPhone) }
    var workshopCity by remember(currentSettings) { mutableStateOf(currentSettings.workshopCity) }
    var autoPrint by remember(currentSettings) { mutableStateOf(currentSettings.autoPrintAfterIntake) }
    var themeMode by remember(currentSettings) { mutableStateOf(currentSettings.themeMode) }

    var supabaseUrl by remember(currentSettings) { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseAnonKey by remember(currentSettings) { mutableStateOf(currentSettings.supabaseAnonKey) }
    var supabaseAutoSync by remember(currentSettings) { mutableStateOf(currentSettings.supabaseAutoSync) }

    var isTestingConnection by remember { mutableStateOf(false) }
    var isSyncingNow by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBg)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Large Page Title (Apple/Google style)
        Text(
            text = Strings.settingsTitle(language),
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (language == AppLanguage.AR) "تخصيص معلومات ورشة الصيانة والطباعة والربط السحابي" else "Personnalisation atelier, stickers & Cloud Sync (Supabase)",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: WORKSHOP IDENTITY
        SettingsGroupCard(title = Strings.workshopInfo(language)) {
            // Workshop Name
            OutlinedTextField(
                value = workshopName,
                onValueChange = { workshopName = it },
                label = { Text(Strings.workshopName(language), color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = BrandBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_workshop_name"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = BorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceSubtle,
                    unfocusedContainerColor = SurfaceSubtle
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Phone
            OutlinedTextField(
                value = workshopPhone,
                onValueChange = { workshopPhone = it },
                label = { Text(Strings.workshopPhone(language), color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldSoft) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_workshop_phone"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = BorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceSubtle,
                    unfocusedContainerColor = SurfaceSubtle
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // City
            OutlinedTextField(
                value = workshopCity,
                onValueChange = { workshopCity = it },
                label = { Text(Strings.workshopCity(language), color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = BorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceSubtle,
                    unfocusedContainerColor = SurfaceSubtle
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 2: CLOUD SYNC & SUPABASE
        SettingsGroupCard(title = if (language == AppLanguage.AR) "الربط السحابي (Supabase / Database)" else "Synchronisation Cloud (Supabase / Base de données)") {
            Text(
                text = if (language == AppLanguage.AR) "قم بربط التطبيق بقاعدة بيانات Supabase لمزامنة هواتف الزبائن مع لوحة تحكم على الحاسوب أو موقع الويب."
                else "Liez l'application à votre base Supabase pour synchroniser les tickets d'atelier avec un tableau de bord web ou PC.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Project URL
            OutlinedTextField(
                value = supabaseUrl,
                onValueChange = { supabaseUrl = it },
                label = { Text("Supabase Project URL", color = TextSecondary) },
                placeholder = { Text("https://your-project.supabase.co", color = TextPlaceholder) },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = BrandBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_supabase_url"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = BorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceSubtle,
                    unfocusedContainerColor = SurfaceSubtle
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Anon Key
            OutlinedTextField(
                value = supabaseAnonKey,
                onValueChange = { supabaseAnonKey = it },
                label = { Text("Supabase Anon Public API Key", color = TextSecondary) },
                placeholder = { Text("eyJhbGciOiJIUzI1NiIsIn...", color = TextPlaceholder) },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = BrandBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_supabase_key"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = BorderSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceSubtle,
                    unfocusedContainerColor = SurfaceSubtle
                )
            )

            if (currentSettings.lastSyncTimestamp.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (language == AppLanguage.AR) "آخر مزامنة: ${currentSettings.lastSyncTimestamp}" else "Dernière synchro : ${currentSettings.lastSyncTimestamp}",
                    color = EmeraldSoft,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons for Supabase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Test Connection
                OutlinedButton(
                    onClick = {
                        if (supabaseUrl.isBlank() || supabaseAnonKey.isBlank()) {
                            Toast.makeText(context, "Veuillez d'abord saisir l'URL et la clé Supabase", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        isTestingConnection = true
                        onTestSupabase(supabaseUrl, supabaseAnonKey) { success, message ->
                            isTestingConnection = false
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BrandBlue, strokeWidth = 2.dp)
                    } else {
                        Text(if (language == AppLanguage.AR) "فحص الاتصال" else "Tester", fontSize = 12.sp)
                    }
                }

                // Sync Now
                Button(
                    onClick = {
                        isSyncingNow = true
                        // Ensure settings are saved first
                        onSaveSettings(currentSettings.copy(supabaseUrl = supabaseUrl, supabaseAnonKey = supabaseAnonKey))
                        onSyncSupabase { success, msg ->
                            isSyncingNow = false
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.weight(1.3f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White)
                ) {
                    if (isSyncingNow) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.AR) "مزامنة الآن" else "Synchroniser", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Copy SQL Script Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceSubtle)
                    .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("SupabaseSQL", SupabaseSyncService.SUPABASE_TABLE_SQL)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, if (language == AppLanguage.AR) "تم نسخ كود SQL لـ Supabase إلى الحافظة !" else "Script SQL Supabase copié !", Toast.LENGTH_SHORT).show()
                    }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (language == AppLanguage.AR) "نسخ كود SQL لإنشاء الجدول في Supabase" else "Copier le script SQL pour Supabase Editor",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Table 'repairs' avec colonnes & politique RLS",
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 3: APPEARANCE & COMFORT
        SettingsGroupCard(title = Strings.appearance(language)) {
            // Theme Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Brightness4, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.AR) "الوضع الفاتح المريح" else "Mode Clair Confort",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "ألوان ناصعة ومريحة لعين الفني في الورشة" else "Palette douce et épurée inspirée d'Apple/Google",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Switch(
                    checked = themeMode == "light",
                    onCheckedChange = { isLight ->
                        themeMode = if (isLight) "light" else "dark"
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = BorderSoft
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Language Selector Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.languageTitle(language),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isAr = language == AppLanguage.AR
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAr) BrandBlue else SurfaceSubtle)
                            .border(1.dp, if (isAr) BrandBlue else BorderSoft, RoundedCornerShape(10.dp))
                            .clickable {
                                onSaveSettings(currentSettings.copy(language = "ar"))
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "العربية",
                            color = if (isAr) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isAr) BrandBlue else SurfaceSubtle)
                            .border(1.dp, if (!isAr) BrandBlue else BorderSoft, RoundedCornerShape(10.dp))
                            .clickable {
                                onSaveSettings(currentSettings.copy(language = "fr"))
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Français",
                            color = if (!isAr) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 4: PRINTING & STICKERS
        SettingsGroupCard(title = Strings.printingPrefs(language)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.autoPrintSticker(language),
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Format: 50x30mm thermal label",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Switch(
                    checked = autoPrint,
                    onCheckedChange = { autoPrint = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = BorderSoft
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 5: DATA & BACKUP
        SettingsGroupCard(title = Strings.dataBackup(language)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceSubtle)
                    .border(1.dp, BorderSoft, RoundedCornerShape(12.dp))
                    .clickable { onOpenBackupExport() }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.AR) "تصدير أو استيراد قاعدة البيانات" else "Exporter ou Importer la base",
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Format JSON • Prêt pour synchronisation web",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Settings Button
        Button(
            onClick = {
                val updated = currentSettings.copy(
                    workshopName = workshopName,
                    workshopPhone = workshopPhone,
                    workshopCity = workshopCity,
                    autoPrintAfterIntake = autoPrint,
                    themeMode = themeMode,
                    supabaseUrl = supabaseUrl,
                    supabaseAnonKey = supabaseAnonKey,
                    supabaseAutoSync = supabaseAutoSync
                )
                onSaveSettings(updated)
                Toast.makeText(context, if (language == AppLanguage.AR) "تم حفظ الإعدادات بنجاح" else "Paramètres enregistrés !", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp), spotColor = BrandBlue.copy(alpha = 0.3f))
                .testTag("save_settings_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.saveChanges(language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Strings.aboutSubtitle(language),
                color = TextMuted,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Maroc • Clean Workshop Register",
                color = TextPlaceholder,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SettingsGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0F000000))
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderSoft, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

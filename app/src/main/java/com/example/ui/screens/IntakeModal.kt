package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RepairRecord
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSoftBg
import com.example.ui.theme.MonospaceCodeLarge
import com.example.ui.theme.MonospacePhone
import com.example.ui.theme.MonospacePrice
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPlaceholder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.Strings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IntakeModal(
    language: AppLanguage,
    nextCodePair: Pair<String, Int>,
    editingRecord: RepairRecord?,
    onSave: (
        code: String,
        codeNumber: Int,
        brand: String,
        model: String,
        color: String,
        problem: String,
        price: Double,
        clientName: String,
        clientPhone: String,
        notes: String,
        andPrint: Boolean
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf(editingRecord?.code ?: nextCodePair.first) }
    var codeNumber by remember { mutableIntStateOf(editingRecord?.codeNumber ?: nextCodePair.second) }
    var isManualCodeOverride by remember { mutableStateOf(false) }

    var selectedBrand by remember { mutableStateOf(editingRecord?.brand ?: "Samsung") }
    var model by remember { mutableStateOf(editingRecord?.model ?: "") }
    var color by remember { mutableStateOf(editingRecord?.color ?: "Noir") }
    var problem by remember { mutableStateOf(editingRecord?.problem ?: "") }
    var priceText by remember { mutableStateOf(editingRecord?.price?.toInt()?.toString() ?: "200") }
    var clientName by remember { mutableStateOf(editingRecord?.clientName ?: "") }
    var clientPhone by remember { mutableStateOf(editingRecord?.clientPhone ?: "") }
    var notes by remember { mutableStateOf(editingRecord?.notes ?: "") }

    val brands = listOf("Samsung", "Apple", "Xiaomi", "Oppo", "Infinix", "Huawei", "Realme", "Vivo", "Honor", "Tecno", "Google", "Autre")
    val colors = listOf("Noir", "Blanc", "Bleu", "Gold", "Vert", "Gris", "Rouge", "Violet", "Autre")
    val quickProblems = listOf(
        "Ecran cassé",
        "Connecteur de charge",
        "Batterie à changer",
        "Afficheur noir",
        "Tombé dans l'eau (oxydé)",
        "Haut-parleur / Micro",
        "Caméra floue / cassée",
        "Déblocage / Flash"
    )

    val commonModelSuggestions = when (selectedBrand) {
        "Samsung" -> listOf("Galaxy A14", "Galaxy A24", "Galaxy A54", "Galaxy S21", "Galaxy S23", "A04s")
        "Apple" -> listOf("iPhone 11", "iPhone 12", "iPhone 13", "iPhone 14 Pro", "iPhone X/XS", "iPhone 8")
        "Xiaomi" -> listOf("Redmi Note 12", "Redmi Note 13", "Redmi 12C", "Poco X5 Pro", "Mi 11 Lite")
        "Oppo" -> listOf("Reno 8", "Reno 10", "A57", "A78", "A17")
        "Infinix" -> listOf("Hot 30", "Hot 40", "Note 30", "Smart 8")
        else -> listOf("Pro", "Plus", "Lite", "Ultra", "Max")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceWhite)
                .testTag("intake_modal"),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (editingRecord != null) (if (language == AppLanguage.AR) "تعديل بيانات الهاتف" else "Modifier Réception") else Strings.newIntake(language),
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "تسجيل الجهاز وتوليد ملصق الصيانة" else "Enregistrement atelier & impression",
                            color = BrandBlue,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Sequential Ticket Code Box (Soft Blue Pill)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandBlueLight)
                            .border(1.dp, BrandBlueBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = Strings.ticketNumber(language),
                                    color = BrandBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                if (isManualCodeOverride) {
                                    OutlinedTextField(
                                        value = code,
                                        onValueChange = {
                                            code = it
                                            codeNumber = it.toIntOrNull() ?: codeNumber
                                        },
                                        singleLine = true,
                                        textStyle = MonospaceCodeLarge.copy(color = BrandBlue),
                                        modifier = Modifier.width(140.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandBlue,
                                            unfocusedBorderColor = BrandBlueBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedContainerColor = SurfaceWhite,
                                            unfocusedContainerColor = SurfaceWhite
                                        )
                                    )
                                } else {
                                    Text(
                                        text = "#$code",
                                        style = MonospaceCodeLarge.copy(color = BrandBlue, fontSize = 26.sp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceWhite)
                                    .border(1.dp, BrandBlueBorder, RoundedCornerShape(8.dp))
                                    .clickable { isManualCodeOverride = !isManualCodeOverride }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isManualCodeOverride) "OK" else (if (language == AppLanguage.AR) "تغيير يدوي" else "Modifier"),
                                    color = BrandBlue,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Brand Selection Pills
                    Text(
                        text = Strings.deviceBrand(language),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        brands.forEach { b ->
                            val isSelected = selectedBrand == b
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BrandBlue else SurfaceSubtle)
                                    .border(1.dp, if (isSelected) BrandBlue else BorderSoft, RoundedCornerShape(10.dp))
                                    .clickable { selectedBrand = b }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = b,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Model Name Input + Suggestions
                    Text(
                        text = Strings.deviceModel(language),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        placeholder = { Text("ex. Galaxy A54 5G, iPhone 11...", color = TextPlaceholder) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_model"),
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

                    // Model Suggestion Chips
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        commonModelSuggestions.forEach { sug ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceSubtle)
                                    .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
                                    .clickable { model = sug }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = sug, color = TextSecondary, fontSize = 11.5.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Color Selector
                    Text(
                        text = Strings.deviceColor(language),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        colors.forEach { c ->
                            val isSelected = color == c
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandBlueLight else SurfaceSubtle)
                                    .border(1.dp, if (isSelected) BrandBlue else BorderSoft, RoundedCornerShape(8.dp))
                                    .clickable { color = c }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = c,
                                    color = if (isSelected) BrandBlue else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Problem / Issue Description
                    Text(
                        text = Strings.problem(language),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = problem,
                        onValueChange = { problem = it },
                        placeholder = { Text("ex. Ecran cassé, problème charge...", color = TextPlaceholder) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_problem"),
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

                    // Quick Problem Selector Chips
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickProblems.forEach { p ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceSubtle)
                                    .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
                                    .clickable { problem = p }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = p, color = TextSecondary, fontSize = 11.5.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Customer Name & Phone Box (Clean Soft Emerald surface)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(EmeraldSoftBg.copy(alpha = 0.4f))
                            .border(1.dp, EmeraldSoft.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.AR) "بيانات الزبون (لمنع الخلط بين الأجهزة):" else "Coordonnées Client (Anti-Confusion) :",
                                color = EmeraldSoft,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Name
                            OutlinedTextField(
                                value = clientName,
                                onValueChange = { clientName = it },
                                label = { Text(Strings.clientName(language), color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_client_name"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldSoft,
                                    unfocusedBorderColor = BorderSoft,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = SurfaceWhite,
                                    unfocusedContainerColor = SurfaceWhite
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Phone
                            OutlinedTextField(
                                value = clientPhone,
                                onValueChange = { clientPhone = it },
                                label = { Text(Strings.clientPhone(language), color = TextSecondary) },
                                placeholder = { Text("06 61 23 45 67", color = TextPlaceholder) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                textStyle = MonospacePhone.copy(fontSize = 15.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_client_phone"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldSoft,
                                    unfocusedBorderColor = BorderSoft,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = SurfaceWhite,
                                    unfocusedContainerColor = SurfaceWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price (DH) with quick +50 / +100 adjusters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.priceDirhams(language),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(50, 100, 200, 300, 450).forEach { pVal ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceSubtle)
                                        .border(1.dp, BorderSoft, RoundedCornerShape(6.dp))
                                        .clickable { priceText = pVal.toString() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "$pVal", color = TextPrimary, fontSize = 11.5.sp, style = MonospacePrice)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MonospacePrice.copy(fontSize = 18.sp, color = TextPrimary),
                        trailingIcon = { Text("DH", color = TextSecondary, style = MonospacePrice, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_price"),
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notes / PIN Code
                    Text(
                        text = Strings.internalNotes(language),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("ex. Code PIN: 0000, vitre légèrement rayée...", color = TextPlaceholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_notes"),
                        maxLines = 2,
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

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Dual Bottom Action Buttons: [Save Only] & [Save & Print Sticker]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: Save Only
                    OutlinedButton(
                        onClick = {
                            val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                            val finalModel = if (model.isBlank()) "Modèle" else model
                            val finalClient = if (clientName.isBlank()) "Client" else clientName
                            onSave(
                                code,
                                codeNumber,
                                selectedBrand,
                                finalModel,
                                color,
                                problem,
                                parsedPrice,
                                finalClient,
                                clientPhone,
                                notes,
                                false
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_only_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.saveOnly(language),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Button 2: Save & Print Sticker
                    Button(
                        onClick = {
                            val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                            val finalModel = if (model.isBlank()) "Modèle" else model
                            val finalClient = if (clientName.isBlank()) "Client" else clientName
                            onSave(
                                code,
                                codeNumber,
                                selectedBrand,
                                finalModel,
                                color,
                                problem,
                                parsedPrice,
                                finalClient,
                                clientPhone,
                                notes,
                                true
                            )
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                            .shadow(elevation = 3.dp, shape = RoundedCornerShape(14.dp), spotColor = BrandBlue.copy(alpha = 0.3f))
                            .testTag("save_and_print_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.saveAndPrint(language),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

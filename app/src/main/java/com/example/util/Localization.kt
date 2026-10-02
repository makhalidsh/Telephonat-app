package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class AppLanguage(val code: String, val displayName: String) {
    AR("ar", "العربية"),
    FR("fr", "Français / EN")
}

object Strings {
    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "سجل إصلاح الهواتف"
        AppLanguage.FR -> "Phone Repair Register"
    }

    fun tabAtelier(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "في المحل"
        AppLanguage.FR -> "Atelier"
    }

    fun tabArchive(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "المسلّمة"
        AppLanguage.FR -> "Livrés"
    }

    fun tabSettings(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الإعدادات"
        AppLanguage.FR -> "Paramètres"
    }

    fun inShopTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "في المحل"
        AppLanguage.FR -> "En atelier"
    }

    fun deliveredTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تم التسليم"
        AppLanguage.FR -> "Livrés"
    }

    fun searchPlaceholder(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "بحث بالرقم (001#)، الهاتف، الزبون، أو الجهاز..."
        AppLanguage.FR -> "Rechercher #001, téléphone, client, modèle..."
    }

    fun statusWaiting(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "قيد الانتظار"
        AppLanguage.FR -> "En attente"
    }

    fun statusRepairing(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "قيد الإصلاح"
        AppLanguage.FR -> "En cours"
    }

    fun statusReady(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "جاهز للاستلام"
        AppLanguage.FR -> "Prêt"
    }

    fun statusDelivered(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تم التسليم"
        AppLanguage.FR -> "Livré"
    }

    fun totalInShop(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "في المحل"
        AppLanguage.FR -> "En atelier"
    }

    fun totalRepairing(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "قيد الإصلاح"
        AppLanguage.FR -> "En cours"
    }

    fun totalReady(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "جاهز"
        AppLanguage.FR -> "Prêts"
    }

    fun totalDelivered(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تم تسليمهم"
        AppLanguage.FR -> "Livrés"
    }

    fun newIntake(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "استلام هاتف"
        AppLanguage.FR -> "Nouveau Téléphone"
    }

    fun ticketNumber(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "رقم التذكرة"
        AppLanguage.FR -> "N° Ticket"
    }

    fun deviceBrand(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "العلامة التجارية"
        AppLanguage.FR -> "Marque"
    }

    fun deviceModel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "موديل الهاتف"
        AppLanguage.FR -> "Modèle de téléphone"
    }

    fun deviceColor(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اللون"
        AppLanguage.FR -> "Couleur"
    }

    fun problem(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "المشكل / العطب"
        AppLanguage.FR -> "Panne / Problème"
    }

    fun clientName(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اسم الزبون"
        AppLanguage.FR -> "Nom du client"
    }

    fun clientPhone(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "رقم الهاتف (المغرب)"
        AppLanguage.FR -> "Téléphone client"
    }

    fun priceDirhams(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "السعر (درهم)"
        AppLanguage.FR -> "Prix (DH)"
    }

    fun internalNotes(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "ملاحظات تقنية / رمز القفل (PIN)"
        AppLanguage.FR -> "Notes internes / Schéma ou PIN"
    }

    fun saveOnly(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حفظ فقط"
        AppLanguage.FR -> "Enregistrer"
    }

    fun saveAndPrint(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حفظ وطباعة الملصق"
        AppLanguage.FR -> "Enregistrer & Imprimer"
    }

    fun printSticker(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الملصق"
        AppLanguage.FR -> "Sticker"
    }

    fun scanSticker(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "مسح الملصق"
        AppLanguage.FR -> "Scanner Sticker"
    }

    fun callClient(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اتصال"
        AppLanguage.FR -> "Appeler"
    }

    fun whatsAppClient(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "واتساب"
        AppLanguage.FR -> "WhatsApp"
    }

    fun emptyInShop(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "لا توجد أجهزة في المحل حالياً"
        AppLanguage.FR -> "Aucun appareil en atelier pour le moment"
    }

    fun emptyDelivered(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "لا توجد أجهزة مسلّمة مسجلة"
        AppLanguage.FR -> "Aucun appareil livré enregistré"
    }

    fun thermalPreviewTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "معاينة ملصق 50×30 مم"
        AppLanguage.FR -> "Aperçu Sticker 50x30mm"
    }

    fun printViaSystem(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "طباعة النظام / PDF"
        AppLanguage.FR -> "Impression Système / PDF"
    }

    fun printBluetooth(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "طابعة حرارية بلوتوث"
        AppLanguage.FR -> "Imprimante Thermique BT"
    }

    fun shareSticker(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "مشاركة صورة الملصق"
        AppLanguage.FR -> "Partager Sticker"
    }

    fun currency(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "د.م"
        AppLanguage.FR -> "DH"
    }

    fun all(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الكل"
        AppLanguage.FR -> "Tous"
    }

    fun markDelivered(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تأكيد التسليم للزبون"
        AppLanguage.FR -> "Marquer Livré"
    }

    fun deleteConfirm(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "هل تريد حذف هذا السجل نهائياً؟"
        AppLanguage.FR -> "Supprimer définitivement cet enregistrement ?"
    }

    fun backupExport(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "نسخ احتياطي واستيراد"
        AppLanguage.FR -> "Sauvegarde & Export JSON"
    }

    // Settings strings
    fun settingsTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "إعدادات التطبيق"
        AppLanguage.FR -> "Paramètres de l'Application"
    }

    fun workshopInfo(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "معلومات المحل (تظهر على الملصقات)"
        AppLanguage.FR -> "Informations de l'Atelier (sur stickers)"
    }

    fun workshopName(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اسم المحل / الورشة"
        AppLanguage.FR -> "Nom de l'atelier"
    }

    fun workshopPhone(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "هاتف المحل"
        AppLanguage.FR -> "Téléphone de contact"
    }

    fun workshopCity(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "المدينة"
        AppLanguage.FR -> "Ville"
    }

    fun appearance(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "المظهر والراحة البصرية"
        AppLanguage.FR -> "Apparence & Confort Visuel"
    }

    fun themeDark(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الوضع الداكن (مريح للعين)"
        AppLanguage.FR -> "Mode Sombre (Confort des yeux)"
    }

    fun themeLight(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الوضع الفاتح"
        AppLanguage.FR -> "Mode Clair"
    }

    fun languageTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اللغة"
        AppLanguage.FR -> "Langue"
    }

    fun printingPrefs(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "خيارات الملصقات والطباعة"
        AppLanguage.FR -> "Options Stickers & Impression"
    }

    fun stickerFormat(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "مقاس الملصق الحراري"
        AppLanguage.FR -> "Format du Sticker"
    }

    fun autoPrintSticker(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "فتح نافذة الطباعة تلقائياً بعد إضافة جهاز"
        AppLanguage.FR -> "Ouvrir l'impression auto après ajout"
    }

    fun dataBackup(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "البيانات والنسخ الاحتياطي"
        AppLanguage.FR -> "Données & Synchronisation Web"
    }

    fun aboutTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حول التطبيق"
        AppLanguage.FR -> "À Propos"
    }

    fun aboutSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "Phone Repair Register v1.0 • أداة مهنية لورشات الصيانة بالمغرب"
        AppLanguage.FR -> "Phone Repair Register v1.0 • Outil professionnel atelier au Maroc"
    }

    fun saveChanges(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حفظ الإعدادات"
        AppLanguage.FR -> "Enregistrer les Paramètres"
    }

    fun formatRelativeTime(iso: String, lang: AppLanguage): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = isoFormat.parse(iso) ?: return iso
            val diffMs = System.currentTimeMillis() - date.time
            val diffMins = (diffMs / (60 * 1000)).toInt()
            val diffHours = (diffMins / 60)
            val diffDays = (diffHours / 24)

            when (lang) {
                AppLanguage.AR -> {
                    when {
                        diffMins < 2 -> "الآن"
                        diffMins < 60 -> "منذ $diffMins د"
                        diffHours == 1 -> "منذ ساعة"
                        diffHours == 2 -> "منذ ساعتين"
                        diffHours < 24 -> "منذ $diffHours س"
                        diffDays == 1 -> "أمس"
                        else -> "منذ $diffDays أيام"
                    }
                }
                AppLanguage.FR -> {
                    when {
                        diffMins < 2 -> "À l'instant"
                        diffMins < 60 -> "${diffMins}m"
                        diffHours < 24 -> "${diffHours}h"
                        else -> "${diffDays}j"
                    }
                }
            }
        } catch (_: Exception) {
            "Récent"
        }
    }
}

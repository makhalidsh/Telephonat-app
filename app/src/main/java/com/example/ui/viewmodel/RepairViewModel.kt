package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.RepairDatabase
import com.example.data.model.RepairRecord
import com.example.data.model.RepairStatus
import com.example.data.repository.IRepairRepository
import com.example.data.repository.RoomRepairRepository
import com.example.data.settings.SettingsRepository
import com.example.data.settings.WorkshopSettings
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RepairViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: IRepairRepository
    private val settingsRepository: SettingsRepository = SettingsRepository(application)
    private val supabaseService: com.example.data.sync.SupabaseSyncService = com.example.data.sync.SupabaseSyncService()

    init {
        val database = RepairDatabase.getDatabase(application, viewModelScope)
        repository = RoomRepairRepository(database.repairDao())
    }

    val workshopSettings: StateFlow<WorkshopSettings> = settingsRepository.settings

    val language: StateFlow<AppLanguage> = workshopSettings.map {
        if (it.language.equals("fr", ignoreCase = true)) AppLanguage.FR else AppLanguage.AR
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = if (workshopSettings.value.language == "fr") AppLanguage.FR else AppLanguage.AR
    )

    val isDarkTheme: StateFlow<Boolean> = workshopSettings.map {
        it.themeMode != "light"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = workshopSettings.value.themeMode != "light"
    )

    // 0 = Atelier (In Shop), 1 = Archive (Delivered), 2 = Settings
    private val _selectedNavIndex = MutableStateFlow(0)
    val selectedNavIndex: StateFlow<Int> = _selectedNavIndex.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // null = All, or "waiting", "repairing", "ready"
    private val _statusSubFilter = MutableStateFlow<String?>(null)
    val statusSubFilter: StateFlow<String?> = _statusSubFilter.asStateFlow()

    val allRepairs: StateFlow<List<RepairRecord>> = repository.getAllRepairs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredRepairs: StateFlow<List<RepairRecord>> = combine(
        allRepairs,
        _selectedNavIndex,
        _statusSubFilter,
        _searchQuery
    ) { repairs, navIndex, subFilter, query ->
        var list = repairs

        // Nav Filter: 0 = In-shop, 1 = Delivered
        list = if (navIndex == 0) {
            list.filter { it.status != RepairStatus.DELIVERED.id }
        } else {
            list.filter { it.status == RepairStatus.DELIVERED.id }
        }

        // Sub-status filter
        if (navIndex == 0 && subFilter != null) {
            list = list.filter { it.status == subFilter }
        }

        // Search query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase().removePrefix("#")
            list = list.filter {
                it.code.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.model.lowercase().contains(q) ||
                it.clientName.lowercase().contains(q) ||
                it.clientPhone.contains(q) ||
                it.problem.lowercase().contains(q)
            }
        }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _nextCodePair = MutableStateFlow(Pair("001", 1))
    val nextCodePair: StateFlow<Pair<String, Int>> = _nextCodePair.asStateFlow()

    // UI Dialog & Modal States
    private val _isIntakeOpen = MutableStateFlow(false)
    val isIntakeOpen: StateFlow<Boolean> = _isIntakeOpen.asStateFlow()

    private val _editingRecord = MutableStateFlow<RepairRecord?>(null)
    val editingRecord: StateFlow<RepairRecord?> = _editingRecord.asStateFlow()

    private val _detailRecord = MutableStateFlow<RepairRecord?>(null)
    val detailRecord: StateFlow<RepairRecord?> = _detailRecord.asStateFlow()

    private val _selectedRecordForSticker = MutableStateFlow<RepairRecord?>(null)
    val selectedRecordForSticker: StateFlow<RepairRecord?> = _selectedRecordForSticker.asStateFlow()

    private val _isScannerOpen = MutableStateFlow(false)
    val isScannerOpen: StateFlow<Boolean> = _isScannerOpen.asStateFlow()

    private val _highlightedCode = MutableStateFlow<String?>(null)
    val highlightedCode: StateFlow<String?> = _highlightedCode.asStateFlow()

    private val _isBackupDialogOpen = MutableStateFlow(false)
    val isBackupDialogOpen: StateFlow<Boolean> = _isBackupDialogOpen.asStateFlow()

    private val _backupJsonString = MutableStateFlow("")
    val backupJsonString: StateFlow<String> = _backupJsonString.asStateFlow()

    init {
        refreshNextCode()
    }

    fun setSelectedNavIndex(index: Int) {
        _selectedNavIndex.value = index
    }

    fun toggleLanguage() {
        val next = if (language.value == AppLanguage.AR) "fr" else "ar"
        settingsRepository.setLanguage(next)
    }

    fun updateWorkshopSettings(newSettings: WorkshopSettings) {
        settingsRepository.updateSettings(newSettings)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusSubFilter(filter: String?) {
        _statusSubFilter.value = filter
    }

    fun refreshNextCode() {
        viewModelScope.launch {
            _nextCodePair.value = repository.getNextSequentialCode()
        }
    }

    fun openDetail(record: RepairRecord) {
        _detailRecord.value = record
    }

    fun closeDetail() {
        _detailRecord.value = null
    }

    fun openIntake(recordToEdit: RepairRecord? = null) {
        _editingRecord.value = recordToEdit
        if (recordToEdit == null) {
            refreshNextCode()
        }
        _isIntakeOpen.value = true
    }

    fun closeIntake() {
        _isIntakeOpen.value = false
        _editingRecord.value = null
    }

    fun openSticker(record: RepairRecord) {
        _selectedRecordForSticker.value = record
    }

    fun closeSticker() {
        _selectedRecordForSticker.value = null
    }

    fun openScanner() {
        _isScannerOpen.value = true
    }

    fun closeScanner() {
        _isScannerOpen.value = false
    }

    fun onBarcodeScanned(rawContent: String) {
        var detectedCode: String? = null
        if (rawContent.contains("ID:")) {
            val parts = rawContent.split("|")
            for (p in parts) {
                if (p.startsWith("ID:")) {
                    detectedCode = p.substring(3).trim()
                    break
                }
            }
        } else {
            val trimmed = rawContent.trim().removePrefix("#")
            if (trimmed.length <= 6) {
                detectedCode = trimmed
            }
        }

        detectedCode?.let { code ->
            _highlightedCode.value = code
            _searchQuery.value = code
            viewModelScope.launch {
                val record = repository.getRepairByCode(code)
                if (record != null) {
                    _selectedNavIndex.value = if (record.status == RepairStatus.DELIVERED.id) 1 else 0
                    _detailRecord.value = record
                }
            }
        }
        _isScannerOpen.value = false
    }

    fun saveRepair(
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
        andPrint: Boolean = false
    ) {
        viewModelScope.launch {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val existing = _editingRecord.value
            val record = if (existing != null) {
                existing.copy(
                    code = code,
                    codeNumber = codeNumber,
                    brand = brand,
                    model = model,
                    color = color,
                    problem = problem,
                    price = price,
                    clientName = clientName,
                    clientPhone = clientPhone,
                    notes = notes
                )
            } else {
                RepairRecord(
                    code = code,
                    codeNumber = codeNumber,
                    brand = brand,
                    model = model,
                    color = color,
                    problem = problem,
                    price = price,
                    clientName = clientName,
                    clientPhone = clientPhone,
                    receivedAt = isoFormat.format(Date()),
                    status = RepairStatus.WAITING.id,
                    notes = notes
                )
            }

            if (existing != null) {
                repository.updateRepair(record)
            } else {
                repository.insertRepair(record)
            }

            closeIntake()
            refreshNextCode()

            val shouldPrint = andPrint || workshopSettings.value.autoPrintAfterIntake
            if (shouldPrint) {
                _selectedRecordForSticker.value = record
            }
        }
    }

    fun updateStatus(record: RepairRecord, newStatus: RepairStatus) {
        viewModelScope.launch {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val deliveredAtTimestamp = if (newStatus == RepairStatus.DELIVERED) {
                isoFormat.format(Date())
            } else null

            val updated = record.copy(
                status = newStatus.id,
                deliveredAt = deliveredAtTimestamp ?: record.deliveredAt
            )
            repository.updateRepair(updated)
            if (_detailRecord.value?.id == record.id) {
                _detailRecord.value = updated
            }
        }
    }

    fun deleteRecord(record: RepairRecord) {
        viewModelScope.launch {
            repository.deleteRepair(record)
            if (_detailRecord.value?.id == record.id) {
                _detailRecord.value = null
            }
        }
    }

    fun openBackupDialog() {
        viewModelScope.launch {
            val json = repository.exportToJson(allRepairs.value)
            _backupJsonString.value = json
            _isBackupDialogOpen.value = true
        }
    }

    fun closeBackupDialog() {
        _isBackupDialogOpen.value = false
    }

    fun importBackup(json: String, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.importFromJson(json)
            refreshNextCode()
            onDone(count)
        }
    }

    fun testSupabaseConnection(url: String, anonKey: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = supabaseService.testConnection(url, anonKey)
            if (result.isSuccess) {
                onResult(true, result.getOrNull() ?: "OK")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Erreur de connexion")
            }
        }
    }

    fun syncWithSupabase(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val current = workshopSettings.value
            if (current.supabaseUrl.isBlank() || current.supabaseAnonKey.isBlank()) {
                onResult(false, "Veuillez configurer l'URL Supabase et la clé Anon Key.")
                return@launch
            }

            // 1. Upload local repairs to cloud
            val uploadRes = supabaseService.uploadRepairs(current.supabaseUrl, current.supabaseAnonKey, allRepairs.value)
            if (uploadRes.isFailure) {
                onResult(false, uploadRes.exceptionOrNull()?.message ?: "Erreur lors de l'envoi vers Supabase")
                return@launch
            }

            // 2. Fetch remote repairs from cloud
            val fetchRes = supabaseService.fetchRepairs(current.supabaseUrl, current.supabaseAnonKey)
            if (fetchRes.isFailure) {
                onResult(false, fetchRes.exceptionOrNull()?.message ?: "Erreur lors de la récupération depuis Supabase")
                return@launch
            }

            val remoteRepairs = fetchRes.getOrNull() ?: emptyList()
            for (remote in remoteRepairs) {
                repository.insertRepair(remote)
            }

            val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            settingsRepository.updateSettings(current.copy(lastSyncTimestamp = now))
            refreshNextCode()
            onResult(true, "Synchronisé avec succès (${remoteRepairs.size} appareils)")
        }
    }
}

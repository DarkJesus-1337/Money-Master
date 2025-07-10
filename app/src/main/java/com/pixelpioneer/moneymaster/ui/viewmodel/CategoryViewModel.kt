package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoriesState =
        MutableStateFlow<UiState<List<TransactionCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<TransactionCategory>>> = _categoriesState

    private val _selectedCategory = MutableStateFlow<UiState<TransactionCategory>>(UiState.Loading)
    val selectedCategory: StateFlow<UiState<TransactionCategory>> = _selectedCategory

    private val _categoryFormState = MutableStateFlow(CategoryFormState())
    val categoryFormState: StateFlow<CategoryFormState> = _categoryFormState

    // Direktes StateFlow für Kategorienliste für UI-Zugriff
    private val _categories = MutableStateFlow<List<TransactionCategory>>(emptyList())
    val categories: StateFlow<List<TransactionCategory>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = UiState.Loading
                categoryRepository.allCategories
                    .catch { e ->
                        _categoriesState.value =
                            UiState.Error(e.message ?: "Unknown error occurred")
                        _categories.value = emptyList()
                    }
                    .collect { categories ->
                        if (categories.isEmpty()) {
                            _categoriesState.value = UiState.Empty
                        } else {
                            _categoriesState.value = UiState.Success(categories)
                        }
                        _categories.value = categories // Setze Liste für UI
                    }
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error(e.message ?: "Unknown error occurred")
                _categories.value = emptyList()
            }
        }
    }

    fun loadCategoryById(id: Long) {
        viewModelScope.launch {
            try {
                _selectedCategory.value = UiState.Loading
                categoryRepository.getCategoryById(id)
                    .catch { e ->
                        _selectedCategory.value =
                            UiState.Error(e.message ?: "Unknown error occurred")
                    }
                    .collect { category ->
                        _selectedCategory.value = UiState.Success(category)
                    }
            } catch (e: Exception) {
                _selectedCategory.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun createCategory() {
        viewModelScope.launch {
            val formState = _categoryFormState.value

            if (!validateCategoryForm()) {
                return@launch
            }

            try {
                val category = TransactionCategory(
                    name = formState.name,
                    color = formState.color,
                    icon = formState.iconResId
                )

                categoryRepository.insertCategory(category)

                resetFormState()

                loadCategories()
            } catch (e: Exception) {
            }
        }
    }

    fun updateCategory(id: Long) {
        viewModelScope.launch {
            val formState = _categoryFormState.value

            if (!validateCategoryForm()) {
                return@launch
            }

            try {
                val category = TransactionCategory(
                    id = id,
                    name = formState.name,
                    color = formState.color,
                    icon = formState.iconResId
                )

                categoryRepository.updateCategory(category)

                resetFormState()

                loadCategories()

                loadCategoryById(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
                loadCategories()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createDefaultCategoriesIfNeeded() {
        viewModelScope.launch {
            try {
                categoryRepository.allCategories.collect { categories ->
                    if (categories.isEmpty()) {
                        categoryRepository.insertDefaultCategories()
                        loadCategories()
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateName(name: String) {
        _categoryFormState.value = _categoryFormState.value.copy(
            name = name,
            nameError = if (name.isBlank()) "Name cannot be empty" else null
        )
    }

    fun updateColor(color: Int) {
        _categoryFormState.value = _categoryFormState.value.copy(color = color)
    }

    fun updateIconResId(iconResId: Int) {
        _categoryFormState.value = _categoryFormState.value.copy(iconResId = iconResId)
    }

    fun resetFormState() {
        _categoryFormState.value = CategoryFormState()
    }

    fun initFormWithCategory(category: TransactionCategory) {
        _categoryFormState.value = CategoryFormState(
            name = category.name,
            color = category.color,
            iconResId = category.icon
        )
    }

    private fun validateCategoryForm(): Boolean {
        val formState = _categoryFormState.value

        if (formState.name.isBlank()) {
            _categoryFormState.value = formState.copy(
                nameError = "Name cannot be empty"
            )
            return false
        }

        return true
    }
}

data class CategoryFormState(
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(),
    val iconResId: Int = 0,
    val nameError: String? = null
)
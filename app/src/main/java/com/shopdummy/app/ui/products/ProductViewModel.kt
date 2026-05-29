package com.shopdummy.app.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopdummy.app.data.repository.ProductRepository
import com.shopdummy.app.domain.model.Product
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    DEFAULT, PRICE_ASC, PRICE_DESC, RATING_DESC
}

@OptIn(FlowPreview::class)
class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
    val sortOption = _sortOption.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    
    // Todos los productos locales (Flow desde Room)
    private val localProducts = repository.getProducts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Los productos que se muestran en la lista, varían según si hay búsqueda o no, y se ordenan
    val products = combine(localProducts, _searchResults, _searchQuery, _sortOption) { local, searchRes, query, sortOpt ->
        val baseList = if (query.isBlank()) {
            local
        } else {
            searchRes
        }
        
        when (sortOpt) {
            SortOption.DEFAULT -> baseList
            SortOption.PRICE_ASC -> baseList.sortedBy { it.price }
            SortOption.PRICE_DESC -> baseList.sortedByDescending { it.price }
            SortOption.RATING_DESC -> baseList.sortedByDescending { it.rating }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    init {
        loadProducts()
        
        // Debounce de la búsqueda
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    val results = repository.searchProducts(query)
                    // Filtrar estrictamente por título localmente también para asegurar que la API no devuelva basura
                    val lowerQuery = query.lowercase()
                    _searchResults.value = results.filter { it.title.lowercase().contains(lowerQuery) }
                }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            repository.refreshProducts()
        }
    }

    fun loadProductById(id: Int) {
        viewModelScope.launch {
            _selectedProduct.value = repository.getProductById(id)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            // Filtrado local instantáneo estricto por título
            val lowerQuery = query.lowercase()
            _searchResults.value = localProducts.value.filter {
                it.title.lowercase().contains(lowerQuery)
            }
        }
    }
    
    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }
}

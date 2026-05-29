package com.shopdummy.app.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopdummy.app.data.repository.ProductRepository
import com.shopdummy.app.domain.model.Product
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    
    // Todos los productos locales (Flow desde Room)
    private val localProducts = repository.getProducts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Los productos que se muestran en la lista, varían según si hay búsqueda o no
    val products = combine(localProducts, _searchResults, _searchQuery) { local, searchRes, query ->
        if (query.isBlank()) {
            local
        } else {
            searchRes
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
                    _searchResults.value = results
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
        }
    }
}

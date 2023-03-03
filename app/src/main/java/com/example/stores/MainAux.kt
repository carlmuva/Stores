package com.example.stores

interface MainAux {
    fun hideFab(isVisible:Boolean=false) // metodo referente al boton flotante

    fun addStore(storeEntity: StoreEntity) // metodo que se utiliza para refrescar la pantalla despues de agregar una tienda
    fun updateStore(storeEntity: StoreEntity) // metodo que se utiliza para refrescar la pantalla despues de actulizar una tienda
}
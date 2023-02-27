package com.example.stores

import android.app.Application
import androidx.room.Room

//Creamos esta aplicacion para poder usar singleton para acceder desde cualquier lado a la base de datos
class StoreApplication:Application() {
    companion object{
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database= Room.databaseBuilder(this,StoreDatabase::class.java,"StoreDatabase")
            .build()
    }



}
package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var  mBinding:ActivityMainBinding
    private lateinit var  mAdapter: StoreAdapter //con esto no traemos lo del adapter
    private lateinit var mGridLayout:GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

/*        mBinding.btnSave.setOnClickListener {// este metodo lo usamos para agregar una historia
            val store = StoreEntity(name = mBinding.etName.text.toString().trim()) //Con esto nos traemos lo que esta en et de la main act, el metodo trim es para quitar espacios

            Thread {//para insertar en un segundo hilo
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/

        mBinding.idFab.setOnClickListener { launchEditFragment() }

        setupRecyclerView()

    }

    private fun launchEditFragment() { // para lanzar fratment
        val fragment = EditStoreFragment()

        val fragmentManager = supportFragmentManager // para controlar nuestro fragment
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain,fragment)
        fragmentTransaction.commit()   // configuracion basica para lanzar un fragmento en kotlin

        mBinding.idFab.hide() // para ocultar el boton flotante
    }

    private fun setupRecyclerView() {  //configuracion del grid
        mAdapter= StoreAdapter(mutableListOf(),this)  //mandamos hablar al adapter
        mGridLayout=GridLayoutManager(this,2) //configuracion del grid

        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)//se utiliza para que no cambie de tamaño al especificado en el layout
            layoutManager =mGridLayout //se utiliza para indicarle que va ser de tipo grid
            adapter = mAdapter
        }

    }

    private fun getStores(){ //Funcion para traernos la consulta de las stores en el hilo principal de android
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>() // configuracion de la cola
        Thread{
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        mAdapter.setStores(queue.take())
    }

    //OnClickListener
    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) { // Funcion para traernos los favoritos
        storeEntity.isFavorite=!storeEntity.isFavorite
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.update(queue.take())
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {  // Funcion para eliminar un elemento
        val queue = LinkedBlockingQueue<StoreEntity>()
        thread {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        mAdapter.delete(queue.take())

    }
}
package com.example.stores

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Website
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), OnClickListener,MainAux {

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

    private fun launchEditFragment(args:Bundle?=null) { // para lanzar fratment
        val fragment = EditStoreFragment()
        if (args != null) fragment.arguments=args  // argumentos

        val fragmentManager = supportFragmentManager // para controlar nuestro fragment
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain,fragment)
        fragmentTransaction.addToBackStack(null)// se usa para poder retroceder en la actividad
        fragmentTransaction.commit()   // configuracion basica para lanzar un fragmento en kotlin

        //mBinding.idFab.hide() // para ocultar el boton flotante
        hideFab()
    }

    private fun setupRecyclerView() {  //configuracion del grid
        mAdapter= StoreAdapter(mutableListOf(),this)  //mandamos hablar al adapter
        mGridLayout=GridLayoutManager(this,resources.getInteger(R.integer.main_columns)) //configuracion del grid columnas

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
    override fun onClick(storeId: Long) {
        val args =Bundle()
        args.putLong(getString(R.string.arg_id), storeId)  // nos traemos el argumento id de la entidad

        launchEditFragment(args)

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

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = arrayOf("Eliminar","LLamar","Ir al sitio web") //Arreglo de opciones
        MaterialAlertDialogBuilder(this)     // Menu de opciones Eliminar, llamar, sitio web
            .setTitle(R.string.dialog_options_title)
            .setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){

                    0->confirmDelete(storeEntity)

                    1->dial(storeEntity.phone)

                    2->goToWebsite(storeEntity.website)

                }
            }).show()

    }

    private fun dial(phone: String){  // funcion para activar y pasar el numero desde nuestro menu sostenido
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data= Uri.parse("tel:$phone")   // le pasamos el numero
        }
        if(callIntent.resolveActivity(packageManager)!=null) // valida que exista una aplicacion de telefono disponible para marcar
            startActivity(callIntent)
        else
            Toast.makeText(this,R.string.main_error_no_resolve,Toast.LENGTH_SHORT).show() // Mensaje que se muestra si no se cuenta con una app compatible

    }

    private fun goToWebsite(website: String){ // funcion para abrir el website desde nuestro menu sostenido
        if(website.isEmpty()){  // valida cuando est5a vacio
            Toast.makeText(this,R.string.main_error_no_website,Toast.LENGTH_LONG).show()
        }else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            if(websiteIntent.resolveActivity(packageManager)!=null) // valida que exista una aplicacion de navegador disponible
                startActivity(websiteIntent)
            else
                Toast.makeText(this,R.string.main_error_no_resolve,Toast.LENGTH_SHORT).show() // Mensaje que se muestra si no se cuenta con una app compatible

        }
    }

    private fun confirmDelete(storeEntity: StoreEntity){// Funcion para eliminar un elemento
        MaterialAlertDialogBuilder(this)// mensaje de advertencia para eliminar una tienda
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm,DialogInterface.OnClickListener{dialogInterface, i ->  // cuando el usuario acepte eliminar se ejecutara el codigo que eliminara el registro

                val queue = LinkedBlockingQueue<StoreEntity>()
                Thread {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    queue.add(storeEntity)
                }.start()
                mAdapter.delete(queue.take())

            })
            .setNegativeButton(R.string.dialog_delete_cancel, null) // en caso no quiera eliminar el registro solo cerramos el dialog
            .show()

    }

    /*
    *MainAux
     */
    override fun hideFab(isVisible: Boolean) { // configuracion del boton flotante
        if (isVisible) mBinding.idFab.show() else mBinding.idFab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) { // la usamos para actualizar la vista despues de agregar una tienda
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}
package com.example.stores

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.LinkedBlockingQueue


class EditStoreFragment : Fragment() {

    private lateinit var mBinding:FragmentEditStoreBinding
    private var mActivity: MainActivity? =null
    private var mIsEditMode: Boolean= false
    private var mStoreEntity: StoreEntity?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id),0)
        if (id != null && id != 0L){
            mIsEditMode=true  
            getStore(id)
        //Toast.makeText(activity,id.toString(),Toast.LENGTH_SHORT).show() // muestra el id del objeto
        }else {
            Toast.makeText(activity,id.toString(),Toast.LENGTH_SHORT).show()
        }

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true) // para agregar un boton de retroseso
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add) // cambiamos el titulo de la barra de acciones

        setHasOptionsMenu(true) // le damos acceso al menu

        mBinding.etPhotoUrl.addTextChangedListener {  // con esto cargamos la imagen url
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }
    }

    private fun getStore(id: Long) { // consulta con un solo atributo
        val queue = LinkedBlockingQueue<StoreEntity?>()
        Thread{
            mStoreEntity=StoreApplication.database.storeDao().getStoreById(id)
            queue.add(mStoreEntity)

            }.start()
        queue.take()?.let { setUiStore(it) }
    }

    private fun setUiStore(storeEntity: StoreEntity) { // con este metodo nos traemos todos los valores de la entidad a la vista
        with(mBinding){
            etName.setText(storeEntity.name)
            etPhone.setText(storeEntity.phone)
            etWebsite.setText(storeEntity.website)
            etPhotoUrl.setText(storeEntity.photoUrl)
            Glide.with(requireActivity())
                .load(storeEntity.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imgPhoto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save,menu) // inflamos la vista del menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home ->{
                hideKeyboard() // para cerra el teclado despues de darle para atras
                mActivity?.onBackPressedDispatcher?.onBackPressed() // para destruir el fragment despues de darle atras
                true
            }
            R.id.action_save->{
                val store = StoreEntity(name = mBinding.etName.text.toString().trim(),    // Aqui nos vamos a traer lo de la interfas para poder guardarlo
                                        phone = mBinding.etPhone.text.toString().trim(),
                                        website = mBinding.etWebsite.text.toString().trim(),
                                        photoUrl = mBinding.etPhotoUrl.text.toString().trim())

                val queue =LinkedBlockingQueue<Long?>()
                Thread{
                    store.id = StoreApplication.database.storeDao().addStore(store) // Aqui almacenamos nuestra tienda
                    queue.add(store.id)


                    hideKeyboard()

                }.start()

                queue.take()?.let{
                    /*Snackbar.make(mBinding.root,getString(R.string.edit_store_message_save_succes),  // para configurar un mensaje para em boton de guardar
                        Snackbar.LENGTH_SHORT)
                        .show()*/
                    Toast.makeText(mActivity,R.string.edit_store_message_save_succes,Toast.LENGTH_SHORT).show()

                    mActivity?.onBackPressedDispatcher?.onBackPressed() // Se utiliza para destruir el fragment despues de darle guardar
                    mActivity?.addStore(store) // se utiliza para actulizar la vista despues de crear una tienda
                }

                true
            }else ->super.onOptionsItemSelected(item)

        }

    }

    private fun hideKeyboard(){ // metodo para cerrar el teclado despues de usarlo
        val  imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken,0)
    }

    override fun onDestroyView() { // intento para cerra teclado no funciona esto
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false) // para ocutlar el boton de retroseso
        mActivity?.supportActionBar?.title=getString(R.string.app_name) // para mostrar el titulo de la app y no del fratment
        mActivity?.hideFab(true) // para visualizar el boton flotante

        setHasOptionsMenu(false)

        super.onDestroy()
    }


}
package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
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
            mIsEditMode=true  // en caso de que vayamos a editar en el fratment
            getStore(id)
        //Toast.makeText(activity,id.toString(),Toast.LENGTH_SHORT).show() // muestra el id del objeto
        }else {
            mIsEditMode=false // en caso de que vayamos a agregar en el fratment
            mStoreEntity= StoreEntity(name="", phone = "", photoUrl = "")

            //Toast.makeText(activity,id.toString(),Toast.LENGTH_SHORT).show()
        }

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true) // para agregar un boton de retroseso
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_store_title_edit) // con esto el titulo de la barra aparecera como editar tienda cuando se este editando
                                            else getString(R.string.edit_store_title_add)// con esto el titulo de la barra sera crear tienda cuando se este agregando una tienda

        setHasOptionsMenu(true) // le damos acceso al menu

        mBinding.etPhotoUrl.addTextChangedListener {  // con esto cargamos la imagen url
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }

        mBinding.etName.addTextChangedListener { validateFields(mBinding.tilName) } // para que cuando estemos corrigiendo el regsitro desaparesca el foco
        mBinding.etPhone.addTextChangedListener { validateFields(mBinding.tilPhone) } // para que cuando estemos corrigiendo el regsitro desaparesca el foco
        mBinding.etPhotoUrl.addTextChangedListener { validateFields(mBinding.tilPhotoUrl) } // para que cuando estemos corrigiendo el regsitro desaparesca el foco

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
            etPhone.text=storeEntity.phone.editable() //otra forma de mandar los datos
            etWebsite.setText(storeEntity.website)
            etPhotoUrl.setText(storeEntity.photoUrl)
            Glide.with(requireActivity())
                .load(storeEntity.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imgPhoto)
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)// otra forma de mandar los datos

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
                if(mStoreEntity!=null && validateFields(mBinding.tilPhotoUrl,mBinding.tilPhone,mBinding.tilName)){
                    /*val store = StoreEntity(name = mBinding.etName.text.toString().trim(),    // Aqui nos vamos a traer lo de la interfas para poder guardarlo
                         phone = mBinding.etPhone.text.toString().trim(),
                         website = mBinding.etWebsite.text.toString().trim(),
                         photoUrl = mBinding.etPhotoUrl.text.toString().trim())*/
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()    // Aqui nos vamos a traer lo de la interfas para poder guardarlo
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()

                    }

                    val queue =LinkedBlockingQueue<StoreEntity?>()
                    Thread{
                        if(mIsEditMode)StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id=StoreApplication.database.storeDao().addStore(mStoreEntity!!)

                        queue.add(mStoreEntity)

                    }.start()

                    with(queue.take()){
                        hideKeyboard()

                        if (mIsEditMode){ // Se actuliza la tienda
                            mActivity?.updateStore(mStoreEntity!!)

                            Snackbar.make(mBinding.root,R.string.edit_store_message_update_succes,Snackbar.LENGTH_SHORT)
                                .show()
                        }else { // Se agrega la tienda
                            mActivity?.addStore(this!!) // se utiliza para actulizar la vista despues de crear una tienda
                            /*Snackbar.make(mBinding.root,getString(R.string.edit_store_message_save_succes),  // para configurar un mensaje para el boton de guardar
                            Snackbar.LENGTH_SHORT)
                            .show()*/


                            Toast.makeText(
                                mActivity,
                                R.string.edit_store_message_save_succes,
                                Toast.LENGTH_SHORT
                            ).show()

                            mActivity?.onBackPressedDispatcher?.onBackPressed() // Se utiliza para destruir el fragment despues de darle guardar
                        }
                    }

                }

                true
            }else ->super.onOptionsItemSelected(item)

        }

    }

    private fun validateFields(vararg textFields: TextInputLayout):Boolean{ // validar que no se guarden registros vacios pero con menos codigo desde nuestro constructor
        var isValid= true

        for (textField in textFields){   // si esta vacio el campo marca un error
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error=getString(R.string.helper_required)
                isValid=false
            } else textField.error=null // cuando se estre escribiendo se va quitar el foco del campo
        }

        if(!isValid)Snackbar.make(mBinding.root,
        R.string.edit_store_message_valid,
        Snackbar.LENGTH_SHORT).show()

        return isValid

    }

    private fun validateFields(): Boolean { // validar que no se guarden registros vacios
        var isValid=true

        if(mBinding.etPhotoUrl.text.toString().trim().isEmpty()){ // validacion de campo photourl
            mBinding.tilPhotoUrl.error=getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid=false
        }
        if(mBinding.etPhone.text.toString().trim().isEmpty()){ // validacion de campo phone
            mBinding.tilPhone.error=getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid=false
        }
        if(mBinding.etName.text.toString().trim().isEmpty()){ // validacion de campo name
            mBinding.tilName.error=getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid=false
        }
        return isValid
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
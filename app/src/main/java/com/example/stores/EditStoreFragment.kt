package com.example.stores

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar


class EditStoreFragment : Fragment() {

    private lateinit var mBinding:FragmentEditStoreBinding
    private var mActivity: MainActivity? =null

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

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true) // para agregar un boton de retroseso
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add) // cambiamos el titulo de la barra de acciones

        setHasOptionsMenu(true) // le damos acceso al menu
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save,menu) // inflamos la vista del menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home ->{
                requireActivity().onBackPressedDispatcher.onBackPressed() // para el botron regresar
                true
            }
            R.id.action_save->{
                Snackbar.make(mBinding.root,getString(R.string.edit_store_message_save_succes),  // para configurar un mensaje para em boton de guardar
                    Snackbar.LENGTH_SHORT)
                        .show()
                true
            }else ->super.onOptionsItemSelected(item)

        }
       // return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false) // para ocutlar el boton de retroseso
        mActivity?.supportActionBar?.title=getString(R.string.app_name) // para mostrar el titulo de la app y no del fratment
        mActivity?.hideFab(true) // para visualizar el boton flotante

        setHasOptionsMenu(false)

        super.onDestroy()
    }


}
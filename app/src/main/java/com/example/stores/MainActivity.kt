package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var  mBinding:ActivityMainBinding
    private lateinit var  mAdapter: StoreAdapter //con esto no traemos lo del adapter
    private lateinit var mGridLayout:GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSave.setOnClickListener {
            val storeEntity = StoreEntity(name = mBinding.etName.text.toString().trim()) //Con esto nos traemos lo que esta en et de la main act, el metodo trim es para quitar espacios
            mAdapter.add(storeEntity)

        }

        setupRecyclerView()

    }

    private fun setupRecyclerView() {  //configuracion del grid
        mAdapter= StoreAdapter(mutableListOf(),this)  //mandamos hablar al adapter
        mGridLayout=GridLayoutManager(this,2) //configuracion del grid

        mBinding.recyclerView.apply {
            setHasFixedSize(true)//se utiliza para que no cambie de tamaño al especificado en el layout
            layoutManager =mGridLayout //se utiliza para indicarle que va ser de tipo grid
            adapter = mAdapter
        }


    }

    //OnClickListener
    override fun onClick(storeEntity: StoreEntity) {

    }
}
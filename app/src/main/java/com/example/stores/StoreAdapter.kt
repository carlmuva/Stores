package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter(private var stores: MutableList<Store>,private var listener:OnClickListener):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    private lateinit var mContext:Context //variable para asignar el contexto de la clase


    inner class ViewHolder (view:View): RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view) //declaramos la variable binding

        fun setListener(store:Store){ //configuramos nuestro listener
            binding.root.setOnClickListener{ listener.onClick(store)}
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext=parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =stores.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)

        with(holder){
            setListener(store)// seteamos nuestro listener pasandole un store

            binding.tvName.text=store.name  // le inicamos la ruta para carga el nombre de nuestra clase store
        }
    }

    fun add(store: Store) {
        stores.add(store) // para agregar la store
        notifyDataSetChanged()// para que refresque la vista despues de agregar
    }

}
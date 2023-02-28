package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter(private var stores: MutableList<StoreEntity>, private var listener:OnClickListener):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    private lateinit var mContext:Context //variable para asignar el contexto de la clase


    inner class ViewHolder (view:View): RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view) //declaramos la variable binding

        fun setListener(storeEntity:StoreEntity){ //configuramos nuestro listener
            binding.root.setOnClickListener{ listener.onClick(storeEntity)}
            binding.cbFavorite.setOnClickListener { listener.onFavoriteStore(storeEntity) }
            binding.root.setOnLongClickListener{listener.onDeleteStore(storeEntity); true} // con un click largo eliminaremos el elemento
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

            //aqui configuramos lo que queremos reflejar en nuestro componentes con nuestro adaptador
            binding.tvName.text=store.name  // le inicamos la ruta para carga el nombre de nuestra clase store
            binding.cbFavorite.isChecked=store.isFavorite // le inidicamos la ruta para cargar el favorito de nuestra clase store
        }
    }

    fun add(storeEntity: StoreEntity) {
        stores.add(storeEntity) // para agregar la store
        notifyDataSetChanged()// para que refresque la vista despues de agregar
    }

    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores =stores // para hacer la consulta de todas las stores
        notifyDataSetChanged() // para noficar un cambio
    }

    fun update(storeEntity: StoreEntity) { // para actualizar favoritos
        val index = stores.indexOf(storeEntity) // obtenemos el index de la tienda
      if(index!=-1){
        stores.set(index,storeEntity)
          notifyItemChanged(index)
        }
    }

    fun delete(storeEntity: StoreEntity) { // para eliminar una store
        val index = stores.indexOf(storeEntity) // obtenemos el index de la tienda
        if(index!=-1){
            stores.removeAt(index)
            notifyItemRemoved(index)
        }
    }

}
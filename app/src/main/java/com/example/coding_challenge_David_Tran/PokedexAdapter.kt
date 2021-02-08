package com.example.coding_challenge_David_Tran

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.*


class PokedexAdapter @SuppressLint("CommitPrefEdits") internal constructor(context: Context?) : RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder>(), Filterable {

    class PokedexViewHolder internal constructor(view: View) : ViewHolder(view) {
        val containerView: LinearLayout = view.findViewById(R.id.pokedex_row)
        val textView: TextView = view.findViewById(R.id.pokedex_row_text_view)

        init {
            containerView.setOnClickListener { v ->
                val current = containerView.tag as Pokemon
                val intent = Intent(v.context, PokemonActivity::class.java)
                intent.putExtra("url", current.url)
                v.context.startActivity(intent)
            }
        }
    }

    private val pokemon: MutableList<Pokemon> = ArrayList()
    private var filtered: List<Pokemon> = pokemon
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    //filters Pokemon list based on search
    private inner class PokemonFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            Log.d("Pokedex", "Filter constraint: $constraint")
            val results = FilterResults()
            val filteredPokemon: MutableList<Pokemon> = ArrayList()
            for (poke in pokemon) {
                if (poke.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredPokemon.add(poke)
                }
            }
            results.values = filteredPokemon
            results.count = filteredPokemon.size
            Log.d("Pokedex", "Filter size: " + String.format("%d", results.count))
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filtered = results.values as List<Pokemon>
            notifyDataSetChanged()
        }
    }

    //Show Pokemon up to Generation 3
    //Generation 1: 1-151
    //Gen 2: 125-251
    //Gen 3: 252-386
    private fun loadPokemon() {
        //could add an offset at the end to allow certain amount to load at a time
        val url = "https://pokeapi.co/api/v2/pokemon?limit=386"
        val request = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            try {
                val results = response.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val result = results.getJSONObject(i)
                    val name = result.getString("name").substring(0,1).toUpperCase() + result.getString("name").substring(1).toLowerCase()
                    pokemon.add(Pokemon(
                            name,
                            result.getString("url"))
                    )
                }
                notifyDataSetChanged()
            } catch (e: JSONException) {
                Log.e("Pokedex", "Json error")
            }
        }, Response.ErrorListener { Log.e("Pokedex", "Pokemon list error") }
        )
        requestQueue.add(request)
    }
    init {
        loadPokemon()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pokedex_row, parent, false)
        return PokedexViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokedexViewHolder, position: Int) {
        val current = filtered[position]
        holder.textView.text = current.name
        holder.containerView.tag = current
    }

    override fun getItemCount(): Int {
        return filtered.size
    }

    override fun getFilter(): Filter {
        return PokemonFilter()
    }

}

package com.example.coding_challenge_David_Tran

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PokemonActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var numberTextView: TextView
    private lateinit var type1TextView: TextView
    private lateinit var type2TextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var url: String
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon)
        url = intent.getStringExtra("url").toString()
        Log.d("Pokedex", "URL: $url")
        requestQueue = Volley.newRequestQueue(applicationContext)
        nameTextView = findViewById(R.id.pokemon_name)
        numberTextView = findViewById(R.id.pokemon_number)
        type1TextView = findViewById(R.id.pokemon_type_1)
        type2TextView = findViewById(R.id.pokemon_type_2)
        descriptionTextView = findViewById(R.id.pokemon_description)
        load()
    }

    //Makes a request to the data base
    private fun load() {
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    loadText(response)
                    loadDescription(response)
                }, Response.ErrorListener { Log.e("Pokedex", "Pokemon detail error") }
        )
        requestQueue.add(request)
    }

    //Loads the name of the Pokemon, type, and description
    private fun loadText(response: JSONObject) {
        try {
            val typeEntries = response.getJSONArray("types")
            val name = "Pokemon: " + response.getString("name").substring(0, 1)
                    .toUpperCase(Locale.ROOT) + response.getString("name")
                    .substring(1).toLowerCase(Locale.ROOT)
            nameTextView.text = name
            numberTextView.text = String.format(Locale.getDefault(), "#%03d", response.getInt("id"))
            for (i in 0 until typeEntries.length()) {
                val typeEntry = typeEntries.getJSONObject(i)
                val slot = typeEntry.getInt("slot")
                val type = "Type: " + typeEntry.getJSONObject("type").getString("name")
                        .substring(0, 1).toUpperCase(Locale.ROOT) + typeEntry.getJSONObject("type").getString("name")
                        .substring(1).toLowerCase(Locale.ROOT)
                if (slot == 1) {
                    type1TextView.text = type
                    type1TextView.visibility = View.VISIBLE
                } else if (slot == 2) {
                    type2TextView.text = type
                    type2TextView.visibility = View.VISIBLE
                }
            }
        } catch (e: JSONException) {
            Log.e("Pokedex", "Pokemon text JSON error")
        }
    }

    private fun loadDescription(response: JSONObject) {
        var speciesUrl = ""
        try {
            speciesUrl = response.getJSONObject("species").getString("url")
        } catch (e: JSONException) {
            Log.e("Pokedex", "Pokemon description JSON error")
        }
        Log.d("Pokedex", "Pokemon species URL: $speciesUrl")
        if (speciesUrl != "") {
            val descriptionResponse = JsonObjectRequest(Request.Method.GET, speciesUrl,
                    null, Response.Listener { response ->
                var descriptions: JSONArray? = null
                try {
                    descriptions = response.getJSONArray("flavor_text_entries")
                } catch (e: JSONException) {
                    Log.e("Pokedex", "Pokemon flavor text array JSON error")
                }
                if (descriptions != null) {
                    for (i in 0 until descriptions.length()) {
                        try {
                            if (descriptions.getJSONObject(i).getJSONObject("language").getString("name") == "en") {
                                val description: String = descriptions.getJSONObject(i).getString("flavor_text")
                                descriptionTextView.text = description
                                break
                            }
                        } catch (e: JSONException) {
                            Log.e("Pokedex", "Pokemon flavor text string JSON error")
                        }
                    }
                }
            }, Response.ErrorListener { Log.e("Pokedex", "Pokemon description request error") }
            )
            requestQueue.add(descriptionResponse)
        }
    }
}

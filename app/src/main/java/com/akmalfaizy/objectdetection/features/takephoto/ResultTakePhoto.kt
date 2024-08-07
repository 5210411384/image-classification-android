package com.akmalfaizy.objectdetection.features.takephoto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akmalfaizy.objectdetection.R
import org.json.JSONObject
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONException
import kotlin.collections.ArrayList

class ResultTakePhoto : AppCompatActivity() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var listDataResult :ArrayList<DataClassResult>
    private lateinit var progressBar : ProgressBar
    private lateinit var textNotFound : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_take_photo)

        listDataResult = ArrayList()
        recyclerView = findViewById(R.id.ResultRV)
        progressBar = findViewById(R.id.ProgressBarSearch)
        textNotFound = findViewById(R.id.textViewNotFound)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val receivedIntent = intent
        val searchQuery = receivedIntent.getStringExtra("data")
        searchData(searchQuery ?: "")
    }

    private fun searchData(searchQuery: String) {
        val adapter = AdapterResult(this, listDataResult)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter
        progressBar.visibility = View.VISIBLE

        val apiKey = "9b3d584b7e433c07d8624165356ab055b7fa66910c275881ace00aa9158c04e1"
        val url = "https://serpapi.com/search.json?q=${searchQuery.trim()}&location=Jakarta,Indonesia&gl=us&google_domain=google.com&api_key=$apiKey"

        AndroidNetworking.get(url)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val organicResultsArray = response.getJSONArray("organic_results")
                        for (i in 0 until organicResultsArray.length()) {
                            val organicObj = organicResultsArray.getJSONObject(i)
                            val title = organicObj.optString("title")
                            val displayed_link = organicObj.optString("link")
                            val snippet = organicObj.optString("snippet")

                            listDataResult.add(DataClassResult(title, displayed_link, snippet))
                            adapter.notifyDataSetChanged()
                            progressBar.visibility = View.GONE

                            if (listDataResult.isEmpty()) {
                                textNotFound.visibility = View.VISIBLE
                            } else {
                                textNotFound.visibility = View.GONE
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@ResultTakePhoto, "No Result found for the search query..", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            })
        }
}
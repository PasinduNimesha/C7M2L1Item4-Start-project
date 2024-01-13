package com.example.littlelemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.littlelemon.ui.theme.LittleLemonTheme
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class MenuCategory(
    val menu: List<String>
)

@Serializable
data class RestaurantMenu(
    val Appetizers: MenuCategory,
    val Salads: MenuCategory,
    val Drinks: MenuCategory,
    val Dessert: MenuCategory
)

class MainActivity : ComponentActivity() {
    private val responseLiveData = MutableLiveData<String>()
    private val httpClient = HttpClient(Android){
        install(ContentNegotiation){
            json(contentType = ContentType.Application.Json)
        }
    }
    private val menuItemsLiveData = MutableLiveData<List<String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LittleLemonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val responseState = responseLiveData.observeAsState("").value

                    Column {
                        Button(
                            onClick = {
                                lifecycleScope.launch {
                                    val menuItems = getMenu("Salads")

                                    runOnUiThread {
                                        menuItemsLiveData.value = menuItems
                                    }
                                }
                            }
                        ) {
                            Text(text = "Download")
                        }

                        Text(text = responseState.toString())
                    }
                }
            }
        }
    }

    private suspend fun fetchContent(): String {
        return httpClient
            .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
            .bodyAsText()
    }

    private suspend fun getMenu(category: String): List<String> {
        val response: Map<String, MenuCategory> =
            httpClient.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
                .body()

        return response[category]?.menu ?: listOf()
    }
}

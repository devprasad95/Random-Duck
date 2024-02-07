package com.example.randomduck

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.randomduck.ui.theme.RandomDuckTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var duck by mutableStateOf(Duck())
    private var isLoading by mutableStateOf(true)


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch {
            sendResponse()
        }
        setContent {
            RandomDuckTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DuckScreen(duck = duck, isLoading = isLoading) {
                        sendResponse()
                    }
                }
            }
        }

    }


    @OptIn(DelicateCoroutinesApi::class)
    fun sendResponse() {
        isLoading = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getDuck()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        duck = response.body()!!
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                isLoading = false
            }

        }

    }

    @Composable
    fun DuckScreen(duck: Duck, isLoading: Boolean, onButtonClick: () -> Unit) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center

            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    AsyncImage(model = duck.url, contentDescription = "Duck")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                onButtonClick.invoke()
            }) {
                Text(text = "Load Random Duck Image")
            }
        }
    }
}

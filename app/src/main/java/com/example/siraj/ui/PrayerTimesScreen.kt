package com.example.siraj.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONObject
import com.example.siraj.ui.theme.GradientBackground


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var timings by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val getPrayerTimes = { location: Location ->
        val url = "https://api.aladhan.com/v1/timings?latitude=${location.latitude}&longitude=${location.longitude}&method=2"
        val queue = Volley.newRequestQueue(context)
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val data: JSONObject = response.getJSONObject("data").getJSONObject("timings")
                timings = mapOf(
                    "Fajr" to data.getString("Fajr"),
                    "Dhuhr" to data.getString("Dhuhr"),
                    "Asr" to data.getString("Asr"),
                    "Maghrib" to data.getString("Maghrib"),
                    "Isha" to data.getString("Isha")
                )
            },
            { it.printStackTrace() }
        )
        queue.add(jsonRequest)
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 1)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    getPrayerTimes(location)
                } else {
                    val request = LocationRequest.create().apply {
                        priority = Priority.PRIORITY_HIGH_ACCURACY
                        interval = 10000
                    }
                    fusedLocationClient.requestLocationUpdates(
                        request,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                fusedLocationClient.removeLocationUpdates(this)
                                result.lastLocation?.let { getPrayerTimes(it) }
                            }
                        },
                        Looper.getMainLooper()
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text("Horaires de prière", fontSize = 22.sp, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (timings.isEmpty()) {
                    Text("Chargement en cours...", fontSize = 16.sp)
                } else {
                    timings.forEach { (name, time) ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name, fontSize = 18.sp, color = Color(0xFF00695C))
                                Text(time, fontSize = 18.sp, color = Color(0xFF37474F))
                            }
                        }
                    }
                }
            }
        }
    }
}

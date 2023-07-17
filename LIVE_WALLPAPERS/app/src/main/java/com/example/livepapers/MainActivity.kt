package com.example.livepapers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.livepapers.databinding.ActivityMainBinding
import androidx.fragment.app.commit
import androidx.fragment.app.replace


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                Toast.makeText(this, "ok", Toast.LENGTH_LONG).show()
                supportFragmentManager.commit {
                    replace<BottomNavFragmentFragment>(R.id.mainContainer)
                }
            } else Toast.makeText(this, "no permissions", Toast.LENGTH_LONG).show()
        }
    //lateinit var repository:Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //repository = Repository(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        checkPermissions()



    }

    private fun checkPermissions() {
        val all = REQUEST_PERMISSIONS.all { perm ->
            ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        }
        if (all) {
            Toast.makeText(this, "permission allowed", Toast.LENGTH_LONG).show()
            supportFragmentManager.commit {
                replace<BottomNavFragmentFragment>(R.id.mainContainer)
            }
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.SET_WALLPAPER)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }.toTypedArray()
    }
}

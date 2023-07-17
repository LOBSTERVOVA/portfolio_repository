package com.example.livepapers

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.livepapers.databinding.BottomNavFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val PHOTO = "photo"
const val REQUEST_CODE = 200
const val CASH_LOC = "cash_loc"
const val TIMERESULT = "time_result"

class BottomNavFragmentFragment : Fragment() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                getTimeResult()

            } else Toast.makeText(requireContext(), "no permissions", Toast.LENGTH_LONG).show()
        }
    var binding: BottomNavFragmentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomNavFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding!!.chooseButton.setOnClickListener {
            choosePhoto()
        }

        binding!!.showButton.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                addToBackStack("AllPresetsFrag")
                setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_in_left
                    )

                replace(R.id.mainContainer, AllPresetsFragment())
                commit()
            }
        }





        ValueAnimator.ofObject(
            GradTypeEvaluator,
            intArrayOf(
                Color.rgb(2, 80, 150),
                Color.rgb(2, 80, 150),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 100, 190),
                Color.rgb(2, 100, 190),
            ),
            intArrayOf(
                Color.rgb(180, 180, 180),
                Color.rgb(2, 100, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 112, 190),
                Color.rgb(2, 112, 190),
            ),
            intArrayOf(
                Color.rgb(2, 100, 180),
                Color.rgb(180, 180, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 112, 190),
                Color.rgb(2, 112, 190),
            ),
            intArrayOf(
                Color.rgb(2, 100, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(180, 180, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 112, 190),
                Color.rgb(2, 112, 190),
            ),
            intArrayOf(
                Color.rgb(2, 100, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(180, 180, 180),
                Color.rgb(2, 112, 190),
                Color.rgb(2, 112, 190),
            ),
            intArrayOf(
                Color.rgb(2, 100, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 112, 190),
                Color.rgb(180, 180, 180),
                Color.rgb(2, 112, 190),
            ),
            intArrayOf(
                Color.rgb(2, 100, 180),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 112, 190),
                Color.rgb(2, 112, 190),
                Color.rgb(180, 180, 180),
            ),
            intArrayOf(
                Color.rgb(2, 80, 150),
                Color.rgb(2, 80, 150),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 2, 130),
                Color.rgb(2, 100, 190),
                Color.rgb(2, 100, 190),
            )
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            duration = 3000
            addUpdateListener {
                val textShader = LinearGradient(
                    0f, 0f,
                    binding!!.textView.paint.measureText(binding!!.textView.text.toString()),
                    binding!!.textView.textSize,
                    it.animatedValue as IntArray,
                    null,
                    Shader.TileMode.CLAMP
                )
                binding!!.textView.paint.shader = textShader
                binding!!.textView.invalidate()
            }
            start()
        }

        binding!!.imageView2.setOnClickListener {
            checkGeoPermission()
        }

        return binding!!.root
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data.toString()
            //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            //repository.addPhotoToRepo(imageUri)
            parentFragmentManager.beginTransaction().apply {
                addToBackStack("BottomNavFrag")
                setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_out_right
                )
                val bundle = Bundle()
                bundle.putString(PHOTO, imageUri)
                replace(R.id.mainContainer, ParametersFragment::class.java, bundle)
                commit()

            }
        }
    }

    object GradTypeEvaluator : TypeEvaluator<IntArray> {
        private val argbEvaluator = ArgbEvaluator()
        override fun evaluate(
            fraction: Float,
            startValue: IntArray?,
            endValue: IntArray?
        ): IntArray {
            return startValue!!.mapIndexed { index, item ->
                argbEvaluator.evaluate(
                    fraction, item, endValue!![index]
                ) as Int
            }.toIntArray()
        }

    }

    private fun checkGeoPermission() {
        val all = REQUEST_PERMISSIONS.all { perm ->
            ContextCompat.checkSelfPermission(requireContext(), perm) == PackageManager.PERMISSION_GRANTED
        }
        if (all) {
            getTimeResult()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getTimeResult(){
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Геопозиция включена
            binding!!.progressBar.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                var latitude = location?.latitude
                var longitude = location?.longitude
                Log.d("loc ", "${latitude}:${longitude}")
                if (latitude == null||longitude==null){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, object :
                        LocationListener {
                        override fun onLocationChanged(location: Location) {
                            latitude = location.latitude
                            longitude = location.longitude
                            if (longitude!=null&&latitude!=null) {
                                Log.d("got coordinates ", "${latitude}:${longitude}")
                                locationManager.removeUpdates(this)
                                viewLifecycleOwner.lifecycleScope.launch {
                                    try {
                                        val timeResult = timeFormatter(timeRetrofit.loadTime(latitude!!, longitude!!))
                                        Log.d("TimeResult", timeResult.toString())
                                        val pref = requireActivity().getSharedPreferences(
                                            CASH_LOC,
                                            Context.MODE_PRIVATE
                                        )
                                        val editor = pref.edit()
                                        editor.putString(TIMERESULT, Gson().toJson(timeResult))
                                        editor.apply()
                                        Snackbar.make(
                                            requireView(),
                                            "LOCATION SAVED",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Snackbar.make(
                                            requireView(),
                                            "ERROR! CHECK INTERNET CONNECTION",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                    binding!!.progressBar.visibility = View.INVISIBLE
                                }
                            }

                        }
                    })
                } else {
                    try {
                        val timeResult = timeFormatter(timeRetrofit.loadTime(latitude!!, longitude!!))
                        Log.d("TimeResult", timeResult.toString())
                        val pref = requireActivity().getSharedPreferences(CASH_LOC, Context.MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putString(TIMERESULT, Gson().toJson(timeResult))
                        editor.apply()
                        Snackbar.make(
                            requireView(),
                            "LOCATION SAVED",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }catch (e:Exception){
                        Snackbar.make(requireView(), "ERROR! CHECK INTERNET CONNECTION", Snackbar.LENGTH_SHORT).show()
                    }
                    binding!!.progressBar.visibility = View.INVISIBLE
                }
            }


        } else {
            // Геопозиция выключена
            Toast.makeText(requireContext(), "turn on geoposition and try again", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun timeFormatter(timeResult: TimeResult): TimeResult {
        val utcFormatter = SimpleDateFormat("hh:mm:ss a", Locale.US)
        utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
        val localFormatter = SimpleDateFormat("HH:mm")
        localFormatter.timeZone = TimeZone.getDefault()
        val srUtc = utcFormatter.parse(timeResult.results.sunrise)
        val sunrise = localFormatter.format(srUtc!!)
        val ssUtc = utcFormatter.parse(timeResult.results.sunset)
        val sunset = localFormatter.format(ssUtc!!)
        val snUtc = utcFormatter.parse(timeResult.results.solar_noon)
        val noon = localFormatter.format(snUtc!!)
        val ctbUtc = utcFormatter.parse(timeResult.results.civil_twilight_begin)
        val ctb = localFormatter.format(ctbUtc!!)
        val cteUtc = utcFormatter.parse(timeResult.results.civil_twilight_end)
        val cte = localFormatter.format(cteUtc!!)
        return TimeResult(Results(sunrise, sunset, noon, ctb, cte))
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }.toTypedArray()
    }

}


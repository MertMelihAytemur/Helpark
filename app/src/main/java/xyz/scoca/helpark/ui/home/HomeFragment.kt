package xyz.scoca.helpark.ui.home

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.MainActivity
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentHomeBinding
import xyz.scoca.helpark.databinding.ItemBottomSheetBinding
import xyz.scoca.helpark.model.park.Park
import xyz.scoca.helpark.model.park.ParkData
import xyz.scoca.helpark.model.park.naerbyparks.NearbyPark
import xyz.scoca.helpark.network.NetworkHelper
import xyz.scoca.helpark.network.nearby.IGoogleApiService
import xyz.scoca.helpark.ui.home.adapter.ParkDataAdapter
import xyz.scoca.helpark.util.OnItemClickListener
import xyz.scoca.helpark.util.common.Common
import java.io.IOException

class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parkDetailBinding: ItemBottomSheetBinding
    private lateinit var mMap: GoogleMap
    private lateinit var parkList: ArrayList<ParkData>

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null

    //Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    //Nearby place api service
    private lateinit var googleApiService: IGoogleApiService
    //internal lateinit var currentPlace: NearbyPark

    //Search Destination
    private lateinit var addressList: List<Address>

    //Navigation Drawer
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView : NavigationView
    private lateinit var headerView : View
    private lateinit var headerEmail : TextView

    companion object {
        private const val LOCATION_PERMISSION_CODE: Int = 1000
        private const val REQUEST_CHECK_SETTING: Int = 0x1
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        parkDetailBinding = ItemBottomSheetBinding.inflate(inflater,container,false)

        setNavigationDrawer()
        isUserLoggedIn()
        setRecyclerViewSnapHelper()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fParkMapLocation) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        googleApiService = Common.googleApiService

        //Request runtime permission
        requestRuntimePermission()
        getParkData()
        searchDestination()

        //Refresh Page
        binding.ivRefresh.setOnClickListener {
            refresh()
        }
        return binding.root
    }

    private fun isUserLoggedIn(){
        if(ClientPreferences(requireContext()).isRememberMe()){
            binding.navView.inflateMenu(R.menu.nav_menu)
            headerEmail.text = ClientPreferences(requireContext()).getUserEmail()
        }else
            binding.navView.inflateMenu(R.menu.nav_menu_guest)
    }
    private fun refresh(){
        mMap.clear()
        getParkData()
        requestRuntimePermission()
    }


    private fun setNavigationDrawer(){
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        headerView = navView.getHeaderView(0)
        headerEmail = headerView.findViewById(R.id.tvUserEmail)

        toggle = ActionBarDrawerToggle(activity as MainActivity,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_login -> findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                R.id.nav_profile -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                R.id.nav_feedback -> findNavController().navigate(R.id.action_homeFragment_to_feedbackFragment)
                R.id.nav_logout -> logout()
                R.id.nav_sync -> refresh()
                R.id.nav_history ->findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
            }
            true
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ), LOCATION_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ), LOCATION_PERMISSION_CODE
                )
                return false
            }
        }
        return true
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest.create()

        locationRequest = LocationRequest()
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000  //set the interval in which you want to get locations.
        locationRequest.fastestInterval =
            60000  // if a location is available sooner you can get it (i.e. another app is using the location services).
        locationRequest.smallestDisplacement = 10f

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val task = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        task.addOnCompleteListener {
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        try {
                            resolvableApiException.startResolutionForResult(
                                requireActivity(),
                                REQUEST_CHECK_SETTING
                            )
                        } catch (e: IntentSender.SendIntentException) {

                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0.locations[p0.locations.size - 1] //get last location
                if (mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                mMarker = mMap.addMarker(markerOptions)

                //Move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f))
                findNearbyPark(Common.PARK_ID)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallBack()

                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(activity as MainActivity)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()!!
                            )
                            mMap.isMyLocationEnabled = true
                        }
                } else
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /*override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }*/

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //init Google play services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }
        } else
            mMap.isBuildingsEnabled = true
        //Enable zoom control
        mMap.uiSettings.isZoomControlsEnabled = true

    }

    private fun setRecyclerViewAdapter(parkList: ArrayList<ParkData>) {
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvParkData.layoutManager = mLayoutManager
        binding.rvParkData.adapter = ParkDataAdapter(requireContext(),parkList, object : OnItemClickListener{
            override fun onClick(position: Int) {
                val parkName = parkList.get(position).parkName.toString()
                val action = HomeFragmentDirections.actionHomeFragmentToReservationFragment(parkName)
                findNavController().navigate(action)
            }
        })
    }

    private fun setRecyclerViewSnapHelper(){
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvParkData)
        binding.rvParkData.isNestedScrollingEnabled = false
    }

    private fun getParkData() {
        NetworkHelper().parkService?.getParkData()
            ?.enqueue(object : Callback<Park> {
                override fun onResponse(
                    call: Call<Park>,
                    response: Response<Park>
                ) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        parkList = response.body()?.parkData!!
                        setRecyclerViewAdapter(parkList)
                    }
                }

                override fun onFailure(call: Call<Park>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        t.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun requestRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(activity as MainActivity)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()!!
                )
            }
        } else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity as MainActivity)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()!!
            )
        }
    }

    private fun findNearbyPark(park: String) {

        //Build url request on location
        val url = getParkUrl(latitude, longitude, park)

        googleApiService.getNearbyPark(url)
            .enqueue(object : Callback<NearbyPark> {
                override fun onResponse(call: Call<NearbyPark>, response: Response<NearbyPark>) {
                    //currentPlace = response.body()!!
                    if (response.isSuccessful) {
                        for (i in 0 until (response.body()?.results!!.size)) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!!.get(i)
                            val lat = googlePlace.geometry?.location?.lat
                            val lng = googlePlace.geometry?.location?.lng

                            val placeName = googlePlace.name
                            val latLng = LatLng(lat!!, lng!!)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            if (park == "parking") {
                                markerOptions.icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_RED
                                    )
                                )
                            }
                            mMap.addMarker(markerOptions)
                            //Move camera
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), 13.5f
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<NearbyPark>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getParkUrl(latitude: Double, longitude: Double, park: String): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=${latitude}%2C${longitude}")
        googlePlaceUrl.append("&radius=3000") // 3Km
        googlePlaceUrl.append("&type=${park}")
        googlePlaceUrl.append("&key=AIzaSyCrJER5ymCvt6Lm6hWeZc8IL3cEQmR5eHQ")

        Log.d("URL_DEBUG", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }


    private fun searchDestination() {
        binding.svDestination.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val location = binding.svDestination.query.toString()
                val geocoder = Geocoder(activity as MainActivity)

                try {
                    mMap.clear()
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val address: Address = addressList[0]
                latitude = address.latitude
                longitude = address.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))

                mMap.addMarker(markerOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                findNearbyPark(Common.PARK_ID)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
    }

    private fun logout(){
        ClientPreferences(requireContext()).clearSharedPref()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }
}
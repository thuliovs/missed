package com.thuliotech.missed.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.thuliotech.missed.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

private val latLngSaver: Saver<LatLng?, Any> = Saver(
    save = { latLng ->
        latLng?.let { listOf(it.latitude, it.longitude) } ?: emptyList<Double>()
    },
    restore = { value ->
        val saved = value as? List<*> ?: return@Saver null
        if (saved.size != 2) {
            null
        } else {
            val lat = saved[0] as? Double ?: return@Saver null
            val lng = saved[1] as? Double ?: return@Saver null
            LatLng(lat, lng)
        }
    }
)

private const val SEARCH_DEBOUNCE_MS = 400L
private const val LOCAL_SEARCH_DELTA = 0.12
private const val EXTENDED_SEARCH_DELTA = 0.35
private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onConfirmDestination: (LatLng) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val defaultLocation = remember { LatLng(38.736946, -9.142685) } // Lisbon
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 5.5f)
    }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var selectedDestination by rememberSaveable(stateSaver = latLngSaver) { mutableStateOf<LatLng?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<MapSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchFeedback by remember { mutableStateOf<String?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var userRegionHint by remember { mutableStateOf<String?>(null) }
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result.values.any { it }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission && !hasRequestedPermission) {
            hasRequestedPermission = true
            permissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            userLocation = fusedLocationClient.fetchCurrentLatLng()
            userRegionHint = userLocation?.let { reverseGeocodeLocality(context, it) }
            userLocation?.let { latLng ->
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            }
        }
    }

    LaunchedEffect(query, userLocation, userRegionHint, isSearchExpanded) {
        if (!isSearchExpanded || query.length < 3) {
            searchResults = emptyList()
            searchFeedback = null
            isSearching = false
            return@LaunchedEffect
        }
        delay(SEARCH_DEBOUNCE_MS)
        isSearching = true
        val result = runCatching { searchLocations(context, query, userLocation, userRegionHint) }
        result.onSuccess { locations ->
            searchResults = locations
            searchFeedback = if (locations.isEmpty()) {
                context.getString(R.string.map_search_no_results)
            } else {
                null
            }
        }.onFailure {
            searchResults = emptyList()
            searchFeedback = context.getString(R.string.map_search_error)
        }
        isSearching = false
    }

    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme
    val mapStyleOptions = rememberMapStyleOptions(colorScheme)

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapStyleOptions = mapStyleOptions
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true
            ),
            contentPadding = PaddingValues(
                top = 140.dp,
                bottom = 220.dp,
                start = 24.dp,
                end = 24.dp
            ),
            onMapClick = { latLng ->
                selectedDestination = latLng
            }
        ) {
            selectedDestination?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = context.getString(R.string.map_selected_point_label)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 42.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CollapsedSearchField(
                    modifier = Modifier.weight(1f),
                    text = query,
                    placeholder = stringResource(R.string.map_search_placeholder),
                    onClick = { isSearchExpanded = true }
                )

                FilledIconButton(
                    modifier = Modifier.size(56.dp),
                    onClick = {
                        val target = selectedDestination ?: userLocation ?: defaultLocation
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    target,
                                    if (target == defaultLocation) 5.5f else 14f
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = stringResource(R.string.map_center_on_me)
                    )
                }
            }
        }

        if (isSearchExpanded) {
            MapSearchOverlay(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .zIndex(2f),
                query = query,
                isSearching = isSearching,
                feedback = searchFeedback,
                results = searchResults,
                onQueryChange = { query = it },
                onDismiss = {
                    isSearchExpanded = false
                    focusManager.clearFocus()
                },
                onClearQuery = { query = "" },
                onResultSelected = { resultItem ->
                    query = resultItem.title
                    selectedDestination = resultItem.location
                    isSearchExpanded = false
                    searchResults = emptyList()
                    searchFeedback = null
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                resultItem.location,
                                14f
                            )
                        )
                    }
                },
                onChooseOnMap = {
                    isSearchExpanded = false
                    focusManager.clearFocus()
                }
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.map_instruction),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onCancel
                    ) {
                        Text(text = stringResource(R.string.map_cancel))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = selectedDestination != null,
                        onClick = { selectedDestination?.let(onConfirmDestination) }
                    ) {
                        Text(text = stringResource(R.string.map_confirm))
                    }
                }
            }
        }
    }
}

private fun Context.hasLocationPermission(): Boolean {
    return LOCATION_PERMISSIONS.any { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

private suspend fun FusedLocationProviderClient.fetchCurrentLatLng(): LatLng? {
    return runCatching {
        val cancellationToken = CancellationTokenSource()
        val currentLocation = getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancellationToken.token
        ).await()
        val bestLocation = currentLocation ?: lastLocation.await()
        bestLocation?.let { LatLng(it.latitude, it.longitude) }
    }.getOrNull()
}

private suspend fun searchLocations(
    context: Context,
    query: String,
    anchor: LatLng?,
    regionHint: String?
): List<MapSearchResult> = withContext(Dispatchers.IO) {
    if (!Geocoder.isPresent()) return@withContext emptyList()
    runCatching {
        val geocoder = Geocoder(context, Locale.getDefault())

        val nearbyResults = anchor?.let {
            val bounds = it.calculateSearchBounds(LOCAL_SEARCH_DELTA)
            @Suppress("DEPRECATION")
            geocoder.getFromLocationName(
                query,
                8,
                bounds.southWest.latitude,
                bounds.southWest.longitude,
                bounds.northEast.latitude,
                bounds.northEast.longitude
            ) ?: emptyList()
        } ?: emptyList()

        val extendedResults = anchor?.let {
            val bounds = it.calculateSearchBounds(EXTENDED_SEARCH_DELTA)
            @Suppress("DEPRECATION")
            geocoder.getFromLocationName(
                query,
                8,
                bounds.southWest.latitude,
                bounds.southWest.longitude,
                bounds.northEast.latitude,
                bounds.northEast.longitude
            ) ?: emptyList()
        } ?: emptyList()

        val regionResults = if (!regionHint.isNullOrBlank()) {
            @Suppress("DEPRECATION")
            geocoder.getFromLocationName("$query $regionHint", 8) ?: emptyList()
        } else {
            emptyList()
        }

        val fallback = if (nearbyResults.isEmpty() && regionResults.isEmpty()) {
            @Suppress("DEPRECATION")
            geocoder.getFromLocationName(query, 8) ?: emptyList()
        } else {
            emptyList()
        }

        val combined = (nearbyResults + regionResults + extendedResults + fallback)
            .distinctBy { it.latitude to it.longitude }

        val sorted = anchor?.let { location ->
            combined.sortedBy { address -> address.distanceMetersTo(location) }
        } ?: combined

        sorted.mapNotNull { address ->
            val distance = anchor?.let { address.distanceMetersTo(it) }
            address.toMapSearchResult(distance)
        }
    }.getOrElse {
        emptyList()
    }
}

private fun Address.toMapSearchResult(distanceMeters: Float?): MapSearchResult? {
    val latitude = latitude
    val longitude = longitude
    if (latitude == 0.0 && longitude == 0.0) return null
    val title = when {
        !featureName.isNullOrBlank() -> featureName
        !thoroughfare.isNullOrBlank() -> thoroughfare
        !locality.isNullOrBlank() -> locality
        !adminArea.isNullOrBlank() -> adminArea
        !countryName.isNullOrBlank() -> countryName
        else -> null
    } ?: return null
    val subtitle = buildList {
        if (!thoroughfare.isNullOrBlank() && thoroughfare != title) add(thoroughfare)
        if (!subLocality.isNullOrBlank()) add(subLocality)
        if (!locality.isNullOrBlank()) add(locality)
        if (!adminArea.isNullOrBlank()) add(adminArea)
    }.distinct()
        .takeIf { it.isNotEmpty() }
        ?.joinToString(separator = ", ")
        ?: getAddressLine(0)?.takeIf { it != title }
    return MapSearchResult(
        title = title,
        subtitle = subtitle,
        location = LatLng(latitude, longitude),
        distanceMeters = distanceMeters
    )
}

private fun Address.distanceMetersTo(target: LatLng): Float {
    val results = FloatArray(1)
    Location.distanceBetween(
        target.latitude,
        target.longitude,
        latitude,
        longitude,
        results
    )
    return results.first()
}

private suspend fun reverseGeocodeLocality(
    context: Context,
    position: LatLng
): String? = withContext(Dispatchers.IO) {
    if (!Geocoder.isPresent()) return@withContext null
    runCatching {
        val geocoder = Geocoder(context, Locale.getDefault())
        @Suppress("DEPRECATION")
        geocoder.getFromLocation(position.latitude, position.longitude, 1)?.firstOrNull()
    }.getOrNull()?.let { address ->
        listOfNotNull(
            address.subLocality,
            address.locality,
            address.adminArea
        )
            .filter { !it.isNullOrBlank() }
            .distinct()
            .joinToString(separator = ", ")
            .ifBlank { null }
    }
}

private data class SearchBounds(
    val southWest: LatLng,
    val northEast: LatLng
)

private fun LatLng.calculateSearchBounds(deltaDegrees: Double = LOCAL_SEARCH_DELTA): SearchBounds {
    val southWest = LatLng(
        (latitude - deltaDegrees).coerceAtLeast(-90.0),
        (longitude - deltaDegrees).coerceAtLeast(-180.0)
    )
    val northEast = LatLng(
        (latitude + deltaDegrees).coerceAtMost(90.0),
        (longitude + deltaDegrees).coerceAtMost(180.0)
    )
    return SearchBounds(southWest, northEast)
}

@Composable
private fun CollapsedSearchField(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (text.isNotEmpty()) text else placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = if (text.isNotEmpty()) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MapSearchOverlay(
    modifier: Modifier = Modifier,
    query: String,
    isSearching: Boolean,
    feedback: String?,
    results: List<MapSearchResult>,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onClearQuery: () -> Unit,
    onResultSelected: (MapSearchResult) -> Unit,
    onChooseOnMap: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            MapSearchToolbar(
                query = query,
                onQueryChange = onQueryChange,
                onDismiss = onDismiss,
                onClear = onClearQuery
            )
            HorizontalDivider()
            SearchResultsSection(
                modifier = Modifier.weight(1f),
                isSearching = isSearching,
                feedback = feedback,
                results = results,
                onResultSelected = onResultSelected
            )
            ChooseOnMapRow(onChooseOnMap = onChooseOnMap)
        }
    }
}

@Composable
private fun MapSearchToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .focusRequester(focusRequester),
        singleLine = true,
        leadingIcon = {
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    onDismiss()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.map_search_back)
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.map_search_clear)
                    )
                }
            }
        },
        placeholder = { Text(text = stringResource(R.string.map_search_placeholder)) },
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() }
        )
    )
}

@Composable
private fun SearchResultsSection(
    modifier: Modifier = Modifier,
    isSearching: Boolean,
    feedback: String?,
    results: List<MapSearchResult>,
    onResultSelected: (MapSearchResult) -> Unit
) {
    when {
        isSearching -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        results.isNotEmpty() -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth()
            ) {
                items(results) { result ->
                    SearchResultRow(
                        result = result,
                        onClick = { onResultSelected(result) }
                    )
                    HorizontalDivider()
                }
            }
        }

        feedback != null -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.map_search_no_results),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChooseOnMapRow(
    onChooseOnMap: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onChooseOnMap),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.map_search_choose_on_map),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                imageVector = ArrowInsertIcon,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SearchResultRow(
    result: MapSearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            result.distanceMeters?.let {
                Text(
                    text = formatDistance(it),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            result.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = ArrowInsertIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDistance(distanceMeters: Float): String {
    return if (distanceMeters >= 1000f) {
        val kilometers = distanceMeters / 1000f
        String.format(Locale.getDefault(), "%.1f km", kilometers)
    } else {
        "${distanceMeters.roundToInt()} m"
    }
}

private val ArrowInsertIcon
    get() = Icons.AutoMirrored.Filled.ArrowForward

@Composable
private fun rememberMapStyleOptions(colorScheme: ColorScheme): MapStyleOptions? {
    val json = remember(colorScheme) { buildMapStyleJson(colorScheme) }
    return remember(json) {
        runCatching { MapStyleOptions(json) }.getOrNull()
    }
}

private fun buildMapStyleJson(colorScheme: ColorScheme): String {
    val surface = colorScheme.surface.toMapHex()
    val surfaceVariant = colorScheme.surfaceVariant.toMapHex()
    val background = colorScheme.background.toMapHex()
    val primary = colorScheme.primary.toMapHex()
    val outline = colorScheme.outline.toMapHex()
    val onSurface = colorScheme.onSurface.toMapHex()

    return """
        [
          {"elementType":"geometry","stylers":[{"color":"$surface"}]},
          {"elementType":"labels.text.fill","stylers":[{"color":"$onSurface"}]},
          {"elementType":"labels.text.stroke","stylers":[{"color":"$surface"}]},
          {"featureType":"administrative","elementType":"geometry.stroke","stylers":[{"color":"$outline"}]},
          {"featureType":"poi","elementType":"geometry","stylers":[{"color":"$surfaceVariant"}]},
          {"featureType":"poi.park","elementType":"geometry","stylers":[{"color":"$background"}]},
          {"featureType":"road","elementType":"geometry","stylers":[{"color":"$surfaceVariant"}]},
          {"featureType":"road","elementType":"labels.text.fill","stylers":[{"color":"$onSurface"}]},
          {"featureType":"transit","elementType":"geometry","stylers":[{"color":"$surfaceVariant"}]},
          {"featureType":"water","elementType":"geometry","stylers":[{"color":"$primary"}]},
          {"featureType":"water","elementType":"labels.text.fill","stylers":[{"color":"$onSurface"}]}
        ]
    """.trimIndent()
}

private fun Color.toMapHex(): String {
    val intColor = toArgb() and 0xFFFFFF
    return String.format("#%06X", intColor)
}

@Stable
data class MapSearchResult(
    val title: String,
    val subtitle: String?,
    val location: LatLng,
    val distanceMeters: Float?
)

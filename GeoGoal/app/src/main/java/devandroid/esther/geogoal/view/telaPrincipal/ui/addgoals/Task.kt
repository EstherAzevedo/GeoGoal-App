package devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals
import com.google.android.gms.maps.model.LatLng
data class Task(
    var title: String? = null,
    var description: String? = null,
    var location: LatLng? = null
)

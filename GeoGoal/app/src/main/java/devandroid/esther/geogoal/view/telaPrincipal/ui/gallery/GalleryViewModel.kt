package devandroid.esther.geogoal.view.telaPrincipal.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Nenhuma foto aqui"
    }
    val text: LiveData<String> = _text
}
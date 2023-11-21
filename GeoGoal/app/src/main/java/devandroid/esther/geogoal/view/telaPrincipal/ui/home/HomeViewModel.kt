package devandroid.esther.geogoal.view.telaPrincipal.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Nenhuma Meta Geográfica aqui"
    }
    val text: LiveData<String> = _text
}
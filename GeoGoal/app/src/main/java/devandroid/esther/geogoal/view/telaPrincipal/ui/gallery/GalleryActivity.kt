package devandroid.esther.geogoal.view.telaPrincipal.ui.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devandroid.esther.geogoal.R

class GalleryActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Configuração da toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val addPhotoButton: FloatingActionButton = findViewById(R.id.addPhoto)
        addPhotoButton.setOnClickListener {
            openGallery()
        }

    }

    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri = data.data!!

            // Aqui você pode salvar a URI da imagem no banco de dados
            saveImageToDatabase(selectedImageUri)

            // Exibe a imagem na ImageView
            val imageView: ImageView = findViewById(R.id.imageView)
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveImageToDatabase(imageUri: Uri) {
        // Aqui você pode salvar a URI no banco de dados, por exemplo, usando o Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("data/completed_tasks/photos")

    }
}

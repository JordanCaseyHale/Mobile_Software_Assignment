package com.example.mobilesoftwareassignment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mobilesoftwareassignment.data.ImageDataDao
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ShowImageActivity : AppCompatActivity() {
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    lateinit var daoObj: ImageDataDao

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val position = result.data?.getIntExtra("position", -1)
            val id = result.data?.getIntExtra("id", -1)
            val del_flag = result.data?.getIntExtra("deletion_flag", -1)
            var intent = Intent().putExtra("position", position)
                .putExtra("id", id).putExtra("deletion_flag", del_flag)
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    this.setResult(result.resultCode, intent)
                    this.finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)
        val bundle: Bundle? = intent.extras
        var position = -1

        daoObj = (this@ShowImageActivity.application as ImageApplication)
            .databaseObj.imageDataDao()

        if (bundle != null) {
            // this is the image position in the itemList
            position = bundle.getInt("position")
            displayData(position)
        }
    }

    private fun displayData(position: Int){
        if (position != -1) {
            val imageView = findViewById<ImageView>(R.id.show_image)
            //val imgTitle = findViewById<Toolbar>(R.id.show_toolbar)
            val imgTitle = findViewById<TextView>(R.id.title)
            val descriptionTextView = findViewById<TextView>(R.id.description)
            val descriptionTextView2 = findViewById<TextView>(R.id.description2)
            val imageData = MyAdapter.items[position]

            imageView.setImageBitmap(MyAdapter.items[position].thumbnail!!)
            imgTitle.text = MyAdapter.items[position].imageTitle
            descriptionTextView.text = MyAdapter.items[position].imageDescription
            descriptionTextView2.text = MyAdapter.items[position].imageDescription
            val fabEdit: ExtendedFloatingActionButton = findViewById(R.id.fab_edit)
            fabEdit.setOnClickListener(View.OnClickListener {
                startForResult.launch(
                    Intent( this, MainActivity::class.java).apply {
                        putExtra("position", position)
                    }
                )
            })
        }
    }
}
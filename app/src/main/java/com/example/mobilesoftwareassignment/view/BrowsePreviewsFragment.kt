package com.example.mobilesoftwareassignment.view

import androidx.fragment.app.FragmentActivity
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.mobilesoftwareassignment.ImageApplication
import com.example.mobilesoftwareassignment.MyAdapter
import com.example.mobilesoftwareassignment.R
import com.example.mobilesoftwareassignment.ShowImageActivity
import com.example.mobilesoftwareassignment.data.ImageData
import com.example.mobilesoftwareassignment.data.ImageDataDao
import com.example.mobilesoftwareassignment.databinding.ActivityGalleryBinding
import com.example.mobilesoftwareassignment.databinding.BrowsePreviewsBinding
import com.example.mobilesoftwareassignment.databinding.ContentCameraBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import pl.aprilapps.easyphotopicker.*
import kotlinx.android.synthetic.main.content_camera.grid_recycler_view

import java.util.ArrayList
class BrowsePreviewsFragment : Fragment() {

    private var myDataset: MutableList<ImageData> = ArrayList<ImageData>()
    private lateinit var daoObj: ImageDataDao
    private lateinit var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var fragment: FragmentActivity
    private lateinit var easyImage: EasyImage

    companion object {
        private val REQUEST_READ_EXTERNAL_STORAGE = 2987
        private val REQUEST_WRITE_EXTERNAL_STORAGE = 7829
        private val REQUEST_CAMERA_CODE = 100
    }


    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val pos = result.data?.getIntExtra("position", -1)!!
                val id = result.data?.getIntExtra("id", -1)!!
                val del_flag = result.data?.getIntExtra("deletion_flag", -1)!!
                if (pos != -1 && id != -1) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        when(del_flag){
                            -1, 0 -> mAdapter.notifyDataSetChanged()
                            else -> mAdapter.notifyItemRemoved(pos)
                        }
                    }
                }
            }
        }

    private var _binding: ActivityGalleryBinding? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = ActivityGalleryBinding.inflate(inflater, container, false)
        val view = ContentCameraBinding.inflate(inflater, container, false)
        // this.activity?.application
        fragment = activity!!
        daoObj = (fragment.application as ImageApplication).databaseObj.imageDataDao()
        //setContentView(R.layout.activity_gallery)
        //val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        initData()
        Log.d("TAG", "message")

        //mRecyclerView = view.findViewById(R.id.grid_recycler_view)
        //mRecyclerView = fragment.findViewById(R.id.grid_recycler_view)
        mRecyclerView = view.gridRecyclerView
        // set up the RecyclerView
        val numberOfColumns = 4
        mRecyclerView.layoutManager = GridLayoutManager(fragment, numberOfColumns)
        mAdapter = MyAdapter(myDataset) as Adapter<RecyclerView.ViewHolder>
        mRecyclerView.adapter = mAdapter


        // required by Android 6.0 +
        checkPermissions(requireActivity().application)
        initEasyImage()

        // the floating button that will allow us to get the images from the Gallery
        val fabGallery: FloatingActionButton = _binding!!.fabGallery
        fabGallery.setOnClickListener(View.OnClickListener {
            easyImage.openChooser(fragment)
        })

        /*val goBack: Button = findViewById(R.id.button1)
        goBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ShowImageActivity::class.java))
        })*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button1.setOnClickListener {
            findNavController().navigate(R.id.action_BrowsePreviews_to_HomePage)
        }
    }

    /**
     * it initialises EasyImage
     */
    private fun initEasyImage() {
        easyImage = EasyImage.Builder(fragment)
            .setChooserTitle("Pick media")
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(true)
            .setCopyImagesToPublicGalleryFolder(true)
            .build()
    }
    /**
     * Init data by loading from the database
     */
    private fun initData() {
        GlobalScope.launch {
            daoObj = (fragment.application as ImageApplication).databaseObj.imageDataDao()
            var data = daoObj.getItems()
            myDataset.addAll(data)
        }
    }
    /**
     * insert a ImageData into the database
     * Called for each image the user adds by clicking the fab button
     * Then retrieves the same image so we can have the automatically assigned id field
     */
    private fun insertData(imageData: ImageData): Int = runBlocking {
        //TODO: remove code
        var insertJob = async { daoObj.insert(imageData) }
        insertJob.await().toInt()
    }

    /**
     * check permissions are necessary starting from Android 6
     * if you do not set the permissions, the activity will simply not work and you will be probably baffled for some hours
     * until you find a note on StackOverflow
     * @param context the calling context
     */
    private fun checkPermissions(context: Context) {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        fragment,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder: AlertDialog.Builder =
                        AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ), REQUEST_READ_EXTERNAL_STORAGE
                            )
                        })
                    val alert: AlertDialog = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        fragment,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        fragment,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("Writing external storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), REQUEST_WRITE_EXTERNAL_STORAGE
                            )
                        })
                    val alert: AlertDialog = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        fragment,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL_STORAGE
                    )
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    fragment,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_CODE);
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(requestCode, resultCode,data,fragment,
            object: DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onPhotosReturned(imageFiles)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }

                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            })
    }

    /**
     * add the selected images to the grid
     * @param returnedPhotos
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun onPhotosReturned(returnedPhotos: Array<MediaFile>) {
        myDataset.addAll(getImageData(returnedPhotos))

        // we tell the adapter that the data is changed and hence the grid needs
        // refreshing
        mAdapter.notifyDataSetChanged()
        mRecyclerView.scrollToPosition(returnedPhotos.size - 1)
    }

    /**
     * given a list of photos, it creates a list of ImageElements
     * we do not know how many elements we will have
     * @param returnedPhotos
     * @return
     */
    private fun getImageData(returnedPhotos: Array<MediaFile>): List<ImageData> {
        val imageDataList: MutableList<ImageData> = ArrayList<ImageData>()
        for (mediaFile in returnedPhotos) {
            val fileNameAsTempTitle = mediaFile.file.name
            var imageData = ImageData(
                //imageTitle = fileNameAsTempTitle,
                imageUri = mediaFile.file.absolutePath
            )
            // Update the database with the newly created object
            var id = insertData(imageData)
            imageData.imageId = id
            imageDataList.add(imageData)
        }
        return imageDataList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
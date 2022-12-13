package ch.tanchuca.daa_labo4

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class ImagesViewAdapter : RecyclerView.Adapter<ImagesViewAdapter.ViewHolder>() {

    var items = listOf<Bitmap>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.imageView)

        fun bind(position: Int){
            var url = "https://daa.iict.ch/images/$position.jpg"
            lifecycleScope.launch {
                val bytes = downloadImage(url)
                val bmp = decodeImage(bytes)
                displayImage(bmp)
            }
        }
    }

    suspend fun downloadImage(url : URL) : ByteArray? = withContext(Dispatchers.IO) {
        try {
            url.readBytes()
        } catch (e: IOException) {
            println("Exception while downloading image " + e.message)
            null
        }
    }

    suspend fun decodeImage(bytes : ByteArray?) : Bitmap? = withContext(Dispatchers.Default) {
        try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
        } catch (e: IOException) {
            println("Exception while decoding image" + e.message)
            null
        }
    }

    suspend fun displayImage(bmp : Bitmap?) = withContext(Dispatchers.Main) {
        if(bmp != null)
            //myImage.setImageBitmap(bmp)
        else{}
            //myImage.setImageResource(android.R.color.transparent)
    }

}
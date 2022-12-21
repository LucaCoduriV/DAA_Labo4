package ch.tanchuca.daa_labo4

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.net.URL

class ImagesViewAdapter(private var lifecycle: LifecycleCoroutineScope) : RecyclerView.Adapter<ImagesViewAdapter.ViewHolder>() {
    private val viewHolders = mutableListOf<ViewHolder>()
    var items : List<Int> = listOf(1.. 10000).flatten()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pic_item, parent, false))
    }

    fun getViewHolders(): List<ViewHolder> {
        return viewHolders
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewHolders.add(holder)
        holder.bind(position)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.stopLoading()
        viewHolders.remove(holder)
    }

    inner class ViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.imageView)
        var progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val imagesCacheDir = File(view.context.cacheDir, "images")
        private var job : Job? = null

        init {
            // Création du folder image dans le cache
            imagesCacheDir.mkdirs()
        }

        fun bind(position: Int){
            image.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            val url = URL("https://daa.iict.ch/images/$position.jpg")
            job = lifecycle.launch {

                val file = File(imagesCacheDir, "$position.jpg")
                var shouldCache = false

                Log.println(Log.INFO, "LOADS", position.toString())
                // Si l'image n'est pas dans le cache ou a été téléchargée il y a plus de 5 minutes
                // on la télécharge
                if (!isActive) {
                    return@launch
                }
                val bytes = if (!file.exists()
                    || System.currentTimeMillis() - file.lastModified() > 5 * 60 * 1000) {
                    shouldCache = true
                    downloadImage(url)
                }
                // Sinon on la lit depuis le cache
                else {
                    file.readBytes()
                }
                val bmp = decodeImage(bytes)

                if (shouldCache) {
                    //println("Caching image $position")
                    cacheImage(bmp, position)
                }

                displayImage(bmp)
            }
        }
        suspend fun downloadImage(url : URL) : ByteArray? = withContext(Dispatchers.IO) {
            Thread.sleep(10_000)
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
            image.setImageBitmap(bmp)
            else{
                image.setImageResource(android.R.color.transparent)
            }
            progressBar.visibility = View.GONE
            image.visibility = View.VISIBLE
        }

        suspend fun cacheImage(bmp : Bitmap?, position: Int) = withContext(Dispatchers.IO) {
            try {
                val file = File(imagesCacheDir, "$position.jpg")
                file.outputStream().use {
                    // TODO: compresser l'image dans un thread Dispatcher.Default ? ou alors écrire directement le byteArray dans le fichier ?
                    bmp?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    it.flush()
                }
            } catch (e: IOException) {
                println("Exception while caching image" + e.message)
            }
        }

        fun stopLoading() {
            job?.cancel()
        }
    }



}
package ie.setu.travelnotes.views.mainview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.travelnotes.databinding.MainPageBinding

class ListView : AppCompatActivity() {

    private lateinit var binding: MainPageBinding
    private lateinit var presenter: ListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        presenter = ListPresenter(this)
    }
}
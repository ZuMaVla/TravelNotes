package ie.setu.travelnotes.views.placeaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.setu.travelnotes.databinding.ActionPlaceBinding

class ActionView : AppCompatActivity() {

    private lateinit var binding: ActionPlaceBinding
    private lateinit var presenter: ActionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActionPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
        presenter = ActionPresenter(this)

        binding.btnAddOrSave.setOnClickListener {
            presenter.doAddOrSave()
        }
    }
}
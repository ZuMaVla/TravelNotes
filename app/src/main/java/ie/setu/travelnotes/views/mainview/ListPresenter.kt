package ie.setu.travelnotes.views.mainview

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.travelnotes.views.placeaction.ActionView
import timber.log.Timber.i

class ListPresenter(val view: ListView) {
    // Presentation logic for the list view will be implemented here later.
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    init {
        registerRefreshCallback()
    }

    fun doAddPlace() {
        i("add button pressed")
        val launcherIntent = Intent(view, ActionView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    view.onRefresh()
                }
            }
    }
}
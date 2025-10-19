package ie.setu.travelnotes.views.placeaction

import android.app.Activity
import timber.log.Timber.i

class ActionPresenter(private val view: ActionView) {

    fun doAddOrSave() {
        i("add or save pressed")
        // In the future, we will save the data here.
        view.setResult(Activity.RESULT_OK)
        view.finish()
    }
}
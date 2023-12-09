package app.priceguard.ui.util.ui

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

fun disableAppBarRecyclerView(
    layoutParams: CoordinatorLayout.LayoutParams,
    recyclerView: RecyclerView
) {
    val viewTreeObserver = recyclerView.viewTreeObserver
    val disabledAblBehavior = getAblBehavior(false)
    val enabledAblBehavior = getAblBehavior(true)

    viewTreeObserver.addOnGlobalLayoutListener {
        if (recyclerView.canScrollVertically(-1).not()) {
            layoutParams.behavior = disabledAblBehavior
        }

        if (recyclerView.canScrollVertically(1)) {
            layoutParams.behavior = enabledAblBehavior
        }
    }
}

fun getAblBehavior(canDrag: Boolean): AppBarLayout.Behavior {
    val ablBehavior = AppBarLayout.Behavior()
    ablBehavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
        override fun canDrag(appBarLayout: AppBarLayout): Boolean {
            return canDrag
        }
    })
    return ablBehavior
}

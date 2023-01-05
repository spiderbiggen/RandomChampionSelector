package com.spiderbiggen.randomchampionselector.presentation.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.NestedScrollingChild
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Tells a [FloatingActionButton] how to behave when a scrolling action is being performed in it's containing [CoordinatorLayout]
 *
 * @author Stefan Breetveld
 */
class ScrollAwareFABBehavior : CoordinatorLayout.Behavior<FloatingActionButton> {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton, directTargetChild: View,
        target: View, axes: Int, type: Int
    ): Boolean = directTargetChild is NestedScrollingChild

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout, fab: FloatingActionButton,
        target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, type: Int, consumed: IntArray
    ) {
        super.onNestedScroll(coordinatorLayout, fab, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        when {
            !target.canScrollVertically(1) && fab.isOrWillBeHidden -> fab.show()
            dyConsumed > 0 && fab.isOrWillBeShown -> fab.hide(FabVisibilityChangedListener())
            dyConsumed < 0 && fab.isOrWillBeHidden -> fab.show()
        }
    }

    private class FabVisibilityChangedListener :
        FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton) {
            fab.visibility = View.INVISIBLE
        }
    }

}
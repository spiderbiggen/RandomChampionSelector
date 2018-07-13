package com.spiderbiggen.randomchampionselector.views.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View

/**
 * Created on 7-3-2018.
 *
 * @author Stefan Breetveld
 */

class ScrollAwareFABBehavior : CoordinatorLayout.Behavior<FloatingActionButton> {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, axes: Int, type: Int): Boolean = true

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        when {
            dyConsumed > 0 && child.visibility == View.VISIBLE -> child.hide(FabVisibilityChangedListener())
            dyConsumed < 0 && child.visibility != View.VISIBLE -> child.show()
        }
    }

    private class FabVisibilityChangedListener : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton) {
            super.onShown(fab)
            fab.visibility = View.INVISIBLE
        }
    }

}
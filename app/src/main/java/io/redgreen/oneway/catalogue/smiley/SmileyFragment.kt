package io.redgreen.oneway.catalogue.smiley

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.smiley.usecases.SmileyUseCases
import kotlinx.android.synthetic.main.smiley_fragment.*

class SmileyFragment : OneWayFragment<SmileyState>() {
  private val intentions = PublishSubject.create<SmileyIntention>()

  private val initialState: SmileyState
    get() = SmileyState.initial(getString(R.string.emoji_thumbs_up))

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      layoutInflater.inflate(R.layout.smiley_fragment, container, false)

  override fun onViewCreated(
      view: View,
      savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    pickSmileyButton.setOnClickListener { SmileyPickerActivity.startForResult(activity as Activity) }
  }

  override fun source(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCopy: Observable<SmileyState>
  ): Observable<SmileyState> =
    SmileyModel.createSource(
        intentions,
        sourceLifecycleEvents,
        SmileyUseCases(initialState, sourceCopy)
    )

  override fun sink(source: Observable<SmileyState>): Disposable =
      source
          .map { it.smiley }
          .subscribe(RxTextView.text(smileyTextView))
}

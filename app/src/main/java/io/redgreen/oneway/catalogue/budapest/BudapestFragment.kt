package io.redgreen.oneway.catalogue.budapest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceLifecycleEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.base.extensions.fastLazy
import io.redgreen.oneway.catalogue.budapest.drivers.BudapestViewDriver
import io.redgreen.oneway.catalogue.budapest.usecases.BudapestUseCases
import kotlinx.android.synthetic.main.budapest_fragment.*

class BudapestFragment : OneWayFragment<BudapestState>(), BudapestView {
  private val intentions by fastLazy {
    BudapestIntentions(
        nameEditText.textChanges().skipInitialValue()
    )
  }

  private val useCases by fastLazy {
    BudapestUseCases(sourceCopy)
  }

  private val viewDriver by fastLazy {
    BudapestViewDriver(this)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      inflater.inflate(R.layout.budapest_fragment, container, false)

  override fun source(
      sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
      sourceCopy: Observable<BudapestState>
  ): Observable<BudapestState> =
      BudapestModel.createSource(intentions.stream(), sourceLifecycleEvents, useCases)

  override fun sink(source: Observable<BudapestState>): Disposable =
      viewDriver.render(source)

  override fun greetStranger() {
    greetingTextView.text = getString(R.string.hello_stranger)
  }

  override fun greet(name: String) {
    greetingTextView.text = getString(R.string.template_greeting, name)
  }
}

package io.redgreen.oneway.catalogue.counter.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.counter.CounterState

class AddDeltaUseCase(
    private val sourceCopy: Observable<CounterState>,
    private val delta: Int
) : ObservableTransformer<Unit, CounterState> {
  override fun apply(deltaEvents: Observable<Unit>): ObservableSource<CounterState> =
      deltaEvents
          .withLatestFrom(sourceCopy) { _, state -> state.add(delta) }
}

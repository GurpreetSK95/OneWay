package io.redgreen.oneway.catalogue.budapest.usecases

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.withLatestFrom
import io.redgreen.oneway.catalogue.budapest.BudapestState
import io.redgreen.oneway.catalogue.budapest.EnterNameIntention
import io.redgreen.oneway.catalogue.budapest.GreeterState
import io.redgreen.oneway.catalogue.budapest.StrangerState

class EnterNameUseCase(
    private val sourceCopy: Observable<BudapestState>
) : ObservableTransformer<EnterNameIntention, BudapestState> {
  override fun apply(
      enterNameIntentions: Observable<EnterNameIntention>
  ): ObservableSource<BudapestState> {
    return enterNameIntentions
        .withLatestFrom(sourceCopy) { intention, state ->
          when (state) {
            is StrangerState -> state.enterName(intention)
            is GreeterState -> state.enterName(intention)
          }
        }
  }
}

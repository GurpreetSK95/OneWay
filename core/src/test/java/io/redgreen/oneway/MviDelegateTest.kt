package io.redgreen.oneway

import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import io.redgreen.oneway.SourceLifecycleEvent.CREATED
import io.redgreen.oneway.SourceLifecycleEvent.RESTORED
import org.junit.Test

class MviDelegateTest {
  private val publisher = PublishSubject.create<String>()

  private val source = object : Source<String> {
    override fun produce(
        sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
        sourceCopy: Observable<String>
    ): Observable<String> =
        publisher
  }

  private val testObserver = TestObserver<String>()
  private val sourceLifecycleEventsTestObserver = TestObserver<SourceLifecycleEvent>()

  private val sink = object : Sink<String> {
    override fun consume(source: Observable<String>): Disposable =
        source.subscribeWith(testObserver)
  }

  private val stateConverter: StateConverter<String, ByteArray> = object : StateConverter<String, ByteArray> {
    override fun to(state: String): ByteArray =
        state.toByteArray()

    override fun from(persistableState: ByteArray): String =
        String(persistableState)
  }

  private val mviDelegate = MviDelegate(stateConverter)

  @Test fun `it creates a subscription on connect()`() {
    // given
    val theValue = "One Way!"
    val source = object : Source<String> {
      override fun produce(
          sourceLifecycleEvents: Observable<SourceLifecycleEvent>,
          sourceCopy: Observable<String>
      ): Observable<String> =
          Observable.merge(Observable.just(theValue), Observable.never())
    }

    // when
    mviDelegate.connect(source, sink)

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(theValue)
    }
  }

  @Test fun `it signals a CREATED source event when the subscription happens for the first time`() {
    // when
    mviDelegate.sourceLifecycleEvents.subscribe(sourceLifecycleEventsTestObserver)
    mviDelegate.connect(source, sink)

    // then
    with(sourceLifecycleEventsTestObserver) {
      assertNoErrors()
      assertValue(CREATED)
      assertNotTerminated()
    }

    assertThat(sourceLifecycleEventsTestObserver.hasSubscription())
        .isTrue()
  }

  @Test fun `it has a sourceCopy that provides access to the latest state`() {
    // given
    val sourceCopyTestObserver = TestObserver<String>()
    val events = arrayOf("ஒன்று", "இரண்டு", "மூன்று")

    // when
    mviDelegate.sourceCopy.subscribe(sourceCopyTestObserver)
    mviDelegate.connect(source, sink)
    events.forEach { publisher.onNext(it) }

    // then
    with(sourceCopyTestObserver) {
      assertNoErrors()
      assertValues(*events)
      assertNotTerminated()
    }

    with(testObserver) {
      assertNoErrors()
      assertValues(*events)
      assertNotTerminated()
    }
  }

  @Test fun `it returns null if a last known state is unavailable`() {
    // given
    mviDelegate.connect(source, sink)

    // when
    val currentState = mviDelegate.getState()

    // then
    assertThat(currentState)
        .isNull()
  }

  @Test fun `it returns the last known state in desired format when available`() {
    // given
    val arrow = "Arrow"
    val persistableArrow = arrow.toByteArray()
    mviDelegate.connect(source, sink)

    // when
    publisher.onNext(arrow)
    val currentState = mviDelegate.getState()

    // then
    assertThat(currentState)
        .isNotNull()
    assertThat(currentState)
        .isEqualTo(persistableArrow)
  }

  @Test fun `it can restore state when a last known state is available`() {
    // given
    val arrow = "Arrow"
    val persistentState = arrow.toByteArray()
    val sourceCopyTestObserver = TestObserver<String>()
    mviDelegate.sourceCopy.subscribe(sourceCopyTestObserver)

    // when
    mviDelegate.putState(persistentState)
    val currentState = mviDelegate.getState()

    // then
    with(sourceCopyTestObserver) {
      assertNoErrors()
      assertValue(arrow)
      assertNotTerminated()
    }

    assertThat(currentState)
        .isEqualTo(persistentState)
  }

  @Test fun `it signals a RESTORED source event when state is restored`() {
    // given
    val persistentState = "Arrow".toByteArray()
    mviDelegate.sourceLifecycleEvents.subscribe(sourceLifecycleEventsTestObserver)

    // when
    mviDelegate.putState(persistentState)
    mviDelegate.connect(source, sink)

    // then
    with(sourceLifecycleEventsTestObserver) {
      assertNoErrors()
      assertValue(RESTORED)
      assertNotTerminated()
    }
  }

  @Test fun `it signals a CREATED source event if the restored state is null`() {
    // given
    mviDelegate.sourceLifecycleEvents.subscribe(sourceLifecycleEventsTestObserver)

    // when
    mviDelegate.connect(source, sink)
    mviDelegate.disconnect()
    mviDelegate.putState(null)
    mviDelegate.connect(source, sink)

    // then
    with(sourceLifecycleEventsTestObserver) {
      assertNoErrors()
      assertValues(CREATED, CREATED)
      assertNotTerminated()
    }
  }

  @Test fun `it disposes the subscription on disconnect()`() {
    // given
    mviDelegate.connect(source, sink)

    // when
    val firstMessage = "Hello"
    publisher.onNext(firstMessage)

    mviDelegate.disconnect()
    publisher.onNext("Unreachable")

    // then
    with(testObserver) {
      assertNoErrors()
      assertValue(firstMessage)
    }

    assertThat(testObserver.isDisposed)
        .isTrue()
  }

  @Test fun `it does not blow up if disconnect() is called multiple times`() {
    // given
    mviDelegate.connect(source, sink)

    // when
    with(mviDelegate) {
      disconnect()
      disconnect()
      disconnect()
      disconnect()
    }
  }

  @Test fun `it does not blow up if disconnect() is called before connect()`() {
    // given
    mviDelegate.sourceLifecycleEvents.subscribe(sourceLifecycleEventsTestObserver)

    // when
    mviDelegate.disconnect()

    // then
    with(sourceLifecycleEventsTestObserver) {
      assertNoErrors()
      assertNoValues()
      assertNotTerminated()
    }
  }
}

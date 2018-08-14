package io.mobsgeeks.oneway.catalogue.base

import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler

class TestSchedulersProvider : SchedulersProvider {
  val testScheduler = TestScheduler()

  override fun computation(): Scheduler =
      testScheduler
}
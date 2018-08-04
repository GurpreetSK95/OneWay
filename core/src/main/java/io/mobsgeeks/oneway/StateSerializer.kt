package io.mobsgeeks.oneway

/** A mechanism that enables saving and restoring state. */
interface StateSerializer<S, P> {
  /** @param state The state that has to be persisted. */
  fun serialize(state: S): P

  /** @param persistableState The persistable state. */
  fun deserialize(persistableState: P): S
}

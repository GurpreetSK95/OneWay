package io.redgreen.oneway.catalogue.signup.form

data class Field<T>(
    val untouched: Boolean = true,
    val unmetConditions: Set<T> = emptySet(),
    val displayingError: Boolean = false
) where T : Enum<T>, T : Condition {
  fun validationResult(unmetConditions: Set<T>): Field<T> =
      copy(untouched = false, unmetConditions = unmetConditions)
}

/*
 * Copyright (C) 2018 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redgreen.oneway.android

import android.os.Bundle
import android.os.Parcelable
import io.redgreen.oneway.android.barebones.Persister

/** [Persister] that can read and write a [Parcelable] from and to a [Bundle]. */
class ParcelablePersister<P: Parcelable>(
    private val stateKey: String
) : Persister<P> {
  /** Write a [Parcelable] state to the [Bundle]. */
  override fun write(persistableState: P, bundle: Bundle) =
      bundle.putParcelable(stateKey, persistableState)

  /** Read a [Parcelable] state from the [Bundle]. */
  override fun read(bundle: Bundle): P? =
      bundle.getParcelable(stateKey)
}

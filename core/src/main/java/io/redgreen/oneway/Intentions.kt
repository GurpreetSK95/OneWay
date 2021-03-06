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
package io.redgreen.oneway

import io.reactivex.Observable

/**
 * Groups intentions and exposes them as a stream. The ideal way to
 * make use of this interface is to have a sealed intention class for
 * the feature and subclasses (preferably data classes) for each intention.
 */
interface Intentions<I> {
  /** Merges and exposes all intentions as an [Observable]. */
  fun stream(): Observable<I>
}

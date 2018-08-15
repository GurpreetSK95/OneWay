package io.redgreen.oneway.catalogue.bmi

import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.changes
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.redgreen.oneway.SourceEvent
import io.redgreen.oneway.android.OneWayFragment
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory
import io.redgreen.oneway.catalogue.bmi.calculator.BmiCategory.*
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.IMPERIAL
import io.redgreen.oneway.catalogue.bmi.calculator.MeasurementSystem.SI
import io.redgreen.oneway.catalogue.bmi.drivers.BmiViewDriver
import io.redgreen.oneway.catalogue.bmi.usecases.BmiUseCases
import kotlinx.android.synthetic.main.bmi_fragment.*

class BmiFragment : OneWayFragment<BmiState>(), BmiView {
  private val bmiOffset
    get() = BmiOffset(
        resources.getInteger(R.integer.min_weight_kg).toDouble(),
        resources.getInteger(R.integer.min_height_cm).toDouble()
    )

  private val intentionsGroup
    get() = BmiIntentionsGroup(
        weightSeekBar.changes().skipInitialValue(),
        heightSeekBar.changes().skipInitialValue(),
        measurementSystemSwitch.checkedChanges().skipInitialValue(),
        bmiOffset
    )

  private val initialState
    get() = BmiState(
        resources.getInteger(R.integer.default_weight_kg).toDouble(),
        resources.getInteger(R.integer.default_height_cm).toDouble(),
        SI
    )

  private val useCases
    get() = BmiUseCases(
        initialState,
        timeline
    )

  private val viewDriver
    get() = BmiViewDriver(this)

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      inflater.inflate(R.layout.bmi_fragment, container, false)

  override fun source(
      sourceEvents: Observable<SourceEvent>,
      timeline: Observable<BmiState>
  ): Observable<BmiState> =
    BmiModel.createSource(intentionsGroup.intentions(), sourceEvents, useCases)

  override fun sink(source: Observable<BmiState>): Disposable =
      viewDriver.render(source)

  override fun showBmi(bmi: Double) {
    bmiTextView.text = getString(R.string.template_bmi, bmi)
  }

  override fun showCategory(category: BmiCategory) {
    bmiCategoryTextView.text = getHumanizedCategoryText(category)
  }

  override fun showWeight(weight: Double, measurementSystem: MeasurementSystem) {
    @StringRes val weightTemplateStringRes = when (measurementSystem) {
      SI       -> R.string.template_weight_si
      IMPERIAL -> R.string.template_weight_imperial
    }
    weightTextView.text = getString(weightTemplateStringRes, weight)
  }

  override fun showHeight(height: Double, measurementSystem: MeasurementSystem) {
    @StringRes val heightTemplateStringRes = when (measurementSystem) {
      SI       -> R.string.template_height_si
      IMPERIAL -> R.string.template_height_imperial
    }
    heightTextView.text = getString(heightTemplateStringRes, height)
  }

  override fun showMeasurementSystem(measurementSystem: MeasurementSystem) {
    @StringRes val measurementSystemRes = when (measurementSystem) {
      SI       -> R.string.si_units
      IMPERIAL -> R.string.imperial_units
    }
    measurementSystemSwitch.text = getString(measurementSystemRes)
  }

  private fun getHumanizedCategoryText(category: BmiCategory): String {
    val categoryTextRes = when(category) {
      VERY_SEVERELY_UNDERWEIGHT -> R.string.very_severely_underweight
      SEVERELY_UNDERWEIGHT -> R.string.severely_underweight
      UNDERWEIGHT -> R.string.underweight
      NORMAL -> R.string.normal
      OVERWEIGHT -> R.string.overweight
      OBESE_CLASS_1 -> R.string.obese_class_1
      OBESE_CLASS_2 -> R.string.obese_class_2
      OBESE_CLASS_3 -> R.string.obese_class_3
      OBESE_CLASS_4 -> R.string.obese_class_4
      OBESE_CLASS_5 -> R.string.obese_class_5
      OBESE_CLASS_6 -> R.string.obese_class_6
    }
    return getString(categoryTextRes)
  }
}
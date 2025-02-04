// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.study.pioglitazone

import java.time.LocalDate
import me.danielpes.spark.datetime.implicits._
import pureconfig.generic.auto._
import fr.polytechnique.cmap.cnam.etl.config.study.StudyConfig
import fr.polytechnique.cmap.cnam.etl.config.{BaseConfig, ConfigLoader}
import fr.polytechnique.cmap.cnam.etl.extractors.events.acts.MedicalActsConfig
import fr.polytechnique.cmap.cnam.etl.extractors.events.diagnoses.DiagnosesConfig
import fr.polytechnique.cmap.cnam.etl.extractors.events.molecules.MoleculePurchasesConfig
import fr.polytechnique.cmap.cnam.etl.extractors.patients.PatientsConfig
import fr.polytechnique.cmap.cnam.etl.transformers.exposures._
import fr.polytechnique.cmap.cnam.etl.transformers.follow_up.FollowUpTransformerConfig
import fr.polytechnique.cmap.cnam.etl.transformers.observation.ObservationPeriodTransformerConfig
import fr.polytechnique.cmap.cnam.etl.transformers.outcomes.OutcomesTransformerConfig
import fr.polytechnique.cmap.cnam.study.pioglitazone.outcomes.CancerDefinition

case class PioglitazoneConfig(
  input: StudyConfig.InputPaths,
  output: StudyConfig.OutputPaths,
  exposures: PioglitazoneConfig.ExposuresConfig = PioglitazoneConfig.ExposuresConfig(),
  outcomes: PioglitazoneConfig.OutcomesConfig = PioglitazoneConfig.OutcomesConfig(),
  filters: PioglitazoneConfig.FiltersConfig = PioglitazoneConfig.FiltersConfig(),
  readFileFormat: String = "parquet",
  writeFileFormat: String = "parquet")
  extends StudyConfig {

  // The following config items are not overridable by the config file
  val base: BaseConfig = PioglitazoneConfig.BaseConfig
  val patients: PatientsConfig = PioglitazoneConfig.PatientsConfig
  val molecules: MoleculePurchasesConfig = PioglitazoneConfig.MoleculePurchasesConfig
  val medicalActs: MedicalActsConfig = PioglitazoneConfig.MedicalActsConfig
  val diagnoses: DiagnosesConfig = PioglitazoneConfig.DiagnosesConfig
  val observationPeriod: ObservationPeriodTransformerConfig = PioglitazoneConfig.ObservationPeriodTransformerConfig
  val followUp: FollowUpTransformerConfig = PioglitazoneConfig.FollowUpTransformerConfig
}

object PioglitazoneConfig extends ConfigLoader with PioglitazoneStudyCodes {

  /**
   * Reads a configuration file and merges it with the default file.
   *
   * @param path The path of the given file.
   * @param env  The environment in the config file (usually can be "cmap", "cnam" or "test").
   * @return An instance of PioglitazoneConfig containing all parameters.
   */
  def load(path: String, env: String): PioglitazoneConfig = {
    val defaultPath = "config/pioglitazone/default.conf"
    loadConfigWithDefaults[PioglitazoneConfig](path, defaultPath, env)
  }

  /** Parameters needed for the Exposures transformer. */
  case class ExposuresConfig(
    override val exposurePeriodAdder: ExposurePeriodAdder = UnlimitedExposureAdder(
      3.months,
      2,
      6.months
    )
  ) extends ExposuresTransformerConfig(exposurePeriodAdder = exposurePeriodAdder)

  /** Parameters needed for the Outcomes transformer. */
  case class OutcomesConfig(
    cancerDefinition: CancerDefinition = CancerDefinition.default)
    extends OutcomesTransformerConfig

  /** Parameters needed for the Filters. */
  case class FiltersConfig(
    filterDiagnosedPatients: Boolean = true,
    filterDelayedEntries: Boolean = true,
    delayedEntryThreshold: Int = 12)

  /** Base fixed parameters for this study. */
  final object BaseConfig extends BaseConfig(
    ageReferenceDate = LocalDate.of(2007, 1, 1),
    studyStart = LocalDate.of(2006, 1, 1),
    studyEnd = LocalDate.of(2009, 12, 31)
    //    ageReferenceDate = LocalDate.of(2012, 1, 1),
    //    studyStart = LocalDate.of(2011, 1, 1),
    //    studyEnd = LocalDate.of(2013  , 1, 1)
  )

  /** Fixed parameters needed for the Patients extractors. */
  final object PatientsConfig extends PatientsConfig(
    ageReferenceDate = PioglitazoneConfig.BaseConfig.ageReferenceDate,
    maxAge = 80,
    minAge = 40
  )

  /** Fixed parameters needed for the Drugs extractors. */
  final object MoleculePurchasesConfig extends MoleculePurchasesConfig(
    drugClasses = List("A10"),
    maxBoxQuantity = 10
  )

  /** Fixed parameters needed for the Diagnoses extractors. */
  final object DiagnosesConfig extends DiagnosesConfig(
    dpCodes = primaryDiagCode :: secondaryDiagCodes,
    drCodes = primaryDiagCode :: secondaryDiagCodes,
    daCodes = List(primaryDiagCode),
    imbCodes = List(primaryDiagCode)
  )

  /** Fixed parameters needed for the Medical Acts extractors. */
  final object MedicalActsConfig extends MedicalActsConfig(
    dcirCodes = dcirCCAMActCodes,
    mcoCIMCodes = mcoCIM10ActCodes,
    mcoCCAMCodes = mcoCCAMActCodes,
    mcoCECodes = List(),
    ssrCCAMCodes = List(),
    ssrCSARRCodes = List(),
    ssrCECodes = List(),
    hadCCAMCodes = List()
  )

  /** Fixed parameters needed for the ObservationPeriod transformer. */
  final object ObservationPeriodTransformerConfig extends ObservationPeriodTransformerConfig(
    studyStart = BaseConfig.studyStart,
    studyEnd = BaseConfig.studyEnd
  )

  /** Fixed parameters needed for the FollowUp transformer. */
  final object FollowUpTransformerConfig extends FollowUpTransformerConfig(
    delayMonths = 6,
    firstTargetDisease = true,
    outcomeName = Some("cancer")
  )

}

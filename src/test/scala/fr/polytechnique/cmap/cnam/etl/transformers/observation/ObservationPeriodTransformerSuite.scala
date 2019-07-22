package fr.polytechnique.cmap.cnam.etl.transformers.observation

import java.time.LocalDate
import fr.polytechnique.cmap.cnam.SharedContext
import fr.polytechnique.cmap.cnam.etl.events._
import fr.polytechnique.cmap.cnam.util.functions._

class ObservationPeriodTransformerSuite extends SharedContext {

  "withObservationStart" should "add a column with the start of the observation period" in {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val input = Seq(
      ("Patient_A", "molecule", "PIOGLITAZONE", makeTS(2008, 1, 20)),
      ("Patient_A", "molecule", "PIOGLITAZONE", makeTS(2008, 1, 1)),
      ("Patient_A", "molecule", "PIOGLITAZONE", makeTS(2008, 1, 10)),
      ("Patient_A", "disease", "Hello World!", makeTS(2007, 1, 1)),
      ("Patient_B", "molecule", "PIOGLITAZONE", makeTS(2009, 1, 1)),
      ("Patient_B", "disease", "Hello World!", makeTS(2007, 1, 1))
    ).toDF("patientID", "category", "eventId", "start")

    val expected = Seq(
      ("Patient_A", "molecule", "PIOGLITAZONE", makeTS(2008, 1, 20), makeTS(2008, 1, 1)),
      ("Patient_A", "molecule", "PIOGLITAZONE", makeTS(2008, 1, 1), makeTS(2008, 1, 1)),
      ("Patient_A", "molecule", "PIOGLITAZONE", makeTS(2008, 1, 10), makeTS(2008, 1, 1)),
      ("Patient_A", "disease", "Hello World!", makeTS(2007, 1, 1), makeTS(2008, 1, 1)),
      ("Patient_B", "molecule", "PIOGLITAZONE", makeTS(2009, 1, 1), makeTS(2009, 1, 1)),
      ("Patient_B", "disease", "Hello World!", makeTS(2007, 1, 1), makeTS(2009, 1, 1))
    ).toDF("patientID", "category", "eventId", "start", "observationStart")

    val testConfig = new ObservationPeriodTransformerConfig(
      studyStart = LocalDate.of(2006, 1, 1),
      studyEnd = LocalDate.of(2010, 1, 1)
    )
    val transformer = new ObservationPeriodTransformer(testConfig)

    // When
    import transformer._
    val result = input.withObservationStart

    // Then
    assertDFs(result, expected)
  }

  "transform" should "return a Dataset[FlatEvent] with the observation events of each patient" in {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    // Given
    val events = Seq[Event[AnyEvent]](
      Molecule("Patient_A", "PIOGLITAZONE", 1.0, makeTS(2008, 1, 20)),
      Molecule("Patient_A", "PIOGLITAZONE", 1.0, makeTS(2008, 1, 1)),
      Molecule("Patient_A", "PIOGLITAZONE", 1.0, makeTS(2008, 1, 10)),
      Outcome("Patient_A", "C67", makeTS(2007, 1, 1)),
      Molecule("Patient_B", "PIOGLITAZONE", 1.0, makeTS(2009, 1, 1)),
      Outcome("Patient_B", "C67", makeTS(2007, 1, 1))
    ).toDS

    val expected = Seq(
      ObservationPeriod("Patient_A", makeTS(2008, 1, 1), makeTS(2010, 1, 1)),
      ObservationPeriod("Patient_B", makeTS(2009, 1, 1), makeTS(2010, 1, 1))
    ).toDS

    val testConfig = new ObservationPeriodTransformerConfig(
      events = Some(events),
      studyStart = LocalDate.of(2006, 1, 1),
      studyEnd = LocalDate.of(2010, 1, 1)
    )

    val result = new ObservationPeriodTransformer(testConfig).transform()

    // Then
    assertDSs(result, expected)
  }
}

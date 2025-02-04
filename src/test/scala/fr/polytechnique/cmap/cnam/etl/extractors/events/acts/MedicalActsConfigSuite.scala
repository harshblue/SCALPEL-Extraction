package fr.polytechnique.cmap.cnam.etl.extractors.events.acts

import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import fr.polytechnique.cmap.cnam.SharedContext

class MedicalActsConfigSuite extends SharedContext {
  "MedicalActsConfig" should "be of type MedicalActsConfig" in {
    MedicalActsConfig.apply() shouldBe a[MedicalActsConfig]
  }
}

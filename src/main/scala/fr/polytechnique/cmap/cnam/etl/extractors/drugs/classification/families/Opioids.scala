// License: BSD 3 clause

package fr.polytechnique.cmap.cnam.etl.extractors.drugs.classification.families

import fr.polytechnique.cmap.cnam.etl.extractors.drugs.classification.{DrugClassConfig, PharmacologicalClassConfig}

object Opioids extends DrugClassConfig {
  override val name: String = "Opioids"
  override val cip13Codes: Set[String] = Set(
    "3400938747393",
    "3400939023649",
    "3400935770073",
    "3400936114548",
    "3400936114487",
    "3400934828096",
    "3400937479462",
    "3400937489928",
    "3400937484145",
    "3400934992308",
    "3400934990007",
    "3400934951374",
    "3400930378540",
    "3400933715014",
    "3400935641281",
    "3400930411469",
    "3400927319143",
    "3400936853690",
    "3400934314797",
    "3400934314568",
    "3400934314278",
    "3400934808166",
    "3400934802362",
    "3400936102132",
    "3400926845926",
    "3400938229783",
    "3400936853812",
    "3400935793195",
    "3400926980801",
    "3400926797348",
    "3400936319714",
    "3400927671029",
    "3400939104874",
    "3400935877154",
    "3400935106704",
    "3400934538193",
    "3400939221601",
    "3400939221540",
    "3400935620699",
    "3400936907553",
    "3400934991295",
    "3400935620170",
    "3400930411230",
    "3400931959328",
    "3400936206830",
    "3400936248182",
    "3400936247123",
    "3400936970366",
    "3400935067340",
    "3400935349897",
    "3400938127676",
    "3400938227024",
    "3400931959038",
    "3400931958956",
    "3400935486554",
    "3400931959496",
    "3400935156440",
    "3400935155788",
    "3400938231045",
    "3400936969247",
    "3400936968936",
    "3400936672598",
    "3400935438478",
    "3400927321214",
    "3400938228373",
    "3400936247642",
    "3400936206779",
    "3400936102651",
    "3400927607035",
    "3400927562396",
    "3400927597237",
    "3400935888730",
    "3400936289406",
    "3400936289284",
    "3400938399097",
    "3400939826240",
    "3400936969995",
    "3400938222920",
    "3400936812420",
    "3400936810709",
    "3400936809758",
    "3400936748804",
    "3400933220754",
    "3400933304652",
    "3400933319090",
    "3400934976339",
    "3400934976278",
    "3400936041295",
    "3400936041417",
    "3400938212983",
    "3400933316778",
    "3400935486783",
    "3400935404640",
    "3400934499760",
    "3400936289635",
    "3400935567031",
    "3400935438249",
    "3400922096032",
    "3400922096322",
    "3400936141391",
    "3400935748713",
    "3400935748652",
    "3400936787193",
    "3400934654329",
    "3400934654039",
    "3400938552454",
    "3400935350039",
    "3400937356473",
    "3400938675443",
    "3400938675214",
    "3400935349958",
    "3400937356305",
    "3400937700177",
    "3400935856913",
    "3400934654787",
    "3400934654558",
    "3400930303320",
    "3400934007286",
    "3400935194121",
    "3400934882845",
    "3400935877505",
    "3400935193810",
    "3400937051323",
    "3400937015943",
    "3400933180850",
    "3400930002285",
    "3400930002278",
    "3400926690908",
    "3400926963422",
    "3400938459852",
    "3400939846460",
    "3400934890659",
    "3400939392325",
    "3400934890888",
    "3400933911881",
    "3400934802133",
    "3400938042887",
    "3400938042597",
    "3400939903392",
    "3400939641706",
    "3400939711874",
    "3400938042139",
    "3400921856996",
    "3400921856828",
    "3400922198088",
    "3400921857658",
    "3400921879599",
    "3400949251087",
    "3400949250547",
    "3400949914821",
    "3400949217540",
    "3400949217250",
    "3400949217199",
    "3400930332016",
    "3400939202099",
    "3400939641355",
    "3400939640983",
    "3400939711935",
    "3400938510232",
    "3400927560446",
    "3400927658532",
    "3400927657702",
    "3400934410932",
    "3400934399435",
    "3400934399084",
    "3400934398544",
    "3400934291463",
    "3400934291234",
    "3400934387609",
    "3400934387258",
    "3400927656989",
    "3400927748752",
    "3400927760587",
    "3400927759239",
    "3400927757976",
    "3400939220710",
    "3400938509342",
    "3400938509861",
    "3400934890420",
    "3400932869947",
    "3400927659591",
    "3400938458732",
    "3400935438300",
    "3400936587281",
    "3400936985155",
    "3400935695161",
    "3400936911635",
    "3400936911055",
    "3400936910683",
    "3400939200668",
    "3400939104355",
    "3400938460223",
    "3400938533415",
    "3400938509113",
    "3400938124255",
    "3400939024479",
    "3400938398618",
    "3400926961121",
    "3400939104584",
    "3400939104416",
    "3400939105765",
    "3400939105185",
    "3400939104935",
    "3400939104706",
    "3400934053702",
    "3400938508741",
    "3400938508512",
    "3400938652840",
    "3400938652321",
    "3400927756337",
    "3400927753725",
    "3400927751653",
    "3400939221021",
    "3400927755095",
    "3400931164531",
    "3400934238536",
    "3400934238307",
    "3400934021305",
    "3400934641541",
    "3400922301938",
    "3400934238475",
    "3400936910515",
    "3400936102422",
    "3400936894563",
    "3400934866067",
    "3400935322982",
    "3400922096261",
    "3400935349729",
    "3400949363940",
    "3400939190051",
    "3400936907782",
    "3400936906891",
    "3400936906372",
    "3400936906143",
    "3400936905771",
    "3400935807366",
    "3400935806826",
    "3400927890031",
    "3400938651089",
    "3400938650488",
    "3400938225532",
    "3400936794238",
    "3400926942489",
    "3400935857392",
    "3400935154729",
    "3400939200439",
    "3400934387487",
    "3400933684792",
    "3400934748042",
    "3400939104645",
    "3400939221489",
    "3400939221311",
    "3400939221250",
    "3400939221199",
    "3400939220888",
    "3400939213507",
    "3400939213446",
    "3400932952724",
    "3400932870028",
    "3400932869718",
    "3400922303420",
    "3400922303079",
    "3400922302768",
    "3400922302300",
    "3400921856477",
    "3400935703477",
    "3400930777473",
    "3400935018663",
    "3400930777534",
    "3400933323813",
    "3400933323752",
    "3400935130594",
    "3400939186788",
    "3400936910454",
    "3400936910225",
    "3400949217489",
    "3400939187679",
    "3400935998651",
    "3400932869886",
    "3400949914531",
    "3400934890130",
    "3400927560965",
    "3400927561337",
    "3400927888601",
    "3400935806307",
    "3400935349378",
    "3400939185668",
    "3400939726205",
    "3400939725192",
    "3400935619921",
    "3400935844217",
    "3400935843845",
    "3400936203068",
    "3400936242388",
    "3400936242159",
    "3400936241909",
    "3400935422279",
    "3400927656750",
    "3400939314952",
    "3400939712017",
    "3400921857948",
    "3400949251209",
    "3400949378678",
    "3400934827846",
    "3400922095950",
    "3400936809178",
    "3400930068571",
    "3400921857139",
    "3400949812134",
    "3400949666621",
    "3400949666331",
    "3400949915590",
    "3400933323691",
    "3400933323523",
    "3400931164821",
    "3400939391144",
    "3400939390543",
    "3400939342757",
    "3400939844510",
    "3400930068519",
    "3400930068649",
    "3400921857887",
    "3400921857719",
    "3400921857597",
    "3400921856767",
    "3400921856538",
    "3400921857368",
    "3400921857078",
    "3400949914302",
    "3400938504897",
    "3400935429483",
    "3400935421500",
    "3400933480059",
    "3400933479978",
    "3400933799229",
    "3400927889721",
    "3400927889370",
    "3400927889080",
    "3400927561108",
    "3400939827070",
    "3400935595874",
    "3400935615558",
    "3400936651548",
    "3400934760228",
    "3400939755588",
    "3400939755878",
    "3400939476803",
    "3400939417899",
    "3400939417370",
    "3400932551897",
    "3400930075722",
    "3400933803681",
    "3400939478173",
    "3400939476223",
    "3400939479415",
    "3400935714244",
    "3400939118833",
    "3400926939359",
    "3400927656699",
    "3400935509369",
    "3400936587632",
    "3400930051047",
    "3400933803452",
    "3400939477404",
    "3400939825649",
    "3400930057834",
    "3400932966332",
    "3400935185884",
    "3400935107534",
    "3400935660091",
    "3400935108074",
    "3400935107183",
    "3400935671677",
    "3400930068960",
    "3400930068892",
    "3400927657641",
    "3400926845117",
    "3400927656811",
    "3400927658471",
    "3400927655630",
    "3400927655920",
    "3400927659423",
    "3400930068823",
    "3400935982452",
    "3400927656002",
    "3400930076033",
    "3400930075937",
    "3400930075623",
    "3400937847698",
    "3400934827617",
    "3400935703767",
    "3400930587508",
    "3400935065100",
    "3400935703248",
    "3400921879711",
    "3400936969537",
    "3400936595965",
    "3400949214587",
    "3400936819344",
    "3400935531650",
    "3400931308492",
    "3400939478814",
    "3400939213736",
    "3400921857429",
    "3400936289055",
    "3400935235893",
    "3400930075388",
    "3400934170195",
    "3400933305314",
    "3400932461219",
    "3400939391892",
    "3400939843629",
    "3400927660313",
    "3400927560675",
    "3400930587737",
    "3400935299130",
    "3400938324747",
    "3400933724467",
    "3400938149500",
    "3400936690349",
    "3400935157041",
    "3400935236036",
    "3400936968646",
    "3400937374514",
    "3400938509571",
    "3400927513541",
    "3400935414007",
    "3400926721466",
    "3400926838072",
    "3400935583239",
    "3400935404701",
    "3400935565839",
    "3400930014103",
    "3400930045350",
    "3400939699257",
    "3400938458442",
    "3400935420909",
    "3400927655869",
    "3400935713063",
    "3400933275815",
    "3400937015653",
    "3400949217311",
    "3400939200729",
    "3400934300660",
    "3400936212053",
    "3400938125955",
    "3400935570970",
    "3400936289925",
    "3400939845340",
    "3400936906204",
    "3400936105034",
    "3400939723471",
    "3400935666826",
    "3400935843494",
    "3400935768872",
    "3400938508970",
    "3400938651720",
    "3400936853751",
    "3400939104294",
    "3400934238765",
    "3400949363599",
    "3400939220949",
    "3400933765910",
    "3400927660252",
    "3400935248442",
    "3400926943141",
    "3400935694911",
    "3400939185897"
  )
  override val pharmacologicalClasses: List[PharmacologicalClassConfig] = List.empty
}

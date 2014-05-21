package com.apetheriotis.logproducer

import scala.util.Random
import scala.collection.mutable.ListBuffer


object LogGenerator {

  var COUNTRIES = List(" LY, Libya ",
    " MA, Morocco ", " MC, Monaco ", " MD, Moldova ", " ME, Montenegro ", " MF, Saint Martin ", " MG, Madagascar ",
    " MH, Marshall Islands ", " MK, F.Y.R.O.M.(Macedonia) ", " ML, Mali ", " MM, Myanmar ", " MN, Mongolia ",
    " MO, Macau ", " MP, Northern Mariana Islands ", " MQ, Martinique ", " MR, Mauritania ", " MS, Montserrat ",
    " MT, Malta ", " MU, Mauritius ", " MV, Maldives ", " MW, Malawi ", " MX, Mexico ", " MY, Malaysia ",
    " MZ, Mozambique ", " NA, Namibia ", " NC, New Caledonia ", " NE, Niger ", " NF, Norfolk Island ",
    " NG, Nigeria ", " NI, Nicaragua ", " NL, Netherlands ", " NO, Norway ", " NP, Nepal ", " NR, Nauru ",
    " NT, Neutral Zone ", " NU, Niue ", " NZ, New Zealand (Aotearoa) ", " OM, Oman ", " PA, Panama ", " PE, Peru ",
    " PF, French Polynesia ", " PG, Papua New Guinea ",
    " PH, Philippines ", " PK, Pakistan ", " PL, Poland ", " PM, St.Pierre and Miquelon ", " PN, Pitcairn ",
    " PR, Puerto Rico ", " PS, Palestinian Territory, Occupied ", " PT, Portugal ", " PW, Palau ", " PY, Paraguay ",
    " QA, Qatar ", " RE, Reunion ", " RS, Serbia ", " RO, Romania ", " RU, Russian Federation ", " RW, Rwanda ",
    " SA, Saudi Arabia ", " SB, Solomon Islands ", " SC, Seychelles ", " SD, Sudan ", " SE, Sweden ",
    " SG, Singapore ", " SH, St.Helena ", " SI, Slovenia ", " SJ, Svalbard & Jan Mayen Islands ",
    " SK, Slovak Republic ", " SL, Sierra Leone ", " SM, San Marino ", " SN, Senegal ", " SO, Somalia ",
    " SR, Suriname ", " SS, South Sudan ", " ST, Sao Tome and Principe ", " SU, USSR(former) ", " SV, El Salvador ",
    " SY, Syria ", " SZ, Swaziland ", " TC, Turks and Caicos Islands ", " TD, Chad ",
    " TF, French Southern Territories ", " TG, Togo ", " TH, Thailand ", " TJ, Tajikistan ", " TK, Tokelau ",
    " TM, Turkmenistan ", " TN, Tunisia ", " TO, Tonga ", " TP, East Timor ", " TR, Turkey ",
    " TT, Trinidad and Tobago ", " TV, Tuvalu ", " TW, Taiwan ", " TZ, Tanzania ", " UA, Ukraine ", " UG, Uganda ",
    " UK, United Kingdom ", " UM, US Minor Outlying Islands ",
    " US, United States ", " UY, Uruguay ", " UZ, Uzbekistan ", " VA, Vatican City State(Holy See) ",
    " VC, Saint Vincent & the Grenadines ", " VE, Venezuela ", " VG, British Virgin Islands ",
    " VI, Virgin Islands (U.S.) ", " VN, Viet Nam ", " VU, Vanuatu ", " WF, Wallis and Futuna Islands ",
    " WS, Samoa ", " XK, Kosovo * ", " YE, Yemen ", " YT, Mayotte ", " YU, Serbia and Montenegro(former Yugoslavia) ",
    " ZA, South Africa ", " ZM, Zambia ", " (ZR, Zaire), See CD Congo, Democratic Republic ", " ZW, Zimbabwe")

  var USER_AGENTS = List(
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)",
    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
    "Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0.1",
    "Mozilla/5.0 (Windows NT 5.1; rv:5.0.1) Gecko/20100101 Firefox/5.0.1",
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",
    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
    "Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/20100101 Firefox/17.0",
    "Mozilla/5.0 (iPad; CPU OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3",
    "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us; rv:1.9.2.3) Gecko/20100401 YFF35 Firefox/3.6.3",
    "Java/1.6.0_04",
    "Java/1.4.1_04",
    "Java/1.7.0_51",
    "Mozilla/4.0 (compatible; MSIE 6.0; MSIE 5.5; Windows NT 5.0) Opera 7.02 Bork-edition [en]",
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; FunWebProducts; .NET CLR 1.1.4322; PeoplePal 6.2)",
    "Mozilla/5.0 (Windows NT 6.1; rv:11.0) Gecko/20100101 Firefox/11.0",
    "Opera/9.80 (Windows NT 6.2; Win64; x64) Presto/2.12.388 Version/12.15",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0",
    "Mozilla/5.0 (Windows NT 5.1; rv:8.0) Gecko/20100101 Firefox/8.0",
    "Mozilla/5.0 (Windows NT 6.1; rv:14.0) Gecko/20100101 Firefox/14.0.1",
    "Mozilla/5.0 (Windows NT 5.1; rv:12.0) Gecko/20100101 Firefox/12.0",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)",
    "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)",
    "Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko/20100101 Firefox/11.0",
    "Mozilla/5.0 (Windows NT 6.1; rv:10.0) Gecko/20100101 Firefox/10.0",
    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",
    "Mozilla/5.0 (Windows NT 5.1; rv:16.0) Gecko/20100101 Firefox/16.0v",
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) )",
    "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0",
    "Mozilla/5.0 (Windows NT 6.1; rv:12.0) Gecko/20100101 Firefox/12.0",
    "Mozilla/5.0 (Windows NT 5.1; rv:13.0) Gecko/20100101 Firefox/13.0",
    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11",
    "Mozilla/5.0 (Windows NT 5.1; rv:17.0) Gecko/20100101 Firefox/17.0",
    "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0.1",
    "Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1",
    "Opera/9.80 (Windows NT 6.1; Win64; x64) Presto/2.12.388 Version/12.11",
    "Opera/9.80 (Windows NT 5.1; MRA 6.0 (build 5831)) Presto/2.12.388 Version/12.10",
    "Opera/9.80 (Windows NT 6.1; U; Edition Yx; ru) Presto/2.10.289 Version/12.02",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2",
    "Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.02",
    "Opera/9.80 (Windows NT 6.1; MRA 6.0 (build 5970)) Presto/2.12.388 Version/12.11",
    "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.152 Safari/535.19 CoolNovo/2.0.3.55",
    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; .NET CLR 3.5.30729)",
    "Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/17.0 Firefox/17.0",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.1634 Safari/535.19 YI",
    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11",
    "Opera/9.80 (Windows NT 5.1; U; ru) Presto/2.10.229 Version/11.64",
    "Mozilla/5.0 (Windows NT 6.1; rv:10.0.2) Gecko/20100101 Firefox/10.0.2",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11",
    "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0",
    "Mozilla/5.0 (Windows NT 6.1; rv:2.0b7pre) Gecko/20100921 Firefox/4.0b7pre",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.1634 Safari/535.19 YE",
    "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.121 Safari/535.2",
    "Java/1.7.0_45",
    "Opera/9.80 (Windows NT 6.1; U; ru) Presto/2.7.39 Version/11.00",
    "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.10",
    "Opera/9.80 (Windows NT 5.1; U; ru) Presto/2.10.289 Version/12.02",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/537.4",
    "Opera/9.80 (Windows NT 6.1; WOW64; U; ru) Presto/2.10.289 Version/12.00",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1",
    "Opera/9.80 (Windows NT 6.1; WOW64; MRA 8.0 (build 5784)) Presto/2.12.388 Version/12.10",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.1634 Safari/535.19 YE",
    "Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
    "Opera/9.80 (Windows NT 6.1; WOW64; U; ru) Presto/2.10.289 Version/12.01",
    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/537.4 u01-09",
    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
    "Mozilla/5.0 (Linux; U; Android 2.2; fr-fr; Desire_A8181 Build/FRF91) App3leWebKit/53.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
    "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.11",
    "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0",
    "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0",
    "Opera/9.80 (Windows NT 5.1; Edition Yx) Presto/2.12.388 Version/12.10",
    "Opera/9.80 (Windows NT 5.1; U; ru) Presto/2.9.168 Version/11.50",
    "Mozilla/5.0 (Windows NT 5.1; rv:16.0) Gecko/20121026 Firefox/16.0 SeaMonkey/2.13.2",
    "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:11.0) Gecko/20120313 Firefox/11.0",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11",
    "Mozilla/5.0 (Windows NT 6.1; rv:15.0) Gecko/20100101 Firefox/15.0.1",
    "Opera/9.80 (Windows NT 6.2; WOW64; MRA 8.0 (build 5784)) Presto/2.12.388 Version/12.10",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/537.4",
    "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.91 Safari/537.11",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4",
    "Opera/9.80 (Windows NT 5.1; Edition Ukraine Local) Presto/2.12.388 Version/12.10",
    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.204 Safari/534.16",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0",
    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.5 (KHTML, like Gecko) YaBrowser/1.1.1084.5409 Chrome/19.1.1084.5409 Safari/536.5",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0",
    "Opera/9.80 (Windows NT 5.1; U; MRA 6.0 (build 5970); ru) Presto/2.7.62 Version/11.00",
    "Opera/9.80 (Windows NT 6.2; U; en) Presto/2.10.289 Version/12.02",
    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0)",
    "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11"
  )

  val RESOURCES = Map(
    "/user" -> 15,
    "/user/login" -> 5,
    "/user/logout" -> 5,
    "/user/authorizedapps/connect/facebook" -> 2,
    "/user/authorizedapps/connect/google" -> 2,
    "/user/authorizedapps/disconnect/google" -> 1,
    "/user/authorizedapps/disconnect/facebook" -> 1,
    "/user/profile" -> 5,
    "/user/points" -> 5,
    "/user/authorizedapps" -> 5,
    "/infopoint" -> 15,
    "/infopoint/comments" -> 5,
    "/infopoint/images" -> 5,
    "/infopoint/image/score" -> 5,
    "/infopoint/comment/score" -> 2,
    "/infopoint/images/comments" -> 2,
    "/infopoint/types" -> 10,
    "/infopoint/geocode" -> 10
  )

  val STATUS_CODES = Map(
    200 -> 9500,
    304 -> 50,
    404 -> 50,
    500 -> 150,
    503 -> 150,
    401 -> 50,
    403 -> 50
  )

  // Convert to a weighted list of resources
  var resourcesWeighted = new ListBuffer[String]()
  RESOURCES.foreach({
    r => resourcesWeighted.insertAll(resourcesWeighted.size, List.fill(r._2)(r._1))
  })

  // Convert to a weighted list of status codes
  var statusCodesWeighted = new ListBuffer[Int]()
  STATUS_CODES.foreach({
    r => statusCodesWeighted.insertAll(statusCodesWeighted.size, List.fill(r._2)(r._1))
  })

  // Generate sample user ids
  var userIdsTemp = new ListBuffer[String]()
  (1 to 10000).foreach(_ => userIdsTemp += Random.alphanumeric.take(15).mkString)
  val userIds = userIdsTemp.toList

  // Generate sample instance ids
  var instanceIdsTemp = new ListBuffer[String]()
  (1 to 100).foreach(_ => instanceIdsTemp += Random.alphanumeric.take(15).mkString)
  val instanceIds = instanceIdsTemp.toList

  /**
   * Constructs a sample resource url like "/api/v1/json/user/profile"
   * @return the URL
   */
  def getResource(): String = {
    "/api/v1/json" + resourcesWeighted.toList(Random.nextInt(100))
  }

  /**
   * Creates a sample response code
   * @return the code
   */
  def getStatusCode(): Int = {
    statusCodesWeighted.toList(Random.nextInt(10000))
  }

  /**
   * Generates a random ip in the range (100-150).(100-150).(0-256).(0-256)
   * @return the generated IP
   */
  def getIp(): String = {
    (Random.nextInt(50) + 100) + "." + (Random.nextInt(50) + 100) + "." + Random.nextInt(256) + "." + Random.nextInt(256)
  }

  /**
   * Generates a random response time between (100-399)
   * @return the generated response time
   */
  def getResponseTime(): Int = {
    (Random.nextInt(3) + 1) * 100 + Random.nextInt(99)
  }

  /**
   * Queries for a random user agent
   * @return the user agent
   */
  def getUserAgent(): String = {
    USER_AGENTS(Random.nextInt(100))
  }

  /**
   * Queries for a random user id
   * @return the user id
   */
  def getUserId(): String = {
    userIds(Random.nextInt(10000))
  }

  /**
   * Queries for a random country
   * @return the country
   */
  def getCountry(): String = {
    COUNTRIES(Random.nextInt(118)).trim()
  }

  /**
   * Queries for a random instance id
   * @return the instance id
   */
  def getInstanceId(): String = {
    instanceIds(Random.nextInt(100))
  }

  def getLogLine(): String = {
    // ip___resource___statusCode___responseTime___userAgent
    getIp()+ "___" + getResource() + "___" + getStatusCode() + "___" + getResponseTime() + "___" +
      getUserAgent() + "___" + getUserId() + "___" + getCountry() + "___" + getInstanceId()
  }


}

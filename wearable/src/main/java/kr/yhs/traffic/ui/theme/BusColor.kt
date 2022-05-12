package kr.yhs.traffic.ui.theme

import androidx.compose.ui.graphics.Color
import kr.yhs.traffic.ui.WearableColors

enum class BusColor(
    val color: Color,
    val typeCode: Int
) {
    // Default
    Default(
        WearableColors.primary,
        0
    ),

    // Seoul
    SeoulAirport(
        Color(0xff8b4513),
        1101
    ),
    SeoulTown(
        Color(0xff5bb025),
        1102
    ),
    SeoulTrunk(
        Color(0xff3d5bab),
        1103
    ),
    SeoulBranch(
        Color(0xff5bb025),
        1104
    ),
    SeoulCircular(
        Color(0xfff99d1c),
        1104
    ),
    SeoulWideArea(
        Color(0xffF72f08),
        1104
    ),

    // Gyeonggi
    GyeonggiCityWideArea(
        Color(0xffFF0000),
        1211
    ),
    GyeonggiCitySeat(
        Color(0xff0075C8),
        1212
    ),
    GyeonggiCityNormal(
        Color(0xff33CC99),
        1213
    ),
    GyeonggiCityWideAreaRapid(
        Color(0xff00A2FF),
        1214
    ),
    GyeonggiCustomized(
        Color(0xffB62367),
        1215
    ),
    GyeonggiCircular(
        Color(0xffFF0000),
        1216
    ),
    GyeonggiCountryWideArea(
        Color(0xffFF0000),
        1221
    ),
    GyeonggiCountrySeat(
        Color(0xff0075C8),
        1222
    ),
    GyeonggiCountryNormal(
        Color(0xff33CC99),
        1223
    ),
    GyeonggiTown(
        Color(0xffF99D1C),
        1213
    ),
    GyeonggiOutSeat(
        Color(0xff0075C8),
        1242
    ),
    GyeonggiOutNormal(
        Color(0xffa800ff),
        1243
    ),
    GyeonggiAirport(
        Color(0xffaa9872),
        1251
    ),
    GyeonggiAirportSeat(
        Color(0xff0075C8),
        1252
    ),
    GyeonggiAirportNormal(
        Color(0xff8b4513),
        1253
    ),

    // Incheon
    IncheonBranch(
        Color(0xff5bb025),
        1301
    ),
    IncheonTrunk(
        Color(0xff3366cc),
        1302
    ),
    IncheonSeat(
        Color(0xff3d5bab),
        1303
    ),
    IncheonWideArea(
        Color(0xfff72f08),
        1304
    ),
    IncheonTown(
        Color(0xff5bb025),
        1306
    ),
    IncheonWideAreaRapid(
        Color(0xff00A2FF),
        1308
    ),
    IncheonBranchCircular(
        Color(0xff5bb025),
        1309
    )
}
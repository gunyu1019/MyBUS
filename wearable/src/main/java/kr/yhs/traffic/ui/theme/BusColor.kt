package kr.yhs.traffic.ui.theme

import androidx.compose.ui.graphics.Color

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
    BUS1101(
        Color(0xff8b4513),
        1101
    ),
    BUS1102(
        Color(0xff5bb025),
        1102
    ),
    BUS1103(
        Color(0xff3d5bab),
        1103
    ),
    BUS1104(
        Color(0xff5bb025),
        1104
    ),
    BUS1105(
        Color(0xfff99d1c),
        1104
    ),
    BUS1106(
        Color(0xffF72f08),
        1104
    ),

    // Gyeonggi
    BUS1211(
        Color(0xffFF0000),
        1211
    ),
    BUS1212(
        Color(0xff0075C8),
        1212
    ),
    BUS1213(
        Color(0xff33CC99),
        1213
    ),
    BUS1214(
        Color(0xff00A2FF),
        1214
    ),
    BUS1215(
        Color(0xffB62367),
        1215
    ),
    BUS1216(
        Color(0xffFF0000),
        1216
    ),
    BUS1221(
        Color(0xffFF0000),
        1221
    ),
    BUS1222(
        Color(0xff0075C8),
        1222
    ),
    BUS1223(
        Color(0xff33CC99),
        1223
    ),
    BUS1230(
        Color(0xffF99D1C),
        1230
    ),
    BUS1242(
        Color(0xff0075C8),
        1242
    ),
    BUS1243(
        Color(0xffa800ff),
        1243
    ),
    BUS1251(
        Color(0xffaa9872),
        1251
    ),
    BUS1252(
        Color(0xff0075C8),
        1252
    ),
    BUS1253(
        Color(0xff8b4513),
        1253
    ),

    // Incheon
    BUS1301(
        Color(0xff5bb025),
        1301
    ),
    BUS1302(
        Color(0xff3366cc),
        1302
    ),
    BUS1303(
        Color(0xff3d5bab),
        1303
    ),
    BUS1304(
        Color(0xfff72f08),
        1304
    ),
    BUS1306(
        Color(0xff5bb025),
        1306
    ),
    BUS1308(
        Color(0xff00A2FF),
        1308
    ),
    BUS1309(
        Color(0xff5bb025),
        1309
    ),

    // Busan
    BUS2101(
        Color(0xfff58220),
        2101
    ),
    BUS2102(
        Color(0xffff0000),
        2102
    ),
    BUS2103(
        Color(0xff3399ff),
        2103
    ),
    BUS2104(
        Color(0xff6EBF46),
        2104
    ),

    // Ulsan
    BUS2211(
        Color(0xff800020),
        2211
    ),
    BUS2212(
        Color(0xffffbc00),
        2212
    ),
    BUS2213(
        Color(0xff9999FF),
        2213
    ),
    BUS2220(
        Color(0xff99cc66),
        2220
    ),
    BUS2230(
        Color(0xff164e7a),
        2230
    ),


    // Changwon
    BUS2401(
        Color(0xff54CBCB),
        2411
    ),
    BUS2402(
        Color(0xff92CA63),
        2412
    ),
    BUS2403(
        Color(0xffFF9956),
        2413
    ),
    BUS2404(
        Color(0xff9D9488),
        2420
    ),

    // Gimhae
    BUS2501(
        Color(0xff00AC6C),
        2501
    ),
    BUS2502(
        Color(0xffFF8C00),
        2502
    )
}
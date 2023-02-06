package com.test.fitnessstudios.data.models.routes

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("routes")
    val routes: List<Route>,
    val status: String
)

/** Google Directions Response
 *
 *
{
"geocoded_waypoints":
[
{
"geocoder_status": "OK",
"place_id": "ChIJ7cv00DwsDogRAMDACa2m4K8",
"types": ["locality", "political"],
},
...
{
"geocoder_status": "OK",
"place_id": "ChIJE9on3F3HwoAR9AhGJW_fL-I",
"types": ["locality", "political"],
},
],
"routes":
[
{
"bounds":
{
"northeast": { "lat": 41.8781139, "lng": -87.6297872 },
"southwest": { "lat": 34.0523525, "lng": -118.2435717 },
},
"copyrights": "Map data ©2022 Google, INEGI",
"legs":
[
{
"distance": { "text": "579 mi", "value": 932311 },
"duration": { "text": "8 hours 48 mins", "value": 31653 },
"end_address": "Joplin, MO, USA",
"end_location": { "lat": 37.0842449, "lng": -94.513284 },
"start_address": "Chicago, IL, USA",
"start_location": { "lat": 41.8781139, "lng": -87.6297872 },
"steps":
[
{
"distance": { "text": "443 ft", "value": 135 },
"duration": { "text": "1 min", "value": 24 },
"end_location": { "lat": 41.8769003, "lng": -87.6297353 },
"html_instructions": "Head <b>south</b> on <b>S Federal St</b> toward <b>W Van Buren St</b>",
"polyline": { "points": "eir~FdezuOdCEjBC" },
"start_location": { "lat": 41.8781139, "lng": -87.6297872 },
"travel_mode": "DRIVING",
},
...
{
"distance": { "text": "0.6 mi", "value": 887 },
"duration": { "text": "3 mins", "value": 182 },
"end_location": { "lat": 41.8689131, "lng": -87.630596 },
"html_instructions": "Turn <b>left</b> at the 1st cross street onto <b>S Clark St</b>",
"maneuver": "turn-left",
"polyline":
{
"points": "qar~F`kzuOlBAb@?zA?\\CnBAZAt@?P?xAAl@C~EGxA?pAAJ?bAAL?NDr@?d@@J?f@?XAf@?rBAH?T?\\?B?v@AZ?",
},
"start_location": { "lat": 41.8768866, "lng": -87.63073 },
"travel_mode": "DRIVING",
}
],
"traffic_speed_entry": [],
"via_waypoint": [],
},
],
"overview_polyline":
{
"points": "eir~FdezuOxyFzfFl`G|qZvtSzqo@d}EhlLtqIdjFhrTfvEdca@{_@p{\\~`\\f}MprT~qRbyPnvObbXd_TxaEdxYxjRxvUnz\\bDfeLbpLxv@pmLroD`fTxrYfkTti\\xp@baMj_MoG|xNrlNpiL~cKn`OloFroRbk@lmDn_FnxHnQnz^~Thlo@uVlcc@|rQrnM~lAvlP|sH~qUlkPbzRxvE`cF~rPn_K~b`@tbDpx\\toK|_g@ldD|s_@piIzwc@nzNtlNdrKxoTtkXpeXrsO`l_@brWbkjAptBxoa@npNfiRv_QtcbAxpBlwb@rt_@ry`@pcKjtPpuPj`H`mNpsFjzHfpN~hIdfbAp}JfxY`Fxo^vdUnhq@}k@~`bAkMvli@cClxIjmGfHblG~bRhtF|eI~jFffQzsLldRr`WdrKxtKxb_@zlNrxU|qUpfYdwNpzP|rRxsc@zgWfdYtdBrqR|~Iz~Qv`ApvRntK`jI`cZz}n@nsW`mcA`}WndvB~iCdiVpjJnlErfNplA`i@bcGojCpsl@yeEn|l@{hDtsu@tl@lxs@~~DditAjcJzrf@~qIr`l@bbJrme@~rI|zC~dBfrO|yEdne@qAj{y@Qrsj@diAdns@l}EpnYwmDjn|@ywAbso@~kB|au@`{Alj|@csLzxcBl^|cr@fyJvwd@rxNxnlAyuJhqWjtCbmy@lwNjwpA`cSdimApgBxeNwbCx~_@{dB|y~@cyC|tfBxU~di@inFpxi@_bLpad@~wEbcNkJdxTwkDdeX`aHxh\\pgKlhs@r]`c_@imGdjUeCvnYajFfnN`Gd}c@u_Unm\\itMldMinIpgBadIzhLskDdy\\}iFz|UuvPnfo@v_A`}n@nvR~da@pdS`qUfnDljY|qLrbRdwO|ig@jtKt}k@frM~qo@~~JpwS~bA~pSgeGbvQc{Bzib@yiWzkuAecD`dh@mjKnhf@qwBl_ZzpDn}Wg_CrfUonH~pVpA|bRapAthi@n|FbgPnd@|p[yi@vjVqzHrhd@cgG|zl@jyE~g^|oLjyYvvBprWlpDrtd@ahHbyhApiBteSxpNzkHjtw@|yDjaPrmTzu@tqZjv@f_TuyGjxL{yMlcFafGh{IxDthNpxC|nZvxBzyUnhEpieAdsN|zw@qmAblk@t}Bhb^gvA|ml@{vPjweAkkDhpj@mmFnyZ`eAnsLjxKbfDnhUnrTdaSjtH``m@f`b@xyBbyGrjJ_|DxyGcyD`~Gfe@drNluRd`GlaC}cBfgObcAhh]vM`q`@|oBvgv@",
},
"summary": "I-55 S and I-44",
"warnings": [],
"waypoint_order": [0, 1],
},
],
"status": "OK",
}
 */
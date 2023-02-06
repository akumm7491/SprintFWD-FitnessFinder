package com.test.fitnessstudios.data.models.studio

import com.google.gson.annotations.SerializedName

data class StudioResponse(
    @SerializedName("businesses")
    val studios: List<Studio>
)

/**  Yelp Business Response Format
 *
 *
"id": "7LJr9F0mzGfVYbZAaEP9ew",
"alias": "body-definitions-scottsdale",
"name": "Body Definitions",
"image_url": "https://s3-media1.fl.yelpcdn.com/bphoto/vSu1nEMsz4uAFawvz3pRlg/o.jpg",
"is_closed": false,
"url": "https://www.yelp.com/biz/body-definitions-scottsdale?adjust_creative=y_ptukjaiO3LobK7A-9LVg&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_search&utm_source=y_ptukjaiO3LobK7A-9LVg",
"review_count": 1,
"categories": [
{
"alias": "healthtrainers",
"title": "Trainers"
}
],
"rating": 5,
"coordinates": {
"latitude": 33.5239486694336,
"longitude": -111.90355682373
},
"transactions": [],
"location": {
"address1": null,
"address2": null,
"address3": "",
"city": "Scottsdale",
"zip_code": "85250",
"country": "US",
"state": "AZ",
"display_address": [
"Scottsdale, AZ 85250"
]
},
"phone": "+16023916210",
"display_phone": "(602) 391-6210",
"distance": 667.8095535679169
 */
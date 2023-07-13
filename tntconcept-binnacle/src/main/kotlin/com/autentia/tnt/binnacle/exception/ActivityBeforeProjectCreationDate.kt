package com.autentia.tnt.binnacle.exception

class ActivityBeforeProjectCreationDate(message: String) : BinnacleException(message) {
    constructor() : this("The start of the activity is prior to the date the project was created.")
}
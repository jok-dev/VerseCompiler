
using { LGLib }
using { LGLib.Base }
using { LGLib.Concurrency }
using { /Verse.org/Simulation }
using { /Fortnite.com/Devices }

lg_device<public> := class<unique>(creative_device) {

    Name<protected> : string = ""
    var Disposed<protected> : logic = false
    var DeviceRoutines<private> : []routine = array{}

    OnBegin<override><final>()<suspends> : void = {
        Print("Hi there {Name}")
    }

    GetName<public>() : string = Name

}